package com.detons97gmail.progetto_embedded.Activities;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.detons97gmail.progetto_embedded.Adapters.SymptomsSearchAdapter;
import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

//TODO: COMMENT CODE AND IMPLEMENT ACTIVITY TO SELECT COUNTRY, CONTACT TYPE AND IF ANIMAL, INSECT OR PLANT. THE ACTIVITY WILL THEN LAUNCH THIS ACTIVITY
public class SymptomsSelectionActivity extends AppCompatActivity implements SymptomsSearchAdapter.OnSymptomCheckListener, ComponentCallbacks2 {
    private String[] symptomsDefaultNames;
    private ArrayList<SymptomsSearchAdapter.DataWrapper> data;
    private String[] downloadedCountries;
    private RecyclerView.LayoutManager manager;
    private SymptomsSearchAdapter adapter;

    private Spinner countries_spinner;
    private Spinner species_spinner;
    private FloatingActionButton fab;

    private static final String DOWNLOADED_COUNTRIES = "SymptomsSelectionActivity.DOWNLOADED_COUNTRIES";
    private static final String CHECKED_COUNTER = "SymptomsSelectionActivity.CHECKED_COUNTER";
    private static final String SELECTIONS = "SymptomsSelectionActivity.SELECTIONS";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_selection);

        fab = findViewById(R.id.fab);
        countries_spinner = findViewById(R.id.countries_spinner);
        species_spinner = findViewById(R.id.species_spinner);

        //Get localized symptoms and species categories to display
        String[] symptoms = Utilities.getLocalizedSymptoms(this);
        String[] categories = Utilities.getLocalizedCategories(this);

        //Get english symptoms names to pass via intent to SpeciesListActivity to interrogate the database
        symptomsDefaultNames = Values.getSymptomsDefaultNames();

        boolean[] selections;
        String[] localizedCountries;
        //If not restoring after orientation change
        if(savedInstanceState == null) {
            //Scan app private folder to look for available resources
            downloadedCountries = Utilities.getDownloadedCountries(this);
            selections = new boolean[symptoms.length];
        }
        else{
            downloadedCountries = savedInstanceState.getStringArray(DOWNLOADED_COUNTRIES);
            selections = savedInstanceState.getBooleanArray(SELECTIONS);
            checkedCounter = savedInstanceState.getInt(CHECKED_COUNTER);
            //We hide the FloatingActionButton if no symptom is checked, we want at least a symptom to interrogate the database
            if(checkedCounter > 0)
                fab.setVisibility(View.VISIBLE);
        }
        //Get the localized countries names
        localizedCountries = Utilities.getLocalizedCountries(this, downloadedCountries);

        countries_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, localizedCountries));
        species_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories));

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchView searchView = (SearchView) searchItem.getActionView();
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

        //Save downloaded country as to not scan storage each configuration change
        savedInstanceState.putStringArray(DOWNLOADED_COUNTRIES, downloadedCountries);
        savedInstanceState.putInt(CHECKED_COUNTER, checkedCounter);

        boolean[] selections = new boolean[data.size()];
        for(int i = 0; i < selections.length; i++)
            selections[i] = data.get(i).isSelected();

        savedInstanceState.putBooleanArray(SELECTIONS, selections);
    }

    @Override public void onDestroy(){
        adapter = null;
        manager = null;
        super.onDestroy();
    }

    private int checkedCounter = 0;

    //Update checkedCounter to determine if FloatingActionButton should be shown or not
    //We want to have at least 1 symptom selected in order to proceed
    @Override
    public void onSymptomChecked(int position, boolean checked) {
        if(checked)
            checkedCounter ++;
        else
            checkedCounter --;

        if(checkedCounter <= 0)
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
}
