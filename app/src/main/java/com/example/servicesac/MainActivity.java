package com.example.servicesac;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    Uri addedUri;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "ksndf", Toast.LENGTH_LONG).show();
        Intent broadcast = new Intent(this, BootComplete.class);
        sendBroadcast(broadcast);
    }


    private Button bt, stop;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.button);
        stop = findViewById(R.id.button2);

        if (!checkSystemWritePermission()) ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else Toast.makeText(this, "ksnadf", Toast.LENGTH_LONG).show();


        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission();
                } else Toast.makeText(MainActivity.this, "ksnadf", Toast.LENGTH_LONG).show();


                setRingtone();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startService(new Intent(MainActivity.this, BackgroundServices.class));

                    }
                }).start();

                Log.i("buttonstart", "dsfj");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, BackgroundServices.class));
            }
        });
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }


    protected void checkPermission() {
        Activity mActivity = MainActivity.this;


        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // When request is cancelled, the results array are empty
                if ((grantResults.length > 0) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions are granted
                    Toast.makeText(MainActivity.this, "Permissions granted.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permissions are denied
                    Toast.makeText(MainActivity.this, "Permissions denied.-", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public void setRingtone() {
        String ringtoneuri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/media/ringtone";
        File file1 = new File(ringtoneuri);
        file1.mkdirs();
        File newSoundFile = new File(ringtoneuri, "myringtone.mp3");


        Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/raw/talk_dirty");


        ContentResolver mCr = this.getContentResolver();
        AssetFileDescriptor soundFile;
        try {
            soundFile = mCr.openAssetFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            soundFile = null;
        }

        try {
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(newSoundFile);
            int i = fis.read(readData);

            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }

            fos.close();
        } catch (IOException io) {
        }


        ContentValues values = new ContentValues();
        values.put(MediaColumns.DATA, newSoundFile.getAbsolutePath());
        values.put(MediaColumns.TITLE, "myringtone");
        values.put(MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaColumns.SIZE, newSoundFile.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(newSoundFile.getAbsolutePath());
        Uri newUri = mCr.insert(uri, values);
        try {
            RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,RingtoneManager.TYPE_ALARM,newUri);
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(this, "New Rigntone set", Toast.LENGTH_SHORT).show();
            Log.i("Ringtone Set","yes");

        } catch (Throwable t) {
            Log.i("sdkmf","sdfkm");
        }



    }


}

