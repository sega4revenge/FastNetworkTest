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

class VerifyEmailPresenter(view : VerifyEmailView) {

    internal var mVerifyEmailView: VerifyEmailView = view
    var verify = "VerifyEmail"
    var verify_finish = "Finish_VerifyEmail"

    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    fun onCreate() {
        // initialization
    }
    private fun getObservable_verify(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(verify)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(verify, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(verify, " bytesSent : " + bytesSent)
                    Log.d(verify, " bytesReceived : " + bytesReceived)
                    Log.d(verify, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_verify(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(verify, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

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

            override fun onComplete() {
                mVerifyEmailView.isVerifyEmailSuccessful(true,0)

            }
        }
    }
    fun verify(email: String) {
        try {
            jsonObject.put("email", email)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_verify("verifyemail")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_verify()))
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private fun getObservable_finish(typesearch: String): Observable<Response> {
    return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
            .setTag(verify_finish)
            .addJSONObjectBody(jsonObject)
            .build()

            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(verify_finish, " timeTakenInMillis : " + timeTakenInMillis)
                Log.d(verify_finish, " bytesSent : " + bytesSent)
                Log.d(verify_finish, " bytesReceived : " + bytesReceived)
                Log.d(verify_finish, " isFromCache : " + isFromCache)
            }

            .getObjectObservable(Response::class.java)
}
    private fun getDisposableObserver_finish(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(verify, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

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

            override fun onComplete() {
                mVerifyEmailView.isVerifyEmailSuccessful(true,1)

            }
        }
    }
    fun finish(newpw: String,code : String, email: String) {
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", newpw)
            jsonObject.put("code",code)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_finish("forgotpassword")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_finish()))
    }
    fun cancelRequest() {
        disposables.clear()
    }
    interface VerifyEmailView {

        fun isVerifyEmailSuccessful(isVerifyEmailSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type : Int)
    }
}