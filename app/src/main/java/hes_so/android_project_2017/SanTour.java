package hes_so.android_project_2017;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SanTour extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView latitudeField;
    private TextView longitudeField;
    private List<LatLng> trackingPoints;
    private boolean tracking;
    private FirebaseDatabase database;
    private DatabaseReference trackRef, poiRef, podRef, gpsdataRef, podcategRef;

    //JCH: Storage reference for image upload onto the storage, button to select the image from gallery and integer for gallery intent
    private StorageReference mStorage;
    private Button bSelectImage;
    private static final int GALLERY_INTENT = 2;



    // some transient state for the activity instance
    private String mGameState;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // recovering the instance state
        if (savedInstanceState != null) {
            mGameState = savedInstanceState.getString("GAMESTATEKEY");
        }



        setContentView(R.layout.activity_san_tour);

        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        trackRef = database.getReference("tracks");
        poiRef = database.getReference("POI");
        podRef = database.getReference("POD");
        gpsdataRef = database.getReference("gpsData");
        podcategRef = database.getReference("PODcategories");


        //To add a new track
//        Track track1 = new Track("Track 1", "This is a first track");
//
//        String key = trackRef.push().getKey();
//
//        Map<String, Object> newTracks = new HashMap<>();
//        newTracks.put( key, track1 );
//        trackRef.updateChildren(newTracks);


        //Code for uploading an image, working on it, Jordi Chafer
        mStorage = FirebaseStorage.getInstance().getReference();
        bSelectImage = (Button) findViewById(R.id.selectImage);
        bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);
            }
        });


        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        startLocationListener();

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
            Button button = (Button) v;
            ((Button) v).setText("Stop");
            ((Button) v).setBackgroundColor(Color.argb(99, 234, 6, 0));
        }else{
            tracking = false;
            Button button = (Button) v;
            ((Button) v).setText("Start");
            ((Button) v).setBackgroundColor(Color.argb(99, 173, 234, 0));
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Going yo your location...", Toast.LENGTH_LONG).show();

            //LatLng coordinate = new LatLng(21.000000, -101.400000);
            //CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
            //mMap.animateCamera(yourLocation);

        } else {
            Toast.makeText(this, "Error getting location!", Toast.LENGTH_LONG).show();
        }
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


    public void updateView(Location loc) {

        float distance;
        if(trackingPoints != null) {
            distance = distFrom(loc.getLatitude(), loc.getLongitude(), trackingPoints.get(trackingPoints.size() - 1).latitude, trackingPoints.get(trackingPoints.size() - 1).longitude);
        }else
        {
            distance = 3;
        }
        Log.d("Update", loc + " // Distance : "+distance);
        if (distance<10) {

            longitudeField.setText(loc.getLongitude() + "");
            latitudeField.setText(loc.getLatitude() + "");

            //turning location into LatLng
            LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (trackingPoints == null) {
                trackingPoints = new ArrayList<>();
            }
            trackingPoints.add(coordinates);

            if (mMap!= null) {
                Polyline route = mMap.addPolyline(new PolylineOptions()
                        .width(12)
                        .color(Color.BLUE)
                        .geodesic(true)
                        .zIndex(1));


                route.setPoints(trackingPoints);
            }
        }
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

