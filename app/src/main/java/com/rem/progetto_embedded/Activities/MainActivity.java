package com.rem.progetto_embedded.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rem.progetto_embedded.Fragments.FirstStartDialogFragment;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

import com.rem.progetto_embedded.Fragments.ConnectionDialogFragment;
import com.rem.progetto_embedded.Fragments.ResourcesDownloadDialogFragment;
import com.rem.progetto_embedded.Services.FakeDownloadIntentService;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;
import com.rem.progetto_embedded.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FakeDownloadIntentService.DownloadCallbacks, FirstStartDialogFragment.FirstStartListener{
    private static final String TAG = "MainActivity";

    //UI Widgets
    private Spinner mSpinnerCountries;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private LinearLayout animals_button;
    private LinearLayout insects_button;
    private LinearLayout plants_button;
    private FakeDownloadIntentService mService;

    private boolean bound;
    private String[] countriesFolders;

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
        drawer = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(actionBarDrawerToggle);
        //display burger icon
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPause(){
        if(bound) {
            unbindService(connection);
            bound = false;
            Log.v(TAG, "Service unbound");
            mService = null;
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
        DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
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

        SharedPreferences sharedPreferences = getSharedPreferences(Values.PREFERENCES_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(Values.FIRST_RUN, true)) {
            Log.i(TAG, "onResume: first run started");
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            boolean alreadyShowing = false;
            for (Fragment fragment : fragments) {
                String fragmentName = fragment.getClass().getSimpleName();
                if (fragmentName.equals(FirstStartDialogFragment.class.getSimpleName()))
                    alreadyShowing = true;
            }
            if(!alreadyShowing)
                new FirstStartDialogFragment().show(getSupportFragmentManager(), "first_run");

            //set update position to true on first run
            sharedPreferences.edit().putBoolean(Values.UPDATE_POSITION,true).apply();
        }
        else
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
            mService = null;
            Log.d(TAG, "Eliminato il service");
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
        if (requestCode == REQUEST_CODE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //Coarse location permission has been granted
                Log.i(TAG, "onRequestPermissionsResult: Permission granted");
            }
            else {
                Log.i(TAG, "onRequestPermissionsResult: Permission not granted");
                Toast.makeText(this, R.string.permissions_not_granted, Toast.LENGTH_SHORT).show();
            }

            //Whatever the result, proceed with normal behaviour since we don't need special permissions to work normally
            checkResourcesAvailability();
        }
    }

    /**
     * Checks the device's storage for the required resources. If those are not present asks the user to download them. If a download is in progress setup callback to get updates.
     */
    private void checkResourcesAvailability(){
        //Check app's files to get downloaded resources
        //Contains the folders names of the resources stored
        countriesFolders = Utilities.getDownloadedCountries(getApplicationContext());
        Intent startIntent = new Intent(MainActivity.this, FakeDownloadIntentService.class);
        //Bind to FakeDownloadIntentService to listen to updates for the downloads
        bindService(startIntent, connection, Context.BIND_AUTO_CREATE);
        if(countriesFolders == null) {
            //Reset adapter if resources were removed while the app was in background
            mSpinnerCountries.setAdapter(null);
            Log.d(TAG, "No resources available");
            //Disable navigation until resources are available
            setUiButtonsEnabled(false);
        }
        else
            updateSpinner();
    }

    /**
     * Update countries spinner with available resources
     */
    private void updateSpinner(){
        countriesFolders = Utilities.getDownloadedCountries(this);
        if(countriesFolders == null)
            return;


        //Translate names of available countries to display them
        String[] localizedCountries = Utilities.localizeCountries(this, countriesFolders);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, localizedCountries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCountries.setAdapter(adapter);
        //Enable navigation now that there are resources (we are certain of it since this method is called only after a download has completed successfully)
        setUiButtonsEnabled(true);
    }

    /**
     * Method handles clicks for the MainActivity's buttons
     * @param v The button clicked
     */
    public void onClickCategory(View v){
        //Translate selected country's name to query SQLite database
        String country = countriesFolders[mSpinnerCountries.getSelectedItemPosition()];
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
            mService = binder.getService();
            bound = true;
            Log.d(TAG, "Service bound");
            //Register as callback to get updates regarding downloads
            mService.setCallback(MainActivity.this);
            if(mService.isRunning() || countriesFolders != null) {
                Log.i(TAG, "Service is running or resources are already available");
            }

            //We ask the user to download resources, it means we should ask the user to download resources
            else {
                //We show a message only if there isn't one already on screen. We want to avoid overlap of identical DialogFragments in case of configuration changes
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                boolean alreadyShowing = false;
                for (Fragment fragment : fragments) {
                    String fragmentName = fragment.getClass().getSimpleName();
                    if (fragmentName.equals(ConnectionDialogFragment.class.getSimpleName()) || fragmentName.equals(ResourcesDownloadDialogFragment.class.getSimpleName()))
                        alreadyShowing = true;
                }
                if (!alreadyShowing) {
                    //We show a cautionary message to the user about downloading with a metered connection if that's the case
                    if(Utilities.isConnectionMetered(getApplicationContext())) {
                        new ConnectionDialogFragment().show(getSupportFragmentManager(), "alert");
                    }

                    else
                        new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "Service unbound");
            bound = false;
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
        Log.d(TAG, "UI buttons are active: " + enabled);
    }

    /**
     * Method called by FakeDownloadIntentService to notify activity when a download completes
     */
    @Override
    public void onNotifyDownloadFinished() {
        Log.d(TAG, "notifyDownloadFinished called");
        //We unbind so that the service may stop
        unbindService(connection);
        bound = false;
        Log.d(TAG, "Service unbound");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateSpinner();
                Log.d(TAG, "Countries spinner has been updated");
            }
        });
    }

    /**
     * Callback method invoked by FirstStartDialogFragment after the user closes it
     */
    @Override
    public void onClosingFirstStartDialog() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Request permission and then proceed with normal behaviour checking resources
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
        //We proceed with the normal behaviour
        else
            checkResourcesAvailability();
    }
}