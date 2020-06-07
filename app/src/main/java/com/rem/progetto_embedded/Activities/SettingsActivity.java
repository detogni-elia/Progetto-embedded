package com.rem.progetto_embedded.Activities;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rem.progetto_embedded.Fragments.ConnectionDialogFragment;
import com.rem.progetto_embedded.Fragments.ResourcesDownloadDialogFragment;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Utilities;


import java.util.Locale;

/**
 * SharedPreference keys:
 * - selectedLanguage
 * -selectedImageQuality
 * -updatePosition
 * -deleteCache
 **/


public class SettingsActivity extends AppCompatActivity{

    Locale myLocale;

    private Spinner mSpinnerLanguages;
    private Spinner mSpinnerImageQuality;

    Button updatePosition, deleteCache;

    //check variables to stop selectedItems to run immediately
    int checkLan=0;
    int checkIm=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.i("TAG", "onCreate: layout created");

        final SharedPreferences mPrefs = getSharedPreferences("com.detons97gmail.progetto_embedded",MODE_PRIVATE);

        //Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }

        updatePosition = findViewById(R.id.update_position_button);
        deleteCache = findViewById(R.id.cache_button);


        String[] supportedLanguages= Utilities.getLocalizedSupportedLanguages(getApplicationContext());
        String[] supportedImageQUality = Utilities.getSupportedImageQuality(getApplicationContext());

        mSpinnerLanguages = findViewById(R.id.settings_spinner_languages);
        mSpinnerImageQuality = findViewById(R.id.settings_spinner_images_quality);

        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, supportedLanguages);
        ArrayAdapter<String> imageQualityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, supportedImageQUality);

        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerLanguages.setAdapter(languagesAdapter);
        mSpinnerImageQuality.setAdapter(imageQualityAdapter);

        String defaultLanguage=mPrefs.getString("selectedLanguage","");
        String defaultImageQuality=mPrefs.getString("selectedImageQuality","");
        Log.i("TAG", "onCreate:default language "+defaultLanguage);
        Log.i("TAG", "onCreate: default quality "+defaultImageQuality);

        int spinnerPos=languagesAdapter.getPosition(defaultLanguage);
        mSpinnerLanguages.setSelection(spinnerPos);
        spinnerPos=imageQualityAdapter.getPosition(defaultImageQuality);
        mSpinnerImageQuality.setSelection(spinnerPos);

        mSpinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView myTextLanguage=(TextView) view;
                String selectedItem=myTextLanguage.getText().toString();
                if(++checkLan>1) {
                    Log.i("TAG", "onItemSelected: " + selectedItem);
                    if ((selectedItem).equalsIgnoreCase("italian")) {
                        setLocale("it");
                    } else
                        setLocale("en");
                }

                SharedPreferences.Editor editor=mPrefs.edit();
                editor.putString("selectedLanguage",selectedItem);
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerImageQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView myTextQuality = (TextView) view;
                String selectedItem = myTextQuality.getText().toString();
                if(++checkIm>1)
                {
                    Log.i("TAG", "onItemSelected: "+selectedItem);
                    if((selectedItem).equalsIgnoreCase("low"))
                        Toast.makeText(getApplicationContext(),"Selected low image quality",Toast.LENGTH_SHORT).show();
                    else if ((selectedItem).equalsIgnoreCase("medium"))
                        Toast.makeText(getApplicationContext(),"Selected medium image quality",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"Selected high image quality",Toast.LENGTH_SHORT).show();
                }


                SharedPreferences.Editor editor1 = mPrefs.edit();
                editor1.putString("selectedImageQuality",selectedItem);
                editor1.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updatePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mPrefs.edit();
                editor.putBoolean("updatePosition",true);
                editor.apply();
            }
        });

        /*
        deleteCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mPrefs.edit();
                editor.putBoolean("deleteCache",true);
                editor.apply();
            }
        });

         */

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
    }

    protected void onPause()
    {
        finish();
        super.onPause();
    }

    public void onClickDeleteCache(View v){
        Utilities.deleteCache(getApplicationContext());
        Utilities.showToast(this, getString(R.string.resources_deleted_message), Toast.LENGTH_SHORT);
    }

    public void onClickDownloadResources(View v){
        //We show a cautionary message to the user about downloading with a metered connection if that's the case
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnectionMetered = cm != null && cm.isActiveNetworkMetered();
        if(isConnectionMetered) {
            new ConnectionDialogFragment().show(getSupportFragmentManager(), "alert");
        }

        else
            new ResourcesDownloadDialogFragment().show(getSupportFragmentManager(), "download");
    }
}
