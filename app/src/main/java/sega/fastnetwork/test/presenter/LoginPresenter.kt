package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.google.firebase.iid.FirebaseInstanceId
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.activity.LoginActivity
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.LoginView

class LoginPresenter(loginActivity: LoginActivity) {

    internal var mLoginView: LoginView = loginActivity
    var login = "LOGIN"
    var register = "REGISTER"
    fun onCreate() {
        // initialization
    }

    fun login(email: String, password: String) {

        val tokenfirebase = FirebaseInstanceId.getInstance().token
        val jsonObject = JSONObject()
        try {

            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("tokenfirebase", tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "authenticate")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(login, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(login, " bytesSent : " + bytesSent)
                    Log.d(login, " bytesReceived : " + bytesReceived)
                    Log.d(login, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(login, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                        mLoginView.getUserDetail(response!!.user!!)
                    }


                    override fun onComplete() {


                        mLoginView.isLoginSuccessful(true)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(login, "onError errorCode : " + anError.errorCode)
                                Log.d(login, "onError errorBody : " + anError.errorBody)
                                Log.d(login, "onError errorDetail : " + anError.errorDetail)
                                mLoginView.isLoginSuccessful(false)
                                mLoginView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(login, "onError errorDetail : " + anError.errorDetail)
                                mLoginView.isLoginSuccessful(false)
                                mLoginView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(login, "onError errorMessage : " + e.message)
                            mLoginView.isLoginSuccessful(false)
                            mLoginView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })

    }

    fun register(user: User, type: Int) {


        val jsonObject = JSONObject()
        try {
            if (type == Constants.FACEBOOK) {
                jsonObject.put("id", user.facebook!!.id)
                jsonObject.put("token", user.facebook!!.token)
                jsonObject.put("name", user.facebook!!.name)
                jsonObject.put("email", user.facebook!!.email)
                jsonObject.put("password", user.password)
                jsonObject.put("photoprofile", user.facebook!!.photoprofile)
                jsonObject.put("type", type)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            } else if (type == Constants.GOOGLE) {
                jsonObject.put("id", user.google!!.id)
                jsonObject.put("token", user.google!!.token)
                jsonObject.put("name", user.google!!.name)
                jsonObject.put("email", user.google!!.email)
                jsonObject.put("password", user.password)
                jsonObject.put("photoprofile", user.google!!.photoprofile)
                jsonObject.put("type", type)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            }
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
                        mLoginView.getUserDetail(response!!.user!!)
                    }


                    override fun onComplete() {

                        mLoginView.isRegisterSuccessful(true,type)
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
                                mLoginView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mLoginView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mLoginView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
}