package com.detons97gmail.progetto_embedded.Activities;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.detons97gmail.progetto_embedded.Values;
import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Utilities;


public class AnimalDetailsActivity extends AppCompatActivity implements ComponentCallbacks2
{

    private ImageView imageView;
    private LinearLayout nameEntry;
    private LinearLayout speciesEntry;
    private LinearLayout dietEntry;
    private LinearLayout symptomsEntry;
    private TextView speciesDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_details);

        Intent intent = getIntent();
        imageView = findViewById(R.id.detailsImage);
        imageView.setImageURI(Uri.parse(intent.getStringExtra(Values.EXTRA_IMAGE_PATH)));

        speciesDescription = findViewById(R.id.speciesDescription);
        speciesDescription.setText(R.string.lorem_ipsum);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.details_toolbar_title);
        }

        if(findViewById(R.id.nameEntry) == null)
            return;

        nameEntry = findViewById(R.id.nameEntry);
        speciesEntry = findViewById(R.id.speciesEntry);
        dietEntry = findViewById(R.id.dietEntry);
        symptomsEntry = findViewById(R.id.symptomsEntry);


        ((TextView)nameEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_name);
        ((TextView)speciesEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_species);
        ((TextView)dietEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_diet);
        ((TextView)symptomsEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_symptoms);

        ((TextView)nameEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(Values.EXTRA_NAME));
        ((TextView)speciesEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(Values.EXTRA_SPECIES));
        ((TextView)dietEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(Values.EXTRA_DIET));
        ((TextView)symptomsEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(Values.EXTRA_SYMPTOMS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_animal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.gps_button)
            Utilities.showToast(this, "Connettere alla mappa", Toast.LENGTH_SHORT);

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(imageView == null) {
            imageView = findViewById(R.id.detailsImage);
            Log.d("ON_RESUME","ImageView ripristinata");
        }
        if(nameEntry == null) {
            nameEntry = findViewById(R.id.nameEntry);
            Log.d("ON_RESUME","nameEntry ripristinata");
        }
        if(speciesEntry == null) {
            speciesEntry = findViewById(R.id.speciesEntry);
            Log.d("ON_RESUME","SpeciesEntry ripristinata");
        }
        if(dietEntry == null)
        {
            dietEntry = findViewById(R.id.dietEntry);
            Log.d("ON_RESUME","DietEntry ripristinata");
        }
        if(symptomsEntry == null) {
            symptomsEntry = findViewById(R.id.symptomsEntry);
            Log.d("ON_RESUME","SymptomsEntry ripristinata");
        }
        if(speciesDescription == null) {
            speciesDescription = findViewById(R.id.speciesDescription);
            Log.d("ON_RESUME","SpeciesDescription ripristinata");
        }
    }

    //METODO NON TESTATO, DA PROVARE QUANDO LA NAVIGAZIONE è' COMPLETA
    public void onTrimMemory(int level)
    {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        {
            //Release all the UI references, app is in background
            imageView=null;
            nameEntry=null;
            speciesEntry=null;
            dietEntry=null;
            symptomsEntry=null;
            speciesDescription=null;
            Log.d("TRIM_MEMORY_UI_HIDDEN"," Eliminati i riferimenti a tutti i widget UI");
        }
    }
}
