package com.rem.progetto_embedded;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.Database.Entity.Contacts;
import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.Database.Entity.Symptoms;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * SpeciesViewModel queries the database and is used inside SpeciesListActivity
 */

public class SpeciesViewModel extends AndroidViewModel implements ComponentCallbacks2 {
    private LiveData<List<Creatures>> species;
    private LiveData<List<Symptoms>> symptoms;
    private LiveData<Contacts> contact;
    private AppDatabase db;

    public SpeciesViewModel(Application application) {
        super(application);
        species = null;
        symptoms = null;
        contact = null;
        db = null;
    }

    /**
     * Get all species for a particular country, category and eventually filter them by contact type and symptoms
     * @param country The selected country
     * @param category The selected category
     * @param symptoms The symptoms to filter by, can be null
     * @param contact The contact type to filter by, can be null
     * @return LiveData containing the List of Creatures objects corresponding to the query parameters, return an empty List if the database cannot be opened
     */
    public LiveData<List<Creatures>> getSpecies(String country, String category, List<String> symptoms, String contact) {
        if(species == null) {
            if(db == null) {
                try {
                    db = AppDatabase.getInstance(getApplication().getApplicationContext(), country);
                }
                //If database not found show error message and return empty List
                catch (FileNotFoundException e) {
                    Context c = getApplication().getApplicationContext();
                    Utilities.showToast(c, c.getString(R.string.error_database_not_found));
                    return new MutableLiveData<>();
                }
            }
            //Is there are no symptoms selected we simply return all species
            if (symptoms == null || symptoms.isEmpty()) {
                species = db.creaturesDao().getFromCategory(category);
            }
            //We filter by symptoms and contact
            else{
                species = db.effectsDao().getCreatures(contact, category, symptoms);
            }
        }
        return species;
    }

    /**
     * Get contact type of particular species
     * @param country The specie's country
     * @param latinName The specie's latin name
     * @return LiveData containing a single Contacts object
     */
    public LiveData<Contacts> getContact(String country, String latinName){
        if(contact == null){
            if(db == null) {
                //Same error checking as before
                try {
                    db = AppDatabase.getInstance(getApplication().getApplicationContext(), country);
                } catch (FileNotFoundException e) {
                    Context c = getApplication().getApplicationContext();
                    Utilities.showToast(c, c.getString(R.string.error_database_not_found));
                    return new MutableLiveData<>();
                }
            }
            contact = db.creaturesDao().getContactOfCreature(latinName);
        }
        return contact;
    }

    /**
     * Get symptoms of particular species
     * @param country The specie's country
     * @param latinName The specie's latin name
     * @return LiveData containing a List of Symptoms
     */
    public LiveData<List<Symptoms>> getSymptoms(String country, String latinName){
        if(symptoms == null){
            if(db == null) {
                try {
                    db = AppDatabase.getInstance(getApplication().getApplicationContext(), country);
                } catch (FileNotFoundException e) {
                    Context c = getApplication().getApplicationContext();
                    Utilities.showToast(c, c.getString(R.string.error_database_not_found));
                    return new MutableLiveData<>();
                }
            }
            symptoms = db.creaturesDao().getSymptomsOfCreature(latinName);
        }
        return symptoms;
    }

    /**
     * Clear memory when RAM is almost full
     * @param level The level of necessity to free memory
     */
    @Override
    public void onTrimMemory(int level) {
        if(level >= TRIM_MEMORY_RUNNING_CRITICAL){
            species = null;
            symptoms = null;
            contact = null;
            db = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration){}

    @Override
    public void onLowMemory() {}
}