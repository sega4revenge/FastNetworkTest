package sega.fastnetwork.test.service

import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Sega on 26/03/2017.
 */


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        showNotification()
        println(remoteMessage!!.data["message"])

    }

    private fun showNotification() {}

}
