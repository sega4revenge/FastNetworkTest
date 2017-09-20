package sega.fastnetwork.test.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import sega.fastnetwork.test.MyApplication
import sega.fastnetwork.test.manager.GoogleApiHelper

/**
 * Created by sega4 on 13/09/2017.
 */

class LocationService : Service(), LocationListener {
    internal var intentBroastcast: Intent = Intent(str_receiver)
    internal var mLocationRequest: LocationRequest? = null
    internal var mLastLocation: Location? = null

    override fun onBind(intent: Intent): IBinder? {
        mLocationRequest = intent.getParcelableExtra("locationrequest")
        println("get data" + mLocationRequest.toString())
        return null
    }

    override fun onCreate() {
        Log.d("Service started Getting", "started!!!!!!!!!!")
        super.onCreate()


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLocationRequest = intent?.getParcelableExtra("locationrequest")
        println("get data" + mLocationRequest.toString())
        val context = this
        MyApplication.getGoogleApiHelper()?.setConnectionListener(object : GoogleApiHelper.ConnectionListener {


            override fun onConnectionFailed(connectionResult: ConnectionResult) {

            }

            override fun onConnectionSuspended(i: Int) {

            }

            override fun onConnected(bundle: Bundle?, client: GoogleApiClient?) {
                Log.d("Connected:Getting ", "LOcation!!!!!!!!!!")


                //use if you want location update

                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, context)
                Log.d("LocationUpdatesRequest", "Granted")
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        client)
                if (mLastLocation != null) {
                    intentBroastcast.putExtra("latutide", mLastLocation!!.latitude.toString())
                    intentBroastcast.putExtra("longitude", mLastLocation!!.longitude.toString())
                    println(mLastLocation)
                    sendBroadcast(intentBroastcast)


                } else {
                    Log.d("Failed", "Connection")
                }
            }
        })
        return super.onStartCommand(intent, flags, startId)

    }


    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        intentBroastcast.putExtra("latutide", location.latitude.toString() + "")
        intentBroastcast.putExtra("longitude", location.longitude.toString() + "")
        sendBroadcast(intentBroastcast)


    }

    companion object {
        var str_receiver = "servicetutorial.service.receiver"
    }


}