package finger.thuetot.vn.presenter

import android.content.Context
import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.model.User
import finger.thuetot.vn.util.CompressImage
import finger.thuetot.vn.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.File


/**
 * Created by sega4 on 27/07/2017.
 */

class DetailUserPresenter(view: DetailUserPresenter.DetailUserView) {
    private var mDetailView: DetailUserView = view
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    var userdetail = "USERDETAIL"
    var upload_image = "upload_image"
    private fun getObservable_getUserDetail(typesearch: String, userid : String): Observable<User> {
        return Rx2AndroidNetworking.get(Constants.BASE_URL + typesearch)
                .addPathParameter("userid",userid)
                .setTag("USERDETAIL")
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(userdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(userdetail, " bytesSent : " + bytesSent)
                    Log.d(userdetail, " bytesReceived : " + bytesReceived)
                    Log.d(userdetail, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(User::class.java)
    }
    private fun getDisposableObserver_getUserDetail(): DisposableObserver<User> {

        return object : DisposableObserver<User>() {

            override fun onNext(user: User) {
                Log.d(userdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                Log.d(userdetail, user.name)
                mDetailView.getUserDetail(user)
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

            override fun onComplete() {
                mDetailView.isgetUserDetailSuccess(true)

            }
        }
    }
    private fun getDisposableObserver_getUserUpdateMoney(): DisposableObserver<User> {

        return object : DisposableObserver<User>() {

            override fun onNext(user: User) {
                Log.d(userdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                Log.d(userdetail, user.name)
                mDetailView.getUserUpdateMoney(user)
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

            override fun onComplete() {
                mDetailView.isgetUpdateMoneySuccess(true)

            }
        }
    }
    fun getUserDetail(userid: String) {

        disposables.add(getObservable_getUserDetail("/data/{userid}",userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_getUserDetail()))
    }
    fun updateMoney(userid: String) {

        disposables.add(getObservable_getUserDetail("/updatemoney/{userid}",userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_getUserUpdateMoney()))
    }
    private fun getObservable_upImage(typesearch: String, file: File, userfrom: String, userto: String, email: String, name: String, context: Context ): Observable<Response> {
        return Rx2AndroidNetworking.upload(Constants.BASE_URL + typesearch)
                .addMultipartParameter("userfrom", userfrom)
                .addMultipartParameter("userto", userto)
                .addMultipartParameter("email", email)
                .addMultipartParameter("name", name)
                .addMultipartFile("image", CompressImage.compressImage(file, context  ))
                .setTag("USERDETAIL")
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(userdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(userdetail, " bytesSent : " + bytesSent)
                    Log.d(userdetail, " bytesReceived : " + bytesReceived)
                    Log.d(userdetail, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_upImage(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(t: Response) {
                mDetailView.getStatusUpdateImage(t.message!!)

            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {

                        // error.getErrorDetail() - just a ANError detail
                        Log.d(upload_image + "_1", "onError errorCode : " + e.errorCode)
                        Log.d(upload_image + "_1", "onError errorBody : " + e.errorBody)
                        Log.d(upload_image + "_1", "onError errorDetail : " + JSONObject(e.errorBody.toString()).getString("message"))

                    } else {

                        Log.d(upload_image + "_1", "onError errorDetail : " + e.errorDetail)
                    }
                } else {
                    Log.d(upload_image + "_1", "onError errorMessage : " + e.message)
                }
            }

            override fun onComplete() {
                Log.d(upload_image + "_1", "onComplete Detail : uploadImage completed")

            }
        }
    }
    fun upImage(file: File, userfrom: String, userto: String, email: String, name: String, context: Context){
        disposables.add(getObservable_upImage("sendimagechat",file, userfrom, userto, email, name, context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_upImage()))
    }

    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface DetailUserView {
        fun getStatusUpdateImage(mess: String)
        fun setErrorMessage(errorMessage: String)
        fun getUserDetail(user : User)
        fun isgetUserDetailSuccess(success : Boolean)
        fun getUserUpdateMoney(user : User)
        fun isgetUpdateMoneySuccess(success : Boolean)

    }
}
