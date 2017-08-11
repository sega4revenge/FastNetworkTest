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
import sega.fastnetwork.test.activity.AddActivity
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.AddView


/**
 * Created by sega4 on 27/07/2017.
 */

class AddPresenter(addActivity: AddActivity) {
    internal var mAddView: AddView = addActivity
  
    var createproduct = "CREATEPRODUCT"

    fun createProduct(userid: String, productname : String, price : String, time: String, number : String, category: String, address: String, description: String, type: Int){

        val jsonObject = JSONObject()
        try {
            jsonObject.put("user", userid)
            jsonObject.put("productname", productname)
            jsonObject.put("price", price)
            jsonObject.put("time",time)
            jsonObject.put("number", number)
            jsonObject.put("category",category)
            jsonObject.put("address",address)
            jsonObject.put("description", description)
            jsonObject.put("type", type)
            Log.e("AAAAA",userid + " " + productname+ " " + price + " " + time  + " " + number  + " " + category  + " " + address+ " " + description+ " "+ type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "createproduct")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(createproduct, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(createproduct, " bytesSent : " + bytesSent)
                    Log.d(createproduct, " bytesReceived : " + bytesReceived)
                    Log.d(createproduct, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(createproduct, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                        mAddView.isCreateSuccess(true, response!!.product!!._id!!, response.product!!.type!!)
                    }


                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(createproduct, "onError errorCode : " + anError.errorCode)
                                Log.d(createproduct, "onError errorBody : " + anError.errorBody)
                                Log.d(createproduct, "onError errorDetail : " + anError.errorDetail)
                                mAddView.isCreateSuccess(false,"","")
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(createproduct, "onError errorDetail : " + anError.errorDetail)
                                mAddView.isCreateSuccess(false,"","")
                            }
                        } else {
                            Log.d(createproduct, "onError errorMessage : " + e.message)
                            mAddView.isCreateSuccess(false,"","")
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
}
