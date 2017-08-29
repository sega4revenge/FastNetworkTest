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
            jsonObject.put("userid", "59939e629db5484486d1e567")
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

                        Log.d(TAG, response!!.user!!.email)
                        Log.d(TAG, response!!.user!!.listproduct!!.size.toString())
                        if(response!!.user!!.listproduct!!.size!=0)
                        {
                            profileview.getListProduct(response!!.user!!.listproduct!!,response!!.user!!)
                        }else{
                            profileview.getUser(response!!.user!!)
                        }

                    }


                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                Log.d(TAG, "onError errorCode : " + anError.errorCode)
                                Log.d(TAG, "onError errorBody : " + anError.errorBody)
                                Log.d(TAG, "onError errorDetail : " + anError.errorDetail)
                                if(anError.errorCode != 404)
                                {  //searchview.setErrorMessage(anError.errorBody.get(0).toString())

                                }else{
                                   // searchview.setMessagerNotFound()
                                }
                            } else {
                                Log.d(TAG, "onError errorDetail : " + anError.errorDetail)

                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + e.message)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

}