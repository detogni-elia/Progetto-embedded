package com.detons97gmail.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.detons97gmail.progetto_embedded.Database.Entity.Contacts;
import com.detons97gmail.progetto_embedded.Database.Entity.Creatures;
import com.detons97gmail.progetto_embedded.Database.Entity.Effects;
import com.detons97gmail.progetto_embedded.Database.Entity.Symptoms;

import java.util.List;

@Dao
public interface EffectsDao
{
    @Insert
    void insert(Effects effects);
    @Delete
    void delete(Effects effect);
    @Query("SELECT * FROM Effects")
    List<Effects> getAll();
    @Query("SELECT creature FROM Effects WHERE contact = :c AND symptom IN (:s)")
    List<String> getCreatures(String c, List<String> s);
}