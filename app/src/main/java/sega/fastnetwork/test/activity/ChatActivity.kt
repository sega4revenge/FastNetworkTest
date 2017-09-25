package sega.fastnetwork.test.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.ChatAdapter
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Chat
import sega.fastnetwork.test.model.ChatMessager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailUserPresenter
import java.util.*



class ChatActivity : AppCompatActivity(), DetailUserPresenter.DetailUserView {
    var mMessager: ArrayList<Chat> = ArrayList()
    var user: User? = null
    var userTo: User? = null
    var userid = ""
    private var mSocket: Socket? = null
    private var mDetailUserPresenter: DetailUserPresenter? = null
    var mUserFrom = ""
    var mUserTo = ""
    var adapter: ChatAdapter? = null
    var data: ArrayList<ChatMessager> = ArrayList()
    var mDataMessager: JSONArray? = JSONArray()
   var  layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mSocket = AppManager.getSocket(application)

        var i = intent
        userid = i.getStringExtra("iduser")

        messageRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        layoutManager?.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager

        mDetailUserPresenter = DetailUserPresenter(this)
        mDetailUserPresenter!!.getUserDetail(AppManager.getAppAccountUserId(this))
        mSocket!!.on("login", onLogin)
        mSocket!!.on("userconnect", onConnect)
        buttonMessage.setOnClickListener {
            mSocket!!.emit("sendchat","",mUserFrom,mUserTo,user?.email,user?.name,editTextMessage.text)
        }

    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
    }

    override fun getUserDetail(user: User) {
        this.user = user
    }

    override fun isgetUserDetailSuccess(success: Boolean) {
        if (success) {
            Log.d("Data1111","33333")
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
            Log.d("messs","sendchat: "+mUserFrom+" - "+mUserTo)
            mSocket!!.on("sendchat: "+mUserFrom+" - "+mUserTo, getMessage)
            mSocket!!.on(mUserFrom+" - "+mUserTo, messData)
            mSocket!!.emit("getData", mUserFrom,mUserTo,user?._id!!)

        }
    }

    override fun onResume() {
        super.onResume()
        //if(mSocket!!.connected()){
       //     mSocket?.connect()
       // }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.off("sendchat: "+mUserFrom+" - "+mUserTo)
        mSocket?.off(mUserFrom+" - "+mUserTo)
    }

    private val getMessage = Emitter.Listener { args ->
        val data = args[0]  as String
        Log.d("mess",data)
        var mObject = JSONObject()
        mObject.put("email",args[2])
        mObject.put("name",args[3])
        mObject.put("message",args[4])
        mDataMessager?.put(mObject)
        updateData()


    }
    private fun updateData(){
        Log.d("Data1111","222222")
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {

                    runOnUiThread {
                        adapter = ChatAdapter(mDataMessager,applicationContext,userTo)
                        messageRecyclerView.adapter = adapter
                        messageRecyclerView.scrollToPosition((adapter?.itemCount!!)-1)
                        Log.d("Data1111",mDataMessager?.length().toString())
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

        if(mdata.length()>0){
            val mdata2 = mdata[0] as JSONObject
            if(!mdata2.getJSONObject("userfrom").getString("_id").equals(userid))
            {
                userTo?.photoprofile = mdata2.getJSONObject("userfrom").getString("photoprofile")
            }else{
                userTo?.photoprofile = mdata2.getJSONObject("userto").getString("photoprofile")
            }
            Log.d("Data",mdata2.getString("userfrom").toString())
            mDataMessager = mdata.getJSONObject(0).getJSONArray("messages")
            updateData()

        }else{
            Log.d("Data","nullll cmnr")
        }


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
