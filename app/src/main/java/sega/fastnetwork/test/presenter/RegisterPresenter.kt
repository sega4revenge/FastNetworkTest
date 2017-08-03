package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import sega.fastnetwork.test.view.RegisterView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.activity.RegisterActivity
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants

class RegisterPresenter(registerActivity: RegisterActivity) {

    internal var mRegisterView: RegisterView = registerActivity
    var register = "REGISTER"
    fun onCreate() {
        // initialization
    }

    fun register(user: User) {


        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", user.name)
            jsonObject.put("email", user.email)
            jsonObject.put("password", user.password)
            jsonObject.put("tokenfirebase", user.tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "users")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(register, " bytesSent : " + bytesSent)
                    Log.d(register, " bytesReceived : " + bytesReceived)
                    Log.d(register, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    }


                    override fun onComplete() {

                        mRegisterView.isRegisterSuccessful(true)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(register, "onError errorCode : " + anError.errorCode)
                                Log.d(register, "onError errorBody : " + anError.errorBody)
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mRegisterView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mRegisterView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mRegisterView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
}