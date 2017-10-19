package sega.fastnetwork.test.activity

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
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
    var mPage = 1
    var numProgressLoading = 0
    var arrLoadingImage: ArrayList<Int> = ArrayList()
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
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisiblesItems = 0
    var adapter: ChatAdapter? = null
    var ProgressLoadingImage: ChatMessager? = ChatMessager()
    var data: ArrayList<ChatMessager> = ArrayList()
    var mDataMessager: ArrayList<ChatMessager>? = ArrayList()
    var mOtherData: ArrayList<ChatMessager>? = ArrayList()
    var mEndList: ChatMessager? = null
    var mHeadList: ChatMessager? = null
   var  layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mSocket = AppManager.getSocket(application)
        //==============
        ProgressLoadingImage?.email = ""
        ProgressLoadingImage?.name = ""
        ProgressLoadingImage?.message = ""
        ProgressLoadingImage?.photoprofile = ""
        //===============
        var i = intent
        userid = i.getStringExtra("iduser")
        avatar= i.getStringExtra("avatar")

        messageRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        layoutManager?.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager

        adapter = ChatAdapter(mDataMessager,applicationContext,ImageAvatar)
        messageRecyclerView.adapter = adapter

        mDetailUserPresenter = DetailUserPresenter(this)
        mDetailUserPresenter!!.getUserDetail(AppManager.getAppAccountUserId(this))

        buttonMessage.setOnClickListener {
            mSocket!!.emit("sendchat",user?._id!!,userid,mUserFrom,mUserTo,user?.email,user?.name,editTextMessage.text,0)
            editTextMessage.setText("")
        }
        imgback.setOnClickListener(){
            finish()
        }

        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({
            mSocket!!.emit("getData", mUserFrom,mUserTo,user?._id!!,mPage)
        })

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
        mDataMessager?.add(ProgressLoadingImage!!)
        adapter?.notifyItemInserted(mDataMessager?.size!!-1)
        arrLoadingImage.add(mDataMessager?.size!!-1)
        mDetailUserPresenter?.upImage(File(getRealFilePath(this, uriList)),mUserFrom,mUserTo,user?.email!!,user?.name!!,this)
    }


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
            mSocket!!.on("sendchat", getMessage)
            mSocket!!.on("getDataMessage", messData)
            mSocket!!.emit("getData", mUserFrom,mUserTo,user?._id!!,mPage)

        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        mDetailUserPresenter?.cancelRequest()
        mSocket!!.emit("outroom",mUserFrom,mUserTo)
        mSocket?.off("sendchat")
        mSocket?.off("getDataMessage")
    }
    override fun getStatusUpdateImage(mess: String) {


        mSocket!!.emit("sendchat",user?._id!!,userid,mUserFrom,mUserTo,user?.email,user?.name,mess,1)

        Log.d("mess",arrLoadingImage.get(numProgressLoading).toString())
        mDataMessager?.remove(ProgressLoadingImage)
        adapter?.notifyItemChanged(arrLoadingImage.get(numProgressLoading))
        numProgressLoading++
    }
    private val getMessage = Emitter.Listener { args ->
        val data = args[0]  as String
        Log.d("mess",data)
        var Chatmess: ChatMessager? = ChatMessager()
        Chatmess?.email = args[2].toString()
        Chatmess?.name = args[3].toString()
        Chatmess?.message = args[4].toString()
        Chatmess?.photoprofile = args[5].toString()
        mDataMessager?.add(Chatmess!!)
        if(adapter?.itemCount!=null)
        {
            adapter?.notifyItemInserted(mDataMessager?.size!!-1)
            if(adapter!!.itemCount >10)
            {
                runOnUiThread {
                    messageRecyclerView.scrollToPosition((adapter!!.itemCount-1))
                }
            }


        }else{
            adapter?.notifyDataSetChanged()
        }

    }


    private val messData = Emitter.Listener { args ->
        val mdata = args[0]  as JSONArray
        val mType = args[1]  as Int
        var arrUser = args[2]  as JSONArray

        if(mType == 0){
            runOnUiThread {
                imgOff.setImageResource(R.drawable.ic_offline)
            }
        }else{
            runOnUiThread {
                imgOff.setImageResource(R.drawable.ic_online_)
            }
        }
        if(ImageAvatar?.equals(""))
        {
            ImageAvatar = arrUser.getJSONObject(0).getString("photoprofile")
        }
        if(mToolbar?.equals(""))
        {
            mToolbar = arrUser.getJSONObject(0).getString("name")
        }


        Log.d("ttttttttttt",ImageAvatar+"11")
        if(mDataMessager?.size!! > 0){
            mOtherData?.addAll(mDataMessager!!)
            mDataMessager?.clear()
        }


        if(mdata.length()>0) {
            var parse = mdata.getJSONObject(0).getJSONArray("messages")

            for (i in 0..(parse.length() - 1)) {
                var mObject = parse.getJSONObject(i)
                if(!mHeadList?.created_at.equals(mObject.getString("created_at"))) {
                    var Chatmess: ChatMessager? = ChatMessager()
                    Chatmess?.created_at = mObject.getString("created_at")
                    Chatmess?.email = mObject.getString("email")
                    Chatmess?.name = mObject.getString("name")
                    Chatmess?.message = mObject.getString("message")
                    Chatmess?.photoprofile = mObject.getString("photoprofile")
                    mDataMessager?.add(Chatmess!!)
                }else{
                    runOnUiThread(Runnable {
                        swipe_refresh.isEnabled = false
                        swipe_refresh.isRefreshing = true
                    })
                    break
                }
            }

            if(mDataMessager?.size!! > 0){
                if(mEndList != null){
                    if(mDataMessager?.get(mDataMessager?.size!!-1)?.created_at!!.equals(mEndList?.created_at)){
                        mDataMessager?.clear()
                        runOnUiThread(Runnable {
                            swipe_refresh.isEnabled = false
                            swipe_refresh.isRefreshing = true
                        })
                    }
                 }
             }


            if(mDataMessager?.size!!>0){
                pastVisiblesItems = visibleItemCount + mDataMessager?.size!!
                visibleItemCount = mDataMessager?.size!!
                mEndList = mDataMessager?.get(mDataMessager?.size!!-1)
                mHeadList =  mDataMessager?.get(0)
            }


            if(mOtherData?.size!!>0)
            {
                mDataMessager?.addAll(mOtherData!!)
            }

            //   updateData()
            runOnUiThread(Runnable {
                adapter?.notifyDataSetChanged()
                if(mOtherData?.size!!>0)
                {
                  //  messageRecyclerView?.smoothScrollToPosition(pastVisiblesItems)
                    messageRecyclerView?.scrollToPosition(pastVisiblesItems-3)
                    mOtherData?.clear()
                }
                txttitle.text = mToolbar
                progressBar.visibility = View.GONE
                mPage++
                swipe_refresh.isRefreshing = false
            })
        }else{

            runOnUiThread(Runnable {
                progressBar.visibility = View.GONE
                txttitle.text = mToolbar
                swipe_refresh.isEnabled = false

            })

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
