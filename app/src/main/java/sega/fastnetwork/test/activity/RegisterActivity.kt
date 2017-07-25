package sega.fastnetwork.test.activity

import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.util.Validation.validateEmail
import sega.fastnetwork.test.util.Validation.validateFields
import sega.fastnetwork.test.R
import sega.fastnetwork.test.util.Utils

/**
 * Created by sega4 on 23/05/2017.
 */

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btnconfirm!!.setOnClickListener { register() }

    }


    private fun register() {

        setError()


        var err = 0

        if (!validateFields(edtname!!.text.toString())) {

            err++
            edtname!!.error = "Name should not be empty !"
        }

        if (!validateEmail(edtemail!!.text.toString())) {

            err++
            edtemail!!.error = "Email should be valid !"
        }

        if (!validateFields(edtpass!!.text.toString())) {

            err++
            edtpass!!.error = "Password should not be empty !"
        }
        if (edtpass!!.text.toString() != edtrepass!!.text.toString()) {

            err++
            edtrepass!!.error = "Password do not match!"

        }
        if (err == 0) {


            registerProcess()

        } else {

            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun setError() {

        edtname!!.error = null
        edtemail!!.error = null
        edtpass!!.error = null
        edtrepass!!.error = null
    }

    private fun registerProcess() {

        val tokenfirebase = FirebaseInstanceId.getInstance().token
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", edtname.text)
            jsonObject.put("email", edtemail.text)
            jsonObject.put("password", edtpass.text)
            jsonObject.put("tokenfirebase", tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "users")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(localClassName, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(localClassName, " bytesSent : " + bytesSent)
                    Log.d(localClassName, " bytesReceived : " + bytesReceived)
                    Log.d(localClassName, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(localClassName, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    }


                    override fun onComplete() {
                        showSnackBarMessage("Success")
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(localClassName, e)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }


    private fun showSnackBarMessage(message: String?) {


        Snackbar.make(findViewById(R.id.root_register), message!!, Snackbar.LENGTH_SHORT).show()

    }


    public override fun onDestroy() {
        super.onDestroy()

    }
}