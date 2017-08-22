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
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.Constants


/**
 * Created by sega4 on 27/07/2017.
 */

class ProductDetailPresenter(view : ProductDetailPresenter.ProductDetailView) {
    internal var mProductDetailView: ProductDetailView = view
    var productdetail = "PRODUCTDETAIL"

    fun getProductDetail(productid : String, userid : String) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("productid", productid)
            jsonObject.put("userid", userid)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Rx2AndroidNetworking.post(Constants.BASE_URL + "productdetail")
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

                        mProductDetailView.getProductDetail(response?.product!!)
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
                                Log.d(productdetail, "onError errorCode : " + anError.errorCode)
                                Log.d(productdetail, "onError errorBody : " + anError.errorBody)
                                Log.d(productdetail, "onError errorDetail : " + anError.errorDetail)
                                mProductDetailView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(productdetail, "onError errorDetail : " + anError.errorDetail)
                                mProductDetailView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(productdetail, "onError errorMessage : " + e.message)
                            mProductDetailView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    interface ProductDetailView {

        fun setErrorMessage(errorMessage: String)
        fun getProductDetail(product : Product)




    }
}
