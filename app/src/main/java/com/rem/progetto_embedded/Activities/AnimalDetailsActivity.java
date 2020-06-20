package com.rem.progetto_embedded.Activities;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.rem.progetto_embedded.Database.Entity.Contacts;
import com.rem.progetto_embedded.Database.Entity.Symptoms;
import com.rem.progetto_embedded.SpeciesViewModel;
import com.rem.progetto_embedded.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.rem.progetto_embedded.Values;
import com.rem.progetto_embedded.R;

import java.util.ArrayList;
import java.util.List;

public class AnimalDetailsActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private ImageView imageView;
    private LinearLayout nameEntry;
    private LinearLayout speciesEntry;
    private LinearLayout dietEntry;
    private LinearLayout symptomsEntry;
    private TextView speciesDescription;

    private Double animalLatitude;
    Double animalLongitude;
    String commonName;


    private final String TAG = this.getClass().getSimpleName();


    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_details);

        //This activity will always be launched via intent
        final Intent intent = getIntent();
        //get fusedLocation for device location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //get latitude,longitude,name to display animal location and information
        animalLatitude = intent.getDoubleExtra(Values.EXTRA_LATITUDE,0);
        animalLongitude = intent.getDoubleExtra(Values.EXTRA_LONGITUDE,0);
        Log.i(TAG, "onCreate: lat,long animal = "+animalLatitude+animalLongitude);

        commonName = intent.getStringExtra(Values.EXTRA_NAME);

        imageView = findViewById(R.id.detailsImage);
        //Show image of the animal/insect/plant
        Glide.with(this).load(intent.getStringExtra(Values.EXTRA_IMAGE_PATH)).placeholder(R.drawable.ic_placeholder_icon_vector).into(imageView);

        //Set toolbar and show "back" button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.details_toolbar_title);
        }

        //Set description of species
        speciesDescription = findViewById(R.id.speciesDescription);
        speciesDescription.setText(intent.getStringExtra(Values.EXTRA_DESCRIPTION));

        //If nameEntry is not present, we are in landscape layout and we do not need to update the other fields
        if(findViewById(R.id.nameEntry) == null)
            return;

        //Set titles of each entry
        nameEntry = findViewById(R.id.nameEntry);
        speciesEntry = findViewById(R.id.speciesEntry);
        dietEntry = findViewById(R.id.dietEntry);
        symptomsEntry = findViewById(R.id.symptomsEntry);

        ((TextView)nameEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_name);
        ((TextView)speciesEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_species);
        ((TextView)nameEntry.findViewById(R.id.layoutEntry)).setText(commonName);
        ((TextView)speciesEntry.findViewById(R.id.layoutEntry)).setText(intent.getStringExtra(Values.EXTRA_SPECIES));

        ((TextView)dietEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_diet);
        ((TextView)symptomsEntry.findViewById(R.id.layoutLabel)).setText(R.string.details_symptoms);

        //If diet is null we are showing a plant and we don't need to display the corresponding View
        String diet = intent.getStringExtra(Values.EXTRA_DIET);
        if(diet == null)
            dietEntry.setVisibility(View.GONE);
        else
            ((TextView)dietEntry.findViewById(R.id.layoutEntry)).setText(diet);

        //Get SpeciesViewModel to query the database to get contact type and symptoms caused by the animal/insect/plant
        final String latinName = intent.getStringExtra(Values.EXTRA_SPECIES);
        final SpeciesViewModel viewModel = new ViewModelProvider(this).get(SpeciesViewModel.class);
        String country = intent.getStringExtra(Values.EXTRA_COUNTRY);
        viewModel.getContact(country, latinName).observe(this, new Observer<Contacts>() {
            @Override
            public void onChanged(Contacts contacts) {
                //Symptoms and contacts are always in English in the database, we need to translate them
                final String contactType = Utilities.localizeContact(getApplicationContext(), contacts.toString());
                viewModel.getSymptoms(intent.getStringExtra(Values.EXTRA_COUNTRY), latinName).observe(AnimalDetailsActivity.this, new Observer<List<Symptoms>>() {
                    @Override
                    public void onChanged(List<Symptoms> result) {
                        List<String> symptomsList = new ArrayList<>();
                        for(Symptoms s: result)
                            symptomsList.add(s.toString());

                        //We display symptoms as a comma-separated list
                        String[] symptoms = Utilities.localizeSymptoms(getApplicationContext(), symptomsList.toArray(new String[]{}));
                        //Set text by formatting default string resource
                        TextView symptomsText = symptomsEntry.findViewById(R.id.layoutEntry);
                        symptomsText.setText(getString(R.string.symptoms_placeholder, contactType, TextUtils.join(",", symptoms)));
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_animal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.gps_button) {


        if(checkPermissions()) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //update or fetch a location
                    if (location != null) {
                        String myLat = String.valueOf(location.getLatitude());
                        String myLon = String.valueOf(location.getLongitude());

                        Log.i(TAG, "FusedLocation onSuccess: lat,lon " + myLat + " " + myLon);

                        SharedPreferences sharedPreferences = getSharedPreferences(Values.PREFERENCES_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Values.LATITUDE,myLat);
                        editor.putString(Values.LONGITUDE,myLon);
                        editor.apply();

                        startMap();

                    } else {
                        startMap();
                    }

                }
            });
        }else{
            //if location permission has not been accepted just display animal position
            startMap();
        }

    }

        if(item.getItemId()==android.R.id.home)
        finish();

        return super.onOptionsItemSelected(item);


}

    @Override
    public void onResume() {
        super.onResume();
        //Restore elements that were removed after a call to onTrimMemory()
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

    //METODO NON TESTATO, DA PROVARE QUANDO LA NAVIGAZIONE è' COMPLETA
    public void onTrimMemory(int level)
    {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        {
            //Release all the UI references, app is in background
            imageView = null;
            nameEntry = null;
            speciesEntry = null;
            dietEntry = null;
            symptomsEntry = null;
            speciesDescription = null;
            Log.d(TAG," Eliminati i riferimenti a tutti i widget UI");
        }
    }

    /**
     * Check if the app has permission to access to the device's Coarse location. Does not ask the user for this permission.
     * @return true if permissions are granted, false otherwise.
     */
    private boolean checkPermissions(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startMap(){
        Intent mapActivity = new Intent(getApplicationContext(), MapActivity.class);
        mapActivity.putExtra(Values.EXTRA_LATITUDE,animalLatitude);
        mapActivity.putExtra(Values.EXTRA_LONGITUDE,animalLongitude);
        mapActivity.putExtra(Values.EXTRA_NAME,commonName);
        startActivity(mapActivity);
    }
}
