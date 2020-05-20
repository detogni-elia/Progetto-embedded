package com.detons97gmail.progetto_embedded.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;
import com.detons97gmail.progetto_embedded.R;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity {
    //Contains the folders names of the resources stored
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
        //localizedCountries will contain translated country name to populate the spinner
        String[] localizedCountries;
        //countriesFoldesrNames will contain the actual folder name in order to pass the intent to the SpeciesListActivity
        //Get supported localizedCountries by parsing the folders in the app FilesDir

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
                //countriesNames contains the english name of all supported countries
                //The resFolders will always use the english names of the countries
                String[] countriesNames = Values.getCountriesDefaultNames();
                countriesFoldersNames = Utilities.getDownloadedCountries(this);
                localizedCountries = Utilities.getLocalizedCountries(this, countriesFoldersNames);
                /*
                //Get folder name and translate country name
                for(int i = 0; i < resFolders.length; i++) {
                    String[] folderPath = resFolders[i].getAbsolutePath().split("/");
                    String folderName = folderPath[folderPath.length - 1];
                    for(int j = 0; j < countriesNames.length; j++){
                        if(folderName.equals(countriesNames[j])) {
                            localizedCountries[i] = getString(Values.COUNTRIES_IDS[j]);
                            countriesFoldersNames[i] = folderName;
                            break;
                        }
                    }
                }

                 */
            }
            //TODO: IF RESOURCES NOT PRESENT, ASK TO DOWNLOAD
            //If resource folders not present, display default values
            else{
                localizedCountries = new String[] {
                        getString(R.string.it),
                        getString(R.string.in),
                        getString(R.string.cn),
                };
            }
        }
        //TODO: IF RESOURCES NOT PRESENT, ASK TO DOWNLOAD
        else{
            localizedCountries = new String[] {
                    getString(R.string.it),
                    getString(R.string.in),
                    getString(R.string.cn),
            };
        }
        // Array adapter to set data in Spinner Widget
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, localizedCountries);
        // Setting the array adapter containing country list to the spinner widget
        mSpinnerCountries.setAdapter(adapter);

    }
        public void onClickCategory(View v){
        String country = countriesFoldersNames[mSpinnerCountries.getSelectedItemPosition()];
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
}

