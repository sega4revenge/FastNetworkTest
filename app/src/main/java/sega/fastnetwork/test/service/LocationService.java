package sega.fastnetwork.test.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import sega.fastnetwork.test.MyApplication;
import sega.fastnetwork.test.manager.GoogleApiHelper;

/**
 * Created by sega4 on 13/09/2017.
 */

public class LocationService extends Service implements LocationListener {

    Intent intent;
    LocationRequest mLocationRequest;

    Location mLastLocation;
    String lat, lon;
    public static String str_receiver = "servicetutorial.service.receiver";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Service started Getting", "started!!!!!!!!!!");

        super.onCreate();

        intent = new Intent(str_receiver);
        final LocationService context = this;
        MyApplication.Companion.getGoogleApiHelper().setConnectionListener(new GoogleApiHelper.ConnectionListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnected(Bundle bundle, GoogleApiClient googleApiClient) {
                Log.d("Connected:Getting ", "LOcation!!!!!!!!!!");
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(30000);
                mLocationRequest.setFastestInterval(10000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                //use if you want location update

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, context);
                Log.d("LocationUpdatesRequest", "Granted");
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        googleApiClient);
                if (mLastLocation != null) {
                    lat = String.valueOf(mLastLocation.getLatitude());
                    lon = String.valueOf(mLastLocation.getLongitude());

                    intent.putExtra("latutide", mLastLocation.getLatitude() + "");
                    intent.putExtra("longitude", mLastLocation.getLongitude() + "");
                    sendBroadcast(intent);

                    Log.d("Latitue!!!!", lat);
                    Log.d("Longitude!!!!", lon);

                } else {
                    Log.d("Failed", "Connection");
                    //Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

/*@Override
public void onConnected(@Nullable Bundle bundle) {

}*/


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        Log.d("changed", "after 30");
        intent.putExtra("latutide", lat + "");
        intent.putExtra("longitude", lon + "");
        sendBroadcast(intent);


    }


}