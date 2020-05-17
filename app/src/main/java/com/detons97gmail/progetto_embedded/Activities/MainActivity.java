package com.detons97gmail.progetto_embedded.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.detons97gmail.progetto_embedded.IntentsExtras;
import com.detons97gmail.progetto_embedded.R;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity {
    private String[] countriesFoldersNames;

    Spinner mSpinnerCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set support action bar, for now it does nothing
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCountries = findViewById(R.id.countries_spinner);
        //countries will contain translated country name to populate the spinner
        String[] countries;
        //countriesFoldesrNames will contain the actual folder name in order to pass the intent to the SpeciesListActivity
        //Get supported countries by parsing the folders in the app FilesDir

         //We should use getFilesDir() in the final version SEE EXPLANATION ON SpeciesListFragment
        File appRootPath = getExternalFilesDir(null);
        if(appRootPath != null) {
            //Get directories in app FilesDir
            File[] resFolders = appRootPath.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if(resFolders != null) {
                countries = new String[resFolders.length];
                countriesFoldersNames = new String[resFolders.length];
                //Get folder name and translate country name
                for(int i = 0; i < resFolders.length; i++) {
                    String[] folderPath = resFolders[i].getAbsolutePath().split("/");
                    String folderName = folderPath[folderPath.length - 1];
                    switch (folderName){
                        case "India":
                            countries[i] = getString(R.string.in);
                            break;
                        case "China":
                            countries[i] = getString(R.string.cn);
                            break;
                        case "Italy":
                            countries[i] = getString(R.string.it);
                        default:
                            countries[i] = getString(R.string.it);
                    }
                    countriesFoldersNames[i] = folderName;
                }
            }
            //If resource folders not present, display default values
            else{
                countries = new String[] {
                        getString(R.string.it),
                        getString(R.string.in),
                        getString(R.string.cn),
                };
            }
        }
        //If could not get app's FilesDir, display default values
        else{
            countries = new String[] {
                    getString(R.string.it),
                    getString(R.string.in),
                    getString(R.string.cn),
            };
        }
        // Array adapter to set data in Spinner Widget
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countries);
        // Setting the array adapter containing country list to the spinner widget
        mSpinnerCountries.setAdapter(adapter);

    }
        public void onClickCategory(View v){
        String country = countriesFoldersNames[mSpinnerCountries.getSelectedItemPosition()];
        String category;
        switch (v.getId()){
            case R.id.animals_button:
                category = "Animals";
                break;
            case R.id.insects_button:
                category = "Insects";
                break;
            case R.id.plants_button:
                category = "Plants";
                break;
            default:
                category = "Animals";
        }
        Intent startIntent = new Intent(this, SpeciesListActivity.class);
        startIntent.putExtra(IntentsExtras.EXTRA_COUNTRY, country);
        startIntent.putExtra(IntentsExtras.EXTRA_CATEGORY, category);
        startActivity(startIntent);
    }
}

