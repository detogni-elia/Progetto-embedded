package com.rem.progetto_embedded.Database;


import android.content.Context;
import android.util.ArrayMap;

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
import java.io.FileNotFoundException;

@Database(entities = {Creatures.class, Contacts.class, Symptoms.class, Effects.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract CreaturesDao creaturesDao();
    public abstract ContactsDao contactsDao();
    public abstract SymptomsDao symptomsDao();
    public abstract EffectsDao effectsDao();
    //ArrayMap saves the instances of all the databases opened during the applications's lifecycle in order to open them only once
    private static ArrayMap<String, AppDatabase> instances = new ArrayMap<>();
    public static synchronized AppDatabase getInstance(Context context, String country) throws FileNotFoundException {
        //Get country's database folder
        File resFolder = Utilities.getResourcesFolder(context);
        File dbPath = new File(resFolder, country + "/Database/database.db");
        //Check whether or not database instance is present in the ArrayMap, if not then put it
        if(!instances.containsKey(country + dbPath.lastModified())){
            if(!dbPath.exists())
                throw new FileNotFoundException("Could not find database file");
            //Save the database with name country + dbPath.lastModified() to load the database from storage each time the user downloads it
            //Since we admit only one database for each country, if the user changes language we want to avoid the one in cache.
            AppDatabase instance = Room.databaseBuilder(context, AppDatabase.class, country + dbPath.lastModified()).createFromFile(dbPath).build();
            instances.put(country + dbPath.lastModified(), instance);
        }
        return instances.get(country + dbPath.lastModified());
    }
}