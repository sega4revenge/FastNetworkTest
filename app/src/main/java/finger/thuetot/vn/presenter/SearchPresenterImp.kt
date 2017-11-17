package finger.thuetot.vn.presenter

import android.util.Log
import com.androidnetworking.error.ANError
import com.google.android.gms.maps.model.LatLng
import com.rx2androidnetworking.Rx2AndroidNetworking
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.ResponseListProduct
import finger.thuetot.vn.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by VinhNguyen on 8/9/2017.
 */

class SearchPresenterImp(searchView: SearchView){
    private val searchview = searchView
    private val TAG: String = "SearchTag"
    private val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    fun searchWithList(key: String, mLocation: String, mCategory: String, mTypeArrange: Int) {

        try {
            jsonObject.put("keysearch", key)
            jsonObject.put("location", mLocation)
            jsonObject.put("category", mCategory)
            jsonObject.put("typeArrange", mTypeArrange)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("search")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))

    }
    fun searchWithMap(key: String, mLatLng: LatLng, mCategory: String, mRadius : Int) {


        try {
            jsonObject.put("keysearch", key)
            jsonObject.put("lat", mLatLng.latitude)
            jsonObject.put("lng", mLatLng.longitude)
            jsonObject.put("category", mCategory)
            jsonObject.put("distance", mRadius)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("searchmap")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))

    }
    private fun getObservable(typesearch: String): Observable<ResponseListProduct> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag("search")
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(TAG, " bytesSent : " + bytesSent)
                    Log.d(TAG, " bytesReceived : " + bytesReceived)
                    Log.d(TAG, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(ResponseListProduct::class.java)
    }

    private fun getDisposableObserver(): DisposableObserver<ResponseListProduct> {

        return object : DisposableObserver<ResponseListProduct>() {

            override fun onNext(response: ResponseListProduct) {
                searchview.getListProduct(response.listproduct!!)
            }

            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(TAG, "onError errorCode : " + e.errorCode)
                    Log.d(TAG, "onError errorBody : " + e.errorBody)
                    Log.d(TAG, e.errorDetail + " : " + e.message)
                    if(e.errorCode == 404){
                        searchview.setMessagerNotFound()
                    }else {
                        searchview.setErrorMessage(e.errorDetail)
                    }

                } else {
                    Log.d(TAG, "onError errorMessage : " + e.message)
                    searchview.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }

    fun cancelRequest() {
        disposables.clear()
    }
    interface SearchView {

        fun setErrorMessage(errorMessage: String)

        fun getListProduct(productlist : ArrayList<Product>)
        fun setMessagerNotFound()
    }

}