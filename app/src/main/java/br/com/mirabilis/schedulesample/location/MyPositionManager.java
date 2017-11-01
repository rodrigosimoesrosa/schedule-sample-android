package br.com.mirabilis.schedulesample.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by ticketservices on 7/10/17.
 */
public class MyPositionManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    protected static final String TAG = MyPositionManager.class.getCanonicalName();
    protected Context context;
    protected GoogleApiClient googleApiClient;
    protected Location location;
    protected LocationManager locationManager;
    protected LocationRequest locationRequest;

    protected long updateInterval;
    protected long fastestInterval;
    protected int numberUpdates;

    protected double longitude;
    protected double latitude;

    protected MyLocationListener myLocationListener;


    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setFastestInterval(long fastestInterval) {
        this.fastestInterval = fastestInterval;
    }

    /**
     *
     * @param numberUpdates
     */
    public void setNumberUpdates(int numberUpdates) {
        if(numberUpdates <0) throw new IllegalArgumentException("The number of update can't be 0");
        this.numberUpdates = numberUpdates;
    }

    /**
     * Builder
     */
    public static class Builder {

        private Context context;
        private MyLocationListener myLocationListener;
        private long updateInterval = 5000;
        private long fastestInterval = 5000;

        public Builder(Context context){
            this.context = context;
        }

        /**
         * Listener
         * @param listener
         * @return
         */
        public MyPositionManager.Builder myLocationListener(MyLocationListener listener){
            this.myLocationListener = listener;
            return this;
        }

        /**
         * Update Interval
         * @param updateInterval
         * @return
         */
        public MyPositionManager.Builder updateInterval(long updateInterval){
            this.updateInterval = updateInterval;
            return this;
        }

        /**
         * Fastest Interval
         * @param fastestInterval
         * @return
         */
        public MyPositionManager.Builder fastestInterval(long fastestInterval){
            this.fastestInterval = fastestInterval;
            return this;
        }

        /**
         * Build obj {@link MyPositionManager}
         *
         * @return
         */
        public MyPositionManager build() {
            MyPositionManager myPositionManager = new MyPositionManager(context);
            myPositionManager.setMyLocationListener(myLocationListener);
            myPositionManager.setUpdateInterval(updateInterval);
            myPositionManager.setFastestInterval(fastestInterval);
            return myPositionManager;
        }
    }


    private MyPositionManager(Context context){
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setMyLocationListener(MyLocationListener myLocationListener) {
        this.myLocationListener = myLocationListener;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();

            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if(location == null){
                latitude = 0.0;
                longitude = 0.0;

                startLocationUpdates();
            }else {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if(myLocationListener != null){
                    myLocationListener.onLocation(location);
                }
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    protected void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(updateInterval)
                .setFastestInterval(fastestInterval);

        if(locationRequest != null && numberUpdates > 0){
            locationRequest.setNumUpdates(numberUpdates);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    public void onLoadLocation(MyLocationListener listener) {
        setMyLocationListener(listener);
        onStart();
    }

    public void onStart(){
        if(googleApiClient != null) googleApiClient.connect();
    }

    public void onStop() {
        if (googleApiClient.isConnected()) googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if(myLocationListener != null){
            myLocationListener.onLocation(location);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Return if provider is ON
     * @param context
     * @return
     */
    public static boolean isProviderOn (Context context, String provider){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(provider);
    }

    public interface MyLocationListener{
        void onLocation(Location location);
    }
}
