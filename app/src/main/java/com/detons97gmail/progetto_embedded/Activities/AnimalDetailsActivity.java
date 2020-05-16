package com.detons97gmail.progetto_embedded.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.detons97gmail.progetto_embedded.Utilities.AnimalDetails;
import com.detons97gmail.progetto_embedded.Adapters.AnimalDetailsAdapter;
import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Utilities;


public class AnimalDetailsActivity extends AppCompatActivity
{
    private AnimalDetails animalInfo;
    private RecyclerView animalDetailsRecyclerView;
    private AnimalDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_animal);
        //Set ActionBar
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.menu_details_title);

        animalInfo=new AnimalDetails();

        //Initialize RecycleView
        animalDetailsRecyclerView=findViewById(R.id.detailsRecyclerView);
        adapter = new AnimalDetailsAdapter(this, animalInfo);
        animalDetailsRecyclerView.setAdapter(adapter);
        animalDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.details_animal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.gps_button)
            Utilities.showToast(this,"Connettere alla mappa", Toast.LENGTH_SHORT);
        else
            Utilities.showToast(this,"Torno indietro", Toast.LENGTH_SHORT);
        return true;
    }
}
