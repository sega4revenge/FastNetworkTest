package finger.thuetot.vn.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.ResponseListProduct
import finger.thuetot.vn.model.User
import finger.thuetot.vn.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject




/**
 * Created by sega4 on 27/07/2017.
 */

class ProductListPresenter(view: ProductListView) {
    internal var mProductListView = view
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

    fun getProductListLikeNew(category: Int) {
        try {
            jsonObject.put("category", category)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservableLikeNew()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))


    }


    fun getSavedProductList(type: Int,page :Int,userid: String) {
//        Log.e(userdetail, page.toString()+category)

        try {
            jsonObject.put("userid", userid)
            jsonObject.put("type", type)
            jsonObject.put("page", page)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservableSave()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserverSaved()))


    }
    private fun getObservableSave(): Observable<ResponseListProduct> {
        return  Rx2AndroidNetworking.post(Constants.BASE_URL + "/allproductsaved")
                .setTag("listproductsaved")
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
    private fun getObservableLikeNew(): Observable<ResponseListProduct> {
        return  Rx2AndroidNetworking.post(Constants.BASE_URL + "/productlikenew")
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
                    if(e.errorCode == 404){
                        mProductListView.setErrorNotFound()
                    }else{
                        mProductListView.setErrorMessage(e.errorDetail)
                    }


                } else {
                    Log.d(userdetail, "onError errorMessage : " + e.message)
                    mProductListView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }
    private fun getDisposableObserverSaved(): DisposableObserver<ResponseListProduct> {

        return object : DisposableObserver<ResponseListProduct>() {

            override fun onNext(response: ResponseListProduct) {
                Log.d(userdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                mProductListView.getListSavedProduct(response.user!!)
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
        fun setErrorNotFound()
        fun setErrorMessage(errorMessage: String)
        fun getListProduct(productlist: ArrayList<Product>)
        fun getListSavedProduct(productsavedlist: User)


    }
    fun stopRequest() {
        disposables.clear()
        println("cancel")
    }
}
