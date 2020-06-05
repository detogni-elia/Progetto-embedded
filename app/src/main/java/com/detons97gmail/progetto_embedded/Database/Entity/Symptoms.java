package com.detons97gmail.progetto_embedded.Database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Symptoms
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="symptom_name")
    public String symptom_name;
}
