package br.com.mirabilis.schedulesample.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

import br.com.mirabilis.schedulesample.R;
import br.com.mirabilis.schedulesample.api.CouponUtil;
import br.com.mirabilis.schedulesample.api.model.ExistsPromotion;
import br.com.mirabilis.schedulesample.schedule.CouponScheduleManager;
import br.com.mirabilis.schedulesample.schedule.model.Period;
import br.com.mirabilis.schedulesample.util.SharedUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rodrigosimoesrosa
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener { //, OnMapReadyCallback {//, LocationListener {

    private static final int PERMISSION_LOCATION = 9999;
    Button btnAskPermissionLocation;
    Button btnRequest;


    private FusedLocationProviderClient mFusedLocationClient;
    //private MapFragment map;
    //private GoogleMap googleMap;


    //MyPositionManager myPosition;

    //private LocationManager locationManager;

    //TicketLocation ticketLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.mirabilis.schedulesample.R.layout.activity_main);

        btnAskPermissionLocation = (Button) findViewById(R.id.btnAskPermissionLocation);
        btnRequest = (Button) findViewById(R.id.btnRequest);
        //map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //map.getMapAsync(this);

        btnAskPermissionLocation.setOnClickListener(this);
        btnRequest.setOnClickListener(this);

        //ticketLocation = new TicketLocation.Builder(this).requireFine(true).build();

        //myPosition = new MyPositionManager.Builder(this).build();

        if(!SharedUtil.isWasNoticationTriggered(this)){
            Log.i("Coupon","scheduleAlarms - starting ...");
            CouponScheduleManager.scheduleAlarms(getApplicationContext(),
                    new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY },
                    Period.MORNING, Period.AFTERNOON, Period.NIGHT);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAskPermissionLocation:
                askPermissionLocation();
                break;
            case R.id.btnRequest:
                doRequest();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void askPermissionLocation() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION);
            }
        }
    }

    private void doRequest() {
        /*if (TicketLocation.isProviderOn(this, LocationManager.GPS_PROVIDER)) {

        }else{
            Toast.makeText(this,"Habilite o GPS",Toast.LENGTH_LONG).show();
        }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lgt = location.getLongitude();

                                /*LatLng myPosition = new LatLng(lat, lgt);
                                googleMap.addMarker(new MarkerOptions().position(myPosition).title("My Position"));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(18.0f).build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                googleMap.moveCamera(cameraUpdate);*/

                        CouponUtil.checkAvailablePromotionRetrofit19(lat, lgt, new OnCheckAvaiablePromotionRetrofit19());
                    }
                }
            });

            //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 1, this);

            //double lat = ticketLocation.getLatitude();
            //double lgt = ticketLocation.getLongitude();

                /*GeoLocationManager manager = new GeoLocationManager.Builder(this).build();
                Location location = manager.getLastKnownLocation();

                double lat = location.getLatitude();
                double lgt = location.getLongitude();*/

            //CouponUtil.checkAvailablePromotionRetrofit19(lat, lgt, new OnCheckAvaiablePromotionRetrofit19());
        }else{
            Toast.makeText(this,"Permita o acesso a localização",Toast.LENGTH_LONG).show();
        }
    }

    /*@Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
    }*/

    /*
    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lgt = location.getLongitude();

        //if(locationManager != null) locationManager.removeUpdates(this);
        CouponUtil.checkAvailablePromotionRetrofit19(lat, lgt, new OnCheckAvaiablePromotionRetrofit19());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
    */

    private class OnCheckAvaiablePromotionRetrofit19 implements Callback<ExistsPromotion> {
        @Override
        public void success(ExistsPromotion promotion, Response response) {
            if (promotion != null) {
                Log.i("Coupon","Service.recupera cupons: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                if(promotion.getPromotions() > 0){
                    Log.i("Coupon","Service.onSuccess: " + promotion.getPromotions());

                }else{
                    Log.i("Coupon","Service.onSuccess but without coupon: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                }
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.i("Coupon","Service.onFailure " + error.getMessage());
        }
    }

    /*private class OnCheckAvailablePromotion implements Callback<ExistsPromotion> {

        @Override
        public void onResponse(Call<ExistsPromotion> call, Response<ExistsPromotion> response) {
            if(response.isSuccessful()){
                ExistsPromotion promotion = response.body();
                Log.i("Coupon","Service.recupera cupons: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                if(promotion.getPromotions() > 0){
                    Log.i("Coupon","Service.onSuccess: " + promotion.getPromotions());

                }else{
                    Log.i("Coupon","Service.onSuccess but without coupon: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                }
            }else{
                Log.i("Coupon","Service.withoutSuccess");

            }
        }

        @Override
        public void onFailure(Call<ExistsPromotion> call, Throwable t) {
            Log.i("Coupon","Service.onFailure");
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Obrigado por conceder a permissão de localização",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,"Permita o acesso a localização",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
