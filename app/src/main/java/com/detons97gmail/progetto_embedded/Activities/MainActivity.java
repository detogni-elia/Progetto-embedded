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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.detons97gmail.progetto_embedded.Services.FakeDownloadIntentService;
import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;
import com.detons97gmail.progetto_embedded.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FakeDownloadIntentService.DownloadCallbacks{
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

    private boolean bound;
    private FakeDownloadIntentService mService;

    //permissions codes
    private static final int REQUEST_CODE=100;
    private static final int INTERNET_FOR_DOWNLOAD = 200;


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

    //Reimposto i riferimenti agli oggetti della UI
    @Override
    public void onResume() {
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

        //Check whether app's resources are available or not, handling accordingly
        checkResourcesAvailability();
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
                }
                else
                {
                    Toast.makeText(this, R.string.permissions_not_granted,Toast.LENGTH_SHORT).show();
                }
                break;
            case INTERNET_FOR_DOWNLOAD:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Internet permission granted for download");
                    Intent startIntent = new Intent(MainActivity.this, FakeDownloadIntentService.class);
                    startIntent.putExtra(Values.EXTRA_COUNTRY, "India");
                    startIntent.putExtra(Values.EXTRA_LANGUAGE, "en");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(startIntent);
                    else
                        startService(startIntent);

                    mService.setCallback(MainActivity.this);
                }
                else{
                    Utilities.showToast(this, "Please allow internet access to the app to download resources", Toast.LENGTH_SHORT);
                }
        }
    }

    /**
     * Checks the device's storage for the required resources. If those are not present asks the user to download them.
     */
    private void checkResourcesAvailability(){
        //localizedCountries will contain translated country name to populate the spinner
        String[] localizedCountries;
        //Check app's files to get downloaded resources
        countriesFolders = Utilities.getDownloadedCountries(this);
        //If no resources available, bind to FakeDownloadService to get updates about downloads
        if(countriesFolders == null || countriesFolders.length == 0) {
            //Reset adapter for spinner
            mSpinnerCountries.setAdapter(null);
            Log.d(TAG, "No resources available");
            Intent startIntent = new Intent(MainActivity.this, FakeDownloadIntentService.class);
            //Bind to FakeDownloadIntentService to listen to updates for the downloads
            bindService(startIntent, connection, Context.BIND_AUTO_CREATE);
            //Disable navigation in the app until resources are available
            setUiButtonsEnabled(false);
        }
        else {
            //Translate country names
            localizedCountries = Utilities.getLocalizedCountries(this, countriesFolders);
            //If at least one country's resources are available

            // Array adapter to set data in Spinner Widget
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, localizedCountries);
            // Setting the array adapter containing country list to the spinner widget
            mSpinnerCountries.setAdapter(adapter);
            setUiButtonsEnabled(true);
        }
    }

    //ServiceConnection object to use for binding with FakeDownloadIntentService
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FakeDownloadIntentService.ServiceBinder binder = (FakeDownloadIntentService.ServiceBinder) service;
            mService = binder.getService();
            Log.d(TAG, "Service bound");
            bound = true;
            //If service is running we do nothing, we don't want to manage multiple downloads and we simply listen for updates from the service
            if(mService.isRunning()) {
                Log.v(TAG, "Service is already running");
                mService.setCallback(MainActivity.this);
            }

            //If service is not running, we ask the user to download resources
            else {
                permissionDialog.setContentView(R.layout.custom_dialog_box);
                //TextView permission_textView = permissionDialog.findViewById(R.id.permission_textView);
                //ImageView permission_ImageView = permissionDialog.findViewById(R.id.permission_ImageView);
                Button ok_button = permissionDialog.findViewById(R.id.ok_button);

                //show dialog box
                Window window = permissionDialog.getWindow();
                if(window != null)
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                permissionDialog.show();

                //Listener for the dialog's OK button
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownloadService("India", "en");
                        permissionDialog.dismiss();
                    }
                });
            }
        }

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
}

