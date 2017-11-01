package br.com.mirabilis.schedulesample.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by ticketservices on 7/7/17.
 */

@SuppressWarnings("MissingPermission")
public class GeoLocationManager {

    private static final long DELAY_TO_GET_LOCATION = 20000;

    private Context context;
    private LocationManager locationManager;

    private Location lastLocation;
    private List<LocationListener> locationListeners;

    private int interval;
    private float distance;

    private Timer timerToUpdate;

    {
        locationListeners = new ArrayList<>();
    }

    private GeoLocationManager(Context context){
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Builder
     */
    public static class Builder {

        private Context context;
        private int interval = 0;
        private float distance = 0f;

        public Builder(Context context){
            this.context = context;
        }

        /**
         * milliseconds to update location
         * @param interval
         */
        public Builder interval(int interval){
            this.interval = interval;
            return this;
        }

        /**
         * milliseconds to update location
         * @param distance
         */
        public Builder distance(int distance){
            this.distance = distance;
            return this;
        }

        /**
         * Build obj {@link GeoLocationManager}
         *
         * @return
         */
        public GeoLocationManager build() {
            GeoLocationManager manager = new GeoLocationManager(context);
            manager.setInterval(interval);
            manager.setDistance(distance);
            return manager;
        }
    }

    public void destroy(){
        clearListeners();
    }

    private void clearListeners() {
        for(LocationListener listener : locationListeners){
            locationManager.removeUpdates(listener);
        }
    }
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void loadNetwork(GeoLocationListener listener){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, distance, new LocationListener(LocationManager.NETWORK_PROVIDER, listener));
        timerToUpdate = new Timer();
        timerToUpdate.schedule(new GetLastLocationTimerTask(listener, LocationManager.NETWORK_PROVIDER), DELAY_TO_GET_LOCATION);
    }

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void loadGPS(GeoLocationListener listener){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, distance, new LocationListener(LocationManager.GPS_PROVIDER, listener));
        timerToUpdate = new Timer();
        timerToUpdate.schedule(new GetLastLocationTimerTask(listener, LocationManager.GPS_PROVIDER), DELAY_TO_GET_LOCATION);
    }

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void loadLocation(GeoLocationListener listener){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, distance, new LocationListener(LocationManager.NETWORK_PROVIDER, listener));
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, distance, new LocationListener(LocationManager.NETWORK_PROVIDER, listener));

        timerToUpdate = new Timer();
        timerToUpdate.schedule(new GetLastLocationTimerTask(listener, LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER), DELAY_TO_GET_LOCATION);
    }

    private class GetLastLocationTimerTask extends TimerTask {

        private final GeoLocationListener listener;
        private List<String> providers;

        public GetLastLocationTimerTask(GeoLocationListener listener, String ... providers) {
            this.providers = Arrays.asList(providers);
            this.listener = listener;
        }

        @Override
        public void run() {

            clearListeners();

            Location networkLocation = null;
            Location gpsLocation = null;

            if (providers.contains(LocationManager.GPS_PROVIDER)){
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if(gpsLocation!=null && networkLocation!=null){
                if(gpsLocation.getTime() > networkLocation.getTime()){
                    listener.updated(gpsLocation);
                }else {
                    listener.updated(networkLocation);
                }
                return;
            }

            if(gpsLocation!=null){
                listener.updated(gpsLocation);
                return;
            }

            if(networkLocation!=null){
                listener.updated(networkLocation);
                return;
            }

            listener.updated(null);
        }
    }

    private class LocationListener implements android.location.LocationListener{

        private GeoLocationListener listener;

        public LocationListener(String provider){
            this(provider,null);
        }

        public LocationListener(String provider, GeoLocationListener geoLocationListener){
            locationListeners.add(this);
            lastLocation = locationManager.getLastKnownLocation(provider);
            listener = geoLocationListener;
        }

        @Override
        public void onLocationChanged(Location location){
            Log.i(GeoLocationManager.class.getCanonicalName(), "onLocationChanged: " + location);
            lastLocation.set(location);
            if(listener != null){
                listener.updated(lastLocation);
            }
        }

        @Override
        public void onProviderDisabled(String provider){
            Log.i(GeoLocationManager.class.getCanonicalName(), "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider){
            Log.i(GeoLocationManager.class.getCanonicalName(), "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){
            Log.i(GeoLocationManager.class.getCanonicalName(), "onStatusChanged: " + provider);
        }
    }

    public interface GeoLocationListener{
        void updated(Location location);
    }
}
