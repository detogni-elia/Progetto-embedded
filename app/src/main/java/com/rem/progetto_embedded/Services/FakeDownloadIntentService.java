package com.rem.progetto_embedded.Services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.rem.progetto_embedded.FakeDownload;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;

import java.io.IOException;

/**
 * Class simulates a download of resources such as images and databases
 */
public class FakeDownloadIntentService extends IntentService {
    private final String TAG = getClass().getSimpleName();
    private final IBinder binder = new ServiceBinder();
    private boolean isRunning;
    private boolean bound;

    /**
     * Interface for notifying clients about the completion of a download
     */
    public interface DownloadCallbacks{
        void onNotifyDownloadFinished();
    }

    private DownloadCallbacks client;

    public FakeDownloadIntentService() {
        super("FakeDownloadIntentService");
    }

    /**
     * Binder to return on onBind method
     */
    public class ServiceBinder extends Binder{
        /**
         * @return This service
         */
        public FakeDownloadIntentService getService(){
            return FakeDownloadIntentService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        bound = true;
        Log.v(TAG, "Service bound");
        return binder;
    }

    @Override
    public void onRebind(Intent intent){
        bound = true;
        Log.v(TAG, "Service rebound");
    }

    @Override
    public boolean onUnbind(Intent intent){
        bound = false;
        client = null;
        Log.v(TAG, "Service unbound");
        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent called");
        isRunning = true;
        String country = intent.getStringExtra(Values.EXTRA_COUNTRY);
        String language = intent.getStringExtra(Values.EXTRA_LANGUAGE);
        String imageQuality = intent.getStringExtra(Values.EXTRA_IMAGE_QUALITY);
        boolean skipDatabase = intent.getBooleanExtra(Values.EXTRA_SKIP_DATABASE, false);
        createNotificationChannel();
        //Build download notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "FakeDownloadNotification")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading")
                .setContentText(getString(R.string.notification_download_message) + country)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        //Start the service and run in foreground
        startForeground(1, builder.build());
        try{Thread.sleep(5000);}
        catch (InterruptedException ignored){}

        try{
            boolean success = FakeDownload.copyAssetsToStorage(getApplicationContext(), country, language, imageQuality, skipDatabase);
            if(!success){
                Utilities.showToast(getApplicationContext(), getString(R.string.not_enough_space));
                Log.d(TAG, "Not enough space to complete the download.");
            }
        }
        catch (IOException e){
            Utilities.showToast(getApplicationContext(),  getString(R.string.unexpected_error));
            Log.e(TAG, "Could not copy assets to storage: " + e.toString());
        }

        //Notify bound client if there is one at the moment
        if(bound && client != null) {
            client.onNotifyDownloadFinished();
            Log.v(TAG, "Notified client of download finished");
        }

        isRunning = false;
        stopForeground(true);
    }

    public void onDestroy(){
        isRunning = false;
        client = null;
        Log.v(TAG, "Service destroyed");
        stopForeground(true);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "FakeDownloadNotificationChannel";
            String description = "Downloading";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("FakeDownloadNotification", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean isRunning(){
        return isRunning;
    }

    /**
     * Method can be called by client to set itself as listener for download status
     * @param callback The component implementing the DownloadCallbacks interface
     */
    public void setCallback(DownloadCallbacks callback){
        client = callback;
    }
}
