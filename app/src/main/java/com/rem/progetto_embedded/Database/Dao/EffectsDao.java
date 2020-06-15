package com.rem.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.Database.Entity.Effects;

import java.util.List;

@Dao
public interface EffectsDao
{
    @Query("SELECT * FROM Effects")
    LiveData<List<Effects>> getAll();
    //List<Effects> getAll();
    @Query("SELECT DISTINCT latin_name, common_name, specie_name, category, diet, description, image, latitude, longitude FROM Effects JOIN Creatures ON Effects.creature=Creatures.latin_name WHERE Effects.contact = :contact AND Creatures.category = :category AND Effects.symptom IN (:s)")
    LiveData<List<Creatures>> getCreatures(String contact, String category, List<String> s);
}