package com.example.servicesac;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class BackgroundServices extends Service {


    MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();


        player = MediaPlayer.create(this, R.raw.femaleorgasmsound);

        player.setLooping(true);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
    }
    Handler handler=new Handler();

    Runnable run=new Runnable() {
        @Override
        public void run() {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
            handler.postDelayed(run, 2000);
        }
    };



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.postDelayed(run, 2000);

        Log.i("isStarted: ", "runn");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setChannelId("NOTIF")
                .setContentTitle("running")
                .setContentText("Just checking")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.createNotificationChannel(new NotificationChannel("NOTIF", "Notification", NotificationManager.IMPORTANCE_HIGH));
//        notificationManager.notify(111, builder.build());

        startForeground(111, builder.build());
        player.start();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
