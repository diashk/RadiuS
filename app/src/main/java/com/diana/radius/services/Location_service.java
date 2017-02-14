package com.diana.radius.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Service that runs in the background and returns our location by broadcast and by shared preference
 */

public class Location_service extends Service implements LocationListener {
    public static final String LOCATION_ACTION = "com.diana.radius.location_action";
    public static SharedPreferences sp;
    public LocationManager locationManager;
    private double LAT, LNG;
    private Intent intent2;

// on create method -----------------------------------------
    @Override
    public void onCreate() {
        super.onCreate();
        intent2 = new Intent(LOCATION_ACTION);
        sp =  PreferenceManager.getDefaultSharedPreferences(this);
    }


    // on bind method ----------------------------------------
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // on start command method -------------------------------
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get the LocationManager object
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // request location updates every 5 seconds with location changes of 50 meters or more
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 50, this);

        return super.onStartCommand(intent, flags, startId);
    }

    // location listener methods

    // on location change - being called at every location change. saves new location to shared preference and sends broadcast
    @Override
    public void onLocationChanged(Location location) {

        LAT = location.getLatitude();
        LNG = location.getLongitude();
        sp.edit().putString("lat",String.valueOf(LAT)).apply();
        sp.edit().putString("lng",String.valueOf(LNG)).apply();

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }
}
