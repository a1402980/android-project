package hes_so.android_project_2017;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;




public class SanTour extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView latitudeField;
    private TextView longitudeField;
    private List<LatLng> trackingPoints;
    private boolean tracking;
    private DatabaseReference trackRef, poiRef, podRef, gpsdataRef, podcategRef, trackPointsRef;
    private Timer t;

    FirebaseDatabase mdatabase = getDatabase();



    // some transient state for the activity instance
    private String mGameState;


    @Override
    protected void onStart() {
        super.onStart();

    }


    private static FirebaseDatabase database;

    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // recovering the instance state
        if (savedInstanceState != null) {
            mGameState = savedInstanceState.getString("GAMESTATEKEY");
        }



        setContentView(R.layout.activity_san_tour);

        setTitle("SanTour");


        trackRef = mdatabase.getReference("tracks");
        poiRef = mdatabase.getReference("POI");
        podRef = mdatabase.getReference("POD");
        gpsdataRef = mdatabase.getReference("gpsData");
        podcategRef = mdatabase.getReference("PODcategories");


        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        startLocationListener();
        startTimer();

    }

    static final int PICK_CONTACT_REQUEST = 1;

    public void buttonAddPOIOnClick(View v) {
        timerIsRunning = false;
        Intent intent = new Intent(SanTour.this, AddPoi.class);
        String longitude = longitudeField.getText().toString();
        String latitude = latitudeField.getText().toString();
        intent.putExtra("longitudeData" ,longitude);
        intent.putExtra("latitudeData" ,latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonSaveTrackOnClick(View v){

        Track track = new Track("test", "test");
        track.addPoint(new LatLng(12, 11));
        track.addPoint(new LatLng(13, 11));
        track.addPoint(new LatLng(14, 11));
        track.addPoint(new LatLng(12, 12));
        track.addPoint(new LatLng(12, 13));
        track.addPoint(new LatLng(12, 23));

        LocalData.setTrack(track);

        POD pod1 = new POD(11, "test", "test", null);
        Difficulty dif1 = new Difficulty(1, "Rocks");
        Difficulty dif2 = new Difficulty(2, "Something");
        pod1.addDifficulty(dif1);
        pod1.addDifficulty(dif2);

        POD pod2 = new POD(2, "test2", "test2", null);
        pod2.addDifficulty(dif1);
        pod2.addDifficulty(dif2);

        LocalData.addPOD(pod1);
        LocalData.addPOD(pod2);

        LocalData.addPOI(new POI(12, "test", "test", null));
        LocalData.addPOI(new POI(236, "asd", "asfdsd", null));

        LocalData.saveDataFirebase();

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to save this track?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                //Upload into DB the tracking points and then reset the track uplon clicking YES
                uploadTracks(trackingPoints);
                minutes = 0;
                seconds = 0;
                distanceComplete = 0;
                trackingPoints = new ArrayList<LatLng>();
                updateView(null);
                updateTime();

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();*/
    }


    private int seconds = 0;
    private int minutes = 0;
    private boolean timerIsRunning = false;

    private void startTimer()
    {

        if (t == null) {
            //Declare the timer
            t = new Timer();
            //Set the schedule function and rate
            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {



                            if (timerIsRunning) {

                                seconds++;
                                if (seconds >= 60) {
                                    minutes++;
                                    seconds = 0;
                                }
                                updateTime();
                            }
                        }

                    });
                }

            }, 0, 1000);
        }

    }

    private void updateTime() {
        TextView tv = (TextView) findViewById(R.id.timeTextView);

        String secondsS;
        String minutesS;

        if (seconds < 10) {
            secondsS = "0" + seconds;
        } else {
            secondsS = "" + seconds;
        }

        if (minutes < 10) {
            minutesS = "0" + minutes;
        } else {
            minutesS = "" + minutes;
        }

        tv.setText(String.valueOf(minutesS) + ":" + String.valueOf(secondsS));
    }
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void startLocationListener() {
        Log.e(TAG, "onCreate");
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void buttonOnClick(View v) {
        if (tracking != true){
            tracking = true;
            timerIsRunning = true;
            Button button = (Button) v;
            ((Button) v).setText("Stop");
            ((Button) v).setBackgroundColor(Color.argb(99, 234, 6, 0));

        }else{
            timerIsRunning = false;
            tracking = false;
            Button button = (Button) v;
            ((Button) v).setText("Start");
            ((Button) v).setBackgroundColor(Color.argb(99, 173, 234, 0));

        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Going yo your location...", Toast.LENGTH_LONG).show();

            //LatLng coordinate = new LatLng(21.000000, -101.400000);
            //CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
            //mMap.animateCamera(yourLocation);

        } else {
            Toast.makeText(this, "Error getting location!", Toast.LENGTH_LONG).show();
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;

    }

    public void uploadTracks(List<LatLng> trackingPoints ){

            //Upload the trek the user did

        String trackPointsKey;
        String trackName = ((EditText) findViewById(R.id.txtTrackName)).getText().toString();

        Track track1 = new Track(trackName, "");
        String trackKey = trackRef.push().getKey();

        Map<String, Object> newTracks = new HashMap<>();
        newTracks.put( trackKey, track1 );
        trackRef.updateChildren(newTracks);

        trackPointsRef = trackRef.child(trackKey).child("GPS");
        Map<String, Object> newTrackPoints = new HashMap<>();

        for (int i = 0; i < trackingPoints.size(); i++) {
            trackPointsKey = String.valueOf(i);
            newTrackPoints.put(trackPointsKey, trackingPoints.get(i));
        }


        trackPointsRef.updateChildren(newTrackPoints);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        //this checks the permission to use GPS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //if allowed to use GPS -> set it on
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

            if (trackingPoints != null) {
                Polyline route = mMap.addPolyline(new PolylineOptions()
                        .width(12)
                        .color(Color.BLUE)
                        .geodesic(true)
                        .zIndex(1));


                route.setPoints(trackingPoints);
            }

        } else {
            // otherwise ask for the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(this);
                        mMap.setOnMyLocationClickListener(this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    private float distanceComplete = 0;

    public void updateView(Location loc) {


        if (loc != null) {
            float distance;
            if (trackingPoints != null) {
                if (trackingPoints.size()>1)
                {
                    distance = distFrom(loc.getLatitude(), loc.getLongitude(), trackingPoints.get(trackingPoints.size() - 1).latitude, trackingPoints.get(trackingPoints.size() - 1).longitude);
                }else {
                    distance = 15;
                }
            } else {
                distance = 15;
            }
            Log.d("Update", loc + " // Distance : " + distance);
            if ((distance<40 && distance>10)) {
                distanceComplete = distanceComplete + distance;
                longitudeField.setText(String.format("%.4f", loc.getLongitude()));
                latitudeField.setText(String.format("%.4f", loc.getLatitude()));

                //turning location into LatLng
                LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
                if (trackingPoints == null) {
                    trackingPoints = new ArrayList<>();
                }
                trackingPoints.add(coordinates);

                if (mMap != null) {
                    Polyline route = mMap.addPolyline(new PolylineOptions()
                            .width(12)
                            .color(Color.BLUE)
                            .geodesic(true)
                            .zIndex(1));


                    route.setPoints(trackingPoints);
                }

            }
        }
        if (loc == null && trackingPoints != null)
        {
            if(trackingPoints.size()>0) {
                LatLng last = trackingPoints.get(trackingPoints.size() - 1);
                longitudeField.setText(String.format("%.4f", last.longitude));
                latitudeField.setText(String.format("%.4f", last.latitude));

                if (mMap != null) {
                    Polyline route = mMap.addPolyline(new PolylineOptions()
                            .width(12)
                            .color(Color.BLUE)
                            .geodesic(true)
                            .zIndex(1));


                    route.setPoints(trackingPoints);
                }

            }
        }
        TextView tvDistance = findViewById(R.id.distanceTextView);
        tvDistance.setText(String.format("%.1f", distanceComplete));
    }

    //Help https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }



    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        trackingPoints = new ArrayList<LatLng>();
        double[] listLatitute = savedInstanceState.getDoubleArray("ARRAY_LATITUDE");
        double[] listLongitude = savedInstanceState.getDoubleArray("ARRAY_LONGITUDE");
        for (int i = 0; i < listLatitute.length; i++)
        {
            LatLng temp = new LatLng(listLatitute[i], listLongitude[i]);
            trackingPoints.add(temp);
        }
        minutes = savedInstanceState.getInt("MINUTES_INT");
        seconds = savedInstanceState.getInt("SECONDES_INT");
        timerIsRunning = savedInstanceState.getBoolean("ISTIMERRUNNING");
        distanceComplete = savedInstanceState.getFloat("DISTANCE_FLOAT");
        startTimer();

        Button button =  findViewById(R.id.trackButton);
        if (timerIsRunning) {
            ((Button) button).setText("Stop");
            ((Button) button).setBackgroundColor(Color.argb(99, 234, 6, 0));
        }else
        {
            ((Button) button).setText("Start");
            ((Button) button).setBackgroundColor(Color.argb(99, 173, 234, 0));
        }

    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();

        double[] listLatitute;
        double[] listLongitude;

        if (trackingPoints != null) {
             listLatitute = new double[trackingPoints.size()];
             listLongitude = new double[trackingPoints.size()];

            for (int i = 0; i < trackingPoints.size(); i++) {
                listLatitute[i] = trackingPoints.get(i).latitude;
                listLongitude[i] = trackingPoints.get(i).longitude;
            }
        }
        else
        {
             listLatitute = new double[0];
             listLongitude = new double[0];
        }
        outState.putDoubleArray("ARRAY_LATITUDE",listLatitute);
        outState.putDoubleArray("ARRAY_LONGITUDE",listLongitude);
        outState.putFloat("DISTANCE_FLOAT", distanceComplete);
        outState.putInt("MINUTES_INT", minutes);
        outState.putInt("SECONDES_INT", seconds);
        outState.putBoolean("ISTIMERRUNNING", timerIsRunning);
        startTimer();


            updateView(null);
    }

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1;

    private class LocationListener implements android.location.LocationListener, Serializable {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }




        @Override
        public void onLocationChanged(Location location) {
            //activate when Start is pushed
            if (tracking == true) {
                Log.e(TAG, "onLocationChanged: " + location);
                mLastLocation.set(location);
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());

                try {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
                    mMap.animateCamera(yourLocation);
                } catch (Exception e) {

                }

                updateView(location);
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}

