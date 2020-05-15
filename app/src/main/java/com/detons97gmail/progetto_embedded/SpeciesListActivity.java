package com.detons97gmail.progetto_embedded;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        ActionBar bar = getSupportActionBar();
        if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        ((SearchView)menu.getItem(0).getActionView()).setQueryHint(getString(R.string.menu_search_tooltip));
        return true;
    }

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
}
