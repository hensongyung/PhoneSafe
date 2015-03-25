package com.hensonyung.phonesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class GPSService extends Service {
    private LocationManager lm;
    private Mylistener mylistener;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        mylistener = new Mylistener();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider =lm.getBestProvider(criteria,true);
        lm.requestLocationUpdates(provider,0,0,mylistener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(mylistener);
        mylistener = null;
    }

    class Mylistener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "J:" +location.getLongitude() +"\n";
            String latitude  = "W:"+location.getLatitude() +"\n";
            String accuracy = "A:"+location.getAccuracy();



            SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastlocation",longitude + latitude +accuracy);
            editor.commit();

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
}
