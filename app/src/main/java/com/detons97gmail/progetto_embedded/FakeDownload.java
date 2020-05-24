package com.detons97gmail.progetto_embedded;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FakeDownload {
    private static final String TAG = "FakeDownload";

    private static void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer=new byte[1024];
        int read;
        while((read=in.read(buffer)) != -1)
            out.write(buffer, 0, read);
    }

    public static void copyAsset(Context context, String fileName)
    {
        String path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/PEFiles";
        File dir=new File(path);
        if(!dir.exists())
            dir.mkdirs();

        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try
        {
            in= assetManager.open(fileName);
            File outFile = new File(path, fileName);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            Log.d("ASSETS_COPY", "Asset copiato");
        }
        catch (IOException e)
        {
            Log.e("ASSETS_COPY", "Errore nella copia dell' asset");
        }
        finally
        {
            if(in != null)
            {
                try{in.close();} catch (IOException e){Log.e("ASSETS_COPY", "Errore chiusura InputStream");}
            }
            if(out != null)
            {
                try{out.close();} catch (IOException e){Log.e("ASSETS_COPY", "Errore chiusura OutputStream");}
            }
        }

    }

    //https://howtodoinjava.com/java/io/how-to-copy-directories-in-java/
    //Suppress NullPointerException warnings
    @SuppressWarnings("ConstantConditions")
    public static void copyAssetsToStorage(Context context, String country, String locale) throws IOException {
        //Get app's dedicated files directory (either in sd card or in emulated sd card)
        File root = context.getExternalFilesDir(null);
        //Create the subfolder for the selected country, if it does not exist
        File countryFolder = new File(root, country);
        countryFolder.mkdirs();
        //Create the subfolder for the images
        File imagesFolder = new File(countryFolder, "Images");
        imagesFolder.mkdirs();
        //Create the subfolder for the databases and the subfolder for the selected locale (language)
        File dbFolder = new File(countryFolder, "Databases/" + locale);
        dbFolder.mkdirs();

        AssetManager assetManager = context.getAssets();
        //Get sub-folders for each category (Animals, Insects, Plants) inside the selected country's Images assets
        String[] categories = assetManager.list(country + "/Images");
        InputStream src;
        OutputStream dest;
        //Iterate
        for(String categoryFolder: categories){
            //Create correct sub-folder inside the imagesFolder directory
            //Final path will be /files/COUNTRY/Images/CATEGORY(Animals, Insects, Plants)/image_file.webp
            File subFolder = new File(imagesFolder, categoryFolder);
            subFolder.mkdirs();
            String[] assetImages = assetManager.list(country + "/Images/" + categoryFolder);
            for(String image: assetImages){
                src = assetManager.open(country + "/Images/" + categoryFolder + "/" + image);
                dest = new FileOutputStream(new File(subFolder, image));
                copyFile(src, dest);
                src.close();
                dest.close();
            }
        }
        //Copy database
        src = assetManager.open(country + "/Databases/" + locale + ".tar");
        dest = new FileOutputStream(dbFolder + "/" + "db.tar");

        copyFile(src, dest);
        src.close();
        dest.close();


        /*
        String dbRes = "Databases-" + locale;
        File root = context.getExternalFilesDir(null);
        String dest;
        if(root != null)
            dest = root.getAbsolutePath() + "/" + country;

        //Check if folder already exists. If it does,

        //String path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/PEFiles";
        File dir=new File(path);
        if(!dir.exists())
            dir.mkdirs();

        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try
        {
            in= assetManager.open(fileName);
            File outFile = new File(path, fileName);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            Log.d("ASSETS_COPY", "Asset copiato");
        }
        catch (IOException e)
        {
            Log.e("ASSETS_COPY", "Errore nella copia dell' asset");
        }
        finally
        {
            if(in != null)
            {
                try{in.close();} catch (IOException e){Log.e("ASSETS_COPY", "Errore chiusura InputStream");}
            }
            if(out != null)
            {
                try{out.close();} catch (IOException e){Log.e("ASSETS_COPY", "Errore chiusura OutputStream");}
            }
        }

         */

    }

}
