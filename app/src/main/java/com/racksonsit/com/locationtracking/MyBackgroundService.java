package com.racksonsit.com.locationtracking;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racksonsit.com.locationtracking.bean.User;

import java.util.ArrayList;

import static com.racksonsit.com.locationtracking.Activity.WelcomeActivity.strname;


public class MyBackgroundService extends Service implements LocationListener {

    ArrayList<User> items;
    User user;

    private LocationManager locationManager;
    private String provider;
    private static final int REQUEST_LOCATION = 1;

    private TextView latituteField;
    private TextView longitudeField;


    public MyBackgroundService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //new


    @Override
    public void onCreate() {
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        super.onCreate();


        items = new ArrayList<User>();


        //new location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        locationManager.requestLocationUpdates(provider, 40, 10, this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        locationManager.requestLocationUpdates(provider, 40, 10, this);

        locationManager.removeUpdates(this);
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }

    //new location
   /* @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 40, 10, this);
    }
*/
    /* Remove the locationlistener updates when Activity is paused */
  /*  @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }*/

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
       /* latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));*/
        Toast.makeText(this, ""+String.valueOf(lat)+","+String.valueOf(lng), Toast.LENGTH_SHORT).show();
       /* lattitude=String.valueOf(lat);
        longitude=String.valueOf(lng);
        tvLatitude.setText("Your current location is" + "\n" + "Lattitude = " + String.valueOf (lat)
                + "\n" + "Longitude = " + String.valueOf(lng));*/
       Log.e("background","location changed");
        firebase(String.valueOf(lat), String.valueOf(lng));

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
        Log.e("firebase wel activity","map");

        //read record// mDatabase..child(userId)
        //addListenerForSingleValueEvent
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
}
