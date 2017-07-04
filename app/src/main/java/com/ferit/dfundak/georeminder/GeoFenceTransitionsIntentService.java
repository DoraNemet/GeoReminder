package com.ferit.dfundak.georeminder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dora on 02/07/2017.
 */

public class GeoFenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "geofence-transitions-service";

    public GeoFenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    ArrayList<reminderItem> reminders;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.i("dora", "error");
            return;
        }
         reminders = this.loadReminders();

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            sendNotification(geofenceTransitionDetails);

            Log.i("dora", geofenceTransitionDetails);
        } else {
            Log.i("dora", "error");
        }
    }

    private String getGeofenceTransitionDetails( int geofenceTransition, List<Geofence> triggeringGeofences) {
        String geofenceTransitionString;

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            geofenceTransitionString = "Enter";
        } else{
            geofenceTransitionString = "Unknown";
        }

        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();

        int j = 0;
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
            Log.i("dora", "triggering " + triggeringGeofencesIdsList.get(j));
            j++;
        }

        String triggeringGeofencesIdsString = "";

        for (int i = 0; i < reminders.size(); i++) {

            int mId = reminders.get(i).getID();
            String mDescription = reminders.get(i).getDescription();
            String mTitle = reminders.get(i).getTitle();

            if(triggeringGeofencesIdsList.contains(Integer.toString(mId))){
                Log.i("dora", "activated " + mId + " " + mDescription + " " + mTitle);
                Log.i("dora", "triggeringGeofencesIdsString: " + triggeringGeofencesIdsString );
               // triggeringGeofencesIdsList.add(mDescription);
                triggeringGeofencesIdsString += mTitle + " ";
            }/*else{
                triggeringGeofencesIdsString += triggeringGeofencesIdsList.get(0);
                Log.i("dora", "else triggeringGeofencesIdsString: " + triggeringGeofencesIdsString );
            }*/
        }

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    private void sendNotification(String notificationDetails) {

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setAutoCancel(true)
                .setContentTitle(notificationDetails)
                .setContentText("opis")
                .setSmallIcon(R.drawable.alarm)
                .setVibrate(new long[]{1000, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(notificationPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
    }

    private ArrayList<reminderItem> loadReminders() {
        return DatabaseHandler.getInstance(this).getAllReminders();
    }
}