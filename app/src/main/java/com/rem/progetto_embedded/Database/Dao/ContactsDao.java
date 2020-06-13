package com.rem.progetto_embedded.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.rem.progetto_embedded.Database.Entity.Contacts;

import java.util.List;

@Dao
public interface ContactsDao
{
    @Query("SELECT * FROM Contacts")
    LiveData<List<Contacts>> getAll();
}
