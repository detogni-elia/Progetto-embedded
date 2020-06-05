package com.detons97gmail.progetto_embedded.Database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "effects",
        primaryKeys = {"creature", "contact", "symptom"},
        foreignKeys = {
                @ForeignKey(entity = com.detons97gmail.progetto_embedded.Database.Entity.Symptoms.class,
                        parentColumns = "symptom_name",
                        childColumns = "symptom",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE),
                @ForeignKey(entity = com.detons97gmail.progetto_embedded.Database.Entity.Contacts.class,
                        parentColumns = "contact_name",
                        childColumns = "contact",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE),
                @ForeignKey(entity = com.detons97gmail.progetto_embedded.Database.Entity.Creatures.class,
                        parentColumns = "latin_name",
                        childColumns = "creature",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE)
        })
public class Effects
{
    @ColumnInfo(name="creature")
    @NonNull
    public String creature;
    @ColumnInfo(name="contact")
    @NonNull
    public String contact;
    @ColumnInfo(name="symptom")
    @NonNull
    public String symptom;

    public String toString()
    {
        return creature+" , "+contact+" , "+symptom;
    }
}
