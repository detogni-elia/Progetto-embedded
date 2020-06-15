package com.rem.progetto_embedded;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.Database.Entity.Creatures;

import java.util.List;

/**
 * SpeciesViewModel queries the database and is used inside SpeciesListActivity
 */

public class SpeciesViewModel extends AndroidViewModel {
    private LiveData<List<Creatures>> data;

    public SpeciesViewModel(Application application) {
        super(application);
        data = null;
    }

    public LiveData<List<Creatures>> getSpecies(String country, String category, List<String> symptoms, String contact) {
        if(data == null) {
            AppDatabase db = AppDatabase.getInstance(getApplication().getApplicationContext(), country);
            if (symptoms == null || symptoms.isEmpty()) {
                data = db.creaturesDao().getFromCategory(category);
            }
            else{
                data = db.effectsDao().getCreatures(contact, category, symptoms);
            }
        }
        return data;
    }
}
