package finger.thuetot.vn.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.ChatActivity
import finger.thuetot.vn.activity.ProductDetailActivity
import finger.thuetot.vn.activity.ReplyCommentActivity
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.util.Constants
import java.io.IOException
import java.net.URL


/**
 * Created by Sega on 26/03/2017.
 */


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    var image: Bitmap? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("firebase: messager ", remoteMessage?.data?.toString())

        if(remoteMessage?.data?.get("productid")!= null){
            showNotification(remoteMessage?.data?.get("productid").toString(),remoteMessage?.data?.get("useridproduct").toString(),remoteMessage?.data?.get("useridcmt").toString())
        }else{
            if(remoteMessage?.data?.get("commentid")!= null)
            {
                showNotificationReply(remoteMessage?.data?.get("commentid").toString(),remoteMessage?.data?.get("username").toString(),remoteMessage?.data?.get("content").toString(),remoteMessage?.data?.get("userreply").toString(),remoteMessage?.data?.get("userown").toString(),remoteMessage?.data?.get("msg").toString())
            }
            else {
                showNotificationChat(remoteMessage?.data?.get("userto").toString(),remoteMessage?.data?.get("name").toString(),remoteMessage?.data?.get("messager").toString(),remoteMessage?.data?.get("avata").toString(),remoteMessage?.data?.get("userfrom").toString(),remoteMessage?.data?.get("idsend").toString())
            }
        }
    }
    fun avatacmt(link: String): String?{
        var result = ""
        if(!link.startsWith("http")){
            result = Constants.IMAGE_URL+link
        }else{
            result = link
        }
        return result
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.smalliconwhite else R.drawable.small_icon
    }
    private fun showNotificationChat(userto : String,name: String,messager: String, avata: String, userfrom : String, idsend : String) {
            var usersendid = ""
            if(userto.equals(AppManager.getAppAccountUserId(this)))
            {
                usersendid = userfrom
            }else{
                usersendid = userto
            }
            val typenotification = idsend.hashCode()
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notif: Notification? = null
            val intent = Intent(this@FirebaseMessagingService, ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("iduser",usersendid)
            intent.putExtra("avatar","")


            if (avata.equals("") || avata == null) {
                val pendingIntent = PendingIntent.getActivity(this, 100, intent,
                        PendingIntent.FLAG_ONE_SHOT)
                notif = Notification.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(name)
                        .setContentText(messager)
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                        .setLights(Color.RED, 3000, 3000)
                        .setSound(defaultSoundUri)
                        .setSmallIcon(getNotificationIcon())
                        .setColor(resources.getColor(R.color.colorAccent))
                        .build()

                notif!!.flags = notif.flags or Notification.FLAG_AUTO_CANCEL
                notificationManager.notify(typenotification, notif)


                val x = Intent()
                x.action = "commmentactivity"
                x.putExtra("reload", true)
                this.sendBroadcast(x)

            } else {
                var avata2 = avatacmt(avata)
                try {
                    val url = URL(avata2)
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val pendingIntent = PendingIntent.getActivity(this, 100, intent,
                            PendingIntent.FLAG_ONE_SHOT)
                    notif = Notification.Builder(this)
                            .setContentIntent(pendingIntent)
                            .setContentTitle(name)
                            .setContentText(messager)
                            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                            .setLights(Color.RED, 3000, 3000)
                            .setSound(defaultSoundUri)
                            .setSmallIcon(getNotificationIcon())
                            .setLargeIcon(getCroppedBitmap(image))
                            .setColor(resources.getColor(R.color.colorAccent))
                            .build()

                    notif!!.flags = notif.flags or Notification.FLAG_AUTO_CANCEL
                    notificationManager.notify(typenotification, notif)


                    val x = Intent()
                    x.action = "commmentactivity"
                    x.putExtra("reload", true)
                    this.sendBroadcast(x)

                } catch (e: IOException) {
                    Log.d("firebase: messager: ",e.toString())
                    System.out.println(e)
                }

            }

    }
    private fun showNotificationReply(commentid : String, username: String, content : String, userreply : String, userown : String, msg : String) {
        try{
                if(userreply != AppManager.getAppAccountUserId(this))
                {
                    val typenotification = 2
                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    var notif: Notification? = null
                    val intent = Intent(this@FirebaseMessagingService, ReplyCommentActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(Constants.comment_ID,commentid)
                    intent.putExtra(Constants.seller_name,username)
                    intent.putExtra(Constants.product_NAME,content)

//            intent.putExtra("comment", false)
                    val pendingIntent = PendingIntent.getActivity(this, 100, intent,
                            PendingIntent.FLAG_ONE_SHOT)
                    notif = Notification.Builder(this)
                            .setContentIntent(pendingIntent)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(msg)
                            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                            .setLights(Color.RED, 3000, 3000)
                            .setSound(defaultSoundUri)
                            .setSmallIcon(R.drawable.small_icon)
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
            this.sendBroadcast(x)}
        catch (e : Exception){
            Log.e("Noti", e.toString())
        }


    }
    private fun showNotification(productid : String, useridproduct: String, useridcmt : String) {
        try{
            if(AppManager.getAppAccountUserId(application) != useridcmt && useridproduct == AppManager.getAppAccountUserId(application))  {
            val typenotification = 2
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notif: Notification? = null
            val intent = Intent(this@FirebaseMessagingService, ProductDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(Constants.product_ID,productid)
            intent.putExtra(Constants.seller_ID,useridproduct)

//            intent.putExtra("comment", false)
            val pendingIntent = PendingIntent.getActivity(this, 100, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            notif = Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.txt_havecm))
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.RED, 3000, 3000)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.small_icon)
                    .setColor(resources.getColor(R.color.colorAccent))
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
            this.sendBroadcast(x)}
        catch (e : Exception){
            Log.e("Noti", e.toString())
        }
    }

    fun getCroppedBitmap(bitmap: Bitmap?): Bitmap {

        val output = Bitmap.createBitmap(bitmap?.width!!,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = 0xff424242.toInt()
        val paint = Paint()
        val rect = Rect(0, 0, bitmap?.width!!, bitmap.height)

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
