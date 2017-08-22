package sega.fastnetwork.test.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.R
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailUserPresenter

class ChatActivity : AppCompatActivity(),DetailUserPresenter.DetailUserView {

    var user : User? = null
    private var mSocket : Socket?=null
    private var mDetailUserPresenter: DetailUserPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mSocket = AppManager.getSocket(application)

        mDetailUserPresenter = DetailUserPresenter(this)
        mDetailUserPresenter!!.getUserDetail(AppManager.getAppAccountUserId(this))
        mSocket!!.on("login", onLogin)

        buttonMessage.setOnClickListener {

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
            mSocket!!.emit("add user", user!!.name)
        }
    }
    private val onLogin = Emitter.Listener { args ->
        val data = args[0] as JSONObject

        val numUsers: Int
        try {
            numUsers = data.getInt("numUsers")
        } catch (e: JSONException) {
            return@Listener
        }
        System.out.println(numUsers)
        val intent = Intent()


    }
}
