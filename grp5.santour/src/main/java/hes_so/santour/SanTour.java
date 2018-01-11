package hes_so.santour;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;




public class SanTour extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView latitudeField;
    private TextView longitudeField;
    private boolean tracking;
    private boolean startPoint;
    private Timer t;
    private DrawerLayout dl;
    public int maxGPS;
    public int minGPS;

    //check if this activity is active
    static boolean active = false;


    // some transient state for the activity instance
    private String mGameState;


    @Override
    protected void onStart() {
        super.onStart();
        active = true;

    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
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

        maxGPS = LocalData.getGpsMaxrange();
        minGPS = LocalData.getGpsMinRange();

        startPoint = false;

        setContentView(R.layout.activity_san_tour);

        setTitle("SanTour");
        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        startLocationListener();
        startTimer();

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.navigation);
//        nvDrawer.setCheckedItem(R.id.createTrack);
        drawerSetup(nvDrawer);

    }


   public void selectItemDrawer(MenuItem menuItem){
        Fragment myFragment = null;
        //Class fragmentClass;
       View  v = this.findViewById(android.R.id.content).getRootView();
        switch (menuItem.getItemId()){
            case R.id.createTrack:
                if (!active){
                    buttonSanTourClick(v);
                }

                break;

            case R.id.createPOD:
                buttonAddPODOnClick(v);
                break;

            case R.id.createPOI:
                buttonAddPOIOnClick(v);
                break;

            case R.id.POIPODlist:

                Intent intent = new Intent(SanTour.this, ListView.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                break;

            case R.id.Options:
                Intent intent2 = new Intent(SanTour.this, Options.class);
                startActivityForResult(intent2, PICK_CONTACT_REQUEST);
                break;


            default:
                buttonSanTourClick(v);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
    private void drawerSetup(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }


    static final int PICK_CONTACT_REQUEST = 1;

    public void buttonSanTourClick(View v){
        Intent intent = new Intent(SanTour.this, SanTour.class);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonAddPOIOnClick(View v) {
        LocalData.setTimerIsRunning(false);
        Intent intent = new Intent(SanTour.this, AddPoi.class);
        String longitude = longitudeField.getText().toString();
        String latitude = latitudeField.getText().toString();
        intent.putExtra("longitudeData", longitude);
        intent.putExtra("latitudeData", latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonAddPODOnClick(View v) {
        LocalData.setTimerIsRunning(false);
        Intent intent = new Intent(SanTour.this, AddPod.class);
        String longitude = longitudeField.getText().toString();
        String latitude = latitudeField.getText().toString();
        intent.putExtra("longitudeData", longitude);
        intent.putExtra("latitudeData", latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonSaveTrackOnClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = (this).getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Confirm");
        builder.setMessage("Overview of the track " + LocalData.getTrack().getKmLength() + "Nb POIs + Nb PODs");

        builder.setView(inflater.inflate(R.layout.dialog_save, null))

        .setPositiveButton("Save track and upload", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                LocalData.getTrack().setName(((TextView) findViewById(R.id.txtTrackName)).getText().toString());
                LocalData.getTrack().setKmLength(distanceComplete / 1000);
                LocalData.getTrack().setTimeDuration(((TextView) findViewById(R.id.timeTextView)).getText().toString());
                LocalData.saveDataFirebase();
                dialog.dismiss();


                seconds = 0;
                minutes = 0;
                distanceComplete = 0;

                ((TextView) findViewById(R.id.txtTrackName)).setText("");
                updateTime();
                LocalData.setTimerIsRunning(false);
                longitudeField.setText("");
                latitudeField.setText("");
                ((TextView)findViewById(R.id.distanceTextView)).setText("0");
                LocalData.getTrack().setName(null);
                LocalData.getTrack().setKmLength(0);
                LocalData.getTrack().setTimeDuration(null);

                Button start = ((Button) findViewById(R.id.trackButton));
                ((Button) start).setText("Start");
                ((Button) start).setBackgroundColor(Color.argb(99, 173, 234, 0));
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }





    private int seconds = 0;
    private int minutes = 0;

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



                            if (LocalData.isTimerIsRunning()) {

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


    public void buttonPauseOnClick(View v){
        if (tracking == true) {
            LocalData.setTimerIsRunning(false);

            tracking = false;
    
        }

        
    }



    public void buttonOnClick(View v) {
        if (tracking != true){
            tracking = true;
            LocalData.setTimerIsRunning(true);

            Button button = (Button) v;
            ((Button) v).setText("Pause");
            ((Button) v).setBackgroundColor(Color.argb(99, 234, 6, 0));

            Button saveTrackButton = ((Button) findViewById(R.id.saveTrack));
            saveTrackButton.setVisibility(View.VISIBLE);

        }else{
            tracking = false;
            LocalData.setTimerIsRunning(false);
            Button button = (Button) v;
            ((Button) v).setText("Resume");
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


    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;

    }




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        //this checks the permission to use GPS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //if allowed to use GPS -> set it on
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

            if (LocalData.getTrack().getTrackingPoints() != null) {
                Polyline route = mMap.addPolyline(new PolylineOptions()
                        .width(12)
                        .color(Color.BLUE)
                        .geodesic(true)
                        .zIndex(1));


                route.setPoints(LocalData.getTrack().getTrackingPoints());
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
            if (LocalData.getTrack().getTrackingPoints() != null) {
                if (LocalData.getTrack().getTrackingPoints().size()>1)
                {
                    distance = distFrom(loc.getLatitude(), loc.getLongitude(), LocalData.getTrack().getTrackingPoints().get(LocalData.getTrack().getTrackingPoints().size() - 1).latitude, LocalData.getTrack().getTrackingPoints().get(LocalData.getTrack().getTrackingPoints().size() - 1).longitude);
                }else {
                    distance = 15;
                }
            } else {
                distance = 15;
            }
            //Log.d("Update", loc + " // Distance : " + distance);
            int max = 0;
            int min = 0;

            maxGPS = LocalData.getGpsMaxrange();
            minGPS = LocalData.getGpsMinRange();

            if (maxGPS != 0){
                max = maxGPS;
            }else{
                max = 40;
            }

            if (minGPS != 0){
                min = minGPS;
            }else{
                min = 10;
            }
            Log.d("MaxGPS", max + " // MinGPS : " + min);
            if ((distance<max && distance>min)) {
                distanceComplete = distanceComplete + distance;
                longitudeField.setText(String.format("%.4f", loc.getLongitude()));
                latitudeField.setText(String.format("%.4f", loc.getLatitude()));


                //set the starting point into the map
                if (!startPoint){
                    double Lat = Double.parseDouble(LocalData.getActuellLangitude());
                    double Lng = Double.parseDouble(LocalData.getActuellLongitute());
                    LatLng startMarker = new LatLng(Lat, Lng);
                    mMap.addMarker(new MarkerOptions().position(startMarker)
                            .title("Starting point")
                            //setting the color
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    startPoint = true;
                }
                

                //turning location into LatLng
                LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
                if (LocalData.getTrack().getTrackingPoints() == null) {
                    LocalData.getTrack().setTrackingPoints(new ArrayList<LatLng>());
                }
                LocalData.getTrack().getTrackingPoints().add(coordinates);

                if (mMap != null) {
                    Polyline route = mMap.addPolyline(new PolylineOptions()
                            .width(12)
                            .color(Color.BLUE)
                            .geodesic(true)
                            .zIndex(1));


                    route.setPoints(LocalData.getTrack().getTrackingPoints());
                }

            }
        }
        if (loc == null && LocalData.getTrack().getTrackingPoints() != null)
        {
            if(LocalData.getTrack().getTrackingPoints().size()>0) {
                LatLng last = LocalData.getTrack().getTrackingPoints().get(LocalData.getTrack().getTrackingPoints().size() - 1);
                longitudeField.setText(String.format("%.4f", last.longitude));
                latitudeField.setText(String.format("%.4f", last.latitude));

                if (mMap != null) {
                    Polyline route = mMap.addPolyline(new PolylineOptions()
                            .width(12)
                            .color(Color.BLUE)
                            .geodesic(true)
                            .zIndex(1));


                    route.setPoints(LocalData.getTrack().getTrackingPoints());
                }

            }
        }
        TextView tvDistance = findViewById(R.id.distanceTextView);
        tvDistance.setText(String.format("%.4f", distanceComplete/1000));
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
        LocalData.getTrack().setTrackingPoints(new ArrayList<LatLng>());
        double[] listLatitute = savedInstanceState.getDoubleArray("ARRAY_LATITUDE");
        double[] listLongitude = savedInstanceState.getDoubleArray("ARRAY_LONGITUDE");
        for (int i = 0; i < listLatitute.length; i++)
        {
            LatLng temp = new LatLng(listLatitute[i], listLongitude[i]);
            LocalData.getTrack().getTrackingPoints().add(temp);
        }
        minutes = savedInstanceState.getInt("MINUTES_INT");
        seconds = savedInstanceState.getInt("SECONDES_INT");
        LocalData.setTimerIsRunning(savedInstanceState.getBoolean("ISTIMERRUNNING"));
        distanceComplete = savedInstanceState.getFloat("DISTANCE_FLOAT");
        startTimer();

        Button button =  findViewById(R.id.trackButton);
        if (LocalData.isTimerIsRunning()) {
            ((Button) button).setText("Pause");
            ((Button) button).setBackgroundColor(Color.argb(99, 234, 6, 0));
            Button saveTrackButton = ((Button) findViewById(R.id.saveTrack));
            saveTrackButton.setVisibility(View.VISIBLE);
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

        if (LocalData.getTrack().getTrackingPoints() != null) {
             listLatitute = new double[LocalData.getTrack().getTrackingPoints().size()];
             listLongitude = new double[LocalData.getTrack().getTrackingPoints().size()];

            for (int i = 0; i < LocalData.getTrack().getTrackingPoints().size(); i++) {
                listLatitute[i] = LocalData.getTrack().getTrackingPoints().get(i).latitude;
                listLongitude[i] = LocalData.getTrack().getTrackingPoints().get(i).longitude;
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
        outState.putBoolean("ISTIMERRUNNING", LocalData.isTimerIsRunning());
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

                LocalData.setActuellLangitude(coordinate.latitude+"");
                LocalData.setActuellLongitute(coordinate.longitude+"");
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

