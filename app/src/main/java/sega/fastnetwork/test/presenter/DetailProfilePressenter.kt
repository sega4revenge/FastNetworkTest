package sega.fastnetwork.test.presenter

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
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants

/**
 * Created by VinhNguyen on 8/23/2017.
 */
class DetailProfilePressenter(profileView : DetailProfileView) {
    private val  profileview : DetailProfileView = profileView
    private val TAG :String = "profileView"
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()

    private fun getObservable(typesearch: String): Observable<ResponseListProduct> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag("Comment")
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

//                        Log.e("Responseeeee", response.toString())
//                        Log.e("Responseeeee2", response!!.user!!.listproduct!!.size.toString())

                profileview.getUser(response.user!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(TAG, "onError errorCode : " + e.errorCode)
                    Log.d(TAG, "onError errorBody : " + e.errorBody)
                    Log.d(TAG, e.errorDetail + " : " + e.message)
                    profileview.setErrorMessage(e.errorDetail)

                } else {
                    Log.d(TAG, "onError errorMessage : " + e.message)
                    profileview.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }
    fun ConnectHttp(id : String) {
        try {
            jsonObject.put("userid",id)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("getfullprofile")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))
    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface DetailProfileView {
        fun setErrorMessage(errorMessage: String)
        fun getListProduct(productlist : ArrayList<Product>, user: User)
        fun getUser(user: User)
    }
}