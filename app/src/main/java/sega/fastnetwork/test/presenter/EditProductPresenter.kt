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
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.EditProductInterface
import sega.fastnetwork.test.view.EditProductView

/**
 * Created by VinhNguyen on 8/30/2017.
 */
class EditProductPresenter(editProductView : EditProductView) : EditProductInterface {
    var createproduct = "EDITPRODUCT"
    var mView = editProductView



    override fun ConnectHttpDeleteProduct(productid: String, listimg: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("productid", productid)
            jsonObject.put("listimg", listimg)
            Log.e("AAAAA", listimg+ " "+ productid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "deleteproduct")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(createproduct, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(createproduct, " bytesSent : " + bytesSent)
                    Log.d(createproduct, " bytesReceived : " + bytesReceived)
                    Log.d(createproduct, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {

                        Log.d(createproduct, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                        mView.isCreateSuccess(true,1)
                    }


                    override fun onComplete() {


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

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    override fun ConnectHttp(userid: String, productname : String, price : String, time: String, number : String, category: String, address: String, description: String, productid: String,imgdel: String){
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user", userid)
            jsonObject.put("productname", productname)
            jsonObject.put("price", price)
            jsonObject.put("time",time)
            jsonObject.put("number", number)
            jsonObject.put("category",category)
            jsonObject.put("address",address)
            jsonObject.put("description", description)
            jsonObject.put("productid", productid)
            jsonObject.put("listimgdel", imgdel)
            Log.e("AAAAA",userid + " " + productname+ " " + price + " " + time  + " " + number  + " " + category  + " " + address+ " " + description+ " "+ productid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "editproduct")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(createproduct, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(createproduct, " bytesSent : " + bytesSent)
                    Log.d(createproduct, " bytesReceived : " + bytesReceived)
                    Log.d(createproduct, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {

                        Log.d(createproduct, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                        mView.isCreateSuccess(true,0)
                    }


                    override fun onComplete() {


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

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
}