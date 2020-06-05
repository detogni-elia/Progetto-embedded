package com.detons97gmail.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.detons97gmail.progetto_embedded.Database.Entity.Symptoms;

import java.util.List;

@Dao
public interface SymptomsDao
{
    @Insert
    void insert(Symptoms symptoms);
    @Delete
    void delete(Symptoms symptom);
    @Query("SELECT * FROM Symptoms")
    List<String> getAll();
    //LiveData è una struttura dati molto comoda in combo con le recycler view
    //LiveData<List<Symptoms>> getAll();
}
