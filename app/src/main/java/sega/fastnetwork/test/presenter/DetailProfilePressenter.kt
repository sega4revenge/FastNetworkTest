package sega.fastnetwork.test.presenter

import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.ResponseListProduct
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.DetailProfileInterface
import sega.fastnetwork.test.view.DetailProfileView

/**
 * Created by VinhNguyen on 8/23/2017.
 */
class DetailProfilePressenter(profileView : DetailProfileView) : DetailProfileInterface {
    private val  profileview = profileView
    private val TAG :String = "SearchTag"


    override fun ConnectHttp(id : String) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("userid",id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "getfullprofile")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(TAG, " bytesSent : " + bytesSent)
                    Log.d(TAG, " bytesReceived : " + bytesReceived)
                    Log.d(TAG, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(ResponseListProduct::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseListProduct> {
                    override fun onNext(response: ResponseListProduct?) {

//                        Log.e("Responseeeee", response.toString())
//                        Log.e("Responseeeee2", response!!.user!!.listproduct!!.size.toString())

                            profileview.getUser(response!!.user!!)

                    }


                    override fun onComplete() {


                    }
                    override fun onError(e: Throwable) {
                        if (e is ANError) {


                            Log.d(TAG, "onError errorCode : " + e.errorCode)
                            Log.d(TAG, "onError errorBody : " + e.errorBody)
                            Log.d(TAG, e.errorDetail + " : " + e.message)
                            profileview.setErrorMessage(e.errorDetail)

                        } else {
                            Log.d(TAG, "onError errorMessage : " + e.message)
                            profileview.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

}