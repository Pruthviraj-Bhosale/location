package com.racksonsit.com.locationtracking.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.racksonsit.com.locationtracking.R;
import com.racksonsit.com.locationtracking.listener.SmsListener;
import com.racksonsit.com.locationtracking.listener.SmsReceiver;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String lat = "", lng = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        messageReceived("s");


        //for read sms
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                if(messageText.contentEquals("Latitude(N)")&&messageText.contentEquals("Longitude(E)")) {
                   messageText="Latitude(N): 1646.8525 Longitude(E): 07433.3939";

                    try {
                        Integer indexdot = messageText.indexOf(":");
                        String submessge = messageText.substring(indexdot + 2);
                        Integer indecseconddot = submessge.lastIndexOf(":");

                        lat = messageText.substring(indexdot + 2);
                        lat = lat.substring(0, 9);
                        lng = submessge.substring(indecseconddot + 2);
                        lng = lng.substring(0, 10);
                        lat = lat(lat);
                        lng = lng(lng);

                    } catch (Exception e) {
                        Toast.makeText(MapsActivity2.this, "Location Not Available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (lat.equals("") && lng.equals("")) {
            Toast.makeText(this, "Location Not Available", Toast.LENGTH_SHORT).show();
        } else {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
            mMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            mMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            mMap.getUiSettings().setZoomGesturesEnabled(true);


            LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            mMap.addMarker(new MarkerOptions().position(location).title("Location"));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }


    }

    public String lat(String  t){
        return String.valueOf((Float.parseFloat(t.substring(0,2)) + (Float.parseFloat(t.substring(2,9))/60)));
    }

    public String lng(String  t){
        return String.valueOf((Float.parseFloat(t.substring(0,3)) + (Float.parseFloat(t.substring(3,10))/60)));
    }

    public void messageReceived(String messageText) {
        Log.d("Text",messageText);
        //messageText="Latitude(N): 1646.8584 Longitude(E): 07433.3899";
        messageText="Latitude(N): 1642.2790 Longitude(E): 07428.6767";

        try {
            Integer indexdot=messageText.indexOf(":");
            String submessge=messageText.substring(indexdot+2);
            Integer indecseconddot=submessge.lastIndexOf(":");

            lat=messageText.substring(indexdot+2);
            lat=lat.substring(0,9);
            lng=submessge.substring(indecseconddot+2);
            lng=lng.substring(0,10);
            lat=lat(lat);
            lng=lng(lng);

        }
        catch (Exception e){
            Toast.makeText(MapsActivity2.this, "Location Not Available", Toast.LENGTH_SHORT).show();
        }
    }

}

