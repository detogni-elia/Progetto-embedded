package com.detons97gmail.progetto_embedded.Activities;

import android.content.Intent;
import android.os.Bundle;
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
public class SymptomsSearchActivity extends AppCompatActivity implements SymptomsSearchAdapter.OnSymptomCheckListener {
    private String[] symptoms;
    private boolean[] selections;
    private ArrayList<SymptomsSearchAdapter.DataWrapper> data;
    private String[] downloadedCountries;
    private String[] localizedCountries;
    private RecyclerView.LayoutManager manager;
    private SymptomsSearchAdapter adapter;

    private Spinner countries_spinner;
    private Spinner species_spinner;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_search);
        fab = findViewById(R.id.fab);
        countries_spinner = findViewById(R.id.countries_spinner);
        species_spinner = findViewById(R.id.species_spinner);

        int[] symptomsIds = Values.SYMPTOMS_IDS;
        if(savedInstanceState == null) {
            symptoms = new String[symptomsIds.length];
            selections = new boolean[symptomsIds.length];
            for (int i = 0; i < symptoms.length; i++) {
                symptoms[i] = getString(symptomsIds[i]);
                selections[i] = false;
            }
        }
        else{
            symptoms = savedInstanceState.getStringArray(Values.EXTRA_SYMPTOMS);
            selections = savedInstanceState.getBooleanArray(Values.EXTRA_SYMPTOMS_SELECTIONS);
            checkedCounter = savedInstanceState.getInt(Values.EXTRA_N_CHECKED_SYMPTOMS);
            if(checkedCounter > 0)
                fab.setVisibility(View.VISIBLE);
        }

        String[] species = {getString(R.string.animals), getString(R.string.insects), getString(R.string.plants)};
        downloadedCountries = Utilities.getDownloadedCountries(this);
        localizedCountries = Utilities.getLocalizationForCountries(this, downloadedCountries);
        countries_spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, localizedCountries));
        species_spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, species));

        data = SymptomsSearchAdapter.DataWrapper.wrapData(symptoms, selections);
        adapter = new SymptomsSearchAdapter(data, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchView searchView = (SearchView) searchItem.getActionView();
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
        String[] symptoms = new String[data.size()];
        boolean[] selections = new boolean[data.size()];
        for(int i = 0; i < symptoms.length; i++){
            symptoms[i] = data.get(i).getSymptom();
            selections[i] = data.get(i).isSelected();
        }
        savedInstanceState.putStringArray(Values.EXTRA_SYMPTOMS, symptoms);
        savedInstanceState.putBooleanArray(Values.EXTRA_SYMPTOMS_SELECTIONS, selections);
        savedInstanceState.putInt(Values.EXTRA_N_CHECKED_SYMPTOMS, checkedCounter);
    }

    @Override public void onDestroy(){
        adapter = null;
        manager = null;
        super.onDestroy();
    }

    private int checkedCounter = 0;

    //Update checkedCounter to determine if FloatingActionButton should be shown or not
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
        ArrayList<String> querySymptoms = new ArrayList<>();
        for(int i = 0; i < selections.length; i++){
            if(selections[i])
                querySymptoms.add(Values.SYMPTOMS_STRINGS[i]);

        }
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
        startIntent.putExtra(Values.EXTRA_SYMPTOMS, querySymptoms.toArray(new String[]{}));
        startActivity(startIntent);
    }
}
