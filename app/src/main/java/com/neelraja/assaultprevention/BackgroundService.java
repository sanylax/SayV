package com.neelraja.assaultprevention;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by saarthaksharma on 3/25/18.
 */

public class BackgroundService extends Service {

    LocationManager mLocationManager = null;

    public int onStartCommand(){
        Toast.makeText(getApplicationContext(), "start Command", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    protected void onHandleIntent(Intent intent){
        Toast.makeText(getApplicationContext(), "handle intent", Toast.LENGTH_SHORT).show();

    }
    LocationListener[] mLocationListeners = new LocationListener[]{
      //new LocationListener()
    };

    public IBinder onBind(Intent arg){
        return null;
    }
    public void onCreate(){

//        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        try{
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//
//        }catch{
//
//        }

    }
}