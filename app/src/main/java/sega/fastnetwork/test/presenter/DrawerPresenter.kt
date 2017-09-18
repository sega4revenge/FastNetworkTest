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
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.util.CompressImage
import sega.fastnetwork.test.util.Constants
import java.io.File


/**
 * Created by sega4 on 27/07/2017.
 */

class DrawerPresenter(view : DrawerView) {
    internal var mDrawerView: DrawerView = view
    var userdetail = "USERDETAIL"

    fun changeAvatar(file: File, userid: String, oldavatar: String, context: Context){


        var TAG : String = "Change Avatar"
        val observable = Rx2AndroidNetworking.upload(Constants.BASE_URL + "changeavatar")
                .addMultipartParameter("userid", userid)
                .addMultipartParameter("oldavatar", oldavatar)
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
//                        Log.d(TAG + "_1", "onResponse object : " + t!!.user!!.photoprofile.toString())

                        mDrawerView.changeAvatarSuccess(t!!)
//                        AppManager.saveAccountUser(context, t!!.user!!, 0)
//                        Log.e("pic",Constants.IMAGE_URL+t!!.user!!.photoprofile)
//                        Glide.with(context)
//                                .load(Constants.IMAGE_URL+t!!.user!!.photoprofile)
//                                .thumbnail(0.1f)
//                                .apply(options)
//                                .into(navigation_view.getHeaderView(0).avatar_header)
//

//                      var a = AppManager.getUserDatafromAccount(activity,AppManager.getAppAccount(context)!!)
//                        Log.e("getUserDatafromAccount",a._id + " " +a.name + " " +a.email + " " +a.photoprofile)
                    }

                    override fun onComplete() {
                        Log.d(TAG + "_1", "onComplete Detail : uploadImage completed")
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG + "_1", "onError errorCode : " + e.errorCode)
                                Log.d(TAG + "_1", "onError errorBody : " + e.errorBody)
                                Log.d(TAG + "_1", "onError errorDetail : " + JSONObject(e.errorBody.toString()).getString("message"))
                                mDrawerView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"))
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
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

    fun eidtInfoUser(userid: String,newname: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("userid", userid)
            jsonObject.put("newname", newname)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "/editinfouser")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(userdetail, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(userdetail, " bytesSent : " + bytesSent)
                    Log.d(userdetail, " bytesReceived : " + bytesReceived)
                    Log.d(userdetail, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(userdetail, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//                        Log.e(userdetail, response!!.user!!.name)
                        mDrawerView.getUserDetail(response!!)
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
                                Log.d(userdetail, "onError errorCode : " + e.errorCode)
                                Log.d(userdetail, "onError errorBody : " + e.errorBody)
                                Log.d(userdetail, "onError errorDetail : " + e.errorDetail)
                                mDrawerView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"))
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(userdetail, "onError errorDetail : " + e.errorDetail)
                                mDrawerView.setErrorMessage(e.errorDetail)
                            }
                        } else {
                            Log.d(userdetail, "onError errorMessage : " + e.message)
                            mDrawerView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    interface DrawerView {
        fun changeAvatarSuccess(t: Response)
        fun setErrorMessage(errorMessage: String)
        fun getUserDetail(response: Response)

    }
}
