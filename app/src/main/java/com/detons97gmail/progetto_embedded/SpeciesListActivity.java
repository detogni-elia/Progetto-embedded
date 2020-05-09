package com.detons97gmail.progetto_embedded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class SpeciesListActivity extends AppCompatActivity implements SpeciesListAdapter.onSpeciesSelectedListener {
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 42;

    private RecyclerView recyclerView;
    private SpeciesListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_list_layout);
        recyclerView = findViewById(R.id.species_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
        else
            setAdapter();
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results){
        switch (code){
            case REQUEST_READ_EXTERNAL_STORAGE:
                if(results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED)
                    setAdapter();
                else{
                    Utilities.showToast(this, "Please allow access to storage", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private void setAdapter(){
        File rootPath = getExternalFilesDir(null);
        File[] images = null;
        if(rootPath != null) {
            String imagesPath = rootPath.getPath() + "/images";
            File imagesFolder = new File(imagesPath);
            images = imagesFolder.listFiles();
            Log.d("TAG", "path vale " + imagesPath);
        }
        if(images == null)
            adapter = new SpeciesListAdapter(this, null, null, this);

        else{
            String[] speciesNames = new String[images.length];
            for(int i = 0; i < images.length; i++){
                speciesNames[i] = images[i].getName();
            }
            adapter = new SpeciesListAdapter(this, images, speciesNames, this);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSpeciesListItemClick(int position) {
        Utilities.showToast(this, "Click on element n. " + position, Toast.LENGTH_SHORT);
    }

    public void onClickImageView(View v) {
        Utilities.showToast(this, "Received click on image", Toast.LENGTH_SHORT);
    }
}
