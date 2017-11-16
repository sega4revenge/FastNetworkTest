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
import finger.thuetot.vn.model.User
import finger.thuetot.vn.util.Constants


/**
 * Created by sega4 on 27/07/2017.
 */

class ChangePasswordPresenter(view: ChangePasswordPresenter.ChangePasswordView) {
    private var mDetailView: ChangePasswordView = view
    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    var changepassword = "CHANGEPASSWORD"
    private fun getObservable(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag("Comment")
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(changepassword, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(changepassword, " bytesSent : " + bytesSent)
                    Log.d(changepassword, " bytesReceived : " + bytesReceived)
                    Log.d(changepassword, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(changepassword, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                mDetailView.getUserDetail(response!!.user!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(changepassword, "onError errorCode : " + e.errorCode)
                        Log.d(changepassword, "onError errorBody : " + e.errorBody)
                        Log.d(changepassword, "onError errorDetail : " + e.errorDetail)
                        mDetailView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"))
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(changepassword, "onError errorDetail : " + e.errorDetail)
                        mDetailView.setErrorMessage(e.errorDetail)
                    }
                } else {
                    Log.d(changepassword, "onError errorMessage : " + e.message)
                    mDetailView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {
                mDetailView.isgetUserDetailSuccess(true)
            }
        }
    }
    fun changepassword(userid: String, oldpass : String, newpass : String) {

        try {
            jsonObject.put("user", userid)
            jsonObject.put("oldpass", oldpass)
            jsonObject.put("newpass", newpass)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("changepassword")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))
    }
    fun cancelRequest() {
        disposables.clear()
    }
    interface ChangePasswordView {

        fun setErrorMessage(errorMessage: String)
        fun getUserDetail(user : User)
        fun isgetUserDetailSuccess(success : Boolean)

    }
}
