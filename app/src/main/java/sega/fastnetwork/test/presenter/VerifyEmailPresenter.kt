package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.Constants

class VerifyEmailPresenter(view : VerifyEmailView) {

    internal var mVerifyEmailView: VerifyEmailView = view
    var verify = "VerifyEmail"
    fun onCreate() {
        // initialization
    }

    fun verify(email: String) {

        Log.e(verify, email)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "verifyemail")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(verify, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(verify, " bytesSent : " + bytesSent)
                    Log.d(verify, " bytesReceived : " + bytesReceived)
                    Log.d(verify, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(verify, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//                        mVerifyEmailView.isVerifyEmailSuccessful(true,0)

                    }


                    override fun onComplete() {

                        mVerifyEmailView.isVerifyEmailSuccessful(true,0)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(verify, "onError errorCode : " + e.errorCode)
                                Log.d(verify, "onError errorBody : " + e.errorBody)
                                Log.d(verify, "onError errorDetail : " + e.errorDetail)
                                mVerifyEmailView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),0)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(verify, "onError errorDetail : " + e.errorDetail)
                                mVerifyEmailView.setErrorMessage(e.errorDetail,0)
                            }
                        } else {
                            Log.d(verify, "onError errorMessage : " + e.message)
                            mVerifyEmailView.setErrorMessage(e.message!!,0)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    fun finish(newpw: String,code : String, email: String) {

        Log.e(verify, newpw + " " + email)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", newpw)
            jsonObject.put("code",code)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "forgotpassword")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(verify, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(verify, " bytesSent : " + bytesSent)
                    Log.d(verify, " bytesReceived : " + bytesReceived)
                    Log.d(verify, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(verify, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    }


                    override fun onComplete() {
                        mVerifyEmailView.isVerifyEmailSuccessful(true,1)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(verify, "onError errorCode : " + e.errorCode)
                                Log.d(verify, "onError errorBody : " + e.errorBody)
                                Log.d(verify, "onError errorDetail : " + e.errorDetail)
                                mVerifyEmailView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),1)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(verify, "onError errorDetail : " + e.errorDetail)
                                mVerifyEmailView.setErrorMessage(e.errorDetail,1)
                            }
                        } else {
                            Log.d(verify, "onError errorMessage : " + e.message)
                            mVerifyEmailView.setErrorMessage(e.message!!,1)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    interface VerifyEmailView {

        fun isVerifyEmailSuccessful(isVerifyEmailSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type : Int)
    }
}