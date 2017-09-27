package sega.fastnetwork.test.activity

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.ChatAdapter
import sega.fastnetwork.test.lib.imagepicker.TedBottomPicker
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Chat
import sega.fastnetwork.test.model.ChatMessager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailUserPresenter
import sega.fastnetwork.test.util.Constants
import java.io.File


class ChatActivity : AppCompatActivity(), DetailUserPresenter.DetailUserView {


    var mMessager: ArrayList<Chat> = ArrayList()
    var user: User? = null
    var ImageAvatar = ""
    var userid = ""
    private val filePath: Uri? = null
    private var bitmap: Bitmap? = null
    private var PICK_IMAGE_REQUEST = 199
    private var SELECT_PICTURE = 100
    private var mSocket: Socket? = null
    private var mDetailUserPresenter: DetailUserPresenter? = null
    var mUserFrom = ""
    var mUserTo = ""
    var mToolbar = ""
    var avatar = ""
    var adapter: ChatAdapter? = null
    var data: ArrayList<ChatMessager> = ArrayList()
    var mDataMessager: ArrayList<ChatMessager>? = ArrayList()
   var  layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mSocket = AppManager.getSocket(application)

        var i = intent
        userid = i.getStringExtra("iduser")
        avatar= i.getStringExtra("avatar")
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.server_unreachable)
                .priority(Priority.HIGH)
        Glide.with(this@ChatActivity)
                .load(avatar)
                .thumbnail(0.1f)
                .apply(options)
                .into(imgAva)
        messageRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        layoutManager?.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager

        mDetailUserPresenter = DetailUserPresenter(this)
        mDetailUserPresenter!!.getUserDetail(AppManager.getAppAccountUserId(this))
     //   mSocket!!.on("login", onLogin)
     //   mSocket!!.on("userconnect", onConnect)
        buttonMessage.setOnClickListener {
            mSocket!!.emit("sendchat",userid,mUserFrom,mUserTo,user?.email,user?.name,editTextMessage.text)
        }
        upimage.setOnClickListener(){
            val permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {

                    val bottomSheetDialogFragment = TedBottomPicker.Builder(this@ChatActivity)
                            .setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
                                override fun onImageSelected(uri: Uri) {
                                    showUriList(uri)
                                }

                            })
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setSelectMaxCount(1)
                            .setCompleteButtonText("Done")
                            .setEmptySelectionText("No Select")
                            .create()

                    bottomSheetDialogFragment.show(supportFragmentManager)


                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>) =
                        Toast.makeText(this@ChatActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()


            }

            TedPermission.with(this@ChatActivity)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check()
        }
    }
    private fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }
    private fun showUriList(uriList:Uri) {
        mDetailUserPresenter?.upImage(File(getRealFilePath(this, uriList)),mUserFrom,mUserTo,user?.email!!,user?.name!!,this)
       // mSocket!!.emit("sendchatimage",mUserFrom,mUserTo,user?.email,user?.name, File(getRealFilePath(this, uriList)))
    }

    //    override fun onRequestPermissionsResult(
//            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                init()
//            }
//        }
//    }
    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
    }

    override fun getUserDetail(user: User) {
        this.user = user
    }

    override fun isgetUserDetailSuccess(success: Boolean) {
        if (success) {
            if(user?._id!!.compareTo(userid)>0)
            {
                mUserFrom = user?._id!!
                mUserTo   = userid
            }else if(user?._id!!.compareTo(userid)<0){
                mUserFrom = userid
                mUserTo   = user?._id!!
            }else{
                mUserFrom = userid
                mUserTo   = user?._id!!
            }
            mSocket!!.on("sendchat: "+mUserFrom+" - "+mUserTo, getMessage)
            mSocket!!.on(mUserFrom+" - "+mUserTo, messData)
            mSocket!!.emit("getData", mUserFrom,mUserTo,user?._id!!)

        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()

        mSocket?.off("sendchat: "+mUserFrom+" - "+mUserTo)
        mSocket?.off(mUserFrom+" - "+mUserTo)
    }
    override fun getStatusUpdateImage(mess: ChatMessager) {
        var Chatmess: ChatMessager? = ChatMessager()
        Chatmess?.email = mess.email
        Chatmess?.name = mess.name
        Chatmess?.message = mess.message
        Chatmess?.photoprofile = mess.photoprofile
        mDataMessager?.add(Chatmess!!)
        updateData()
    }
    private val getMessage = Emitter.Listener { args ->
        val data = args[0]  as String
        Log.d("mess",data)
        var Chatmess: ChatMessager? = ChatMessager()
        Chatmess?.email = args[2].toString()
        Chatmess?.name = args[3].toString()
        Chatmess?.message = args[4].toString()
        Chatmess?.photoprofile = ""
        mDataMessager?.add(Chatmess!!)
        updateData()


    }
    private fun updateData(){
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {

                    runOnUiThread {
                        val options = RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.server_unreachable)
                            .priority(Priority.HIGH)
                    Glide.with(this@ChatActivity)
                            .load(avatacmt(ImageAvatar))
                            .thumbnail(0.1f)
                            .apply(options)
                            .into(imgAva)
                        txttitle.text = mToolbar
                        adapter = ChatAdapter(mDataMessager,applicationContext,ImageAvatar)
                        messageRecyclerView.adapter = adapter
                        messageRecyclerView.scrollToPosition((adapter?.itemCount!!)-1)
                    }
                } catch (exception: Exception) {
                   Log.d("Error", exception.toString())
                }

                return null
            }
        }.execute()

    }

    private val messData = Emitter.Listener { args ->
        val mdata = args[0]  as JSONArray
        val mType = args[1]  as Int

        if(mType == 0){
            runOnUiThread {
                imgOff.setImageResource(R.drawable.ic_offline)
            }
        }else{
            runOnUiThread {
                imgOff.setImageResource(R.drawable.ic_online_)
            }
        }
        if(mdata.length()>0){
            val mdata2 = mdata[0] as JSONObject
            if(mdata2.getJSONObject("userfrom").getString("_id").equals(userid))
            {
                ImageAvatar = mdata2.getJSONObject("userfrom").getString("photoprofile")
                mToolbar = mdata2.getJSONObject("userfrom").getString("name")
            }else{
                ImageAvatar = mdata2.getJSONObject("userto").getString("photoprofile")
                mToolbar = mdata2.getJSONObject("userto").getString("name")

            }
            var parse = mdata.getJSONObject(0).getJSONArray("messages")
            for(i in 0..(parse.length()-1)){
                var mObject = parse.getJSONObject(i)
                var Chatmess: ChatMessager? = ChatMessager()
                Chatmess?.email = mObject.getString("email")
                Chatmess?.name =  mObject.getString("name")
                Chatmess?.message =  mObject.getString("message")
                Chatmess?.photoprofile =  mObject.getString("photoprofile")
                mDataMessager?.add(Chatmess!!)
            }

            updateData()

        }else{
            Log.d("Data","nullll cmnr")
        }


    }
    fun avatacmt(link: String): String?{
        var result = ""
        if(!link.startsWith("http")){
            result = Constants.IMAGE_URL+link
        }
        return result
    }
    private val onLogin = Emitter.Listener { args ->
        val data = args[0] as JSONObject

        var numUsers = ""
        try {
            numUsers = data.getString("username")

        } catch (e: JSONException) {
            return@Listener
        }

        val intent = Intent()
        runOnUiThread(Runnable {
            Toast.makeText(applicationContext, numUsers + " đã gia nhập phòng", Toast.LENGTH_LONG).show()
        })

    }
    private val onConnect = Emitter.Listener { args ->
        val data = args[0] as JSONObject

        var numUsers = ""
        var name = ""
        try {
            name = data.getString("username")
            numUsers = data.getString("numUsers")
        } catch (e: JSONException) {
            return@Listener
        }

        val intent = Intent()
        runOnUiThread(Runnable {
            Toast.makeText(applicationContext, name + " đã gia nhập phòng , hiện có" + numUsers + " người trong phòng", Toast.LENGTH_LONG).show()
        })

    }
}
