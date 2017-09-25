package sega.fastnetwork.test.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.ChatActivity
import sega.fastnetwork.test.activity.ProductDetailActivity
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.util.Constants
import java.io.IOException
import java.net.URL


/**
 * Created by Sega on 26/03/2017.
 */


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    var image: Bitmap? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("OKKKOOKOKKO", remoteMessage?.data?.toString())

        if(remoteMessage?.data?.get("productid")!= null){
            Log.e("OKKKOOKOKKO", "Productid: "+remoteMessage?.data?.get("productid").toString() + " useridproduct: " +remoteMessage?.data?.get("useridproduct").toString() + " useridcmt: " + remoteMessage?.data?.get("useridcmt").toString())
            showNotification(remoteMessage?.data?.get("productid").toString(),remoteMessage?.data?.get("useridproduct").toString(),remoteMessage?.data?.get("useridcmt").toString())
        }else{
            showNotificationChat(remoteMessage?.data?.get("userto").toString(),remoteMessage?.data?.get("name").toString(),remoteMessage?.data?.get("messager").toString(),remoteMessage?.data?.get("avata").toString(),remoteMessage?.data?.get("userfrom").toString())
        }
    }
    fun avatacmt(link: String): String?{
        var result = ""
        if(!link.startsWith("http")){
            result = Constants.IMAGE_URL+link
        }
        return result
    }
    private fun showNotificationChat(userto : String,name: String,messager: String, avata: String, userfrom : String) {
    //    Log.e("OKKKOOKOKKO",userfrom )
       var avata2 = avatacmt(avata)
        if (avata2.equals("") || avata2 == null) {
            image = BitmapFactory.decodeResource(applicationContext.resources,
                    R.mipmap.home_icon)
        } else {
            try {
                val url = URL(avata2)
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                System.out.println(e)
            }

        }
            val typenotification = 2
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notif: Notification? = null
            val intent = Intent(this@FirebaseMessagingService, ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("iduser",userfrom)
            val pendingIntent = PendingIntent.getActivity(this, 100, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            notif = Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(name)
                    .setContentText(messager)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.RED, 3000, 3000)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.mipmap.home_icon)
                    .setLargeIcon(getCroppedBitmap(image!!))
                    .build()

            notif!!.flags = notif.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(typenotification, notif)


        val x = Intent()
        x.action = "commmentactivity"
        x.putExtra("reload", true)
        this.sendBroadcast(x)

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

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = 0xff424242.toInt()
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }
}
