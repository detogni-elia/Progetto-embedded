package com.detons97gmail.progetto_embedded.Activities;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;

import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Fragments.SpeciesListFragment;

/**
 * Activity to show a list of species with relative name and image
 */

public class SpeciesListActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Load different layouts in relation to memory available
        //TODO: Insert fragment programmatically in order to pass information about which data to show
        setContentView(R.layout.activity_species_list_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        //Initialize and insert SpeciesListFragment
        //If we are restoring from a previous state, do nothing as to not overlap fragments
        if(savedInstanceState != null)
            return;

        Bundle fragBundle = getIntent().getExtras();
        SpeciesListFragment speciesListFragment = new SpeciesListFragment();
        speciesListFragment.setArguments(fragBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.species_list_fragment_container, speciesListFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        ((SearchView)menu.getItem(0).getActionView()).setQueryHint(getString(R.string.menu_search_tooltip));
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            //Should use NavUtils.navigateUpTo(this, new Intent(this, Activity.class))
            //With NavUtils we can navigate to a custom destination while destroying this Activity
            //TODO: Add ToolBar instead of letting the app set a default
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

     */
}
