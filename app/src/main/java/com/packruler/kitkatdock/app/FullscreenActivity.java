package com.packruler.kitkatdock.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class FullscreenActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();
    static TextView artistText;
    static TextView titleText;
    static TextView albumText;
    static ImageView albumArtwork;
    private MainReceiver mainreceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Intent serviceIntent = new Intent(this, ListenerService.class);
        startService(serviceIntent);

        // Initialize receiver to get data from broadcasts
        mainreceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.packruler.kitkatdock.app.MAIN_APP_UPDATES");
        registerReceiver(mainreceiver, filter);

        //Initialize TextView and ImagaeView fields for updating track info
        artistText = (TextView) findViewById(R.id.artistUpdate);
        albumText = (TextView) findViewById(R.id.albumUpdate);
        titleText = (TextView) findViewById(R.id.trackUpdate);
        albumArtwork = (ImageView) findViewById(R.id.albumArt);
        artistText.setText("");
        albumText.setText("");
        titleText.setText("");
    }


    /**
     * Determines which button is pressed then broadcasts request for service to handle requested action
     *
     * @param v Used by system to pass View when button clicked
     */
    public void buttonClicked(View v) {
        if (v.getId() == R.id.skipBack) {
            Intent i = new Intent("com.packruler.kitkatdock.app.NOTIFICATION_LISTENER_SERVICE");
            i.putExtra("command", "skipBack");
            sendBroadcast(i);
        } else if (v.getId() == R.id.playPause) {
            Intent i = new Intent("com.packruler.kitkatdock.app.NOTIFICATION_LISTENER_SERVICE");
            i.putExtra("command", "playPause");
            sendBroadcast(i);
        } else if (v.getId() == R.id.skipForward) {
            Intent i = new Intent("com.packruler.kitkatdock.app.NOTIFICATION_LISTENER_SERVICE");
            i.putExtra("command", "skipForward");
            sendBroadcast(i);
        }
    }

    class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received Broadcast");
        }
    }
}
