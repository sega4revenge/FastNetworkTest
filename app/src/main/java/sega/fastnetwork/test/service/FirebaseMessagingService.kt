package sega.fastnetwork.test.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import sega.fastnetwork.test.R

/**
 * Created by Sega on 26/03/2017.
 */


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("OKKKOOKOKKO", remoteMessage?.data?.get("message").toString() + " " + remoteMessage?.data?.get("type").toString())
        showNotification(remoteMessage?.data?.get("message").toString(),remoteMessage?.data?.get("type").toString())
    }


    private fun showNotification(message: String, type : String) {
//            val intent = Intent(this@FirebaseMessagingService, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////            intent.putExtra("noti", false)
//            val pendingIntent = PendingIntent.getActivity(this, 100, intent,
//                    PendingIntent.FLAG_ONE_SHOT)
            //        messenger = "bbbbbbbbbbb";
            val typenotification = 2
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notif: Notification? = null
            notif = Notification.Builder(this)
//                    .setContentIntent(pendingIntent)
                    .setContentTitle("RENT")
                    .setContentText(message)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.RED, 3000, 3000)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.mipmap.home_icon)
                    .build()

            notif!!.flags = notif.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(typenotification, notif)

//            val a = Intent()
//            a.action = "appendChatScreenMsg"
//            a.putExtra("reload", true)
//            this.sendBroadcast(a)

            val x = Intent()
            x.action = "commmentactivity"
            x.putExtra("reload", true)
            this.sendBroadcast(x)

    }

    private fun showNotification() {}

}
