package com.rem.progetto_embedded;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.widget.Toast;

import com.rem.progetto_embedded.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Utilities {
    public static void showToast(Context context, String message, int duration){
        Toast.makeText(context, message, duration).show();
    }

    public static String[] getDownloadedCountries(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Values.PREFERENCES_NAME, Context.MODE_PRIVATE);
        File downloadPath = null;
        switch (sharedPreferences.getString(Values.DOWNLOAD_LOCATION, Values.LOCATION_INTERNAL)){
            case Values.LOCATION_INTERNAL:
                downloadPath = context.getExternalFilesDir(null);
                break;
            case Values.LOCATION_EXTERNAL:
                if(hasWritableSd(context))
                    downloadPath = context.getExternalFilesDirs(null)[1];
        }
        String[] downloadedCountries;
        if(downloadPath != null){
            //Get directories in app FilesDir
            File[] resFolders = downloadPath.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if(resFolders != null){
                if(resFolders.length == 0)
                    return null;
                //countriesNames contains the english name of all supported countries
                //The resFolders will always use the english names of the countries
                String[] countriesNames = Values.getCountriesDefaultNames();
                downloadedCountries = new String[resFolders.length];
                //Get folder name and translate country name
                for(int i = 0; i < resFolders.length; i++) {
                    String[] folderPath = resFolders[i].getAbsolutePath().split("/");
                    String folderName = folderPath[folderPath.length - 1];
                    for (String countryName : countriesNames) {
                        //If found folder with name matching one country, add it to the list
                        if (folderName.equals(countryName)) {
                            downloadedCountries[i] = folderName;
                            break;
                        }
                    }
                }
                return downloadedCountries;
            }
        }

        return null;
    }

    public static File getResourcesFolder(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Values.PREFERENCES_NAME, Context.MODE_PRIVATE);
        File downloadPath = null;
        File[] folders = context.getExternalFilesDirs(null);
        switch (sharedPreferences.getString(Values.DOWNLOAD_LOCATION, Values.NOT_YET_DECIDED)) {
            case Values.LOCATION_INTERNAL:
                downloadPath = folders[0];
                break;
            case Values.LOCATION_EXTERNAL:
                if (hasWritableSd(context))
                    downloadPath = folders[1];
                break;
            case Values.NOT_YET_DECIDED:
                downloadPath = requestNewResourcesFolder(context);
        }
        return downloadPath;
    }

    public static File requestNewResourcesFolder(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Values.PREFERENCES_NAME, Context.MODE_PRIVATE);
        File downloadPath = null;
        File[] folders = context.getExternalFilesDirs(null);
        if(hasWritableSd(context) && checkAvailableSpace(folders[1], 10)){
            downloadPath = folders[1];
            sharedPreferences.edit().putString(Values.DOWNLOAD_LOCATION, Values.LOCATION_EXTERNAL).apply();
        }
        else{
            if(checkAvailableSpace(folders[0], 10)){
                downloadPath = folders[0];
                sharedPreferences.edit().putString(Values.DOWNLOAD_LOCATION, Values.LOCATION_INTERNAL).apply();
            }
        }
        return downloadPath;
    }

    public static String[] getLocalizedCountries(Context context, String[] toLocalize){
        ArrayList<String> localized = new ArrayList<>();
        int[] countriesIds = Values.getCountriesIds();
        String[] countries = Values.getCountriesDefaultNames();
        for(String s: toLocalize) {
            for (int i = 0; i < countries.length; i++) {
                if (s.equals(countries[i]))
                    localized.add(context.getString(countriesIds[i]));
            }
        }
        /*
        if(countriesHashMap.isEmpty()){
            int[] supportedCountriesIds = Values.getCountriesIds();
            String[] supportedCountriesDefaultNames = Values.getCountriesDefaultNames();
            for(int i = 0; i < supportedCountriesDefaultNames.length; i++)
                countriesHashMap.put(supportedCountriesDefaultNames[i], supportedCountriesIds[i]);
        }
        ArrayList<String> localizedCountries = new ArrayList<>();
        for (String s : toLocalize) {
            if (countriesHashMap.containsKey(s)) {
                int countryId = countriesHashMap.get(s);
                String localized = context.getString(countryId);
                localizedCountries.add(localized);
            }
        }
        return localizedCountries.toArray(new String[]{});

         */
        return localized.toArray(new String[]{});

    }

    public static String[] getLocalizedSymptoms(Context context){
        int[] symptomsIds = Values.getSymptomsIds();
        String[] localizedSymptoms = new String[symptomsIds.length];
        for(int i = 0; i < localizedSymptoms.length; i++)
            localizedSymptoms[i] = context.getString(symptomsIds[i]);

        return localizedSymptoms;
    }

    public static String[] getLocalizedCategories(Context context){
        int[] speciesIds = Values.getSpeciesIds();
        String[] localizedSpecies = new String[speciesIds.length];
        for(int i = 0; i < localizedSpecies.length; i++)
            localizedSpecies[i] = context.getString(speciesIds[i]);

        return localizedSpecies;
    }

    public static String[] getLocalizedContacts(Context context){
        int[] contactsIds = Values.getContactsTypeIds();
        String[] localizedContactsTypes = new String[contactsIds.length];
        for(int i = 0; i < localizedContactsTypes.length; i++)
            localizedContactsTypes[i] = context.getString(contactsIds[i]);

        return localizedContactsTypes;
    }

    public static String[] getLocalizedSupportedLanguages(Context context){
        int[] languagesIds = Values.getLanguagesIds();
        String[] localizedLanguages = new String[languagesIds.length];
        for(int i = 0; i < languagesIds.length; i++)
            localizedLanguages[i] = context.getString(languagesIds[i]);
        return localizedLanguages;
    }

    public static String[] getLocalizedSupportedCountries(Context context){
        int[] countriesIds = Values.getCountriesIds();
        String[] localizedCountries = new String[countriesIds.length];
        for(int i = 0; i < countriesIds.length; i++)
            localizedCountries[i] = context.getString(countriesIds[i]);

        return localizedCountries;
    }

    public static String[] getSupportedImageQuality(Context context){
        int[] imageQualityIds = Values.getImageQualityIds();
        String[] supportedImageQuality = new String[imageQualityIds.length];
        for(int i = 0; i < imageQualityIds.length; i++)
            supportedImageQuality[i] = context.getString(imageQualityIds[i]);

        return supportedImageQuality;
    }

    public static void deleteCache(Context context){
        File baseDir = getResourcesFolder(context);
        for(File file: baseDir.listFiles())
            deleteRecursively(file);
    }

    private static void deleteRecursively(File file) {
        if(file.isDirectory()) {
            for (File content : file.listFiles())
                deleteRecursively(content);
        }

        file.delete();
    }

    public static boolean hasWritableSd(Context context){
        File[] paths = context.getExternalFilesDirs(null);
        if(paths.length > 1){
            String state = Environment.getExternalStorageState(paths[1]);
            return state.equals(Environment.MEDIA_MOUNTED);
        }
        return false;
    }

    public static boolean checkAvailableSpace(File path, int requiredMb){
        long spaceInMb = path.getUsableSpace() / (1024 * 1024);
        return spaceInMb >= requiredMb;
    }

    public static class AnimalDetails {
        private int imageRef;
        private LinkedList<String> attributeName;
        private LinkedList<String> attributeContent;

        public AnimalDetails() {
            //Riempito per un esempio, sarebbe da collegare ad un database
            attributeName=new LinkedList<>();
            attributeContent=new LinkedList<>();

            //Initialize imageRef
            imageRef= R.drawable.taz;

            //Initialize attributeNameList
            attributeName.addLast("Nome:");
            attributeName.addLast("Specie:");
            attributeName.addLast("Dieta:");
            attributeName.addLast("Descrizione:");

            //Initialize attributeContentList
            attributeContent.addLast("Taz");
            attributeContent.addLast("Diavolo della Tasmania");
            attributeContent.addLast("Carnivoro");
            attributeContent.addLast("Taz è generalmente raffigurato come un feroce (seppur ottuso) carnivoro, irascibile e poco paziente." +
                    " Anche se può essere molto subdolo, a volte è anche dolce. Il suo enorme appetito sembra non conoscere limiti, poiché mangia qualsiasi cosa sul suo cammino. " +
                    "È noto soprattutto per i suoi discorsi costituiti principalmente da grugniti e ringhi (nelle sue prime apparizioni, parla con una grammatica primitiva) e per la sua capacità di girare come un vortice e mordere quasi tutto." +
                    "Taz ha un punto debole: può essere calmato da quasi ogni musica." +
                    " Mentre si trova in questo stato calmo, può essere facilmente affrontato. " +
                    "L'unica musica nota per non pacificare Taz è la cornamusa, che trova insopportabile.");
        }

        public int getImageRef()
        {
            return imageRef;
        }
        public String getAttributeName(int position) {
            //position-1 perchè il primo elemento èp sempre un' immagine
            return attributeName.get(position-1);
        }

        public String getAttributeContent(int position) {
            //position-1 perchè il primo elemento è sempre un' immagine
            return attributeContent.get(position-1);
        }

        public int size() {
            //Size+1 perchè conto anche la foto
            return attributeName.size()+1;
        }
    }
}
