package com.skipperuzumaki.bastionandroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Foreground extends Service {
    public static final String CHANNEL_ID = "Contact Tracing";
    Broadcast Broadcaster;
    UuidHelper UuidH;
    Encoder Encode;
    Crypto Cryptography;
    Trace _Trace;
    HandlerThread handlerThread;
    Boolean Run;

    @Override
    public void onCreate() {
        super.onCreate();
        Broadcaster = new Broadcast();
        UuidH = new UuidHelper();
        Encode = new Encoder();
        Run = false;
        File FileDirectory;
        FileDirectory = getFilesDir();
        _Trace = new Trace(FileDirectory);
        try {
            Cryptography = new Crypto(FileDirectory);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = "Contact tracing Running in Background";
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        _Trace.Start();
        handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Broadcaster.Start(UuidH.Generate(Encode.Encode(Cryptography.Encrypt())));
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Run = true;
                while(Run) {
                    System.out.println("Looping");
                    try {
                        Broadcaster.update(UuidH.Generate(Encode.Encode(Cryptography.Encrypt())));
                    } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        Run = false;
        handlerThread.quit();
        try {
            Broadcaster.Stop();
            _Trace.Stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
