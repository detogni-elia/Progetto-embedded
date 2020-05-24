package com.detons97gmail.progetto_embedded.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.detons97gmail.progetto_embedded.FakeDownload;
import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;
import com.detons97gmail.progetto_embedded.R;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";

    //Contains the folders names of the resources stored
    private String[] countriesFolders;

    //UI Widgets
    private Spinner mSpinnerCountries;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Button animals_button;
    private Button insects_button;
    private Button plants_button;

    private Dialog permissionDialog;

    //permissions code
    static final int REQUEST_CODE=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar
        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);

        //set drawer
        drawer=findViewById(R.id.drawer);
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(actionBarDrawerToggle);
        //display burger icon
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView=findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //shared preference for the first run
        //permission dialog stuff
        SharedPreferences sharedPreferences = getSharedPreferences("com.detons97gmail.progetto_embedded", MODE_PRIVATE);
        permissionDialog=new Dialog(this);

        //is firstrun true? if so execute code for first run
        if (sharedPreferences.getBoolean("firstrun", true)) {
            Log.i("TAG", "onResume: first run started");
            // start code for first run
            permissionDialog.setContentView(R.layout.custom_dialog_box);
            //TextView permission_textView = permissionDialog.findViewById(R.id.permission_textView);
            //ImageView permission_ImageView = permissionDialog.findViewById(R.id.permission_ImageView);
            Button ok_button = permissionDialog.findViewById(R.id.ok_button);

            //show dialog box
            permissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            permissionDialog.show();

            //ok button click listener, ask for permissions if not already granted
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)+
                            ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)+
                                 ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                                     ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //permission not granted
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                        permissionDialog.dismiss();
                    }else{
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                        permissionDialog.dismiss();
                    }
                }
            });

            // set false if first run is completed
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }

    }
        public void onClickCategory(View v){
        String country = countriesFolders[mSpinnerCountries.getSelectedItemPosition()];
        String category;
        switch (v.getId()){
            case R.id.animals_button:
                category = Values.CATEGORY_ANIMALS;
                break;
            case R.id.insects_button:
                category = Values.CATEGORY_INSECTS;
                break;
            case R.id.plants_button:
                category = Values.CATEGORY_PLANTS;
                break;
            default:
                category = Values.CATEGORY_ANIMALS;
        }
        Intent startIntent = new Intent(this, SpeciesListActivity.class);
        startIntent.putExtra(Values.EXTRA_COUNTRY, country);
        startIntent.putExtra(Values.EXTRA_CATEGORY, category);
        startActivity(startIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.symptoms_drawer) {
            Intent symptomsIntent = new Intent(this, SymptomsSelectionActivity.class);
            startActivity(symptomsIntent);
        }
        if(item.getItemId()==R.id.bar_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return true;
    }

    //Reimposto i riferimenti agli oggetti della UI
    @Override
    public void onResume()
    {
        super.onResume();

        if(toolbar == null) {
            toolbar=findViewById(R.id.drawer_toolbar);
            Log.d("ON_RESUME", "Ripristinata la toolbar");
        }
        if(mSpinnerCountries == null) {
            findViewById(R.id.countries_spinner);
            Log.d("ON_RESUME", "Ripristinata lo spinner");
        }
        if(drawer == null) {
            drawer=findViewById(R.id.drawer);
            Log.d("ON_RESUME", "Ripristinata il drawer");
        }
        if(actionBarDrawerToggle == null){
            actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
            Log.d("ON_RESUME", "Ripristinata actionBarDrawerToggle");
        }
        if(navigationView == null){
            navigationView=findViewById(R.id.navigation_view);
            Log.d("ON_RESUME", "Ripristinata la navigationView");
        }
        animals_button = findViewById(R.id.animals_button);
        insects_button = findViewById(R.id.insects_button);
        plants_button = findViewById(R.id.plants_button);

        mSpinnerCountries = findViewById(R.id.countries_spinner);
        //localizedCountries will contain translated country name to populate the spinner
        String[] localizedCountries;
        //Check app's files to get downloaded resources
        countriesFolders = Utilities.getDownloadedCountries(this);
        //If no resources available, ask to download
        if(countriesFolders == null || countriesFolders.length == 0) {
            try {
                //We fake the download by copying the app's assets into the device's storage
                FakeDownload.copyAssetsToStorage(this, "India", "en");
                countriesFolders = Utilities.getDownloadedCountries(this);
            } catch (IOException e) {
                //TODO: TRANSLATE ERROR
                Utilities.showToast(this, "There was an error downloading the necessary files", Toast.LENGTH_SHORT);
                Log.e(TAG, "Error copying files to device's storage: " + e.toString());
            }
        }
        //Translate country names
        localizedCountries = Utilities.getLocalizedCountries(this, countriesFolders);

        if(localizedCountries.length > 0) {
            // Array adapter to set data in Spinner Widget
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, localizedCountries);
            // Setting the array adapter containing country list to the spinner widget
            mSpinnerCountries.setAdapter(adapter);
            animals_button.setEnabled(true);
            insects_button.setEnabled(true);
            plants_button.setEnabled(true);
        }

        //If download failed or was rejected by user, disable the ui buttons
        else{
            animals_button.setEnabled(false);
            insects_button.setEnabled(false);
            plants_button.setEnabled(false);
        }
    }

    //Gestione delle callbacks legate alla memoria
    public void onTrimMemory(int level)
    {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        {
            //Se l' app passa in background elimino i riferimenti all UI
            toolbar=null;
            Log.d("TRIM_MEMORY-UI-HIDDEN","Eliminata la toolbar");
            mSpinnerCountries=null;
            Log.d("TRIM_MEMORY-UI-HIDDEN","Eliminato lo spinner");
            drawer=null;
            Log.d("TRIM_MEMORY-UI-HIDDEN","Eliminato il drawer");
            actionBarDrawerToggle=null;
            Log.d("TRIM_MEMORY-UI-HIDDEN","Eliminato la actionBarDrawerTool");
            navigationView=null;
            Log.d("TRIM_MEMORY-UI-HIDDEN","Eliminato la navigationView");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if((grantResults.length>0)&&(grantResults[0]+grantResults[1]+grantResults[2]+grantResults[3]==PackageManager.PERMISSION_GRANTED))
            {
                //permissions have been granted
                Log.i("TAG", "onRequestPermissionsResult: Permission granted");
            }
            else
            {
                Toast.makeText(this, R.string.permissions_not_granted,Toast.LENGTH_SHORT).show();
            }
        }
    }
}

