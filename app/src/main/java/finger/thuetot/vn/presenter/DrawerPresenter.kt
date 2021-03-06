package finger.thuetot.vn.presenter

import android.content.Context
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.util.CompressImage
import finger.thuetot.vn.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
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

    fun editphone(userid: String,phone: String, androidid: String) {

        try {
            jsonObject.put("userid", userid)
            jsonObject.put("phone", phone)
            jsonObject.put("androidid", androidid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_eidtInfoUser("referralandroidid")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_editphonenumber()))
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
                mDrawerView.referralSuccess(response)

            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {

                        Log.d(userdetail, "onError errorCode : " + e.errorCode)
                        Log.d(userdetail, "onError errorBody : " + e.errorBody)
                        Log.d(userdetail, "onError errorDetail : " + e.errorDetail)
                        if(e.errorCode == 405)
                        {
                            mDrawerView.setErrorPhonenumber("")
                        }else if(e.errorCode == 406){
                            mDrawerView.setErrorPhonenumber("block")
                        }else{
                            mDrawerView.setErrorPhonenumber(JSONObject(e.errorBody.toString()).getString("message"))
                        }

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
    private fun getDisposableObserver_cancelReferral(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                mDrawerView.cancelreferralSuccess(response)

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
                        mDrawerView.setErrorPhonenumber(JSONObject(e.errorBody.toString()).getString("message"))
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
    fun editphonenumber(userid: String,phone: String,token: String) {
        try {
            jsonObject.put("userid", userid)
            jsonObject.put("phone", phone)
            jsonObject.put("token", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_editphonenumber("referralandroidid")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_editphonenumber()))
    }
    fun cancelReferral(userid: String) {
        try {
            jsonObject.put("userid", userid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_editphonenumber("cancelreferral")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_cancelReferral()))
    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface DrawerView {
        fun changeAvatarSuccess(t: Response)
        fun setErrorMessage(errorMessage: String)
        fun setErrorPhonenumber(errorMessage: String)
        fun getUserDetail(response: Response)
        fun referralSuccess(response: Response)
        fun cancelreferralSuccess(response: Response)

    }
}
