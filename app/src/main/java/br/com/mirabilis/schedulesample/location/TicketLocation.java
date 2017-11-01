package br.com.mirabilis.schedulesample.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

import java.util.Random;

import br.com.mirabilis.schedulesample.R;

/**
 * This class is responsible to get information about {@link Location} of smartphone.
 * Created by Edenred Mobile Team
 * @author rodrigosimoesrosa
 * rodrigo.rosa@consulting-for.edenred.com
 */
public class TicketLocation {

    private static final float kilometerToMeter = 1000.0f;
    private static final float latitudeToKilometer = 111.133f;
    private static final float longitudeToKilometerAtZeroLatitute = 111.320f;
    private static final Random random = new Random();
    private static final double squareRootTwo = Math.sqrt(2);

    private static Location cachedPosition;
    private final LocationManager locationManager;
    private final Context context;

    private boolean requireFine;
    private boolean passive;
    private boolean requireNewLocation;
    private long intervalToReceiveLocation;

    private int blurRadius;
    private LocationListener locationListener;
    private Location position;
    private TicketListenerPositionChange listener;

    /**
     * Interface which is responsible to dispatch when position are changed
     */
    public interface TicketListenerPositionChange {
        void onPositionChanged();
    }

    /**
     * Coordinates latitude and longitude
     */
    public static class Point implements Parcelable {

        public final double latitude;
        public final double longitude;

        public Point(double lat, double lgt) {
            latitude = lat;
            longitude = lgt;
        }

        @Override
        public String toString() {
            return "(" + latitude + ", " + longitude + ")";
        }

        public static final Creator<Point> CREATOR = new Creator<Point>() {

            @Override
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            @Override
            public Point[] newArray(int size) {
                return new Point[size];
            }

        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

        private Point(Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }
    }

    /**
     * Builder
     */
    public static class Builder {

        private Context context;
        private boolean requireFine;
        private boolean passive;
        private boolean requireNewLocation;
        private TicketListenerPositionChange listenerPositionChange;

        private long intervalToReceiveLocation = 10 * 60 * 1000;

        public Builder(Context context){
            this.context = context;
        }

        /**
         * This provider determines location using satellites. Depending on conditions, this provider may take a while to return a location fix. Requires the permission ACCESS_FINE_LOCATION.
         * @param value
         * @return
         */
        public Builder requireFine(boolean value){
            this.requireFine = value;
            return this;
        }

        /**
         * A special location provider for receiving locations without actually initiating a location fix.
         * @param value
         * @return
         */
        public Builder passive(boolean value){
            this.passive = value;
            return this;
        }

        /**
         * Listener
         * @param listenerPositionChange
         * @return
         */
        public Builder listenerPositionChange(TicketListenerPositionChange listenerPositionChange){
            this.listenerPositionChange = listenerPositionChange;
            return this;
        }

        /**
         * milliseconds to update location
         * @param intervalToReceiveLocation
         */
        public Builder intervalToReceiveLocation(long intervalToReceiveLocation){
            this.intervalToReceiveLocation = intervalToReceiveLocation;
            return this;
        }

        /**
         * Control cache
         * @param value
         */
        public void requireNewLocation(boolean value){
            this.requireNewLocation = value;
        }

        /**
         * Build obj {@link TicketLocation}
         * @return
         */
        public TicketLocation build() {
            TicketLocation location = new TicketLocation(context);
            location.setRequireFine(requireFine);
            location.setPassive(passive);
            location.setListener(listenerPositionChange);
            location.setIntervalToReceiveLocation(intervalToReceiveLocation);
            location.setRequireNewLocation(requireNewLocation);

            return location;
        }
    }

    /**
     * Constructor
     * @param context
     */
    public TicketLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public void setRequireFine(boolean requireFine) {
        this.requireFine = requireFine;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public void setIntervalToReceiveLocation(long interval){
        this.intervalToReceiveLocation = interval;
    }

    public void setRequireNewLocation(boolean requireNewLocation) {
        this.requireNewLocation = requireNewLocation;

        if (!this.requireNewLocation) {
            position = getCachedPosition();
            cachePosition();
        }
    }

    public void setListener(final TicketListenerPositionChange listener) {
        this.listener = listener;
    }

    public boolean isLocationEnabled() {
        return isLocationEnabled(getProviderName());
    }

    private boolean isLocationEnabled(final String providerName) {
        try {
            return locationManager.isProviderEnabled(providerName);
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("MissingPermission")
    public void beginUpdates() {
        if (locationListener != null) {
            endUpdates();
        }

        if (!requireNewLocation) {
            position = getCachedPosition();
        }

        locationListener = new OnLocationListener();
        locationManager.requestLocationUpdates(getProviderName(), intervalToReceiveLocation, 0, locationListener);
    }

    public void endUpdates() {
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
    }

    private Location blurWithRadius(final Location originalLocation) {
        if (blurRadius <= 0) {
            return originalLocation;
        } else {
            Location newLocation = new Location(originalLocation);
            double blurMeterLong = calculateRandomOffset(blurRadius) / squareRootTwo;
            double blurMeterLat = calculateRandomOffset(blurRadius) / squareRootTwo;
            newLocation.setLongitude(newLocation.getLongitude() + meterToLongitude(blurMeterLong, newLocation.getLatitude()));
            newLocation.setLatitude(newLocation.getLatitude() + meterToLatitude(blurMeterLat));
            return newLocation;
        }
    }

    private static int calculateRandomOffset(final int radius) {
        return random.nextInt((radius + 1) * 2) - radius;
    }

    public Point getPosition() {
        if (position == null) {
            return null;
        } else {
            Location position = blurWithRadius(this.position);
            return new Point(position.getLatitude(), position.getLongitude());
        }
    }

    public double getLatitude() {
        if (position == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(this.position);
            return position.getLatitude();
        }
    }

    public double getLongitude() {
        if (position == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(this.position);
            return position.getLongitude();
        }
    }

    public float getSpeed() {
        if (position == null) {
            return 0.0f;
        } else {
            return position.getSpeed();
        }
    }

    public double getAltitude() {
        if (position == null) {
            return 0.0f;
        } else {
            return position.getAltitude();
        }
    }

    public void setBlurRadius(final int blurRadius) {
        this.blurRadius = blurRadius;
    }

    private class OnLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            position = location;
            cachePosition();

            if (listener != null) {
                listener.onPositionChanged();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    private String getProviderName() {
        return getProviderName(requireFine);
    }

    private String getProviderName(final boolean requireFine) {
        if (requireFine) {
            if (passive) {
                return LocationManager.PASSIVE_PROVIDER;
            }
            else {
                return LocationManager.GPS_PROVIDER;
            }
        } else {
            if (isLocationEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (passive) {
                    throw new RuntimeException(context.getString(R.string.location_error_passive_network));
                } else {
                    return LocationManager.NETWORK_PROVIDER;
                }
            } else {
                if (isLocationEnabled(LocationManager.GPS_PROVIDER) || isLocationEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    return getProviderName(true);
                } else {
                    return LocationManager.NETWORK_PROVIDER;
                }
            }
        }
    }

    /**
     * Returns the last position from the cache
     */
    @SuppressWarnings("MissingPermission")
    private Location getCachedPosition() {
        if (cachedPosition != null) {
            return cachedPosition;
        } else {
            try {
                return locationManager.getLastKnownLocation(getProviderName());
            } catch (Exception e) {
                return null;
            }
        }
    }

    /** Caches the current position */
    private void cachePosition() {
        if (position != null) {
            cachedPosition = position;
        }
    }

    /**
     * Opens the device's settings screen where location access can be enabled
     */
    public static void openSettings(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    /**
     * Converts a difference in latitude to a difference in kilometers
     */
    public static double latitudeToKilometer(double latitude) {
        return latitude * latitudeToKilometer;
    }

    /**
     * Converts a difference in kilometers to a difference in latitude
     */
    public static double kilometerToLatitude(double kilometer) {
        return kilometer / latitudeToKilometer(1.0f);
    }

    /**
     * Converts a difference in latitude to a difference in meters
     */
    public static double latitudeToMeter(double latitude) {
        return latitudeToKilometer(latitude) * kilometerToMeter;
    }

    /**
     * Converts a difference in meters to a difference in latitude
     */
    public static double meterToLatitude(double meter) {
        return meter / latitudeToMeter(1.0f);
    }

    /**
     * Converts a difference in longitude to a difference in kilometers
     */
    public static double longitudeToKilometer(double longitude, double latitude) {
        return longitude * longitudeToKilometerAtZeroLatitute * Math.cos(Math.toRadians(latitude));
    }

    /**
     * Converts a difference in kilometers to a difference in longitude
     */
    public static double kilometerToLongitude(double kilometer, double latitude) {
        return kilometer / longitudeToKilometer(1.0f, latitude);
    }

    /**
     * Converts a difference in longitude to a difference in meters
     */
    public static double longitudeToMeter(double longitude, double latitude) {
        return longitudeToKilometer(longitude, latitude) * kilometerToMeter;
    }

    /**
     * Converts a difference in meters to a difference in longitude
     */
    public static double meterToLongitude(double meter, double latitude) {
        return meter / longitudeToMeter(1.0f, latitude);
    }

    /**
     * Calculates the difference from the start position to the end position (in meters)
     *
     * @param start the start position
     * @param end the end position
     * @return the distance in meters
     */
    public static double calculateDistance(Point start, Point end) {
        return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude);
    }

    /**
     * Calculates the difference from the start position to the end position (in meters)
     *
     * @param startLat the latitude of the start position
     * @param startLgt the longitude of the start position
     * @param endLat the latitude of the end position
     * @param endLgt the longitude of the end position
     * @return Distance in meters
     */
    public static double calculateDistance(double startLat, double startLgt, double endLat, double endLgt) {
        float[] results = new float[3];
        Location.distanceBetween(startLat, startLgt, endLat, endLgt, results);
        return results[0];
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
}