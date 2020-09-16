package com.example.musicclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.example.musiccentral.aidlfile.KeyGenerator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnStartService, btnStopService, btnAll, btnSpecific, btnPauseResume, btnStop;
    ListView lv;
    TextView status;
    protected static final String TAG = "MusicClientMainActivity";
    protected KeyGenerator musicService = null;
    Intent intent;

    //Pop up window
    private PopupWindow mPopupWindow;
    private ConstraintLayout mLayout;

    //Program state variables
    boolean isServiceStarted = false;
    boolean isBound = false;

    //Constants
    final String SERVICE_NAME = "musiccentral";
    final String SERVICE_PACKAGE = "com.example.musiccentral";
    final int GET_ALL = 0;
    final int GET_SINGLE = 1;
    final int PLAY = 2;
    final int PAUSE = 3;
    final int START = 4;
    final int STOP = 5;

    protected int selectionMusicStatus;
    protected int selectionStatus;

    MediaPlayer mediaPlayer;

    int pauseLength=0;

    ContentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing views
        lv = (ListView) findViewById(R.id.output);
        btnStopService = findViewById(R.id.stop_button);
        btnStartService = findViewById(R.id.start_button);
        btnAll = findViewById(R.id.key_button);
        btnSpecific = findViewById(R.id.specific_button);
        btnPauseResume = findViewById(R.id.music_pause_button);
        btnStop = findViewById(R.id.music_stop_button);
        status = findViewById(R.id.status);

        //Create explicit intent to connect to the service
        intent = new Intent(SERVICE_NAME);
        intent.setPackage(SERVICE_PACKAGE);

        //Start Service
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isServiceStarted) {
                    selectionStatus = START;
                    startForegroundService(intent);
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
                    status.setText("Service is Connected");
                    isServiceStarted = true;
                    enableDisableViews();
                    Log.i(TAG,"Pressed start button");

                }else{
                    Toast.makeText(MainActivity.this,
                            "Service is already running",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Stop Service
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceStarted) {
                    selectionStatus = STOP;
                    Log.i(TAG,"Pressed stop service button");
                    unbindService(connection);
                    musicService = null;
                    isBound = false;
                    status.setText("Service is Disonnected");
                    //stopService(i);
                    isServiceStarted = false;
                    enableDisableViews();
                }else{
                    Toast.makeText(MainActivity.this,
                            "Service isn't running",Toast.LENGTH_SHORT).show();;
                }
            }
        });

        //Display songs by Binding the service
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceStarted) {
                    Log.i(TAG,"Pressed play service button");
                    selectionStatus = GET_ALL;
                    enableDisableViews();
                }else{
                    Toast.makeText(MainActivity.this, "Service isn't running",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Display pop up window for the specific song (radio button based)
        btnSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceStarted) {
                    Log.i(TAG,"Pressed single service button");
                    selectionStatus = GET_SINGLE;
                    enableDisableViews();


                }else{
                    Toast.makeText(MainActivity.this, "Service isn't running",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Pause or Resume Music
        btnPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //To pause music
                if (selectionMusicStatus == PLAY) {
                    Log.i(TAG, "In Pause condition");
                    try{
                        selectionMusicStatus = PAUSE;
                        btnStop.setEnabled(true);
                        mediaPlayer.pause();
                        pauseLength = mediaPlayer.getCurrentPosition();
                    }catch(Exception ex){
                        Log.e(TAG,"Error: " + ex.getMessage());
                    }
                    //To resume music
                } else if (selectionMusicStatus == PAUSE) {
                    Log.i(TAG, "In Resume condition");
                    try{
                        selectionMusicStatus = PLAY;
                        btnStop.setEnabled(true);
                        mediaPlayer.seekTo(pauseLength);
                        mediaPlayer.start();
                    }catch(Exception ex){
                        Log.e(TAG,"Error: " + ex.getMessage());
                    }
                }
            }
        });

        //Stop Music
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Log.i(TAG, "In Stop music condition");
                    btnPauseResume.setEnabled(false);
                    mediaPlayer.stop();
                    mediaPlayer = null;
                }catch(Exception ex){
                    Log.e(TAG,"Error: " + ex.getMessage());
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound){
            unbindService(connection);
        }
        stopService(intent);
    }


    // Function:
    // 1. Enable/Disable views
    // 2. Populate list view, list view item click listener
    // 3. Get popup window on clicking get specific info (on clicking selecting a radio button)
    public void enableDisableViews() {
        try {
            switch (selectionStatus) {
                case GET_ALL: {
                    ContentAdapter.selectedAnswers.clear();
                    Log.i(TAG, "In get all case");
                    if(!isBound){
                        Log.i(TAG, "Binding to service if not binded");
                        bindService(intent, connection, Context.BIND_AUTO_CREATE);
                    }
                    if (isBound) {
                        Log.i(TAG, "In get all info case");
                        List<Information> info = musicService.getAllInfo();
                        int index = 0;
                        Bitmap[] images = new Bitmap[info.size()];
                        String[] names = new String[info.size()];
                        String[] artist = new String[info.size()];
                        for (Information i : info){
                            images[index] = i.getImage();
                            names[index] = i.getName();
                            artist[index] = i.getArtist();
                            index++;
                        }
                        adapter = new ContentAdapter(this, images, names, artist);
                        lv.setAdapter(adapter);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                if(!isBound){
                                    Log.i(TAG, "Binding to service if not binded");
                                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
                                }
                                if (isBound){
                                    Log.i(TAG, "In get music list item listener case");
                                    btnPauseResume.setEnabled(true);
                                    selectionMusicStatus = PLAY;
                                    try {
                                        String songURL = musicService.getSongURL(i);
                                        try {

                                            if (mediaPlayer != null){
                                                mediaPlayer.stop();
                                            }

                                            // Start media player
                                            mediaPlayer = new MediaPlayer();
                                            mediaPlayer.setAudioAttributes(
                                                    new AudioAttributes.
                                                            Builder().
                                                            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).
                                                            build());

                                            mediaPlayer.setDataSource(songURL);

                                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    mp.start();
                                                }
                                            });
                                            mediaPlayer.prepareAsync();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }catch (RemoteException e){
                                        e.printStackTrace();
                                    }
                                }
                            }

                        });
                        btnAll.setEnabled(true);
                        btnSpecific.setEnabled(true);
                        btnStopService.setEnabled(true);
                        btnStartService.setEnabled(false);
                        btnPauseResume.setEnabled(true);
                        btnStop.setEnabled(true);

                    }
                    break;

                }
                case GET_SINGLE: {
                    Log.i(TAG, "In get single case");
                    if(!isBound) {
                        Log.i(TAG, "Binding to service if not binded");
                        bindService(intent, connection, Context.BIND_AUTO_CREATE);
                    }
                    if (isBound) {
                        Log.i(TAG, "In get single song info binded case");
                        if (ContentAdapter.selectedAnswers.indexOf(true) != -1){
                            System.out.println(ContentAdapter.selectedAnswers.indexOf(true));
                            Information info = musicService.getSpecificInfo(ContentAdapter.selectedAnswers.indexOf(true));
                            Log.i(TAG, info.getName());

                            //Pop up window
                            mLayout = (ConstraintLayout) findViewById(R.id.ll);

                            LayoutInflater inflater = (LayoutInflater)
                                    getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.popup, null);

                            mPopupWindow = new PopupWindow(
                                    popupView,
                                    800,800
                            );

                            if(Build.VERSION.SDK_INT>=21){
                                mPopupWindow.setElevation(5.0f);
                            }

                            TextView name = (TextView) popupView.findViewById(R.id.popup_name);
                            TextView artist = (TextView) popupView.findViewById(R.id.popup_artist);
                            ImageView image = (ImageView) popupView.findViewById(R.id.popup_image);

                            name.setText(info.getName());
                            artist.setText(info.getArtist());
                            image.setImageBitmap(info.image);

                            mPopupWindow.showAtLocation(mLayout, Gravity.CENTER,0,0);

                            View container = mPopupWindow.getContentView().getRootView();
                            if(container != null) {
                                WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                                WindowManager.LayoutParams p = (WindowManager.LayoutParams)container.getLayoutParams();
                                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                p.dimAmount = 0.5f;
                                if(wm != null) {
                                    wm.updateViewLayout(container, p);
                                }
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please select a song",Toast.LENGTH_SHORT).show();
                        }
                        btnAll.setEnabled(true);
                        btnSpecific.setEnabled(true);
                        btnStopService.setEnabled(true);
                        btnStartService.setEnabled(false);
                        btnPauseResume.setEnabled(true);
                        btnStop.setEnabled(true);

                    }
                    break;

                }
                case START: {
                    Log.i(TAG, "In start case");
                    btnAll.setEnabled(true);
                    btnSpecific.setEnabled(false);
                    btnStopService.setEnabled(true);
                    btnStartService.setEnabled(false);
                    btnPauseResume.setEnabled(false);
                    btnStop.setEnabled(false);
                    break;
                }
                case STOP: {
                    Log.i(TAG, "In stop case");
                    btnAll.setEnabled(false);
                    btnSpecific.setEnabled(false);
                    btnStopService.setEnabled(false);
                    btnStartService.setEnabled(true);
                    lv.setAdapter(null);
                    break;
                }
                default: {
                    Toast.makeText(MainActivity.this, "Invalid inputs",Toast.LENGTH_SHORT).show();
                    break;
                }

            }
        } catch (RemoteException e) {

            Log.e(TAG, e.toString());

        }

    }

    //Creating service connection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = KeyGenerator.Stub.asInterface(service);
            isBound = true;
            status.setText("Service is Connected & Bound");
            Toast.makeText(MainActivity
                    .this, "Service was connected.",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
            Log.i(TAG, "Service Disconnected");
            Toast.makeText(MainActivity
                    .this, "Service was disconnected.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Toast.makeText(MainActivity.this, "Binding to service died..",Toast.LENGTH_SHORT).show();;
        }
    };
}
