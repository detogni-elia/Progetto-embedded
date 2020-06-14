package com.rem.progetto_embedded.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.Database.Entity.Contacts;
import com.rem.progetto_embedded.Database.Entity.Symptoms;
import com.rem.progetto_embedded.Values;
import com.rem.progetto_embedded.R;

import java.util.List;


public class AnimalDetailsActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private ImageView imageView;
    private LinearLayout nameEntry;
    private LinearLayout speciesEntry;
    private LinearLayout dietEntry;
    private LinearLayout symptomsEntry;
    private TextView speciesDescription;
    private String contactType;
    private String[] symptoms;

    private final String TAG = this.getClass().getSimpleName();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_details);

        Intent intent = getIntent();
        imageView = findViewById(R.id.detailsImage);
        imageView.setImageURI(Uri.parse(intent.getStringExtra(Values.EXTRA_IMAGE_PATH)));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.details_toolbar_title);
        }

        speciesDescription = findViewById(R.id.speciesDescription);
        speciesDescription.setText(intent.getStringExtra(Values.EXTRA_DESCRIPTION));

        if(savedInstanceState != null){
            contactType = savedInstanceState.getString(Values.EXTRA_CONTACT);
            symptoms = savedInstanceState.getStringArray(Values.EXTRA_SYMPTOMS);
        }

        //If nameEntry is not present, we are in landscape layout and we do not have to update the TextViews
        if(findViewById(R.id.nameEntry) == null)
            return;

        final String speciesName = intent.getStringExtra(Values.EXTRA_SPECIES);

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


        if(savedInstanceState == null){
            final AppDatabase db = AppDatabase.getInstance(this, intent.getStringExtra(Values.EXTRA_COUNTRY));
            db.creaturesDao().getContactOfCreature(speciesName).observe(this, new Observer<Contacts>() {
                @Override
                public void onChanged(Contacts contacts) {
                    contactType = contacts.toString();
                    db.creaturesDao().getSymptomsOfCreature(speciesName).observe(AnimalDetailsActivity.this, new Observer<List<Symptoms>>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onChanged(List<Symptoms> symptomsList) {
                            int i = 0;
                            symptoms = new String[symptomsList.size()];
                            for(Symptoms s: symptomsList)
                                symptoms[i++] = s.toString();

                            StringBuilder builder = new StringBuilder();
                            for(i = 0; i < symptoms.length - 1; i++)
                                builder.append(symptoms[i]).append(", ");

                            builder.append(symptoms[symptoms.length - 1]);
                            ((TextView)symptomsEntry.findViewById(R.id.layoutEntry)).setText(contactType + ": " + builder.toString());
                        }
                    });
                }
            });
        }
        else {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < symptoms.length - 1; i++)
                builder.append(symptoms[i]).append(", ");

            builder.append(symptoms[symptoms.length - 1]);

            ((TextView) symptomsEntry.findViewById(R.id.layoutEntry)).setText(contactType + ": " + builder.toString());
        }
        //((TextView)symptomsEntry.findViewById(R.id.layoutEntry)).setText(contactType + ": " + builder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_animal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.gps_button)
            {
                if(checkPermissions())
                    {

                        //Intent mapActivity = new Intent(getApplicationContext(),MapActivity.class);
                        //startActivity(mapActivity);
                    }
                else
                    {Toast.makeText(getApplicationContext(),R.string.permissions_not_granted,Toast.LENGTH_SHORT).show();}
            }

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
            Log.d(TAG,"ImageView ripristinata");
        }
        if(nameEntry == null) {
            nameEntry = findViewById(R.id.nameEntry);
            Log.d(TAG,"nameEntry ripristinata");
        }
        if(speciesEntry == null) {
            speciesEntry = findViewById(R.id.speciesEntry);
            Log.d(TAG,"SpeciesEntry ripristinata");
        }
        if(dietEntry == null)
        {
            dietEntry = findViewById(R.id.dietEntry);
            Log.d(TAG,"DietEntry ripristinata");
        }
        if(symptomsEntry == null) {
            symptomsEntry = findViewById(R.id.symptomsEntry);
            Log.d(TAG,"SymptomsEntry ripristinata");
        }
        if(speciesDescription == null) {
            speciesDescription = findViewById(R.id.speciesDescription);
            Log.d(TAG,"SpeciesDescription ripristinata");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Values.EXTRA_CONTACT, contactType);
        savedInstanceState.putStringArray(Values.EXTRA_SYMPTOMS, symptoms);
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
            Log.d(TAG," Eliminati i riferimenti a tutti i widget UI");
        }
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
}
