package com.rem.progetto_embedded.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.R;

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
        //TODO: HO COMMENTATO QUESTA PARTE DI CODICE PERCHE' MI DAVA ERRORE E NON RIUSCIVO A TESTARE LE ALTRE ACTIVITIES
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
