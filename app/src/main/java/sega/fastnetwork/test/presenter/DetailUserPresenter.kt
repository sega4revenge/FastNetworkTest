package sega.fastnetwork.test.presenter

import android.content.Context
import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import sega.fastnetwork.test.model.ChatMessager
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.CompressImage
import sega.fastnetwork.test.util.Constants
import java.io.File


/**
 * Created by sega4 on 27/07/2017.
 */

class DetailUserPresenter(view: DetailUserPresenter.DetailUserView) {
    private var mDetailView: DetailUserView = view



    var userdetail = "USERDETAIL"
    fun getUserDetail(userid: String) {

        Rx2AndroidNetworking.get(Constants.BASE_URL + "/data/{userid}")
                .addPathParameter("userid",userid)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(userdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(userdetail, " bytesSent : " + bytesSent)
                    Log.d(userdetail, " bytesReceived : " + bytesReceived)
                    Log.d(userdetail, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(User::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<User> {
                    override fun onNext(user: User?) {
                        Log.d(userdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                        mDetailView.getUserDetail(user!!)

                    }


                    override fun onComplete() {
                        mDetailView.isgetUserDetailSuccess(true)

                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(userdetail, "onError errorCode : " + e.errorCode)
                                Log.d(userdetail, "onError errorBody : " + e.errorBody)
                                Log.d(userdetail, "onError errorDetail : " + e.errorDetail)
                                mDetailView.setErrorMessage(e.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(userdetail, "onError errorDetail : " + e.errorDetail + "errmess: "+e.message)
                                mDetailView.setErrorMessage(e.errorDetail)
                            }
                        } else {
                            Log.d(userdetail, "onError errorMessage : " + e.message)
                            mDetailView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    fun upImage(file: File, userfrom: String, userto: String, email: String, name: String, context: Context){


        var TAG : String = "Up Image"
        val observable = Rx2AndroidNetworking.upload(Constants.BASE_URL + "sendimagechat")
                .addMultipartParameter("userfrom", userfrom)
                .addMultipartParameter("userto", userto)
                .addMultipartParameter("email", email)
                .addMultipartParameter("name", name)
                .addMultipartFile("image", CompressImage.compressImage(file, context))
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(TAG, " bytesSent : " + bytesSent)
                    Log.d(TAG, " bytesReceived : " + bytesReceived)
                    Log.d(TAG, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(t: Response?) {
                        mDetailView.getStatusUpdateImage(t?.listchat!!)
                    }

                    override fun onComplete() {
                        Log.d(TAG + "_1", "onComplete Detail : uploadImage completed")
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {

                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG + "_1", "onError errorCode : " + e.errorCode)
                                Log.d(TAG + "_1", "onError errorBody : " + e.errorBody)
                                Log.d(TAG + "_1", "onError errorDetail : " + JSONObject(e.errorBody.toString()).getString("message"))

                            } else {

                                Log.d(TAG + "_1", "onError errorDetail : " + e.errorDetail)
                            }
                        } else {
                            Log.d(TAG + "_1", "onError errorMessage : " + e.message)
                        }

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                })


    }

    interface DetailUserView {
        fun getStatusUpdateImage(mess : ChatMessager)
        fun setErrorMessage(errorMessage: String)
        fun getUserDetail(user : User)
        fun isgetUserDetailSuccess(success : Boolean)

    }
}
