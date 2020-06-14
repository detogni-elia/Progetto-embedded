package com.rem.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.rem.progetto_embedded.Database.Entity.Creatures;

import java.util.List;

@Dao
public interface CreaturesDao
{
    @Query("SELECT * FROM Creatures")
    LiveData<List<Creatures>> getAll();
    @Query("SELECT * FROM Creatures")
    List<Creatures> getAllAsList();
    @Query("SELECT * FROM Creatures WHERE latin_name = :latinName")
    Creatures getCreature(String latinName);
    @Query("SELECT * FROM Creatures WHERE category = :cat")
    LiveData<List<Creatures>> getFromCategory(String cat);
    @Query("SELECT * FROM Creatures WHERE common_name = :commonName")
    Creatures getCreatureFromName(String commonName);
}
