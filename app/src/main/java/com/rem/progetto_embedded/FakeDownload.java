package com.rem.progetto_embedded;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeDownload {
    private static final String TAG = FakeDownload.class.getSimpleName();

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer=new byte[1024];
        int read;
        while((read=in.read(buffer)) != -1)
            out.write(buffer, 0, read);
    }

    //Suppress NullPointerException warnings
    @SuppressWarnings("ConstantConditions")
    public static boolean copyAssetsToStorage(Context context, String country, String language, String imageQuality, boolean skipDatabase) throws IOException {
        Log.v(TAG, "Copying assets to storage");
        File root = Utilities.getResourcesFolder(context);

        if(root == null || !Utilities.checkAvailableSpace(root, 10)){
            Log.d(TAG, "Could not copy. Destination not found or not enough space available");
            Log.d(TAG, "Requesting new resources folder");
            root = Utilities.requestNewResourcesFolder(context);
            if(root == null) {
                Log.d(TAG, "Could not acquire resources folder");
                return false;
            }
        }

        //Get app's dedicated files directory (either in sd card or in emulated sd card)
        //Create the subfolder for the selected country, if it does not exist
        File countryFolder = new File(root, country);
        if(countryFolder.mkdirs())
            Log.v(TAG, "Created country folder");

        //Create the subfolder for the images
        File imagesFolder = new File(countryFolder, "Images");
        if(imagesFolder.mkdirs())
            Log.v(TAG, "Created Images folder");

        //Create the subfolder for the databases and the subfolder for the selected language
        File dbFolder = new File(countryFolder, "Database");
        if(dbFolder.mkdirs())
            Log.v(TAG, "Created Database folder");

        AssetManager assetManager = context.getAssets();
        //Get sub-folders for each category (Animals, Insects, Plants) inside the selected country's Images assets
        String[] categories = assetManager.list(country + "/Images/" + imageQuality);
        InputStream src;
        OutputStream dest;
        //Iterate
        for(String categoryFolder: categories){
            //Create correct sub-folder inside the imagesFolder directory
            //Final path will be /files/COUNTRY/Images/CATEGORY(Animals, Insects, Plants)/image_file.webp
            File subFolder = new File(imagesFolder, categoryFolder);
            subFolder.mkdirs();
            String[] assetImages = assetManager.list(country + "/Images/"+ imageQuality + "/" +  categoryFolder);
            for(String image: assetImages){
                src = assetManager.open(country + "/Images/" + imageQuality + "/" + categoryFolder + "/" + image);
                dest = new FileOutputStream(new File(subFolder, image));
                copyFile(src, dest);
                src.close();
                dest.close();
                Log.v(TAG, "Copied " + image + " to storage");
            }
        }
        if(!skipDatabase) {
            Log.v(TAG, "Copying database");
            //Copy database
            src = assetManager.open(country + "/Databases/" + language + ".db");
            dest = new FileOutputStream(dbFolder + "/" + "database.db");
            copyFile(src, dest);
            src.close();
            dest.close();
            Log.v(TAG, "Database copied");
        }
        return true;
    }
}
