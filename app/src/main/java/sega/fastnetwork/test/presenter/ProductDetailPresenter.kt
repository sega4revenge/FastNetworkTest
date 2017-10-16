package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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
    private val disposables = CompositeDisposable()
    private val jsonObject = JSONObject()
    private fun getObservable(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag("Comment")
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

//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("productid", productid)
//            jsonObject.put("userid", userid)
//
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        Rx2AndroidNetworking.post(Constants.BASE_URL + "productdetail")
//                .addJSONObjectBody(jsonObject)
//                .build()
//                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
//                    Log.d(productdetail, " timeTakenInMillis : " + timeTakenInMillis)
//                    Log.d(productdetail, " bytesSent : " + bytesSent)
//                    Log.d(productdetail, " bytesReceived : " + bytesReceived)
//                    Log.d(productdetail, " isFromCache : " + isFromCache)
//                }
//                .getObjectObservable(Response::class.java)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<Response> {
//                    override fun onNext(response: Response?) {
//                        Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//
//                        mProductDetailView.getProductDetail(response!!)
//                    }
//
//
//                    override fun onComplete() {
//
//
//                    }
//                    override fun onError(e: Throwable) {
//                        if (e is ANError) {
//
//
//                            Log.d(productdetail, "onError errorCode : " + e.errorCode)
//                            Log.d(productdetail, "onError errorBody : " + e.errorBody)
//                            Log.d(productdetail, e.errorDetail + " : " + e.message)
//                            mProductDetailView.setErrorMessage(e.errorDetail)
//
//                        } else {
//                            Log.d(productdetail, "onError errorMessage : " + e.message)
//                            mProductDetailView.setErrorMessage(e.message!!)
//                        }
//                    }
//
//
//
//                    override fun onSubscribe(d: Disposable) {
//
//                    }
//
//
//                })
    }

    fun SaveProduct(productid : String, userid : String,type: String) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("productid", productid)
            jsonObject.put("userid", userid)
            jsonObject.put("type", type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "saveproduct")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(productdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(productdetail, " bytesSent : " + bytesSent)
                    Log.d(productdetail, " bytesReceived : " + bytesReceived)
                    Log.d(productdetail, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(productdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                         mProductDetailView.getStatusSave(true)
                    }


                    override fun onComplete() {


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

                    override fun onSubscribe(d: Disposable) {

                    }


                })
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
