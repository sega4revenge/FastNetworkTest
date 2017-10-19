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


/**
 * Created by sega4 on 27/07/2017.
 */

class AddPresenter(view: AddView) {
    internal var mAddView: AddView = view
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()

    var addproduct = "addproduct"

    var createproduct = "CREATEPRODUCT"
    private fun getObservable(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag("Comment")
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(addproduct, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(addproduct, " bytesSent : " + bytesSent)
                    Log.d(addproduct, " bytesReceived : " + bytesReceived)
                    Log.d(addproduct, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(createproduct, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                mAddView.isCreateSuccess(true, response.product?._id!!, response.product?.type!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        Log.d(createproduct, "onError errorCode : " + e.errorCode)
                        Log.d(createproduct, "onError errorBody : " + e.errorBody)
                        Log.d(createproduct, "onError errorDetail : " + e.errorDetail)
                        mAddView.isCreateSuccess(false,"","")
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(createproduct, "onError errorDetail : " + e.errorDetail)
                        mAddView.isCreateSuccess(false,"","")
                    }
                } else {
                    Log.d(createproduct, "onError errorMessage : " + e.message)
                    mAddView.isCreateSuccess(false,"","")
                }
            }

            override fun onComplete() {

            }
        }
    }
    fun createProduct(userid: String, productname : String, price : String, time: String, number : String, category: String, address: String, description: String,lat: String, lot: String, type: Int){

        try {
            jsonObject.put("user", userid)
            jsonObject.put("productname", productname)
            jsonObject.put("price", price)
            jsonObject.put("time",time)
            jsonObject.put("number", number)
            jsonObject.put("category",category)
            jsonObject.put("address",address)
            jsonObject.put("description", description)
            jsonObject.put("lat",lat)
            jsonObject.put("lot",lot)
            jsonObject.put("type", type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("createproduct")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))
    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface AddView {
        fun isCreateSuccess(success : Boolean,productid : String, type: String)

    }
}
