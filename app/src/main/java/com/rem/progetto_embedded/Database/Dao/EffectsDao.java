package com.rem.progetto_embedded.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.rem.progetto_embedded.Database.Entity.Effects;

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