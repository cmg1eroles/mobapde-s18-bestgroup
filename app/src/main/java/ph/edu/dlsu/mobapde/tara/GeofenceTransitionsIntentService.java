/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ph.edu.dlsu.mobapde.tara;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class GeofenceTransitionsIntentService extends IntentService {

    String status;
    DatabaseReference racesRef;
    long timestamp;
    long deadline;
    String deadlineID;
    long numOnTime, numEarly, numLate;
    long points, newPoints;
    private static final String TAG = "GeofenceTransitionsIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
        Log.d("ito", "GeofenceTransitionsIntentService() constructor");
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.d("ito", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            // Send notification and log the transition details.

            Log.d("ito", "sending notif");
            sendNotification(geofenceTransitionDetails);
            Log.d("ito", geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.d("ito", getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        status = geofenceTransitionString;
        
        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MapsActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        if (status.equals("Arrived EARLY") || status.equals("Arrived ON TIME")){
            // Define the notification settings.
            builder.setSmallIcon(R.drawable.logo)
                    // In a real app, you may want to use a library like Volley
                    // to decode the Bitmap.
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.logo))
                    .setColor(Color.RED)
                    .setContentTitle(notificationDetails)
                    .setContentText("You received " + newPoints + " points")
                    .setContentIntent(notificationPendingIntent);

        }else if (status.equals("Arrived LATE")){
            // Define the notification settings.
            builder.setSmallIcon(R.drawable.logo)
                    // In a real app, you may want to use a library like Volley
                    // to decode the Bitmap.
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.logo))
                    .setColor(Color.RED)
                    .setContentTitle(notificationDetails)
                    .setContentText("You lost " + newPoints + " points")
                    .setContentIntent(notificationPendingIntent);

        }else{
            // Define the notification settings.
            builder.setSmallIcon(R.drawable.logo)
                    // In a real app, you may want to use a library like Volley
                    // to decode the Bitmap.
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.logo))
                    .setColor(Color.RED)
                    .setContentTitle(notificationDetails)
                    .setContentText("Press to open the app.")
                    .setContentIntent(notificationPendingIntent);

        }


        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /*
    public int getPoints(){
        return (deadline - timestamp)/(timestamp + deadline) *
    }
    */

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);




        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deadlineID = (String) dataSnapshot.child("currentRace").getValue();
                racesRef = FirebaseDatabase.getInstance().getReference("races").child(deadlineID).child("date");
                numEarly = (long) dataSnapshot.child("numEarly").getValue();
                numLate = (long) dataSnapshot.child("numLate").getValue();
                numOnTime = (long) dataSnapshot.child("numOnTime").getValue();
                points = (long) dataSnapshot.child("points").getValue();

                racesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        deadline = (long) dataSnapshot.child("time").getValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Intent intent = new Intent(getBaseContext(), ArrivedDialog.class);


        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:


                timestamp = System.currentTimeMillis();
                if (timestamp == deadline){
                    numOnTime++;
                    userRef.child("numOnTime").setValue(numOnTime);
                    setPoints("ON TIME");
                    return "Arrived ON TIME";
                }else if (timestamp < deadline){
                    numEarly++;
                    userRef.child("numEarly").setValue(numEarly);
                    setPoints("EARLY");
                    return "Arrived EARLY";
                }
                numLate++;
                userRef.child("numLate").setValue(numLate);
                setPoints("LATE");
                return "Arrived LATE";

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    public void setPoints(String status){
        switch (status){
            case "ON TIME":
                points += 25;
                newPoints = 25;
                return;
            case "EARLY":
                points += 50;
                newPoints = 50;
                return;
            case "LATE":
                points -= 25;
                newPoints = 25;
                return;
            default:
                points += 0;
                return;

        }
    }
}