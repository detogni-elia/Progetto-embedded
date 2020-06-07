package com.rem.progetto_embedded.Services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.rem.progetto_embedded.FakeDownload;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;

import java.io.IOException;

//TODO: COMMENT EVERYTHING
public class FakeDownloadIntentService extends IntentService {
    private final String TAG = getClass().getSimpleName();
    private final IBinder binder = new ServiceBinder();
    private boolean isRunning;
    private boolean bound;

    public interface DownloadCallbacks{
        void notifyDownloadFinished();
    }

    private DownloadCallbacks client;

    public FakeDownloadIntentService() {
        super("FakeDownloadIntentService");
    }

    public class ServiceBinder extends Binder{
        public FakeDownloadIntentService getService(){
            return FakeDownloadIntentService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.v(TAG, "Service bound");
        bound = true;
        return binder;
    }

    @Override
    public void onRebind(Intent intent){
        bound = true;
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.v(TAG, "Service unbound");
        bound = false;
        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Called onHandleIntent");
        if(isRunning)
            return;
        isRunning = true;
        String country = intent.getStringExtra(Values.EXTRA_COUNTRY);
        String language = intent.getStringExtra(Values.EXTRA_LANGUAGE);
        String imageQuality = intent.getStringExtra(Values.EXTRA_IMAGE_QUALITY);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notificationId")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading")
                .setContentText("Downloading resources for: " + country)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        startForeground(1, builder.build());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }

        try {
            boolean hasSpace = FakeDownload.copyAssetsToStorage(getApplicationContext(), country, language, imageQuality);
            if(!hasSpace) {
                Utilities.showToast(getApplicationContext(), getString(R.string.not_enough_space), Toast.LENGTH_SHORT);
                Log.d(TAG, "Not enough space to complete the download.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not copy assets to storage: " + e.toString());
        }
        stopSelf();
        if(bound) {
            client.notifyDownloadFinished();
            Log.v(TAG, "Notified client of download finished");
        }
    }

    public void onDestroy(){
        Log.v(TAG, "Stopping");
        isRunning = false;
        stopForeground(true);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "channel";
            String description = "Downloading";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notificationId", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void setCallback(DownloadCallbacks callback){
        client = callback;
    }
}
