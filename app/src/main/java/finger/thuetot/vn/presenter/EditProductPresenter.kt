package finger.thuetot.vn.presenter

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
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.util.Constants

/**
 * Created by VinhNguyen on 8/30/2017.
 */
class EditProductPresenter(editProductView : EditProductView)  {
    var createproduct = "EDITPRODUCT"
    var mView : EditProductView = editProductView
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    private fun getObservable_DeleteProduct(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(createproduct)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(createproduct, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(createproduct, " bytesSent : " + bytesSent)
                    Log.d(createproduct, " bytesReceived : " + bytesReceived)
                    Log.d(createproduct, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_DeleteProduct(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {

                Log.d(createproduct, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                mView.isCreateSuccess(true,1)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(createproduct, "onError errorCode : " + e.errorCode)
                        Log.d(createproduct, "onError errorBody : " + e.errorBody)
                        Log.d(createproduct, "onError errorDetail : " + e.errorDetail)
                        mView.isCreateSuccess(false,999)
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(createproduct, "onError errorDetail : " + e.errorDetail)
                        mView.isCreateSuccess(false,999)
                    }
                } else {
                    Log.d(createproduct, "onError errorMessage : " + e.message)
                    mView.isCreateSuccess(false,999)
                }
            }

            override fun onComplete() {

            }
        }
    }
    fun ConnectHttpDeleteProduct(productid: String, listimg: String) {
        try {
            jsonObject.put("productid", productid)
            jsonObject.put("listimg", listimg)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_DeleteProduct("deleteproduct")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_DeleteProduct()))
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
private fun getObservable_Editproduct(typesearch: String): Observable<Response> {
    return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
            .setTag(createproduct)
            .addJSONObjectBody(jsonObject)
            .build()

            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(createproduct, " timeTakenInMillis : " + timeTakenInMillis)
                Log.d(createproduct, " bytesSent : " + bytesSent)
                Log.d(createproduct, " bytesReceived : " + bytesReceived)
                Log.d(createproduct, " isFromCache : " + isFromCache)
            }

            .getObjectObservable(Response::class.java)
}
    private fun getDisposableObserver_Editproduct(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(createproduct, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                mView.isCreateSuccess(true,0)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(createproduct, "onError errorCode : " + e.errorCode)
                        Log.d(createproduct, "onError errorBody : " + e.errorBody)
                        Log.d(createproduct, "onError errorDetail : " + e.errorDetail)
                        mView.isCreateSuccess(false,999)
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(createproduct, "onError errorDetail : " + e.errorDetail)
                        mView.isCreateSuccess(false,999)
                    }
                } else {
                    Log.d(createproduct, "onError errorMessage : " + e.message)
                    mView.isCreateSuccess(false,999)
                }
            }

            override fun onComplete() {

            }
        }
    }
    fun ConnectHttp(userid: String, productname: String, price: String, time: String, number: String, category: String, address: String, description: String, status: String, productid: String, imgdel: String){
        try {
            jsonObject.put("user", userid)
            jsonObject.put("productname", productname)
            jsonObject.put("price", price)
            jsonObject.put("time",time)
            jsonObject.put("number", number)
            jsonObject.put("category",category)
            jsonObject.put("address",address)
            jsonObject.put("description", description)
            jsonObject.put("status", status)
            jsonObject.put("productid", productid)
            jsonObject.put("listimgdel", imgdel)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_Editproduct("editproduct")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_Editproduct()))

    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface EditProductView {
        fun isCreateSuccess(success : Boolean, mtype: Int)
    }
}