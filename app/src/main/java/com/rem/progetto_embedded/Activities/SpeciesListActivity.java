package com.rem.progetto_embedded.Activities;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.rem.progetto_embedded.Adapters.SpeciesListAdapter;
import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.SpeciesViewModel;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;

import java.util.List;

/**
 * Activity to show a list of species with relative name and image
 */

public class SpeciesListActivity extends AppCompatActivity implements SpeciesListAdapter.OnSpeciesSelectedListener, ComponentCallbacks2 {
    private final String TAG = this.getClass().getSimpleName();

    private SpeciesListAdapter adapter;
    private SpeciesViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.species_list_toolbar_title);
        }

        RecyclerView recyclerView = findViewById(R.id.species_list_recycler_view);

        //Display items as list if orientation is portrait, else display them in rows of 2 elements per row
        int orientation = getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }
        else {
            layoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(layoutManager);
        }

        //Get SpeciesViewModel and query data
        viewModel = new ViewModelProvider(this).get(SpeciesViewModel.class);
        String country = getIntent().getStringExtra(Values.EXTRA_COUNTRY);
        String category = getIntent().getStringExtra(Values.EXTRA_CATEGORY);
        List<String> symptoms = getIntent().getStringArrayListExtra(Values.EXTRA_SYMPTOMS);
        String contact = getIntent().getStringExtra(Values.EXTRA_CONTACT);
        //viewModel.getData(country, category, null);
        adapter = new SpeciesListAdapter(getApplicationContext(), this);
        viewModel.getSpecies(country, category, symptoms, contact).observe(this, new Observer<List<Creatures>>() {
            @Override
            public void onChanged(List<Creatures> dataWrappers) {
                adapter.setData(dataWrappers);
                adapter.notifyDataSetChanged();
                if(dataWrappers.isEmpty()) {
                    Utilities.showToast(getApplicationContext(), getString(R.string.wow));
                    ((ImageView)findViewById(R.id.such_empty)).setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(adapter == null){
            viewModel = new ViewModelProvider(this).get(SpeciesViewModel.class);
            String country = getIntent().getStringExtra(Values.EXTRA_COUNTRY);
            String category = getIntent().getStringExtra(Values.EXTRA_CATEGORY);
            List<String> symptoms = getIntent().getStringArrayListExtra(Values.EXTRA_SYMPTOMS);
            String contact = getIntent().getStringExtra(Values.EXTRA_CONTACT);
            adapter = new SpeciesListAdapter(getApplicationContext(), this);
            viewModel.getSpecies(country, category, symptoms, contact).observe(this, new Observer<List<Creatures>>() {
                @Override
                public void onChanged(List<Creatures> dataWrappers) {
                    adapter.setData(dataWrappers);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        adapter.setClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.species_list_query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        adapter.setClickListener(null);
        Log.v(TAG, "onPause");
    }

    //Release Memory when system resources becomes low
    //NON TESTATO NEI CASI DI MEMORY RUNNING LOW E CRITICAL
    @Override
    public void onTrimMemory(int level) {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            //Sembrava funzionare correttamente anche senza onResume
            //App in background --> Remove UI widget
            viewModel = null;
            Log.d(TAG, "ViewModel eliminato");
            adapter = null;
            Log.d(TAG, "Adapter eliminata");
        }
    }

    @Override
    public void onSpeciesListItemClick(final int position) {
        final Intent startIntent = new Intent(this, AnimalDetailsActivity.class);
        Creatures element = adapter.getElementAt(position);
        startIntent.putExtra(Values.EXTRA_IMAGE_PATH, Utilities.getResourcesFolder(getApplicationContext()) + element.getImage());
        startIntent.putExtra(Values.EXTRA_NAME, element.getCommonName());
        startIntent.putExtra(Values.EXTRA_SPECIES, element.getLatinName());
        startIntent.putExtra(Values.EXTRA_DIET, element.getDiet());
        startIntent.putExtra(Values.EXTRA_DESCRIPTION, element.getDescription());
        startIntent.putExtra(Values.EXTRA_COUNTRY, getIntent().getStringExtra(Values.EXTRA_COUNTRY));
        startIntent.putExtra(Values.EXTRA_LATITUDE,element.getLatitude());
        startIntent.putExtra(Values.EXTRA_LONGITUDE,element.getLongitude());
        startActivity(startIntent);
    }
}
