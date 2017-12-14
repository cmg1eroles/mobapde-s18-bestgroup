package ph.edu.dlsu.mobapde.tara;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.graphics.Color.argb;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,OnCompleteListener<Void> {

    private GoogleMap mMap;
    private LatLng notifArea;

    //Play Services location
    private static final int MY_PERMISSION_REQUEST_CODE = 1234;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 2629;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    //DatabaseReference ref;

    DatabaseReference onlineRef, currentUserRef, counterRef, racesRef; // from PlayersActivity
    private  static HashMap<String, Tracking> users = new HashMap<>();              //made to get all users

    VerticalSeekBar mSeekBar;

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //ref = FirebaseDatabase.getInstance().getReference("Users");

        //added these stuff
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()); //creates child in lastonline with key uid
        racesRef = FirebaseDatabase.getInstance().getReference("races"); //reference for races
        //geofire = new GeoFire(ref);

        mSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(progress), 2000, null);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Get the geofences used. Geofence data is hard coded in this sample.
        //populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        //addGeofences(); //+++++++++++++++++++++++++++++++++++++++++

        setupLocation();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }


    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {

        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        //++++++++++++++++++++++++++++
        //mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
        //        .addOnCompleteListener(this);
        //++++++++++++++++++++++++++++

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ito", "Geofence is added");
                        // Geofences added
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ito", "Geofence is NOT added");
                        // Failed to add geofences
                        // ...
                    }
                });
    }


    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();
    }


    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }



    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     * @param task the resulting Task, containing either a result or error.
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            //setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w("woo", errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


        /**
         * This sample hard codes geofence data. A real app might dynamically create geofences based on
         * the user's location.
         */
    private void populateGeofenceList() {
        Log.d("woo", "populating geofence");
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {

        //for (Map.Entry<String, LatLng> entry : ) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(System.currentTimeMillis()) // (Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private void populateGeofenceList(LatLng latlng, String key) {
        Log.d("ito", "populating geofence");
           mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(key)
                    .setCircularRegion(
                            latlng.latitude,
                            latlng.longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(System.currentTimeMillis())  //(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

        mMap.addCircle(new CircleOptions()
                .center(latlng)
                .radius(500) //THIS IS IN METERS PO
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)
        );

    }

    private void populateGeofenceList(LatLng latlng, String key, Date date) {

        Log.d("woo", "populating geofence");
        Log.d("woo", "date.getTime() is: " + date.getTime() + "  System.currentTimeMillis():  " + System.currentTimeMillis());
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        latlng.latitude,
                        latlng.longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(date.getTime() - System.currentTimeMillis())  //(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mMap.addCircle(new CircleOptions()
                .center(latlng)
                .radius(500) //THIS IS IN METERS PO
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(20, 0, 255, 127))
                .strokeWidth(5.0f)
        );

    }


    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("woo", "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSION_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i("woo", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CODE);
        }
    }

    //Press Ctrl+O


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                if (grantResults.length <= 0) {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i("woo", "User interaction was cancelled.");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("woo", "Permission granted.");
                    performPendingGeofenceTask();
                } else {
                    // Permission denied.

                    // Notify the user via a SnackBar that they have rejected a core permission for the
                    // app, which makes the Activity useless. In a real app, core permissions would
                    // typically be best requested during a welcome-screen flow.

                    // Additionally, it is important to remember that a permission might have been
                    // rejected without asking the user for permission (device policy or "Never ask
                    // again" prompts). Therefore, a user interface affordance is typically implemented
                    // when permissions are denied. Otherwise, your app could appear unresponsive to
                    // touches or interactions which have required permissions.
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Build intent that displays the App settings screen.
                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                    mPendingGeofenceTask = PendingGeofenceTask.NONE;
                }
                break;
        }
    }

    //this checks if the app can access the coarse and the fine location
    private void setupLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //REQUEST RUNTIME PERMISSION
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);


        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }


    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            /*ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(mLastLocation.getLatitude()),String.valueOf(mLastLocation.getLongitude()
                    ), "Active"));*/


            // ______________________________________________________________________________
            onlineRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(Boolean.class)) {
                        currentUserRef.onDisconnect().removeValue(); // delete old value

                        //adds an online user to the list
                        counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                        String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()
                                ), "Active"));


                    }

                }


                    @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //____________________________________________________________________

            racesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        if (noteSnapshot.child("participants").hasChildren()){
                            String uId = noteSnapshot.getKey();
                            Date date = noteSnapshot.child("date").getValue(Date.class);
                            Double lat = noteSnapshot.child("location").child("latitude").getValue(Double.class);
                            Double lng = noteSnapshot.child("location").child("longitude").getValue(Double.class);
                            String title = (String) noteSnapshot.child("title").getValue();
                            HashMap<String, Boolean> ulist = (HashMap<String, Boolean>) noteSnapshot.child("participants").getValue();
                            //Race race = noteSnapshot.getValue(Race.class);
                            Log.d("woo", "uId: " + uId + " date: " + date + " lat: " + lat + " lng: " + lng + " title: " + title + " ulist: " + ulist);

                            Race race = new Race(uId, date, new LatLng(lat,lng), title, ulist);

                            if(race.getUsers().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Log.d("woo", "geofence should be on now since I am participating");
                                Log.d("woo", "RACE PASSED: " + race.getDate());
                                populateGeofenceList(race.getLocation(),race.getTitle(), race.getDate());
                            }

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            counterRef.addValueEventListener(new ValueEventListener() {
            //counterRef.addChildEventListener(new ChildEventListener() {
                //int ctr = 0;


                @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Tracking note = noteSnapshot.getValue(Tracking.class);
                       Log.d("ITO", "HASH MAP: " + users);
                       if (users.containsKey(note.getUid()))
                           users.get(note.getUid()).getM().remove();

                       users.put(note.getUid(), note);

                        if(Double.parseDouble(note.getLat()) == notifArea.latitude && Double.parseDouble(note.getLng()) == notifArea.longitude)
                            sendNotification("Tara", String.format("%s is at the finish line", note.getEmail()));

                    }

                    for (String key: users.keySet()){

                        Log.d("ITO", users.get(key).getEmail() + " has the M " + users.get(key).getM());

                        //Marker m = null;
                        if(users.get(key).getM() != null) {
                            Log.d("ITO", "@@@@@@@@@@@@@@" + users.get(key).getEmail() + " changed position!");
                            users.get(key).getM().remove(); //removes the outdated marker
                        }
                        users.get(key).setM(mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(users.get(key).getLat()), Double.parseDouble(users.get(key).getLng())))
                                .title(users.get(key).getEmail())
                                .snippet(users.get(key).getEmail())));
                            Log.d("ITO", "****************" + users.get(key).getEmail() + " has the M " + users.get(key).getM());
                        //markers.add(m);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));

                    }

                }


                /*
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("ITO", "LAMAN NG HASHMAP USERS: " + users);
                    Tracking newUser = dataSnapshot.getValue(Tracking.class);
                    newUser.setM(mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(newUser.getLat()), Double.parseDouble(newUser.getLng())))
                            .title(newUser.getEmail())
                            .snippet(newUser.getEmail())));
                    Log.d("ITO", "****************" + newUser.getEmail() + " has the M " + newUser.getM());
                    //markers.add(m);
                    if (users.containsKey(s)) {
                        Log.d("ITO", ">>>>>>>>>>  " + users.get(s).getEmail() + " was removed");
                        users.get(newUser.getUid()).getM().remove();
                        users.remove(newUser.getUid());
                    }

                    Log.d("ITO", ">!>!>!>!  " + newUser.getEmail() + " was added");
                    users.put(newUser.getUid(), newUser);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Tracking newUser = dataSnapshot.getValue(Tracking.class);
                    if (users.containsKey(s)) {
                        users.get(newUser.getUid()).getM().remove();
                        users.remove(newUser.getUid());
                    }

                    newUser.setM(mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(newUser.getLat()), Double.parseDouble(newUser.getLng())))
                            .title(newUser.getEmail())
                            .snippet(newUser.getEmail())));
                    users.put(newUser.getUid(), newUser);
                    Log.d("ITO", "@@@@@@@@@@@" + newUser.getEmail() + " changed position!");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Tracking removedUser = dataSnapshot.getValue(Tracking.class);

                    if(users.get(removedUser.getUid()) != null) {
                        users.get(removedUser.getUid()).getM().remove();
                        users.remove(removedUser.getUid());
                        Log.d("ITO", "!!!!!!!!!!!" + removedUser.getEmail() + " was removed!");


                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                */
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            //List<Tracking> track = new ArrayList<>();

/*
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Tracking note = noteSnapshot.getValue(Tracking.class);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Integer.parseInt(note.getLat()), Integer.parseInt(note.getLng()))).title(note.getEmail()));



                        //update the location in firebase
                        geofire.setLocation("You", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //add the marker
                                if(myCurrLoc != null)
                                    myCurrLoc.remove(); //removes the outdated marker
                                myCurrLoc = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title("You"));


                                //move camera to this position
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                            }
                        });

                        Log.d("hey", String.format("Your location was changed %f/%f", latitude, longitude));
                        //track.add(note);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        */
        } else
            Log.d("hey", "Cannot get your location");
    }

    //sets up the locaiton request
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    //This method creates a user who will use the API of Google
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    //This method checks if the device is supported
    private boolean checkPlayServices() {
        // GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) is depracated daw
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        //create an area where you will get a notif



        notifArea = new LatLng(14.657540, 121.013261);    //BASTA DAPAT PWEDENG PALITAN 'TO!!!
       /*
        mMap.addCircle(new CircleOptions()
                .center(notifArea)
                .radius(500) //THIS IS IN METERS PO
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)
        );
        (/


        //ADD GEOQUERY HERE
        //0.05f = 50 meters


        /*GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(notifArea.latitude, notifArea.longitude), 0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("Tara", String.format("%s is close to the destination!", key));

            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("Tara", String.format("%s passed the destination ????", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("move",String.format("yas we moved to this location: [%f/%f]", location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("error", ""+error);
            }

        });*/


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void sendNotification(String hey, String format) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(hey)
                .setContentText(format);
        NotificationManager notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MapsActivity.class);

        // FLAG_IMMUTABLE > FLAG_UPDATE_CURRENT
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();

        //notification.flags != Notification.FLAG_AUTO_CANCEL;
        //notification.defaults != Notification.DEFAULT_SOUND;

        notifManager.notify(new Random().nextInt(), notification);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }


}