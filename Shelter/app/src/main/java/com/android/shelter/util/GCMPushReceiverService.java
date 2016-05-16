package com.android.shelter.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.android.shelter.HomeActivity;
import com.android.shelter.R;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by vaishnavigalgali on 5/14/16.
 */
public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("the_message");

//        Toast.makeText(GCMPushReceiverService.this, message, Toast.LENGTH_LONG).show();
        sendNotification(message);

    }
    private void sendNotification(String message){
        Intent intent = new Intent(this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Shelter")
                .setContentText(message)
                .setAutoCancel(true)
                .setTicker(message)
                .setContentIntent(pendingIntent)
                //.setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                .setSound(sound);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,noBuilder.build());
    }
}
