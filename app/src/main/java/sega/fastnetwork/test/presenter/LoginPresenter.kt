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
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants

class LoginPresenter(view: LoginView) {

    internal var mLoginView: LoginView = view
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


                            Log.d(login, "onError errorCode : " + e.errorCode)
                            Log.d(login, "onError errorBody : " + e.errorBody)
                            Log.d(login, e.errorDetail + " : " + e.message)
                            mLoginView.isLoginSuccessful(false)

                        } else {
                            Log.d(login, "onError errorMessage : " + e.message)
                            mLoginView.isLoginSuccessful(false)
                        }
                    }
//                    override fun onError(e: Throwable) {
//                        if (e is ANError) {
//                            if (e.errorCode != 0) {
//                                // received ANError from server
//                                // error.getErrorCode() - the ANError code from server
//                                // error.getErrorBody() - the ANError body from server
//                                // error.getErrorDetail() - just a ANError detail
//                                Log.d(login, "onError errorCode : " + e.errorCode)
//                                Log.d(login, "onError errorBody : " + e.errorBody)
//                                Log.d(login, "onError errorDetail : " + e.errorDetail)
//                                mLoginView.isLoginSuccessful(false)
////                                mLoginView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),3)
//                            } else {
//                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
//                                Log.d(login, "onError errorDetail : " + e.errorDetail)
//                                mLoginView.isLoginSuccessful(false)
////                                mLoginView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),3)
//                            }
//                        } else {
//                            Log.d(login, "onError errorMessage : " + e.message)
//                            mLoginView.isLoginSuccessful(false)
//                            mLoginView.setErrorMessage(e.message!!,3)
//                        }
//                    }

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
            } else {
                jsonObject.put("name", user.name)
                jsonObject.put("email", user.email)
                    jsonObject.put("type", type)
                jsonObject.put("password", user.password)
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

                        mLoginView.isRegisterSuccessful(true, type)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(register, "onError errorCode : " + e.errorCode)
                                Log.d(register, "onError errorBody : " + e.errorBody)
                                Log.d(register, "onError errorDetail : " + e.errorDetail)
                                mLoginView.isRegisterSuccessful(false, type)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + e.errorDetail)
                                mLoginView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),1)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mLoginView.isRegisterSuccessful(false, type)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    fun register_finish(email: String, code: String) {


        val jsonObject = JSONObject()
        try {

            jsonObject.put("email", email)
            jsonObject.put("code", code)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "registerfinish")
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

                        mLoginView.isRegisterSuccessful(true,3)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(register, "onError errorCode : " + e.errorCode)
                                Log.d(register, "onError errorBody : " + e.errorBody)
                                Log.d(register, "onError errorDetail : " + e.errorDetail)
                                mLoginView.isRegisterSuccessful(false, 2)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + e.errorDetail)
                                mLoginView.isRegisterSuccessful(false, 2)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mLoginView.isRegisterSuccessful(false, 2)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    interface LoginView {

        fun isLoginSuccessful(isLoginSuccessful: Boolean)
        fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type: Int)
        fun getUserDetail(user: User)
    }
}