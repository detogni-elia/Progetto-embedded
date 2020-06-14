package com.rem.progetto_embedded.Database;


import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import com.rem.progetto_embedded.Database.Dao.ContactsDao;
import com.rem.progetto_embedded.Database.Dao.CreaturesDao;
import com.rem.progetto_embedded.Database.Dao.EffectsDao;
import com.rem.progetto_embedded.Database.Dao.SymptomsDao;
import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.Database.Entity.Effects;
import com.rem.progetto_embedded.Database.Entity.Symptoms;
import com.rem.progetto_embedded.Database.Entity.Contacts;
import com.rem.progetto_embedded.Utilities;

import java.io.File;

@Database(entities = {Creatures.class, Contacts.class, Symptoms.class, Effects.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract CreaturesDao creaturesDao();
    public abstract ContactsDao contactsDao();
    public abstract SymptomsDao symptomsDao();
    public abstract EffectsDao effectsDao();
    private static ArrayMap<String, AppDatabase> instances=new ArrayMap<>();

    public static synchronized AppDatabase getInstance(Context context, String country)
    {
        //Controllo se la chiave è presente nel dizionario, se non c'è apro il database e salvo l'istanza
        if(!instances.containsKey(country)) {
            AppDatabase instance;
            File resFolder = Utilities.getResourcesFolder(context);
            File dbPath = new File(resFolder, country + "/Database/database.db/");
            instance = Room.databaseBuilder(context, AppDatabase.class, country).createFromFile(dbPath).build();
            instances.put(country, instance);
        }
        return instances.get(country);
    }
}

