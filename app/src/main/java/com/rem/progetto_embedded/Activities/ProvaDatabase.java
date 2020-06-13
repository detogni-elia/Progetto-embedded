package com.rem.progetto_embedded.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.Database.Entity.Effects;
import com.rem.progetto_embedded.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProvaDatabase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_database);
        ExecutorService executor= Executors.newSingleThreadExecutor();
        final TextView tw=findViewById(R.id.textView2);
        //Apro il database
        final AppDatabase database=AppDatabase.getInstance(this);
        Log.d("DATABASE", "Apertura ok");
        //Provo la query
        //TODO: HO COMMENTATO QUESTA PARTE DI CODICE PERCHE' MI DAVA ERRORE E NON RIUSCIVO A TESTARE LE ALTRE ACTIVITIES
        executor.execute(new Runnable() {
            @Override
            public void run() {
                /*
                String[] sym={"Vomiting", "Swelling"};
                List<Creatures> creatures =database.effectsDao().getCreatures("Bite", sym);
                Log.d("DATABASE", "Query ok");
                String s=creatures.get(0).toString();
                tw.setText(s);
                */


            }
        });
    }
}
