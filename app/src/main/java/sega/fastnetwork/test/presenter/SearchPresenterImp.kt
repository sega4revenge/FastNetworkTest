package sega.fastnetwork.test.presenter

import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.ResponseListProduct
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.SearchInterface
import sega.fastnetwork.test.view.SearchView

/**
 * Created by VinhNguyen on 8/9/2017.
 */

class SearchPresenterImp(searchView : SearchView) : SearchInterface {
    private val  searchview = searchView
    private val TAG :String = "SearchTag"
    var request: Observable<ResponseListProduct>? = null
    override fun ConnectHttp(key: String, mLocation: String, mCategory: String, mTypeArrange: Int) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("keysearch", key)
            jsonObject.put("location", mLocation)
            jsonObject.put("category", mCategory)
            jsonObject.put("typeArrange", mTypeArrange)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

      request =  Rx2AndroidNetworking.post(Constants.BASE_URL + "search")
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

                request!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseListProduct> {
                    override fun onNext(response: ResponseListProduct?) {

                        Log.d("AAAAAAAAA", response?.listproduct!![0].toString())
                        searchview.getListProduct(response?.listproduct!!)
                    }


                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                Log.d(TAG, "onError errorCode : " + e.errorCode)
                                Log.d(TAG, "onError errorBody : " + e.errorBody)
                                Log.d(TAG, "onError errorDetail : " + e.errorDetail)
                                if(e.errorCode != 404)
                                {  searchview.setErrorMessage(e.errorBody.get(0).toString())

                                }else{
                                    searchview.setMessagerNotFound()
                                }
                            } else {
                                Log.d(TAG, "onError errorDetail : " + e.errorDetail)

                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + e.message)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })

    }

    fun cancelRequest(){
        request?.unsubscribeOn(Schedulers.io())
    }



}