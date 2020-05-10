package com.detons97gmail.progetto_embedded;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;;
import android.os.Bundle;
import android.view.Menu;

/**
 * Activity to show a list of species with relative name and image
 */

public class SpeciesListActivity extends AppCompatActivity {
    private static final int REQUEST_READ_EXTERNAL_STORAGE_TO_LOAD_DATA = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Load different layouts in relation to memory available
        //TODO: Insert fragment programmatically in order to pass information about which data to show
        setContentView(R.layout.activity_species_list_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        ((SearchView)menu.getItem(0).getActionView()).setQueryHint(getString(R.string.menu_search_tooltip));
        return true;
    }
}
