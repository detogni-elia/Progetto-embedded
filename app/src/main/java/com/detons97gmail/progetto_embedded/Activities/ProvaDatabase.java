package com.detons97gmail.progetto_embedded.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.widget.TextView;

import com.detons97gmail.progetto_embedded.Database.AppDatabase;
import com.detons97gmail.progetto_embedded.Database.Entity.Contacts;
import com.detons97gmail.progetto_embedded.Database.Entity.Creatures;
import com.detons97gmail.progetto_embedded.Database.Entity.Symptoms;
import com.detons97gmail.progetto_embedded.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProvaDatabase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_database);
        ExecutorService executor= Executors.newSingleThreadExecutor();
        //Apro il database
        final AppDatabase database=AppDatabase.getInstance(this);
        //Provo la query
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //List<Effects> effects =database.effectsDao().getAll();
                //List<Contacts> contacts= database.contactsDao().getAll();
                //String s=effects.get(0).toString();
                //String s = contacts.get(1).toString();
                //List<Symptoms> symptoms=database.symptomsDao().getAll();
                //String s=symptoms.get(1).toString();
                //creatures =database.creaturesDao().getAll();
                //String s=creatures.get(1).toString();
                //TextView tw=findViewById(R.id.textView2);
                //tw.setText(s);
                //List<String> symptoms=database.symptomsDao().getAll();
                //List<String> contacts=database.contactsDao().getAll();
                //List<String> creatures=database.effectsDao().getCreatures(contacts.get(0),symptoms);
                //TextView tw=findViewById(R.id.textView2);
                //tw.setText(creatures.get(2));
            }
        });
    }
}
