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
import sega.fastnetwork.test.model.Chat
import sega.fastnetwork.test.model.ResponseListProduct
import sega.fastnetwork.test.util.Constants

/**
 * Created by VinhNguyen on 9/24/2017.
 */
class InboxPressenter(view: InboxListView){

    internal var mInboxListView = view
    var inbox = "INBOXCHAT"
    private val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()


    fun getProductList(id: String) {

        try {
            jsonObject.put("userid", id)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))
    }
        private fun getObservable(): Observable<ResponseListProduct> {
        return  Rx2AndroidNetworking.post(Constants.BASE_URL + "/getInbox")
                .setTag("listproduct")
                .setTag(this)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(inbox, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(inbox, " bytesSent : " + bytesSent)
                    Log.d(inbox, " bytesReceived : " + bytesReceived)
                    Log.d(inbox, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(ResponseListProduct::class.java)
    }
    private fun getDisposableObserver(): DisposableObserver<ResponseListProduct> {

        return object : DisposableObserver<ResponseListProduct>() {

            override fun onNext(response: ResponseListProduct) {
                Log.d(inbox, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                mInboxListView.getListInbox(response.listinbox!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(inbox, "onError errorCode : " + e.errorCode)
                    Log.d(inbox, "onError errorBody : " + e.errorBody)
                    Log.d(inbox, e.errorDetail + " : " + e.message)
                    mInboxListView.setErrorMessage(e.errorDetail)

                } else {
                    Log.d(inbox, "onError errorMessage : " + e.message)
                    mInboxListView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface InboxListView {
        fun setErrorMessage(errorMessage: String)
        fun getListInbox(mChat: ArrayList<Chat>?)
    }
}