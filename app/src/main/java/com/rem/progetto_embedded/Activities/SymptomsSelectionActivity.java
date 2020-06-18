package com.rem.progetto_embedded.Activities;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rem.progetto_embedded.Adapters.SymptomsSearchAdapter;
import com.rem.progetto_embedded.Fragments.ConnectionDialogFragment;
import com.rem.progetto_embedded.Fragments.ResourcesDownloadDialogFragment;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Services.FakeDownloadIntentService;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SymptomsSelectionActivity extends AppCompatActivity implements SymptomsSearchAdapter.OnSymptomCheckListener, ComponentCallbacks2, FakeDownloadIntentService.DownloadCallbacks {
    private final String TAG = this.getClass().getSimpleName();

    private String[] symptomsDefaultNames;
    private String[] contactsDefaultNames;
    private List<SymptomsSearchAdapter.DataWrapper> data;
    private String[] countriesFolders;
    private RecyclerView.LayoutManager manager;
    private SymptomsSearchAdapter adapter;
    private Spinner countriesSpinner;
    private Spinner speciesSpinner;
    private Spinner contactsSpinner;
    private FloatingActionButton fab;
    private int checkedCounter = 0;
    private boolean areResourcesAvailable;
    private boolean bound;
    private FakeDownloadIntentService mService;

    //Class's keys to restore instance state
    private static final String CHECKED_COUNTER = "SymptomsSelectionActivity.CHECKED_COUNTER";
    private static final String SELECTIONS = "SymptomsSelectionActivity.SELECTIONS";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_selection);

        setActivity(savedInstanceState);

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
        getMenuInflater().inflate(R.menu.search_menu_symptoms, menu);
        MenuItem searchItem = menu.getItem(1);
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
            mService = null;
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
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_reset:
                setActivity(null);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method sets the activity to the state described in savedInstanceState
     * @param savedInstanceState The Activity's saved instance state to restore
     */
    private void setActivity(Bundle savedInstanceState){
        fab = findViewById(R.id.fab);
        countriesSpinner = findViewById(R.id.countries_spinner);
        speciesSpinner = findViewById(R.id.species_spinner);
        contactsSpinner = findViewById(R.id.contact_spinner);

        //Get localized symptoms and species categories to display
        String[] symptoms = Utilities.localizeSymptoms(this, null);
        String[] categories = Utilities.getLocalizedCategories(this);
        String[] contacts = Utilities.getLocalizedContacts(this);

        //Get english symptoms names to pass via intent to SpeciesListActivity to interrogate the database
        symptomsDefaultNames = Values.getSymptomsDefaultNames();
        contactsDefaultNames = Values.getContactTypesDefaultNames();

        boolean[] selections;
        //If not restoring after orientation change but resetting the state (after pressing the reset button)
        if(savedInstanceState == null) {
            selections = new boolean[symptoms.length];
            checkedCounter = 0;
        }

        else{
            selections = savedInstanceState.getBooleanArray(SELECTIONS);
            checkedCounter = savedInstanceState.getInt(CHECKED_COUNTER);
            //We hide the FloatingActionButton if no symptom is checked, we want at least a symptom to interrogate the database
        }

        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(speciesAdapter);
        ArrayAdapter<String> contactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contacts);
        contactsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactsSpinner.setAdapter(contactsAdapter);

        //Wrap the data for SymptomSearchAdapter
        data = SymptomsSearchAdapter.DataWrapper.wrapData(symptoms, selections);

        adapter = new SymptomsSearchAdapter(data, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        updateNavigationState();
    }

    //Update checkedCounter to determine if FloatingActionButton should be shown or not
    //We want to have at least 1 symptom selected in order to proceed
    @Override
    public void onSymptomChecked(int position, boolean checked) {
        if(checked)
            checkedCounter ++;
        else
            checkedCounter --;

        updateNavigationState();
    }

    /**
     * Launch SpeciesListActivity with the parameters selected in the UI
     * @param v The fab calling this method
     */
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
        startIntent.putExtra(Values.EXTRA_CONTACT, contactsDefaultNames[contactsSpinner.getSelectedItemPosition()]);
        startIntent.putExtra(Values.EXTRA_SYMPTOMS, querySymptoms);
        startIntent.putExtra(Values.EXTRA_COUNTRY, countriesFolders[countriesSpinner.getSelectedItemPosition()]);
        startIntent.putExtra(Values.EXTRA_CONTACT, contactsDefaultNames[contactsSpinner.getSelectedItemPosition()]);

        switch (speciesSpinner.getSelectedItemPosition()){
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
        if(countriesSpinner == null) {
            countriesSpinner = findViewById(R.id.countries_spinner);
            Log.d("ON_RESUME","Countries_spinner ripristinato");
        }
        if(speciesSpinner == null) {
            speciesSpinner = findViewById(R.id.species_spinner);
            Log.d("ON_RESUME","Species_spinner ripristinato");
        }
        checkResourcesAvailability();
    }

    public void onTrimMemory(int level)
    {
        if(level==ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        {
            countriesSpinner =null;
            Log.d("TRIM_MEMOTY_UI_HIDDEN","Contries_spinner eliminato");
            speciesSpinner =null;
            Log.d("TRIM_MEMOTY_UI_HIDDEN","Species_spinner eliminato");
            fab=null;
            Log.d("TRIM_MEMOTY_UI_HIDDEN","Fab eliminato");
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FakeDownloadIntentService.ServiceBinder binder = (FakeDownloadIntentService.ServiceBinder) service;
            mService = binder.getService();
            Log.d(TAG, "Service bound");
            bound = true;
            //Register as callback to get updates regarding downloads
            mService.setCallback(SymptomsSelectionActivity.this);
            //If service is running we do nothing, we don't want to manage multiple downloads and we simply listen for updates from the service
            if(mService.isRunning() || countriesFolders != null) {
                Log.d(TAG, "Service is running or resources are already available");
            }
            else {
                //We show a message only if there isn't one already on screen. We want to avoid overlap of identical DialogFragments in case of configuration changes
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                boolean alreadyShowing = false;
                for (Fragment fragment : fragments) {
                    String fragmentName = fragment.getClass().getSimpleName();
                    if (fragmentName.equals(ConnectionDialogFragment.class.getSimpleName()) || fragmentName.equals(ResourcesDownloadDialogFragment.class.getSimpleName()))
                        alreadyShowing = true;
                }
                if (!alreadyShowing) {
                    //We show a cautionary message to the user about downloading with a metered connection if that's the case
                    if(Utilities.isConnectionMetered(getApplicationContext())) {
                        new ConnectionDialogFragment().show(getSupportFragmentManager(), "alert");
                    }

                    else
                        new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");
                }
            }
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
        //Check app's files to get downloaded resources
        countriesFolders = Utilities.getDownloadedCountries(this);
        Intent startIntent = new Intent(SymptomsSelectionActivity.this, FakeDownloadIntentService.class);
        //Bind to FakeDownloadIntentService to listen to updates for the downloads
        bindService(startIntent, connection, Context.BIND_AUTO_CREATE);
        //If no resources available, bind to FakeDownloadService to get updates about downloads
        if(countriesFolders == null) {
            //Reset adapter for spinner
            countriesSpinner.setAdapter(null);
            Log.d(TAG, "No resources available");
            areResourcesAvailable = false;
        }

        else
            updateCountriesSpinner();

        updateNavigationState();
    }

    /**
     * Sets the fab accordingly to the state of the Activity. Only if at least one symptom is selected and if at least one country has resources available
     * then activates the fab, making it visible. Otherwise the fab will be invisible and so not clickable
     */
    private void updateNavigationState(){
        //Show FAB only when both resources are available and at least one symptom is checked
        if(checkedCounter > 0 && areResourcesAvailable)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
    }

    /**
     * Populate countriesSpinner with the available countries
     */
    private void updateCountriesSpinner(){
        //Translate country names
        countriesFolders = Utilities.getDownloadedCountries(this);
        if(countriesFolders == null)
            return;
        String[] localizedCountries = Utilities.localizeCountries(this, countriesFolders);
        // Array adapter to set data in Spinner Widget
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, localizedCountries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the array adapter containing country list to the spinner widget
        countriesSpinner.setAdapter(adapter);
        areResourcesAvailable = true;
        updateNavigationState();
    }

    /**
     * Implementation of FakeDownloadIntentService's interface callback method
     */
    @Override
    public void onNotifyDownloadFinished() {
        unbindService(connection);
        bound = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCountriesSpinner();
            }
        });
    }
}
