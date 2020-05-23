package com.detons97gmail.progetto_embedded;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeDownload {

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

    //Riesco a copiare gli assets su cartelle esterne, ma non riesco a fare il viceversa

    /*public static void fakeDownload(Context context, String fileName)
    {
        String path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/PEFiles";
        //File extFile= new File(path);
        File inFile=new File(context.getFilesDir(), fileName);
        InputStream in = null;
        OutputStream out = null;
        File appSpecificExternalDir = new File(context.getExternalFilesDir(path), "filename");

        try
        {
            Log.d("FAKE_DOWNLOAD", "aperto nulla");
            out = new FileOutputStream(inFile);
            Log.d("FAKE_DOWNLOAD", "Aperto out");
            in = new FileInputStream(appSpecificExternalDir);
            Log.d("FAKE_DOWNLOAD", "Aperto in");
            copyFile(in,out);
            Log.d("FAKE_DOWNLOAD", "FIle copiato");
        }
        catch (IOException e)
        {
            Log.e("FAKE_DOWNLOAD", "Errore nella copia del file");
        }
        finally
        {
            if(in != null)
            {
                try{in.close();} catch (IOException e){Log.e("FAKE_DOWNLOAD", "Errore chiusura InputStream");}
            }
            if(out != null)
            {
                try{out.close();} catch (IOException e){Log.e("FAKE_DOWNLOAD", "Errore chiusura OutputStream");}
            }
        }

    }

    public static String mediaDirPath(Context context, String name)
    {
        // Get the pictures directory that's inside the app-specific directory on external storage.
        File file=new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), name);
        if(!file.mkdirs() || file == null)
            Log.d("MEDIA_DIR_PATH", "Directory non creata");
        return file.getAbsolutePath();
    }*/

}
