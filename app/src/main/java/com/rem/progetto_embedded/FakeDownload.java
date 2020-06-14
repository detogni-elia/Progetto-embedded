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

    private static void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer=new byte[1024];
        int read;
        while((read=in.read(buffer)) != -1)
            out.write(buffer, 0, read);
    }

    //Suppress NullPointerException warnings
    @SuppressWarnings("ConstantConditions")
    public static boolean copyAssetsToStorage(Context context, String country, String language, String imageQuality) throws IOException {
        //File root;
        //if (Utilities.hasWritableSd(context) && Utilities.checkAvailableSpace(paths[1], 10)) {
        //    root = paths[1];
        //    sharedPreferences.edit().putString(Values.DOWNLOAD_LOCATION, Values.LOCATION_EXTERNAL).apply();
        //}
        File root = Utilities.getResourcesFolder(context);

        if(root == null || !Utilities.checkAvailableSpace(root, 10)){
            root = Utilities.requestNewResourcesFolder(context);
            if(root == null)
                return false;
        }

        //Get app's dedicated files directory (either in sd card or in emulated sd card)
        //File root = context.getExternalFilesDir(null);
        //Create the subfolder for the selected country, if it does not exist
        File countryFolder = new File(root, country);
        countryFolder.mkdirs();
        //Create the subfolder for the images
        File imagesFolder = new File(countryFolder, "Images");
        imagesFolder.mkdirs();
        //Create the subfolder for the databases and the subfolder for the selected language
        File dbFolder = new File(countryFolder, "Database");
        dbFolder.mkdirs();

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
            }
        }
        //Copy database
        src = assetManager.open(country + "/Databases/" + language + ".db");
        dest = new FileOutputStream(dbFolder + "/" + "database.db");

        copyFile(src, dest);
        src.close();
        dest.close();
        return true;
    }

    private static boolean hasEnoughSpace(Context context){
        long spaceInMb = context.getExternalFilesDir(null).getUsableSpace() / (1024 * 1024);
        Log.v(TAG, "Available space: " + spaceInMb + " Mb");
        return spaceInMb > 10;
    }
}
