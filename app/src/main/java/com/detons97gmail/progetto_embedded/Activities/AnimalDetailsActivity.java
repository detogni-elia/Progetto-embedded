package com.detons97gmail.progetto_embedded.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.detons97gmail.progetto_embedded.IntentsExtras;
import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Utilities;


public class AnimalDetailsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ImageView imageView;
        LinearLayout nameEntry;
        LinearLayout speciesEntry;
        LinearLayout dietEntry;
        LinearLayout symptomsEntry;
        TextView speciesDescription;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_details);

        Intent intent = getIntent();
        imageView = findViewById(R.id.detailsImage);
        imageView.setImageURI(Uri.parse(intent.getStringExtra(IntentsExtras.EXTRA_IMAGE_PATH)));

        speciesDescription = findViewById(R.id.speciesDescription);
        speciesDescription.setText(R.string.lorem_ipsum);

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

        ((TextView)nameEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(IntentsExtras.EXTRA_NAME));
        ((TextView)speciesEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(IntentsExtras.EXTRA_SPECIES));
        ((TextView)dietEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(IntentsExtras.EXTRA_DIET));
        ((TextView)symptomsEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(IntentsExtras.EXTRA_SYMPTOMS));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.details_toolbar_title);
        }
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
}
