package com.rem.progetto_embedded.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.rem.progetto_embedded.Fragments.ConnectionDialogFragment;
import com.rem.progetto_embedded.Fragments.FirstStartDialogFragment;
import com.rem.progetto_embedded.Fragments.ResourcesDownloadDialogFragment;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Services.FakeDownloadIntentService;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;

import java.util.Locale;

//TODO: COMMENT EVERYTHING
public class SettingsActivity extends AppCompatActivity implements ConnectionDialogFragment.ConnectionDialogDismissListener, FakeDownloadIntentService.DownloadCallbacks, FirstStartDialogFragment.FirstStartListener {
    private static final int REQUEST_CODE = 100;
    private String[] countries;
    private Spinner imageQualitySpinner;
    private Spinner downloadedCountriesSpinner;
    private FakeDownloadIntentService mService;
    private boolean bound;

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }

        updateSpinners();
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent bindIntent = new Intent(SettingsActivity.this, FakeDownloadIntentService.class);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(bound) {
            unbindService(connection);
            mService = null;
            bound = false;
            Log.v(TAG, "Service unbound");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickChangeImageQuality(View v){
        //We show a cautionary message to the user about downloading with a metered connection if that's the case
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnectionMetered = cm != null && cm.isActiveNetworkMetered();
        if(isConnectionMetered) {
            new ConnectionDialogFragment().show(getSupportFragmentManager(), "alert-no-dialog");
        }
        else
            onConnectionDialogDismiss();
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
            mService.setCallback(SettingsActivity.this);
            if(mService.isRunning()) {
                Log.i(TAG, "Service is already running");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "Service unbound");
            bound = false;
        }
    };

    public void onClickDeleteCache(View v){
        Utilities.deleteCache(getApplicationContext());
        //Spinner could be null if onCreate was called and no resources were found
        if(downloadedCountriesSpinner != null) {
            downloadedCountriesSpinner.setAdapter(null);
            imageQualitySpinner.setAdapter(null);
            findViewById(R.id.update_image_quality_button).setEnabled(false);
            Utilities.showToast(this, getString(R.string.resources_deleted_message));
        }
    }

    public void onClickDownloadResources(View v){
        //We show a cautionary message to the user about downloading with a metered connection if that's the case
        if(Utilities.isConnectionMetered(this)) {
            new ConnectionDialogFragment().show(getSupportFragmentManager(), "alert");
        }
        else
            new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");
    }

    public void onClickGrantPermissions(View v){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            new FirstStartDialogFragment().show(getSupportFragmentManager(), "start");
    }

    @Override
    public void onConnectionDialogDismiss() {
        String[] qualities = Values.getImageQualityNames();
        Intent startIntent = new Intent(this, FakeDownloadIntentService.class);
        startIntent.putExtra(Values.EXTRA_COUNTRY, countries[downloadedCountriesSpinner.getSelectedItemPosition()]);
        //Set english language if system language is not supported
        String locale = Locale.getDefault().getLanguage();
        String[] languages = Values.getLanguagesDefaultNames();
        boolean supported = false;
        for(String lang: languages){
            if(locale.equals(lang)) {
                supported = true;
                break;
            }
        }
        if(!supported)
            locale = "en";

        startIntent.putExtra(Values.EXTRA_LANGUAGE, locale);
        startIntent.putExtra(Values.EXTRA_IMAGE_QUALITY, qualities[imageQualitySpinner.getSelectedItemPosition()]);
        startIntent.putExtra(Values.EXTRA_SKIP_DATABASE, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(startIntent);
        else
            startService(startIntent);
    }

    @Override
    public void onNotifyDownloadFinished() {
        Log.d(TAG, "notifyDownloadFinished called");
        //We unbind so that the service may stop
        unbindService(connection);
        bound = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateSpinners();
                Log.d(TAG, "Spinners have been updated");
            }
        });
    }

    private void updateSpinners(){
        //Fill country spinner
        countries = Utilities.getDownloadedCountries(this);
        if(countries == null || countries.length == 0){
            findViewById(R.id.update_image_quality_button).setEnabled(false);
            return;
        }
        findViewById(R.id.update_image_quality_button).setEnabled(true);
        downloadedCountriesSpinner = findViewById(R.id.settings_spinner_country);
        String[] localizedCountries = Utilities.localizeCountries(this, countries);
        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, localizedCountries);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        downloadedCountriesSpinner.setAdapter(countriesAdapter);

        //Fill image quality spinner
        imageQualitySpinner = findViewById(R.id.settings_spinner_images_quality);
        String[] qualities = Utilities.getLocalizedImagesQualities(this);
        ArrayAdapter<String> qualitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, qualities);
        qualitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageQualitySpinner.setAdapter(qualitiesAdapter);
    }

    @Override
    public void onClosingFirstStartDialog() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Request permission and then proceed with normal behaviour checking resources
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
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
        }
    }
}
