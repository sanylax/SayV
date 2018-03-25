package com.neelraja.assaultprevention;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import android.provider.Settings.Secure;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    protected double latitude, longitude;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;
    private FirebaseDatabase database;
    private String android_id = Secure.getString(MainActivity.this.getContentResolver(), Secure.ANDROID_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //


        //Get location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            contactCloseUsers(latitude, longitude, 0);
                        }
                    }
                });
        //Write location to firebase
        database = FirebaseDatabase.getInstance();
        final DatabaseReference latRef = database.getReference("users/"+android_id+"/lat");
        final DatabaseReference longRef = database.getReference("users/"+android_id+"/long");

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                latRef.setValue(latitude);
                longRef.setValue(longitude);
            }
        });

//        latRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                // Toast.makeText(getApplicationContext(), "Latitude: " + value, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//                Toast.makeText(getApplicationContext(), "Failed to read value.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        longRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                //Toast.makeText(getApplicationContext(), "Longitude: " + value, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//                Toast.makeText(getApplicationContext(), "Failed to read value.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void contactCloseUsers(final double latitude, final double longitude, double radius){

        DatabaseReference users =  database.getReference().child("users");
        final ArrayList close_users = new ArrayList<String>();
        final double r = radius;

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot snapshot: dataSnapshot.getChildren() ){
                    double otherLat = snapshot.child("lat").getValue(Double.class);
                    double otherLong = snapshot.child("long").getValue(Double.class);


                    System.out.println("Distance: " + distance(latitude, longitude, otherLat, otherLong));
                    System.out.println("Lat/Long: " + latitude + " " + longitude);
                    System.out.println("Other Lat/Long: " + otherLat + " " + otherLong);

                    double dist = distance(latitude, longitude, otherLat, otherLong);
                    Toast.makeText(getApplicationContext(), ""+distance(latitude, longitude, otherLat, otherLong), Toast.LENGTH_SHORT).show();

                    if(dist <= r){
                        //close_users.add(snapshot.getKey());
                        //database.getReference()
                        DatabaseReference ref = snapshot.child("contact").getRef();
                        ref.setValue(true);
                    }
                }

                //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });

    }

    public double distance(double lat1,  double lon1, double lat2, double lon2){
        double r = 6371 * 1000; // metres
        double w1 = Math.toRadians(lat1);
        double w2 =  Math.toRadians(lat2);
        double deltaW = Math.toRadians(lat2-lat1);
        double deltaL = Math.toRadians(lon2-lon1);

        double a = Math.sin(deltaW/2) * Math.sin(deltaW/2) +
                Math.cos(deltaW) * Math.cos(deltaW) *
                        Math.sin(deltaL/2) * Math.sin(deltaL/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = r * c;

        return d;
    }
}
