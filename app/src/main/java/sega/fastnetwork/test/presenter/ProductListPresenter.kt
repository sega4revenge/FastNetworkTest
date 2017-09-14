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
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.ResponseListProduct
import sega.fastnetwork.test.util.Constants




/**
 * Created by sega4 on 27/07/2017.
 */

class ProductListPresenter(view: ProductListView) {
    internal var mProductListView: ProductListView = view
    var userdetail = "USERDETAIL"
    private val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    fun getProductList(type: Int,page :Int,category: Int) {
        Log.e(userdetail, page.toString()+category)

        try {
            jsonObject.put("category", category)
            jsonObject.put("type", type)
            jsonObject.put("page", page)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))


    }

    private fun getObservable(): Observable<ResponseListProduct> {
        return  Rx2AndroidNetworking.post(Constants.BASE_URL + "/allproduct")
                .setTag("listproduct")
                .setTag(this)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(userdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(userdetail, " bytesSent : " + bytesSent)
                    Log.d(userdetail, " bytesReceived : " + bytesReceived)
                    Log.d(userdetail, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(ResponseListProduct::class.java)
    }

    private fun getDisposableObserver(): DisposableObserver<ResponseListProduct> {

        return object : DisposableObserver<ResponseListProduct>() {

            override fun onNext(response: ResponseListProduct) {
                Log.d(userdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                mProductListView.getListProduct(response.listproduct!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(userdetail, "onError errorCode : " + e.errorCode)
                    Log.d(userdetail, "onError errorBody : " + e.errorBody)
                    Log.d(userdetail, e.errorDetail + " : " + e.message)
                    mProductListView.setErrorMessage(e.errorDetail)

                } else {
                    Log.d(userdetail, "onError errorMessage : " + e.message)
                    mProductListView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }

    interface ProductListView {

        fun setErrorMessage(errorMessage: String)
        fun getListProduct(productlist: ArrayList<Product>)


    }
    fun stopRequest() {
        disposables.clear()
        println("cancel")
    }
}
