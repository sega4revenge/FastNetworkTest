package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.Constants

class ForgotPwPresenter(view : ForgotPwView) {

    internal var mForgotPwView: ForgotPwView = view
    var forgot = "FORGOTPW"
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    fun onCreate() {
        // initialization
    }
    private fun getObservable_forgot(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(forgot)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(forgot, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(forgot, " bytesSent : " + bytesSent)
                    Log.d(forgot, " bytesReceived : " + bytesReceived)
                    Log.d(forgot, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_forgot(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(forgot, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(forgot, "onError errorCode : " + e.errorCode)
                        Log.d(forgot, "onError errorBody : " + e.errorBody)
                        Log.d(forgot, "onError errorDetail : " + e.errorDetail)
                        mForgotPwView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),0)
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(forgot, "onError errorDetail : " + e.errorDetail)
                        mForgotPwView.setErrorMessage(e.errorDetail,0)
                    }
                } else {
                    Log.d(forgot, "onError errorMessage : " + e.message)
                    mForgotPwView.setErrorMessage(e.message!!,0)
                }
            }

            override fun onComplete() {
                mForgotPwView.isFotgotPwSuccessful(true,0)
            }
        }
    }
    fun forgot(phone: String) {
        try {
            jsonObject.put("phone", phone)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_forgot("forgotpassword")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_forgot()))


    }
    private fun getObservable_newpw(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(forgot)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(forgot, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(forgot, " bytesSent : " + bytesSent)
                    Log.d(forgot, " bytesReceived : " + bytesReceived)
                    Log.d(forgot, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_newpw(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(forgot, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(forgot, "onError errorCode : " + e.errorCode)
                        Log.d(forgot, "onError errorBody : " + e.errorBody)
                        Log.d(forgot, "onError errorDetail : " + e.errorDetail)
                        mForgotPwView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"),1)
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(forgot, "onError errorDetail : " + e.errorDetail)
                        mForgotPwView.setErrorMessage(e.errorDetail,1)
                    }
                } else {
                    Log.d(forgot, "onError errorMessage : " + e.message)
                    mForgotPwView.setErrorMessage(e.message!!,1)
                }
            }

            override fun onComplete() {
                mForgotPwView.isFotgotPwSuccessful(true,1)
            }
        }
    }
    fun newpw(newpw: String,code : String, email: String) {
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", newpw)
            jsonObject.put("code",code)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_newpw("forgotpassword")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_newpw()))

    }

    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface ForgotPwView {
        fun isFotgotPwSuccessful(isForgotPwSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type : Int)
    }
}