package com.detons97gmail.progetto_embedded.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.detons97gmail.progetto_embedded.Adapters.SymptomsSearchAdapter;
import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Values;

import java.util.ArrayList;

//TODO: USE FRAGMENT TO RETAIN ADAPTER AND CLEAN CODE
public class SymptomsSearchActivity extends AppCompatActivity{
    private String[] symptoms;
    private ArrayList<SymptomsSearchAdapter.DataWrapper> data;
    private SymptomsSearchAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_search);
        int[] symptomsIds = Values.SYMPTOMS_IDS;
        symptoms = new String[symptomsIds.length];
        for(int i = 0; i < symptoms.length; i++)
            symptoms[i] = getString(symptomsIds[i]);

        data = SymptomsSearchAdapter.DataWrapper.getWrappedData(symptoms);
        adapter = new SymptomsSearchAdapter(data);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
