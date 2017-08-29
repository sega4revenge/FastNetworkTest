package sega.fastnetwork.test.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.CommentActivity
import sega.fastnetwork.test.activity.MainActivity
import sega.fastnetwork.test.activity.ProductDetailActivity
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.util.Constants

/**
 * Created by Sega on 26/03/2017.
 */


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("OKKKOOKOKKO", "Productid: "+remoteMessage?.data?.get("productid").toString() + " useridproduct: " +remoteMessage?.data?.get("useridproduct").toString() + " useridcmt: " + remoteMessage?.data?.get("useridcmt").toString())
        showNotification(remoteMessage?.data?.get("productid").toString(),remoteMessage?.data?.get("useridproduct").toString(),remoteMessage?.data?.get("useridcmt").toString())
    }


    private fun showNotification(productid : String, useridproduct: String, useridcmt : String) {

        if(AppManager.getAppAccountUserId(application) != useridcmt && useridproduct == AppManager.getAppAccountUserId(application))  {
            val typenotification = 2
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notif: Notification? = null
            val intent = Intent(this@FirebaseMessagingService, ProductDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(Constants.product_ID,productid)
//            intent.putExtra("comment", false)
            val pendingIntent = PendingIntent.getActivity(this, 100, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            notif = Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("RENT")
                    .setContentText("Co nguoi binh luan san pham cua ban")
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.RED, 3000, 3000)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.mipmap.home_icon)
                    .build()

            notif!!.flags = notif.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(typenotification, notif)
        }

//            val a = Intent()
//            a.action = "appendChatScreenMsg"
//            a.putExtra("reload", true)
//            this.sendBroadcast(a)

            val x = Intent()
            x.action = "commmentactivity"
            x.putExtra("reload", true)
            this.sendBroadcast(x)

    }


}
