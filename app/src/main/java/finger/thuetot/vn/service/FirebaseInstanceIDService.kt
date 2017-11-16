package finger.thuetot.vn.service

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by Sega on 26/03/2017.
 */


class FirebaseInstanceIDService : FirebaseInstanceIdService() {


    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)


    }

    companion object {

        private val TAG = "MyFirebaseIIDService"
    }
}
