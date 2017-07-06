package com.ferit.dfundak.georeminder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static com.ferit.dfundak.georeminder.MainActivity.MY_PREFS_NAME;

/**
 * Created by Dora on 02/07/2017.
 */

public class GeoFenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "geofence-transitions";

    public GeoFenceTransitionsIntentService() {
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
            Log.i(TAG, "error geofencingEvent");
            return;
        }
         reminders = this.loadReminders();

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            ArrayList<String> geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            sendNotification(geofenceTransitionDetails.get(0), geofenceTransitionDetails.get(1));

        } else {
            Log.i(TAG, "error geofence enter");
        }
    }

    private ArrayList<String> getGeofenceTransitionDetails( int geofenceTransition, List<Geofence> triggeringGeofences) {
        String geofenceTransitionString;
        String title;

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            geofenceTransitionString = "Enter";
        } else{
            geofenceTransitionString = "Unknown";
        }

        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();

        int j = 0;
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
            j++;
        }

        String triggeringGeofencesDesctiption = "";
        String triggeringGeofencesTitle = "";

        for (int i = 0; i < reminders.size(); i++) {

            int mId = reminders.get(i).getID();
            String mDescription = reminders.get(i).getDescription();
            String mTitle = reminders.get(i).getTitle();

            if(triggeringGeofencesIdsList.contains(Integer.toString(mId))){
                triggeringGeofencesTitle += mTitle + " ";
                triggeringGeofencesDesctiption += mDescription + " ";
            }
        }
        title = geofenceTransitionString + ": " + triggeringGeofencesTitle;

        if(title.equals(" ")){
            title = "GeoReminder geo notification";
        }
        if(triggeringGeofencesDesctiption.equals(" ")){
            triggeringGeofencesDesctiption = "You are on set location!";
        }
        ArrayList<String> titleDescription = new ArrayList<>();
        titleDescription.add(title);
        titleDescription.add(triggeringGeofencesDesctiption);

        return titleDescription;
    }


    private void sendNotification(String notificationTitle, String notificationDescription) {

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("fromNotification", true);
        editor.commit();

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSmallIcon(R.drawable.alarm)
                .setVibrate(new long[]{1000, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(notificationPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify((int)System.currentTimeMillis(), builder.build());
    }

    private ArrayList<reminderItem> loadReminders() {
        return DatabaseHandler.getInstance(this).getAllReminders();
    }
}