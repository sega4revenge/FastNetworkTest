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

class ForgotPwPresenter(view : ForgotPwView) {

    internal var mForgotPwView: ForgotPwView = view
    var forgot = "FORGOTPW"
    fun onCreate() {
        // initialization
    }

    fun forgot(email: String) {

        Log.e(forgot, email)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "forgotpassword")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(forgot, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(forgot, " bytesSent : " + bytesSent)
                    Log.d(forgot, " bytesReceived : " + bytesReceived)
                    Log.d(forgot, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(forgot, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    }


                    override fun onComplete() {

                        mForgotPwView.isFotgotPwSuccessful(true,0)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(forgot, "onError errorCode : " + anError.errorCode)
                                Log.d(forgot, "onError errorBody : " + anError.errorBody)
                                Log.d(forgot, "onError errorDetail : " + anError.errorDetail)
                                mForgotPwView.setErrorMessage(JSONObject(anError.errorBody.toString()).getString("message"),0)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(forgot, "onError errorDetail : " + anError.errorDetail)
                                mForgotPwView.setErrorMessage(anError.errorDetail,0)
                            }
                        } else {
                            Log.d(forgot, "onError errorMessage : " + e.message)
                            mForgotPwView.setErrorMessage(e.message!!,0)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    fun newpw(newpw: String,code : String, email: String) {

        Log.e(forgot, newpw.toString() + " " + email)
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
                    Log.d(forgot, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(forgot, " bytesSent : " + bytesSent)
                    Log.d(forgot, " bytesReceived : " + bytesReceived)
                    Log.d(forgot, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(forgot, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    }


                    override fun onComplete() {
                        mForgotPwView.isFotgotPwSuccessful(true,1)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(forgot, "onError errorCode : " + anError.errorCode)
                                Log.d(forgot, "onError errorBody : " + anError.errorBody)
                                Log.d(forgot, "onError errorDetail : " + anError.errorDetail)
                                mForgotPwView.setErrorMessage(JSONObject(anError.errorBody.toString()).getString("message"),1)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(forgot, "onError errorDetail : " + anError.errorDetail)
                                mForgotPwView.setErrorMessage(anError.errorDetail,1)
                            }
                        } else {
                            Log.d(forgot, "onError errorMessage : " + e.message)
                            mForgotPwView.setErrorMessage(e.message!!,1)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    interface ForgotPwView {

        fun isFotgotPwSuccessful(isForgotPwSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type : Int)
    }
}