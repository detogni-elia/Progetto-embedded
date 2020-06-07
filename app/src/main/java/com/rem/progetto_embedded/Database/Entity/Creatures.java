package com.rem.progetto_embedded.Database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Entità database che rappresenta un animale, con i nomi, specie, descrizione e percorso immagine

@Entity
public class Creatures
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="latin_name")
    public String latinName;
    @ColumnInfo(name="common_name")
    @NonNull
    public String commonName;
    @ColumnInfo(name="specie_name")
    @NonNull
    public String specieName;
    @ColumnInfo(name="diet")
    public String diet;
    @ColumnInfo(name="description")
    @NonNull
    public String description;
    @ColumnInfo(name="category")
    @NonNull
    public String category;
    @ColumnInfo(name="image")
    public String image;

    public String getLatinName() {return latinName;}
    public String getCommonName() {return commonName;}
    public String getSpecieName() {return specieName;}
    public String getDiet() {return diet;}
    public String getDescription() {return description;}
    public String getCategory() {return category;}
    public String getImage() {return image;}

    public String toString()
    {
        return latinName+" , "+commonName+" , "+specieName+" , "+description+" , "+category;
    }
}
