package com.detons97gmail.progetto_embedded.Activities;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Fragments.SpeciesListFragment;

/**
 * Activity to show a list of species with relative name and image
 */

public class SpeciesListActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private Toolbar toolbar;
    private ActionBar actionBar;

   private SpeciesListFragment speciesListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Load different layouts in relation to memory available
        setContentView(R.layout.activity_species_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.species_list_toolbar_title);
        }

        //Initialize and insert SpeciesListFragment
        //If we are restoring from a previous state, do nothing as to not overlap fragments
        if(savedInstanceState != null)
            return;


        Bundle fragBundle = getIntent().getExtras();
        speciesListFragment = new SpeciesListFragment();
        speciesListFragment.setArguments(fragBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.species_list_fragment_container, speciesListFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        ((SearchView)menu.getItem(0).getActionView()).setQueryHint(getString(R.string.species_list_query_hint));
        return true;
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

    //Se i riferimenti agli oggetti UI sono stati eliminati, li ripristino
    @Override
    public void onResume()
    {
        super.onResume();
        if(toolbar == null) {
            toolbar = findViewById(R.id.toolbar);
            Log.d("ON_RESUME", "Toolbar ripristinata");
        }
        if(actionBar == null){
            actionBar=getSupportActionBar();
            Log.d("ON_RESUME", "ActionBar ripristinata");
        }
    }
    //Release Memory when system resources becomes low
    //NON TESTATO NEI CASI DI MEMORY RUNNING LOW E CRITICAL
    //If e non SWITCH perchè mi da un warning se no implemento tutti i casi, ma non serve nel nostro caso
    public void onTrimMemory(int level)
    {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            //Sembrava funzionare correttamente anche senza onResume
            //App in background --> Remove UI widget
            toolbar = null;
            Log.d("TRIM_MEMORY_UI_HIDDEN", "Toolbar eliminata");
            actionBar = null;
            Log.d("TRIM_MEMORY_UI_HIDDEN", "ActionBar eliminata");
        }
        else if(level== ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            //Memory is on low level ==> resize the cache
            //10 Mb of cache
            speciesListFragment.resizeImageCache();
        }
        else if(level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            //Memory is on critical level ==> release the cache
            //Garbage Collector will clean
            speciesListFragment.deleteImageCache();
        }
    }
}
