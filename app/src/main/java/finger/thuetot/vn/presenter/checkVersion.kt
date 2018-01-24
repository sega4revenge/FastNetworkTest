package finger.thuetot.vn.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by VinhNguyen on 1/16/2018.
 */
class checkVersion(view : checkVersion.IntroView) {

    internal var mcheckVersion: IntroView = view
    private val disposables = CompositeDisposable()
    private val jsonObject = JSONObject()
    var productdetail = "GET VER FROM SERVER"


    fun getVersionServer() {

        disposables.add(getObservable("getversion")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))

    }
    fun checkAndroidId(userid: String,androidid: String) {
        try {
            jsonObject.put("iduser", userid)
            jsonObject.put("androidid", androidid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("addAndroidId")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver2()))

    }
    private fun getObservable(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(productdetail)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(productdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(productdetail, " bytesSent : " + bytesSent)
                    Log.d(productdetail, " bytesReceived : " + bytesReceived)
                    Log.d(productdetail, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver2(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
              //  mcheckVersion.getVersion(response?.message!!)
            }

            override fun onError(e: Throwable) {
                if (e is ANError) {
                    Log.d(productdetail, "onError errorCode : " + e.errorCode)
                    Log.d(productdetail, "onError errorBody : " + e.errorBody)
                    Log.d(productdetail, e.errorDetail + " : " + e.message)
                    if(e.errorBody.equals("200")){
                     //   mcheckVersion.getStatusCheckAndroidId()
                    }

                } else {
                    Log.d(productdetail, "onError errorMessage : " + e.message)

                }
            }

            override fun onComplete() {
            }
        }
    }
    private fun getDisposableObserver(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                mcheckVersion.getVersion(response?.message!!)
            }

            override fun onError(e: Throwable) {
                if (e is ANError) {
                    Log.d(productdetail, "onError errorCode : " + e.errorCode)
                    Log.d(productdetail, "onError errorBody : " + e.errorBody)
                    Log.d(productdetail, e.errorDetail + " : " + e.message)


                } else {
                    Log.d(productdetail, "onError errorMessage : " + e.message)

                }
            }

            override fun onComplete() {
            }
        }
    }

    interface IntroView {
        fun getVersion(ver: String)
      //  fun getStatusCheckAndroidId()
    }
}