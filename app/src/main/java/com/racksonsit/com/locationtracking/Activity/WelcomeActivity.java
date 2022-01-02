package com.racksonsit.com.locationtracking.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racksonsit.com.locationtracking.Adapter.UsersLIstRecycleViewAdapter;
import com.racksonsit.com.locationtracking.GoogleService;
import com.racksonsit.com.locationtracking.R;
import com.racksonsit.com.locationtracking.bean.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

public class WelcomeActivity extends AppCompatActivity implements LocationListener {


    TextView tv_address, tvLatitude;
    Button btn_showonmap;
    Button btn_show_all;

    User user;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    public static String lattitude, longitude;

    ArrayList<User> items;//= new ArrayList<>();
    ArrayList<User> userLists;
    RecyclerView recyclerview_product;
    RecyclerView.Adapter adapter_product;
    RecyclerView.LayoutManager layoutManager_product;


    //new
   /* private TextView latituteField;
    private TextView longitudeField;*/
    //private LocationManager locationManager;
    private String provider;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;

    SharedPreferences mPref;
    SharedPreferences.Editor medit;


    public static String strname, stremail;
    TextView tvname;
    public static String straddress = "";
    public static String strotherandroidid = "";


    //new location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        tv_address = (TextView) findViewById(R.id.tv_address);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvname = (TextView) findViewById(R.id.tv_name);
        btn_showonmap = (Button) findViewById(R.id.btn_show_on_map);
        btn_show_all = (Button) findViewById(R.id.btn_show_all);

        strname = getIntent().getExtras().getString("name");
        stremail = getIntent().getExtras().getString("email");

        tvname.setText("Welcome " + strname);




        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        fn_permission();


            if (mPref.getString("service", "").matches("")) {
                medit.putString("service", "service").commit();

                Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                startService(intent);

            } else {

                Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                stopService(intent);
                medit.putString("service", "service").commit();

                Intent intent1 = new Intent(getApplicationContext(), GoogleService.class);
                startService(intent1);
            }



        /*Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);*/

        //textName.setText(user.getDisplayName());
        //textEmail.setText(user.getEmail());

        recyclerview_product = (RecyclerView) findViewById(R.id.recycler_products);
        layoutManager_product = new LinearLayoutManager(getApplicationContext());
        recyclerview_product.setLayoutManager(layoutManager_product);
        recyclerview_product.setItemAnimator(new DefaultItemAnimator());
        items = new ArrayList<User>();
        adapter_product = new UsersLIstRecycleViewAdapter(getApplicationContext(), items);
        recyclerview_product.setAdapter(adapter_product);
        firebase1();


        btn_showonmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lattitude.equals("null") && longitude.equals("null")) {
                    Toast.makeText(WelcomeActivity.this, "Your location not Available.", Toast.LENGTH_SHORT).show();
                } else {

                    lattitude=lat(lattitude);
                    longitude=lat(longitude);


                    Intent i = new Intent(WelcomeActivity.this, MapsActivity.class);
                    i.putExtra("name", strname);
                    // i.putExtra("email", lattitude + "," + longitude);
                    startActivity(i);
                }
            }
        });

        btn_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Background Service Stop...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                stopService(intent);
            }
        });


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        TrackLocation1();
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

    }


    //new location

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }



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
       // registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
        locationManager.requestLocationUpdates(provider, 40, 10, this);
}

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);locationManager.removeUpdates(this);
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
        tvLatitude.setText("Your current location is" + "\n" + "Lattitude = " + String.valueOf(lat)
                + "\n" + "Longitude = " + String.valueOf(lng));
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

                    notifyDataSetChanged();


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


    public void firebase1() {
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
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    items.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user1 = postSnapshot.getValue(User.class);
                        items.add(user1);
                    }

                    notifyDataSetChanged();


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


    private void notifyDataSetChanged() {
        adapter_product.notifyDataSetChanged();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double latitude,longitude;
            Geocoder geocoder;
            geocoder = new Geocoder(WelcomeActivity.this, Locale.getDefault());
            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);

               /* tv_area.setText(addresses.get(0).getAdminArea());
                tv_locality.setText(stateName);*/
                tv_address.setText(countryName);



            } catch (IOException e1) {
                e1.printStackTrace();
            }


            /*tv_latitude.setText(latitude+"");
            tv_longitude.setText(longitude+"");*/
            tv_address.getText();


        }
    };



    private void TrackLocation1() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
            // firebase(lattitude, longitude);

        }
    }

    private void TimerMethod() {
        //START METHORD
        this.runOnUiThread(Timer_Tick);
    }
    //LOCATION REPORTING METHORD
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            getLocation();
            firebase(lattitude, longitude);
        }
    };
    //new
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (WelcomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                straddress = lattitude + "," + longitude;
                tvLatitude.setText("Your current location is" + "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude);

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                straddress = lattitude + "," + longitude;
                tvLatitude.setText("Your current location is" + "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude);


            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                straddress = lattitude + "," + longitude;
                tvLatitude.setText("Your current location is" + "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude);

            } else {


                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public String lat(String  t){
        return String.valueOf((Integer.parseInt(t.substring(0,2)) + (Integer.parseInt(t.substring(2,9))/60)));
    }

    public String lng(String  t){
        return String.valueOf((Integer.parseInt(t.substring(0,3)) + (Integer.parseInt(t.substring(3,10))/60)));
    }

}


