package com.detons97gmail.progetto_embedded.Activities;

import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.detons97gmail.progetto_embedded.Adapters.SymptomsSearchAdapter;
import com.detons97gmail.progetto_embedded.Fragments.ResourcesDownloadDialogFragment;
import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Services.FakeDownloadIntentService;
import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SymptomsSelectionActivity extends AppCompatActivity implements SymptomsSearchAdapter.OnSymptomCheckListener, ComponentCallbacks2, FakeDownloadIntentService.DownloadCallbacks {
    private final String TAG = this.getClass().getSimpleName();

    private String[] symptomsDefaultNames;
    private String[] contactsDefaultNames;
    private ArrayList<SymptomsSearchAdapter.DataWrapper> data;
    private String[] downloadedCountries;
    private RecyclerView.LayoutManager manager;
    private SymptomsSearchAdapter adapter;
    private Spinner countries_spinner;
    private Spinner species_spinner;
    private Spinner contact_spinner;
    private FloatingActionButton fab;
    private int checkedCounter = 0;
    private boolean areResourcesAvailable;
    private boolean bound;

    //Class's keys to restore instance state
    private static final String CHECKED_COUNTER = "SymptomsSelectionActivity.CHECKED_COUNTER";
    private static final String SELECTIONS = "SymptomsSelectionActivity.SELECTIONS";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_selection);

        fab = findViewById(R.id.fab);
        countries_spinner = findViewById(R.id.countries_spinner);
        species_spinner = findViewById(R.id.species_spinner);
        contact_spinner = findViewById(R.id.contact_spinner);

        //Get localized symptoms and species categories to display
        String[] symptoms = Utilities.getLocalizedSymptoms(this);
        String[] categories = Utilities.getLocalizedCategories(this);
        String[] contacts = Utilities.getLocalizedContacts(this);

        //Get english symptoms names to pass via intent to SpeciesListActivity to interrogate the database
        symptomsDefaultNames = Values.getSymptomsDefaultNames();
        contactsDefaultNames = Values.getContactTypesDefaultNames();

        boolean[] selections;
        //If not restoring after orientation change
        if(savedInstanceState == null)
            selections = new boolean[symptoms.length];

        else{
            selections = savedInstanceState.getBooleanArray(SELECTIONS);
            checkedCounter = savedInstanceState.getInt(CHECKED_COUNTER);
            //We hide the FloatingActionButton if no symptom is checked, we want at least a symptom to interrogate the database
        }
        species_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories));
        contact_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, contacts));

        //Wrap the data for SymptomSearchAdapter
        data = SymptomsSearchAdapter.DataWrapper.wrapData(symptoms, selections);

        adapter = new SymptomsSearchAdapter(data, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        //Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.symptoms_search_toolbar_title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.symptoms_search_query_hint));
        //Listener for the search button on the ToolBar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(CHECKED_COUNTER, checkedCounter);
        boolean[] selections = new boolean[data.size()];
        for(int i = 0; i < selections.length; i++)
            selections[i] = data.get(i).isSelected();

        savedInstanceState.putBooleanArray(SELECTIONS, selections);
    }

    @Override
    protected void onPause(){
        if(bound) {
            unbindService(connection);
            bound = false;
            Log.v(TAG, "Service unbound");
        }
        super.onPause();
    }

    @Override public void onDestroy(){
        adapter = null;
        manager = null;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            //Should use NavUtils.navigateUpTo(this, new Intent(this, Activity.class))
            //With NavUtils we can navigate to a custom destination while destroying this Activity
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Update checkedCounter to determine if FloatingActionButton should be shown or not
    //We want to have at least 1 symptom selected in order to proceed
    @Override
    public void onSymptomChecked(int position, boolean checked) {
        if(checked)
            checkedCounter ++;
        else
            checkedCounter --;

        if(checkedCounter <= 0 || !areResourcesAvailable)
            fab.setVisibility(View.GONE);
        else
            fab.setVisibility(View.VISIBLE);
    }

    public void onFabClicked(View v){
        Intent startIntent = new Intent(this, SpeciesListActivity.class);
        //Get the symptoms to pass via Intent
        ArrayList<String> querySymptoms = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            if(data.get(i).isSelected()) {
                querySymptoms.add(symptomsDefaultNames[i]);
                if(querySymptoms.size() >= checkedCounter)
                    break;
            }

        }
        startIntent.putExtra(Values.EXTRA_CONTACT, contactsDefaultNames[contact_spinner.getSelectedItemPosition()]);
        startIntent.putExtra(Values.EXTRA_SYMPTOMS, querySymptoms.toArray(new String[]{}));
        startIntent.putExtra(Values.EXTRA_COUNTRY, downloadedCountries[countries_spinner.getSelectedItemPosition()]);

        switch (species_spinner.getSelectedItemPosition()){
            case 0:
                startIntent.putExtra(Values.EXTRA_CATEGORY, "Animals");
                break;
            case 1:
                startIntent.putExtra(Values.EXTRA_CATEGORY, "Insects");
                break;
            case 2:
                startIntent.putExtra(Values.EXTRA_CATEGORY, "Plants");
        }
        startActivity(startIntent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(fab == null) {
            fab = findViewById(R.id.fab);
            Log.d("ON_RESUME","Fab ripristinato");
        }
        if(countries_spinner == null) {
            countries_spinner = findViewById(R.id.countries_spinner);
            Log.d("ON_RESUME","Countries_spinner ripristinato");
        }
        if(species_spinner == null) {
            species_spinner = findViewById(R.id.species_spinner);
            Log.d("ON_RESUME","Species_spinner ripristinato");
        }
        checkResourcesAvailability();
    }

    public void onTrimMemory(int level)
    {
        if(level==ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        {
            countries_spinner=null;
            Log.d("TRIM_MEMOTY_UI_HIDDEN","Contries_spinner eliminato");
            species_spinner=null;
            Log.d("TRIM_MEMOTY_UI_HIDDEN","Species_spinner eliminato");
            fab=null;
            Log.d("TRIM_MEMOTY_UI_HIDDEN","Fab eliminato");
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FakeDownloadIntentService.ServiceBinder binder = (FakeDownloadIntentService.ServiceBinder) service;
            FakeDownloadIntentService mService = binder.getService();
            Log.d(TAG, "Service bound");
            bound = true;
            //If service is running we do nothing, we don't want to manage multiple downloads and we simply listen for updates from the service
            if(mService.isRunning()) {
                Log.v(TAG, "Service is already running");
            }
            //If service is not running, we ask the user to download resources
            else
                new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");

            mService.setCallback(SymptomsSelectionActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "Service unbound");
            bound = false;
        }
    };

    /**
     * Checks the device's storage for the required resources. If those are not present asks the user to download them.
     */
    private void checkResourcesAvailability(){
        //localizedCountries will contain translated country name to populate the spinner
        String[] localizedCountries;
        //Check app's files to get downloaded resources
        downloadedCountries = Utilities.getDownloadedCountries(this);
        //If no resources available, bind to FakeDownloadService to get updates about downloads
        if(downloadedCountries == null || downloadedCountries.length == 0) {
            //Reset adapter for spinner
            countries_spinner.setAdapter(null);
            Log.d(TAG, "No resources available");
            Intent startIntent = new Intent(SymptomsSelectionActivity.this, FakeDownloadIntentService.class);
            //Bind to FakeDownloadIntentService to listen to updates for the downloads
            bindService(startIntent, connection, Context.BIND_AUTO_CREATE);
            areResourcesAvailable = false;
        }
        else {
            //Translate country names
            localizedCountries = Utilities.getLocalizedCountries(this, downloadedCountries);
            //If at least one country's resources are available

            // Array adapter to set data in Spinner Widget
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, localizedCountries);
            // Setting the array adapter containing country list to the spinner widget
            countries_spinner.setAdapter(adapter);
            areResourcesAvailable = true;
        }
        //Show FAB only when both resources are available and at least one symptom is checked
        if(checkedCounter > 0 && areResourcesAvailable)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
    }

    //Implementation of FakeDownloadIntentService's interface callback method
    @Override
    public void notifyDownloadFinished() {
        unbindService(connection);
        bound = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkResourcesAvailability();
            }
        });
    }
}
