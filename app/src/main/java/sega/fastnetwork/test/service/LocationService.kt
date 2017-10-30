package sega.fastnetwork.test.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
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

            @SuppressLint("MissingPermission")
            override fun onConnected(bundle: Bundle?, client: GoogleApiClient?) {
                Log.d("Connected:Getting ", "LOcation!!!!!!!!!!")


                //use if you want location update
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        client)
                if (mLastLocation != null) {
                    intentBroastcast.putExtra("latutide", mLastLocation!!.latitude.toString())
                    intentBroastcast.putExtra("longitude", mLastLocation!!.longitude.toString())
                    intentBroastcast.putExtra("first", true)
                    println(mLastLocation)
                    sendBroadcast(intentBroastcast)


                } else {
                    Log.d("Failed", "Connection")
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, context)
                Log.d("LocationUpdatesRequest", "Granted")

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