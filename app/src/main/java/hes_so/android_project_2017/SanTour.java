package hes_so.android_project_2017;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.content.ComponentName;

import android.content.ServiceConnection;

import android.os.IBinder;


public class SanTour extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

        private GoogleMap mMap;
        private TextView latitudeField;
        private TextView longitudeField;
        MyService service;
        boolean myServiceBound = false;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);


            setContentView(R.layout.activity_san_tour);

            latitudeField = (TextView) findViewById(R.id.TextView02);
            longitudeField = (TextView) findViewById(R.id.TextView04);
            MyService ser = MyService.getInstance();
            MyService.getInstance().registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            double latitude = intent.getDoubleExtra(MyService.EXTRA_LATITUDE, 0);
                            double longitude = intent.getDoubleExtra(MyService.EXTRA_LONGITUDE, 0);
                            latitudeField.setText(latitude+"");
                            longitudeField.setText(longitude+"");

                        }
                    }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
            );

            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        public void buttonOnClick(View v){


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Going yo your location...", Toast.LENGTH_LONG).show();

                LatLng coordinate = new LatLng(21.000000, -101.400000);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
                mMap.animateCamera(yourLocation);

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



    }

