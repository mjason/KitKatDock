package com.packruler.kitkatdock.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by Packruler on 4/8/2014.
 */
class ListenerService extends NotificationListenerService implements RemoteController.OnClientUpdateListener{

    private final String TAG = this.getClass().getSimpleName();
    private ServiceReceiver serviceReceiver;
    private RemoteController mRemoteController;
    private Context mContext;
    private Bitmap albumBitmap = null;


    @Override
    public void onCreate() {
        super.onCreate();
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.packruler.kitkatdock.app.NOTIFICATION_LISTENER_SERVICE");
        registerReceiver(serviceReceiver, filter);

        mContext = getApplicationContext();
        mRemoteController = new RemoteController(mContext, this);
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        boolean controllerInitialized = audioManager.registerRemoteController(mRemoteController);
        if(!controllerInitialized) {
            //handle registration failure
            Log.i(TAG, "Remote Not Initialized");
        } else {
            mRemoteController.setArtworkConfiguration(300, 300);
            //mRemoteController.setSynchronizationMode(RemoteController.POSITION_SYNCHRONIZATION_CHECK);
            Log.i(TAG, "Remote Initialized");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
        ((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).unregisterRemoteController(mRemoteController);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        /*Intent i = new  Intent("com.example.nlstest.NOTIFICATION_LISTENER_EXAMPLE");
        String ticker = "" + sbn.getNotification().tickerText;
        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n" + ticker + "\n");
        Notification current;
        if(sbn.getPackageName().equals("com.google.android.music")){
            Log.i(TAG,"Success");
            current = sbn.getNotification();
        }
        sendBroadcast(i);*/

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.i(TAG,"********** onNotificationRemoved");
        //Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        /*Intent i = new  Intent("com.example.nlstest.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");
        sendBroadcast(i);*/
    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        Log.i(TAG, "Metadata Update");

        //Update TextViews
        String artistTemp = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST, "");
        if (artistTemp.isEmpty()){
            artistTemp = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, "");
        }
        FullscreenActivity.artistText.setText(artistTemp);
        FullscreenActivity.albumText.setText(metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUM, ""));
        FullscreenActivity.titleText.setText(metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, ""));


        albumBitmap = metadataEditor.getBitmap(metadataEditor.BITMAP_KEY_ARTWORK, albumBitmap);
        FullscreenActivity.albumArtwork.setImageBitmap(albumBitmap);
    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags){

    }

    @Override
    public void onClientPlaybackStateUpdate(int state){

    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed){

    }
    @Override
    public void onClientChange(boolean clearing){

    }


    @SuppressWarnings("ConstantConditions")
    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("playPause")) {
                Log.i(TAG,"Service received Play/Pause");
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                boolean first = mRemoteController.sendMediaKeyEvent(keyEvent);
                keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                boolean second = mRemoteController.sendMediaKeyEvent(keyEvent);
                if (!first || !second) {
                    Log.i(TAG, "FUCK" + "\n" + "Action Down = " + first + " | Action Up = " + second);
                }
            }
            else if(intent.getStringExtra("command").equals("skipBack")) {
                Log.i(TAG,"Service received Reverse");
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                boolean first = mRemoteController.sendMediaKeyEvent(keyEvent);
                keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                boolean second = mRemoteController.sendMediaKeyEvent(keyEvent);
                if (!first || !second) {
                    Log.i(TAG, "FUCK" + "\n" + "Action Down = " + first + " | Action Up = " + second);
                }
            }
            else if(intent.getStringExtra("command").equals("skipForward")) {
                Log.i(TAG,"Service received Forward");
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
                boolean first = mRemoteController.sendMediaKeyEvent(keyEvent);
                keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT);
                boolean second = mRemoteController.sendMediaKeyEvent(keyEvent);
                if (!first || !second) {
                    Log.i(TAG, "FUCK" + "\n" + "Action Down = " + first + " | Action Up = " + second);
                }
            }
        }
    }
}
