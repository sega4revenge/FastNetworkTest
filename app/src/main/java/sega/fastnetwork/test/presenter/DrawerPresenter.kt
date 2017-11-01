package sega.fastnetwork.test.presenter

import android.content.Context
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
    var changeAvatar = "changeAvatar"
    var eidtInfoUser = "eidtInfoUser"
    var editphonenumber = "editphonenumber"
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    private fun getObservable_changeAvatar(typesearch: String,file: File, userid: String, oldavatar: String, context: Context): Observable<Response> {
        return Rx2AndroidNetworking.upload(Constants.BASE_URL + typesearch)
                .addMultipartParameter("userid", userid)
                .addMultipartParameter("oldavatar", oldavatar)
                .addMultipartFile("image", CompressImage.compressImage(file, context))
                .setTag("changeAvatar")
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(changeAvatar, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(changeAvatar, " bytesSent : " + bytesSent)
                    Log.d(changeAvatar, " bytesReceived : " + bytesReceived)
                    Log.d(changeAvatar, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_changeAvatar(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(t: Response) {
                mDrawerView.changeAvatarSuccess(t)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(changeAvatar + "_1", "onError errorCode : " + e.errorCode)
                        Log.d(changeAvatar + "_1", "onError errorBody : " + e.errorBody)
                        Log.d(changeAvatar + "_1", "onError errorDetail : " + JSONObject(e.errorBody.toString()).getString("message"))
                        mDrawerView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"))
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(changeAvatar + "_1", "onError errorDetail : " + e.errorDetail)
                    }
                } else {
                    Log.d(changeAvatar + "_1", "onError errorMessage : " + e.message)
                }
            }

            override fun onComplete() {

            }
        }
    }

    fun changeAvatar(file: File, userid: String, oldavatar: String, context: Context){

        disposables.add(getObservable_changeAvatar("changeavatar", file, userid, oldavatar, context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_changeAvatar()))
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private fun getObservable_eidtInfoUser(typesearch: String): Observable<Response> {
    return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
            .setTag(eidtInfoUser)
            .addJSONObjectBody(jsonObject)
            .build()

            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(eidtInfoUser, " timeTakenInMillis : " + timeTakenInMillis)
                Log.d(eidtInfoUser, " bytesSent : " + bytesSent)
                Log.d(eidtInfoUser, " bytesReceived : " + bytesReceived)
                Log.d(eidtInfoUser, " isFromCache : " + isFromCache)
            }

            .getObjectObservable(Response::class.java)
}
    private fun getDisposableObserver_eidtInfoUser(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.e("ALOALO","AlO")
                mDrawerView.getUserDetail(response)

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

            override fun onComplete() {

            }
        }
    }
    fun eidtInfoUser(userid: String,newname: String, newphone: String) {

        try {
            jsonObject.put("userid", userid)
            jsonObject.put("newname", newname)
            jsonObject.put("newphone", newphone)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_eidtInfoUser("editinfouser")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_eidtInfoUser()))
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private fun getObservable_editphonenumber(typesearch: String): Observable<Response> {
    return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
            .setTag(editphonenumber)
            .addJSONObjectBody(jsonObject)
            .build()

            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(editphonenumber, " timeTakenInMillis : " + timeTakenInMillis)
                Log.d(editphonenumber, " bytesSent : " + bytesSent)
                Log.d(editphonenumber, " bytesReceived : " + bytesReceived)
                Log.d(editphonenumber, " isFromCache : " + isFromCache)
            }

            .getObjectObservable(Response::class.java)
}
    private fun getDisposableObserver_editphonenumber(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                mDrawerView.getUserDetail(response)

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

            override fun onComplete() {

            }
        }
    }
    fun editphonenumber(userid: String,phone: String) {
        try {
            jsonObject.put("userid", userid)
            jsonObject.put("phone", phone)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_editphonenumber("editphonenumber")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_editphonenumber()))
    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface DrawerView {
        fun changeAvatarSuccess(t: Response)
        fun setErrorMessage(errorMessage: String)
        fun getUserDetail(response: Response)

    }
}
