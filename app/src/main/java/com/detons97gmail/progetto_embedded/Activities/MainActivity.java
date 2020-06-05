package com.detons97gmail.progetto_embedded.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import java.util.List;
import java.util.Locale;

import com.detons97gmail.progetto_embedded.Fragments.ConnectionDialogFragment;
import com.detons97gmail.progetto_embedded.Fragments.ResourcesDownloadDialogFragment;
import com.detons97gmail.progetto_embedded.Services.FakeDownloadIntentService;
import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;
import com.detons97gmail.progetto_embedded.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FakeDownloadIntentService.DownloadCallbacks{
    private static final String TAG = "MainActivity";

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

    private boolean bound;

    //permissions codes
    private static final int REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar
        toolbar = findViewById(R.id.toolbar);
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
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("com.detons97gmail.progetto_embedded", MODE_PRIVATE);
        permissionDialog=new Dialog(this);

        //is firstrun true? if so execute code for first run

        if (sharedPreferences.getBoolean("firstrun", true)) {
            Log.i(TAG, "onResume: first run started");
            // start code for first run
            permissionDialog.setContentView(R.layout.first_start_permissions_dialog_layout);
            //TextView permission_textView = permissionDialog.findViewById(R.id.permission_textView);
            //ImageView permission_ImageView = permissionDialog.findViewById(R.id.permission_ImageView);
            Button ok_button = permissionDialog.findViewById(R.id.ok_button);

            //show dialog box
            Window window = permissionDialog.getWindow();
            if(window != null)
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            permissionDialog.show();

            //ok button click listener, ask for permissions if not already granted
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                            + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                            + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
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
            //set update position to true on first run
            sharedPreferences.edit().putBoolean("updatePosition",true).apply();
            //set delete cache to false on first run
            sharedPreferences.edit().putBoolean("deleteCache",false).apply();
        }

         */
    }

    @Override
    protected void onPause(){
        if(bound) {
            unbindService(connection);
            bound = false;
            Log.v(TAG, "Service unbound");
        }
        super.onPause();
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

    @Override
    public void onResume() {
        super.onResume();
        //Reimposto i riferimenti agli oggetti della UI
        if(toolbar == null) {
            toolbar=findViewById(R.id.toolbar);
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
            Log.d(TAG, "Ripristinata actionBarDrawerToggle");
        }
        if(navigationView == null){
            navigationView=findViewById(R.id.navigation_view);
            Log.d(TAG, "Ripristinata la navigationView");
        }

        if(animals_button == null || insects_button == null || plants_button == null){
            animals_button = findViewById(R.id.animals_button);
            insects_button = findViewById(R.id.insects_button);
            plants_button = findViewById(R.id.plants_button);
            Log.d(TAG, "Restored UI buttons");
        }
        if(mSpinnerCountries == null){
            mSpinnerCountries = findViewById(R.id.countries_spinner);
            Log.d(TAG, "Restored spinner");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("com.detons97gmail.progetto_embedded", MODE_PRIVATE);
        permissionDialog=new Dialog(this);

        if (sharedPreferences.getBoolean("firstrun", true)) {
            Log.i(TAG, "onResume: first run started");
            // start code for first run
            permissionDialog.setContentView(R.layout.first_start_permissions_dialog_layout);
            //TextView permission_textView = permissionDialog.findViewById(R.id.permission_textView);
            //ImageView permission_ImageView = permissionDialog.findViewById(R.id.permission_ImageView);
            Button ok_button = permissionDialog.findViewById(R.id.ok_button);

            //show dialog box
            Window window = permissionDialog.getWindow();
            if(window != null)
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            permissionDialog.show();

            //ok button click listener, ask for permissions if not already granted
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                            + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                            + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //permission not granted
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                        permissionDialog.dismiss();

                    }
                    else{
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                        permissionDialog.dismiss();
                        checkResourcesAvailability();
                    }
                }
            });

            // set false if first run is completed
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
            //set update position to true on first run
            sharedPreferences.edit().putBoolean("updatePosition",true).apply();
            //set delete cache to false on first run
            sharedPreferences.edit().putBoolean("deleteCache",false).apply();
        }
        else
            checkResourcesAvailability();

        //Check whether app's resources are available or not, handling accordingly
        //checkResourcesAvailability();
    }

    //Gestione delle callbacks legate alla memoria
    public void onTrimMemory(int level)
    {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        {
            Log.d(TAG, "onTrimMemory called");
            //Se l' app passa in background elimino i riferimenti all UI
            toolbar=null;
            Log.d(TAG,"Eliminata la toolbar");
            mSpinnerCountries=null;
            Log.d(TAG,"Eliminato lo spinner");
            drawer=null;
            Log.d(TAG,"Eliminato il drawer");
            actionBarDrawerToggle=null;
            Log.d(TAG,"Eliminato la actionBarDrawerTool");
            navigationView=null;
            Log.d(TAG,"Eliminato la navigationView");
            animals_button = null;
            insects_button = null;
            plants_button = null;
            Log.d(TAG, "Removed references to UI buttons");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if((grantResults.length>0)&&(grantResults[0]+grantResults[1]+grantResults[2]+grantResults[3]==PackageManager.PERMISSION_GRANTED))
                {
                    //permissions have been granted
                    Log.i("TAG", "onRequestPermissionsResult: Permission granted");
                    checkResourcesAvailability();
                }
                else
                {
                    Toast.makeText(this, R.string.permissions_not_granted,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Checks the device's storage for the required resources. If those are not present asks the user to download them.
     */
    private void checkResourcesAvailability(){
        //localizedCountries will contain translated country name to populate countriesSpinner
        String[] localizedCountries;
        //Check app's files to get downloaded resources
        //Contains the folders names of the resources stored
        String[] countriesFolders = Utilities.getDownloadedCountries(this);
        //If no resources available, bind to FakeDownloadIntentService to get updates about downloads
        if(countriesFolders == null || countriesFolders.length == 0) {
            //Reset adapter if resources were removed while the app was in background
            mSpinnerCountries.setAdapter(null);
            Log.d(TAG, "No resources available");
            Intent startIntent = new Intent(MainActivity.this, FakeDownloadIntentService.class);
            //Bind to FakeDownloadIntentService to listen to updates for the downloads
            bindService(startIntent, connection, Context.BIND_AUTO_CREATE);
            //Disable navigation until resources are available
            setUiButtonsEnabled(false);
        }
        else {
            //Translate names of available countries to display them
            localizedCountries = Utilities.getLocalizedCountries(this, countriesFolders);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, localizedCountries);
            mSpinnerCountries.setAdapter(adapter);
            //Enable navigation now that there are resources
            setUiButtonsEnabled(true);
        }
    }

    /**
     * Method handles clicks for the MainActivity's buttons
     * @param v The button clicked
     */
    public void onClickCategory(View v){
        //Translate selected country's name to query SQLite database
        String country = Utilities.getCountryNameInEnglish(this, (String)mSpinnerCountries.getSelectedItem());
        //String country = countriesFolders[mSpinnerCountries.getSelectedItemPosition()];
        String category;
        switch (v.getId()){
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

    //ServiceConnection object to use for binding with FakeDownloadIntentService
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FakeDownloadIntentService.ServiceBinder binder = (FakeDownloadIntentService.ServiceBinder) service;
            FakeDownloadIntentService mService = binder.getService();
            Log.d(TAG, "Service bound");
            bound = true;
            //If service is running we do nothing, we don't want to manage multiple downloads and we simply listen for updates from the service
            if(mService.isRunning()) {
                Log.v(TAG, "Service is already running");
            }

            //If service is not running, it means we should ask the user to download resources
            else {
                //We show a message only if there isn't one already on screen. We want to avoid overlap of identical DialogFragments
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                boolean alreadyShowing = false;
                for (Fragment fragment : fragments) {
                    String fragmentName = fragment.getClass().getSimpleName();
                    if (fragmentName.equals(ConnectionDialogFragment.class.getSimpleName()) || fragmentName.equals(ResourcesDownloadDialogFragment.class.getSimpleName()))
                        alreadyShowing = true;
                }
                if (!alreadyShowing) {
                    //We show a cautionary message to the user about downloading with a metered connection if that's the case
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean isConnectionMetered = cm != null && cm.isActiveNetworkMetered();
                    if(isConnectionMetered)
                        new ConnectionDialogFragment().show(getSupportFragmentManager(), "alert");
                        //new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");

                    else
                        new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");
                }
            }

            //Register as callback to get updates regarding downloads
            mService.setCallback(MainActivity.this);
            /*
            else {
                permissionDialog.setContentView(R.layout.resources_download_dialog_layout);
                Button ok_button = permissionDialog.findViewById(R.id.ok_button);
                Button cancel_button = permissionDialog.findViewById(R.id.cancel_button);

                final Spinner countries_spinner = permissionDialog.findViewById(R.id.countries_spinner);
                String[] supportedCountries = Utilities.getLocalizedSupportedCountries(getApplicationContext());
                ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, supportedCountries);
                countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                countries_spinner.setAdapter(countriesAdapter);

                final Spinner languages_spinner = permissionDialog.findViewById(R.id.languages_spinner);
                String[] supportedLanguages = Utilities.getLocalizedSupportedLanguages(getApplicationContext());
                ArrayAdapter<String> languagesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, supportedLanguages);
                languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                languages_spinner.setAdapter(languagesAdapter);

                final Spinner image_quality_spinner = permissionDialog.findViewById(R.id.image_quality_spinner);
                String[] supportedImageQuality = Utilities.getSupportedImageQuality(getApplicationContext());
                ArrayAdapter<String> imagesQualityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, supportedImageQuality);
                imagesQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                image_quality_spinner.setAdapter(imagesQualityAdapter);

                //show dialog box
                Window window = permissionDialog.getWindow();
                if(window != null)
                    //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                permissionDialog.show();

                //Listener for the dialog's OK button
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String country = (String) countries_spinner.getSelectedItem();
                        String language = (String)languages_spinner.getSelectedItem();
                        String imageQuality = (String)image_quality_spinner.getSelectedItem();
                        //save in SharedPreference for settings activity display information layout
                        SharedPreferences prefs=getSharedPreferences("com.detons97gmail.progetto_embedded",MODE_PRIVATE);
                        SharedPreferences.Editor editor=prefs.edit();
                        editor.putString("selectedLanguage",language);
                        editor.putString("selectedImageQuality",imageQuality);
                        editor.apply();
                        //change language displayed in run time
                        Log.i(TAG, "onClick: ");
                        if(language.equalsIgnoreCase("italian"))
                            setLocale("it");
                        else
                            setLocale("en");

                        startDownloadService(Utilities.getDefaultCountryName(getApplicationContext(), country), Utilities.getDefaultLanguageName(getApplicationContext(), language));
                        permissionDialog.dismiss();
                    }
                });
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionDialog.dismiss();
                    }
                });
            }
             */
        }
        /*
        private void startDownloadService(String country, String language){
            Intent startIntent = new Intent(MainActivity.this, FakeDownloadIntentService.class);
            startIntent.putExtra(Values.EXTRA_COUNTRY, country);
            startIntent.putExtra(Values.EXTRA_LANGUAGE, language);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(startIntent);
            else
                startService(startIntent);

            mService.setCallback(MainActivity.this);
        }

         */

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "Service unbound");
            bound = false;
            //checkResourcesState();
        }
    };

    /**
     * Disable UI buttons
     * @param enabled if buttons are to be enabled or disabled
     */
    private void setUiButtonsEnabled(boolean enabled){
        animals_button.setEnabled(enabled);
        insects_button.setEnabled(enabled);
        plants_button.setEnabled(enabled);
    }

    /**
     * Method called by FakeDownloadIntentService to notify activity when a download completes
     */
    @Override
    public void notifyDownloadFinished() {
        Log.d(TAG, "notifyDownloadFinished called");
        //We unbind so that the service may stop
        unbindService(connection);
        bound = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkResourcesAvailability();
            }
        });
    }

    //set language method called when language is selected at first launch
    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
    }

}