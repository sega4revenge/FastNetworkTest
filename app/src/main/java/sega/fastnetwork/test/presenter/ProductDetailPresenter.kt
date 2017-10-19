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

class ProductDetailPresenter(view : ProductDetailPresenter.ProductDetailView) {
    internal var mProductDetailView: ProductDetailView = view
    var productdetail = "PRODUCTDETAIL"
    var saveproduct = "SAVEPRODUCT"

    private val disposables = CompositeDisposable()
    private val jsonObject = JSONObject()
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
    private fun getDisposableObserver(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
//                Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//                Log.e("Size", response!!.comment.toString())
                Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//
                        mProductDetailView.getProductDetail(response)
            }

            override fun onError(e: Throwable) {
                if (e is ANError) {
                            Log.d(productdetail, "onError errorCode : " + e.errorCode)
                            Log.d(productdetail, "onError errorBody : " + e.errorBody)
                            Log.d(productdetail, e.errorDetail + " : " + e.message)
                            mProductDetailView.setErrorMessage(e.errorDetail)

                        } else {
                            Log.d(productdetail, "onError errorMessage : " + e.message)
                            mProductDetailView.setErrorMessage(e.message!!)
                        }
            }

            override fun onComplete() {
            }
        }
    }
    fun getProductDetail(productid : String, userid : String) {
        try {
            jsonObject.put("productid", productid)
            jsonObject.put("userid", userid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("productdetail")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))


    }
///////////////////////////////////////////////////////////////////////////////////////
private fun getObservable_SaveProduct(typesearch: String): Observable<Response> {
    return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
            .setTag(saveproduct)
            .addJSONObjectBody(jsonObject)
            .build()

            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(saveproduct, " timeTakenInMillis : " + timeTakenInMillis)
                Log.d(saveproduct, " bytesSent : " + bytesSent)
                Log.d(saveproduct, " bytesReceived : " + bytesReceived)
                Log.d(saveproduct, " isFromCache : " + isFromCache)
            }

            .getObjectObservable(Response::class.java)
}
    private fun getDisposableObserver_SaveProduct(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                mProductDetailView.getStatusSave(true)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(productdetail, "onError errorCode : " + e.errorCode)
                    Log.d(productdetail, "onError errorBody : " + e.errorBody)
                    Log.d(productdetail, e.errorDetail + " : " + e.message)
                    mProductDetailView.setErrorMessage(e.errorDetail)

                } else {
                    Log.d(productdetail, "onError errorMessage : " + e.message)
                    mProductDetailView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }
    fun SaveProduct(productid : String, userid : String,type: String) {
        try {
            jsonObject.put("productid", productid)
            jsonObject.put("userid", userid)
            jsonObject.put("type", type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_SaveProduct("saveproduct")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_SaveProduct()))

    }
    fun cancelRequest() {
        Log.e("productdetail","productdetail cancel request")
        disposables.clear()
    }
    interface ProductDetailView {
        fun setErrorMessage(errorMessage: String)
        fun getProductDetail(response: Response)
        fun getStatusSave(boll: Boolean)
    }
}
