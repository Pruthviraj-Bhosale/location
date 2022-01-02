package com.racksonsit.com.locationtracking.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racksonsit.com.locationtracking.R;
import com.racksonsit.com.locationtracking.bean.User;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.racksonsit.com.locationtracking.Activity.WelcomeActivity.straddress;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;
    String strname="";

    String strlatitude, strlongitude;
    TextView tv_map_default,tv_map_satelight,tv_map_terrian,tv_map_hybrid;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude,longitude;

    public static double longtude;
    public static double latitude;

    //new location
    private TextView latituteField;
    private TextView longitudeField;
    //private LocationManager locationManager;
    private String provider;
    ArrayList<User> items;
    User user;
    String strotherandroidid="";

    int userchoose=1;
    //new
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        strname=getIntent().getExtras().getString("name");
        try {
            straddress = getIntent().getExtras().getString("email");
            strotherandroidid = getIntent().getExtras().getString("androidid");

            if(straddress==null){
                userchoose=0;
            }else {
                userchoose=1;
            }
        }
        catch (Exception e){

        }
       /* GoogleMap googleMap = null;
        mMap = googleMap;*/
        tv_map_default=(TextView)findViewById(R.id.tv_map_default);
        tv_map_satelight=(TextView)findViewById(R.id.tv_map_satelight);
        tv_map_terrian=(TextView)findViewById(R.id.tv_map_terrian);
        tv_map_hybrid=(TextView)findViewById(R.id.tv_map_hybrid);



        //location get current
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        items = new ArrayList<User>();
        //new location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
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
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            /*latituteField.setText("Location not available");
            longitudeField.setText("Location not available");*/
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
        }
        //ends

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        tv_map_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        tv_map_satelight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        tv_map_terrian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        tv_map_hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });



        //map additional setting
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // mMap.setMapType(GoogleMap.MAP_TYPE_NONE);


        // Showing / hiding your current location
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

        try {

            if(userchoose==0) {
                //own location
                // Add a marker in Sydney and move the camera
                //LatLng sydney = new LatLng(-34, 151);
                Integer commaindex = straddress.lastIndexOf(",");
                strlatitude = straddress.substring(0, commaindex - 1);
                strlongitude = straddress.substring(commaindex + 1);
                // LatLng sydney1 = new LatLng(16.790600, 74.551200);
                // LatLng sydney = new LatLng(Double.parseDouble(strlatitude), Double.parseDouble(strlongitude));
                LatLng sydney = new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude));
                mMap.addMarker(new MarkerOptions().position(sydney).title(strname));
                //mMap.addMarker(new MarkerOptions().position(sydney1).title("Demo user"));
               /* mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));*/
                latitude = Double.parseDouble(strlatitude);
                longtude = Double.parseDouble(strlongitude);

               /* mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);*/

            }else {
                //other location
                // Add a marker in Sydney and move the camera
                //LatLng sydney = new LatLng(-34, 151);
                firebase1();
                Integer commaindex = straddress.lastIndexOf(",");
                strlatitude = straddress.substring(0, commaindex - 1);
                strlongitude = straddress.substring(commaindex + 1);
                // LatLng sydney1 = new LatLng(16.790600, 74.551200);
                // LatLng sydney = new LatLng(Double.parseDouble(strlatitude), Double.parseDouble(strlongitude));
                LatLng sydney = new LatLng(Double.parseDouble(strlatitude), Double.parseDouble(strlongitude));
                mMap.addMarker(new MarkerOptions().position(sydney).title(strname));
                //mMap.addMarker(new MarkerOptions().position(sydney1).title("Demo user"));
               /* mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));*/
               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                latitude = Double.parseDouble(strlatitude);
                longtude = Double.parseDouble(strlongitude);

               /* mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);*/
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(latitude,longtude)).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
        catch (Exception e){

        }
    }


    //new location
    @Override
    protected void onResume() {
        super.onResume();
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
        locationManager.requestLocationUpdates(provider, 400, 10, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
       /* latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));*/
        //Toast.makeText(this, ""+String.valueOf(lat)+","+String.valueOf(lng), Toast.LENGTH_SHORT).show();
        lattitude=String.valueOf(lat);
        longitude=String.valueOf(lng);
        try {
            if(userchoose==0) {
                //own location
                LatLng sydney = new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(sydney).title(strname));
                //mMap.addMarker(new MarkerOptions().position(sydney1).title("Demo user"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                firebase(String.valueOf(lat), String.valueOf(lng));
            }else {

            }
        }
        catch (Exception e){

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }







    public void firebase(final String strLongitude, final String strLatitude) {
        final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //insert
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
        //final String userId = mDatabase.push().getKey();

        // creating user object
        Log.e("firebase map activity","map");


        //read record// mDatabase..child(userId)
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    items.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user1 = postSnapshot.getValue(User.class);
                        items.add(user1);
                    }

                    //notifyDataSetChanged();


                    if (dataSnapshot.child(androidId).exists()) {
                        user = dataSnapshot.child(androidId).getValue(User.class);


                        Log.d("firebase", "User name: " + user.getName() + ", address " + user.getAddress());

                        //update
                        Log.d("firebase", "user exist");
                        String newAddress = "" + strLongitude + "," + strLatitude;

                        mDatabase.child(androidId).child("address").setValue(newAddress);

                    } else {
                        User user1 = new User(strname, androidId, strLongitude + "," + strLatitude);
                        // pushing user to 'users' node using the userId
                        mDatabase.child(androidId).setValue(user1);

                    }

                } catch (Exception e) {
                    Log.d("firebase", String.valueOf(e));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });

        //update


    }


    //firebase update run time
    public void firebase1() {

        final String androidId = strotherandroidid;

        //insert
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
        //final String userId = mDatabase.push().getKey();

        // creating user object
        Log.e("firebase map activity","map");


        //read record// mDatabase..child(userId)
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    items.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user1 = postSnapshot.getValue(User.class);
                        items.add(user1);

                        /*if(user.getAndroidid().equals(androidId)){
                            items.add(user1);

                        }*/


                    }

                    //notifyDataSetChanged();


                    if (dataSnapshot.child(androidId).exists()) {
                        user = dataSnapshot.child(androidId).getValue(User.class);


                        Log.d("firebase", "User name: " + user.getName() + ", address " + user.getAddress());

                        //update
                        Log.d("firebase", "user exist");
                        straddress=user.getAddress();
                        Integer commaindex = straddress.lastIndexOf(",");
                        strlatitude = straddress.substring(0, commaindex - 1);
                        strlongitude = straddress.substring(commaindex + 1);
                        mMap.clear();
                        // LatLng sydney1 = new LatLng(16.790600, 74.551200);
                        // LatLng sydney = new LatLng(Double.parseDouble(strlatitude), Double.parseDouble(strlongitude));
                        LatLng sydney = new LatLng(Double.parseDouble(strlatitude), Double.parseDouble(strlongitude));
                        mMap.addMarker(new MarkerOptions().position(sydney).title(strname));
                        //mMap.addMarker(new MarkerOptions().position(sydney1).title("Demo user"));
                       /* mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));*/
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                        latitude = Double.parseDouble(strlatitude);
                        longtude = Double.parseDouble(strlongitude);

               /* mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);*/
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                new LatLng(latitude,longtude)).zoom(12).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                      /*  String newAddress = "" + strLongitude + "," + strLatitude;

                        mDatabase.child(androidId).child("address").setValue(newAddress);*/

                    }/* else {
                        User user1 = new User(strname, androidId, strLongitude + "," + strLatitude);
                        // pushing user to 'users' node using the userId
                        mDatabase.child(androidId).setValue(user1);

                    }*/

                } catch (Exception e) {
                    Log.d("firebase", String.valueOf(e));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });


    }


    private void notifyDataSetChanged() {

    }




   /* private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                straddress=lattitude+","+longitude;
               // tvLatitude.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                 //       + "\n" + "Longitude = " + longitude);
            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                straddress=lattitude+","+longitude;
                //tvLatitude.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                  //      + "\n" + "Longitude = " + longitude);
            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                straddress=lattitude+","+longitude;
               // tvLatitude.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                //        + "\n" + "Longitude = " + longitude);
            }else{
                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();
            }
        }
    }

   */




}




