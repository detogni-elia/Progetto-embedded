package com.rem.progetto_embedded;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

/**
 * Class contains various methods used throughout the app to translate resources and to manage storage
 */

public class Utilities {
    private final static String TAG = "Utilities";
    private static String lastLanguage = "en";
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method scans app's resources folder (either in external sd card or on the internal memory) and returns the relative countries names
     * @param context The application context
     * @return Array containing the folder names of the countries available in the device (names are in english) or null if no resources are found
     */
    public static String[] getDownloadedCountries(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Values.PREFERENCES_NAME, Context.MODE_PRIVATE);
        File downloadPath = null;
        //We can save resources either in external sd or internal shared storage, we scan the last known download location
        switch (sharedPreferences.getString(Values.DOWNLOAD_LOCATION, Values.LOCATION_INTERNAL)){
            case Values.LOCATION_INTERNAL:
                downloadPath = context.getExternalFilesDir(null);
                break;
            case Values.LOCATION_EXTERNAL:
                if(hasWritableSd(context))
                    downloadPath = context.getExternalFilesDirs(null)[1];
                break;
            //If the user has yet to download a package, we return null. This branch is repetitive but added for clarity
            case Values.NOT_YET_DECIDED:
                downloadPath = null;
                break;
        }
        if(downloadPath != null){
            String[] downloadedCountries;
            //Get list of directories, each corresponding to a different country
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
                //All resFolders will always use the english names of the countries
                String[] countriesNames = Values.COUNTRIES_DEFAULT_NAMES;
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

    /**
     * Get position of app's resources, choosing between internal shared storage and external sd card if needed, prioritizing external storage.
     * @param context The application context
     * @return File relative to application's folder either in sd card or internal storage, choosing one of the two if it's the first time calling this method.
     * Returns null only if it's the first time calling this method and both filesystems are low on storage.
     */
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
                //If it's the first time choosing one of the two possible storage locations, make the choice
                downloadPath = requestNewResourcesFolder(context);
        }
        return downloadPath;
    }

    /**
     * Method selects a storage location between internal shared storage and external sd, prioritizing external sd.
     * @param context The application context
     * @return File relative to the app's folder in the selected storage, null if both are full.
     */
    static File requestNewResourcesFolder(Context context){
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

    /**
     * Check if device has sd storage and if we have write access
     * @param context The application context
     * @return true if sd is present and we can write on it, false otherwise
     */
    private static boolean hasWritableSd(Context context){
        File[] paths = context.getExternalFilesDirs(null);
        if(paths.length > 1){
            String state = Environment.getExternalStorageState(paths[1]);
            return state.equals(Environment.MEDIA_MOUNTED);
        }
        return false;
    }

    /**
     * Check if filesystem for the specified path has at least requiredMb of space available
     * @param path File inside the target filesystem
     * @param requiredMb Minimum number of Mb required
     * @return true if filesystem has at least requiredMb Mb of space, false otherwise
     */
    static boolean checkAvailableSpace(File path, int requiredMb){
        long spaceInMb = path.getUsableSpace() / (1024 * 1024);
        return spaceInMb >= requiredMb;
    }

    private static final ArrayMap<String, String> countriesTranslations = new ArrayMap<>();

    /**
     * Translate countries names from english to current device's language
     * @param context The application context
     * @param toLocalize Array containing the english names of the countries to localize
     * @return String array containing the localizations
     */
    public static String[] localizeCountries(Context context, String[] toLocalize){
        String language = Locale.getDefault().getLanguage();
        if(!language.equals(lastLanguage)){
            lastLanguage = language;
            resetArrayMaps();
        }
        if(countriesTranslations.isEmpty()){
            //Mapping english names and android ids allows to translate
            int[] countriesIds = Values.COUNTRIES_IDS;
            String[] countries = Values.COUNTRIES_DEFAULT_NAMES;
            for(int i = 0; i < countries.length; i++)
                countriesTranslations.put(countries[i], context.getString(countriesIds[i]));
        }
        String[] localized = new String[toLocalize.length];
        for(int i = 0; i < toLocalize.length; i++)
            localized[i] = countriesTranslations.get(toLocalize[i]);

        return localized;
    }

    private static final ArrayMap<String, String> symptomsTranslations = new ArrayMap<>();

    /**
     * Get all selected symptoms translated from english to the device's current language.
     * @param context The application context
     * @return String array containing all localized symptoms
     */
    public static String[] localizeSymptoms(Context context, String[] toLocalize){
        String language = Locale.getDefault().getLanguage();
        if(!language.equals(lastLanguage)) {
            lastLanguage = language;
            resetArrayMaps();
        }
        if(symptomsTranslations.isEmpty()){
            int[] symptomsIds = Values.SYMPTOMS_IDS;
            String[] symptoms = Values.SYMPTOMS_DEFAULT_NAMES;
            for(int i = 0; i < symptoms.length; i++)
                symptomsTranslations.put(symptoms[i], context.getString(symptomsIds[i]));
        }
        String[] localizedSymptoms;
        if(toLocalize == null) {
            localizedSymptoms = new String[symptomsTranslations.size()];
            String[] allSymptoms = Values.SYMPTOMS_DEFAULT_NAMES;
            for(int i = 0; i < localizedSymptoms.length; i++)
                localizedSymptoms[i] = symptomsTranslations.get(allSymptoms[i]);
        }
        else {
            localizedSymptoms = new String[toLocalize.length];
            for(int i = 0; i < toLocalize.length; i++)
                localizedSymptoms[i] = symptomsTranslations.get(toLocalize[i]);
        }
        return localizedSymptoms;
    }

    /**
     * Get species' categories localized for the system language of the device
     * @param context The application context
     * @return String array containing all localized categories
     */
    public static String[] getLocalizedCategories(Context context){
        int[] speciesIds = Values.SPECIES_IDS;
        String[] localizedSpecies = new String[speciesIds.length];
        for(int i = 0; i < localizedSpecies.length; i++)
            localizedSpecies[i] = context.getString(speciesIds[i]);

        return localizedSpecies;
    }

    private static ArrayMap<String, String> contactsTranslations = new ArrayMap<>();

    /**
     * Localize contact type from english to device's system language
     * @param context The application Context
     * @param toLocalize The String contact to localize
     * @return The localized contact
     */
    public static String localizeContact(Context context, String toLocalize){
        String language = Locale.getDefault().getLanguage();
        if(!language.equals(lastLanguage)) {
            lastLanguage = language;
            resetArrayMaps();
        }
        if(contactsTranslations.isEmpty()) {
            int[] contactsIds = Values.CONTACTS_IDS;
            String[] contacts = Values.CONTACTS_DEFAULT_NAMES;
            for (int i = 0; i < contacts.length; i++)
                contactsTranslations.put(contacts[i], context.getString(contactsIds[i]));
        }
        return contactsTranslations.get(toLocalize);
    }

    /**
     * Get localized contact types
     * @param context The application context
     * @return The String array containing the localized contact types
     */
    public static String[] getLocalizedContacts(Context context){
        String language = Locale.getDefault().getLanguage();
        if(!language.equals(lastLanguage)) {
            lastLanguage = language;
            resetArrayMaps();
        }
        String[] contacts = Values.CONTACTS_DEFAULT_NAMES;
        if(contactsTranslations.isEmpty()) {
            int[] contactsIds = Values.CONTACTS_IDS;
            for (int i = 0; i < contacts.length; i++)
                //localizedContactsTypes[i] = context.getString(contactsIds[i]);
                contactsTranslations.put(contacts[i], context.getString(contactsIds[i]));
        }
        String[] toReturn  = new String[contacts.length];
        for(int i = 0; i < toReturn.length; i++)
            toReturn[i] = contactsTranslations.get(contacts[i]);

        return toReturn;
    }

    /**
     * Get all of the application's supported languages
     * @param context the application context
     * @return Array containing all the supported languages
     */
    public static String[] getLocalizedSupportedLanguages(Context context){
        int[] languagesIds = Values.LANGUAGES_IDS;
        String[] localizedLanguages = new String[languagesIds.length];
        for(int i = 0; i < languagesIds.length; i++)
            localizedLanguages[i] = context.getString(languagesIds[i]);
        return localizedLanguages;
    }

    public static String[] getLocalizedSupportedCountries(Context context){
        int[] countriesIds = Values.COUNTRIES_IDS;
        String[] localizedCountries = new String[countriesIds.length];
        for(int i = 0; i < countriesIds.length; i++)
            localizedCountries[i] = context.getString(countriesIds[i]);

        return localizedCountries;
    }

    public static String[] getLocalizedImagesQualities(Context context){
        int[] imageQualityIds = Values.IMAGE_QUALITY_IDS;
        String[] supportedImageQuality = new String[imageQualityIds.length];
        for(int i = 0; i < imageQualityIds.length; i++)
            supportedImageQuality[i] = context.getString(imageQualityIds[i]);

        return supportedImageQuality;
    }

    /**
     * Delete all downloaded resources
     * @param context The application context
     */
    public static void deleteCache(Context context){
        File baseDir = getResourcesFolder(context);
        //basDir could be null if storage has not yet been selected (extreme cases where memory is too low)
        if(baseDir != null) {
            File[] content = baseDir.listFiles();
            if(content != null)
                for (File file : content)
                    deleteRecursively(file);
        }
    }

    /**
     * Delete files recursively
     * @param file The file or folder to delete
     */
    private static void deleteRecursively(File file) {
        if(file.isDirectory()) {
            File[] children = file.listFiles();
            if(children != null)
                for (File child : children)
                    deleteRecursively(child);
        }

        if(file.delete())
            Log.v(TAG, "Eliminated: " + file.toString());
    }

    /**
     * Method checks if connection is metered. Does not check if device is actually connected to internet.
     * @param context The application context
     * @return true if connection is metered, false if connection is not metered or if mobile data is not active, even if the device is not actually connected to internet.
     */
    public static boolean isConnectionMetered(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager != null && manager.isActiveNetworkMetered();
    }

    private static void resetArrayMaps(){
        contactsTranslations.clear();
        symptomsTranslations.clear();
        countriesTranslations.clear();
    }
}
