package com.detons97gmail.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.detons97gmail.progetto_embedded.Database.Entity.Creatures;

import java.util.List;

@Dao
public interface CreaturesDao
{
    @Insert
    void insert(Creatures creatures);
    @Delete
    void delete(Creatures creature);
    @Query("SELECT * FROM Creatures")
    //List<Creatures> getAll();
    LiveData<List<Creatures>> getAll();
    @Query("SELECT * FROM Creatures WHERE latin_name = :creature")
    Creatures getCreature(String creature);
    //@Query("SELECT common_name, image FROM Creatures WHERE category = :cat")
    //List<Creatures> getFromCategory(String cat);
    @Query("SELECT * FROM Creatures WHERE common_name = :name")
    Creatures getCreatureFromName(String name);
}
