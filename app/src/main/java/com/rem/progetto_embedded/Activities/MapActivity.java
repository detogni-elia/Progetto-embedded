package com.rem.progetto_embedded.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Values;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Double deviceLatitude;
    Double deviceLongitude;

    Double animalLan;
    Double animalLon;

    String animalName;

    //set min and max for random generation of area
    final int MIN = 100000;
    final int MAX = 120000;

    LatLngBounds mMapBoundary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Values.PREFERENCES_NAME,MODE_PRIVATE);

        deviceLatitude=Double.parseDouble(sharedPreferences.getString(Values.LATITUDE,"0"));
        deviceLongitude=Double.parseDouble(sharedPreferences.getString(Values.LONGITUDE,"0"));

        Intent intent = getIntent();

        animalLan = intent.getDoubleExtra(Values.EXTRA_LATITUDE,0);
        animalLon = intent.getDoubleExtra(Values.EXTRA_LONGITUDE,0);
        animalName = intent.getStringExtra(Values.EXTRA_NAME);

        //Set toolbar and show "back" button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.species_location);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(deviceLatitude!=0 && deviceLongitude!=0){

            LatLng device = new LatLng(deviceLatitude,deviceLongitude);

            String my_pos = getResources().getString(R.string.your_position);
            //Add a marker for device position
            mMap.addMarker(new MarkerOptions().position(device).title(my_pos));


        }


        // Add a marker for animal position
        LatLng animal = new LatLng(animalLan,animalLon);
        mMap.addMarker(new MarkerOptions().position(animal).title(animalName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        Circle circle = mMap.addCircle(new CircleOptions().center(animal).radius(randomRadius()).strokeColor(R.color.colorPrimaryDark));
        setView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private Double randomRadius(){
        return MIN + (Math.random() * ((MAX - MIN) + 1));
    }

    private void setView()
    {
        double bottomB=animalLan- 3.5;
        double leftB=animalLon- 3.5;
        double upB=animalLan+ 3.5;
        double rightB=animalLon+ 3.5;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomB,leftB),
                new LatLng(upB,rightB)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,0));
    }
}
