package com.rem.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.rem.progetto_embedded.Database.Entity.Symptoms;

import java.util.List;

@Dao
public interface SymptomsDao
{
    @Query("SELECT * FROM Symptoms")
    LiveData<List<Symptoms>> getAll();
}
