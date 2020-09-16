package com.example.musiccentral;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.musiccentral.aidlfile.KeyGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Service {

    final String TAG = "MusicClient";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG,"Start Command was called");

        String NOTIFICATION_CHANNEL_ID = "com.example.musiccentral";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG,"Service has been binded");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"Service has been Un-binded");
        return super.onUnbind(intent);
    }

    private final KeyGenerator.Stub binder = new KeyGenerator.Stub() {

        List<Information> infoList = Collections.synchronizedList(new ArrayList<Information>());

        @Override
        public String[] getKey() throws RemoteException {
            String[] temp= {"Hello", "Keshvi"};
            return temp;
        }

        @Override
        public synchronized List<Information> getAllInfo() throws RemoteException {

            Bitmap[] image = {BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.breakmyheart),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.beautifulpeople),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.trampoline),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.alltimelow),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.mysteryoflove)};

            infoList.clear();
            int arraySize = getResources().getStringArray(R.array.song_name).length;
            for (int i=0; i<arraySize; i++){

                Information info = new Information(image[i], getResources().getStringArray(R.array.song_name)[i],
                        getResources().getStringArray(R.array.artist_name)[i]);
                infoList.add(info);

            }
            return infoList;
        }

        @Override
        public synchronized Information getSpecificInfo(int id) throws RemoteException {

            Bitmap[] image = {BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.breakmyheart),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.beautifulpeople),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.trampoline),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.alltimelow),
                    BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.mysteryoflove)};

            Information info = new Information(image[id], getResources().getStringArray(R.array.song_name)[id],
                    getResources().getStringArray(R.array.artist_name)[id]);
            return info;
        }

        @Override
        public synchronized String getSongURL(int id) throws RemoteException {
            return getResources().getStringArray(R.array.url_name)[id];

        }

    };

}