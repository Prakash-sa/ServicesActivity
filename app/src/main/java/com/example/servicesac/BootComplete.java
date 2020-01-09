package com.example.servicesac;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class BootComplete extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
                Log.i("BroadcastReceiver :","Called");
                // context.startService(new Intent(context, BackgroundServices.class));
        }

}
