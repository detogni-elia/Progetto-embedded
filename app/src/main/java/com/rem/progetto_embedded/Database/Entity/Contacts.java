package com.rem.progetto_embedded.Database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contacts
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="contact_name")
    public String contact_name;
}
