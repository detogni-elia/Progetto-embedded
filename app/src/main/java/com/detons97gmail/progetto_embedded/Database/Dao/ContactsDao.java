package com.detons97gmail.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.detons97gmail.progetto_embedded.Database.Entity.Contacts;

import java.util.List;

@Dao
public interface ContactsDao
{
    @Insert
    void insert(Contacts contacts);
    @Delete
    void delete(Contacts contact);
    @Query("SELECT * FROM Contacts")
    //List<String> getAll();
    LiveData<List<Contacts>> getAll();
}
