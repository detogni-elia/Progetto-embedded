package com.detons97gmail.progetto_embedded;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    Button mBtAnimals;
    Button mBtInsects;
    Button mBtPlants;

    Spinner mSpinnerCountries;

    // An array containing list of Country Names
    String[] countries = new String[] {
            getString(R.string.it),
            getString(R.string.in),
            getString(R.string.cn),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtAnimals= findViewById(R.id.animals_button);
        mBtInsects=findViewById(R.id.insects_button);
        mBtPlants=findViewById(R.id.plants_button);

        mSpinnerCountries=findViewById(R.id.countries_spinner);

        // Array adapter to set data in Spinner Widget
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countries);

        // Setting the array adapter containing country list to the spinner widget
        mSpinnerCountries.setAdapter(adapter);

    }



}

