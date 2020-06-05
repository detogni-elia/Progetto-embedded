package com.detons97gmail.progetto_embedded.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import com.detons97gmail.progetto_embedded.Database.Dao.ContactsDao;
import com.detons97gmail.progetto_embedded.Database.Dao.CreaturesDao;
import com.detons97gmail.progetto_embedded.Database.Dao.EffectsDao;
import com.detons97gmail.progetto_embedded.Database.Dao.SymptomsDao;
import com.detons97gmail.progetto_embedded.Database.Entity.Creatures;
import com.detons97gmail.progetto_embedded.Database.Entity.Effects;
import com.detons97gmail.progetto_embedded.Database.Entity.Symptoms;
import com.detons97gmail.progetto_embedded.Database.Entity.Contacts;

import java.util.List;

@Database(entities = {Creatures.class, Contacts.class, Symptoms.class, Effects.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract CreaturesDao creaturesDao();
    public abstract ContactsDao contactsDao();
    public abstract SymptomsDao symptomsDao();
    public abstract EffectsDao effectsDao();

    private static AppDatabase instance;
    //Bisogna rendere relativo il file sorgente del database
    public static synchronized AppDatabase getInstance(Context context)
    {
        if(instance == null)
            instance = Room.databaseBuilder(context, AppDatabase.class, "DatProva.db").createFromAsset("PD.db").build();
        return instance;
    }
}

