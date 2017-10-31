package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.google.firebase.iid.FirebaseInstanceId
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants

class LoginPresenter(view: LoginView) {

    internal var mLoginView: LoginView = view
    var login = "LOGIN"
    var register = "REGISTER"
    var register_finish = "REGISTER_FINISH"

    val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    fun onCreate() {
        // initialization
    }
    private fun getObservable_login(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(login)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(login, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(login, " bytesSent : " + bytesSent)
                    Log.d(login, " bytesReceived : " + bytesReceived)
                    Log.d(login, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_login(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(login, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                mLoginView.getUserDetail(response.user!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(login, "onError errorCode : " + e.errorCode)
                    Log.d(login, "onError errorBody : " + e.errorBody)
                    Log.d(login, e.errorDetail + " : " + e.message)
                    mLoginView.isLoginSuccessful(false)
                    if(e.errorCode == 404)
                    {
                        mLoginView.setErrorMessage("User Not Found !",0)
                    }else if(e.errorCode == 401){
                        mLoginView.setErrorMessage("Incorrect password !",0)
                    }else if(e.errorCode == 403){
                        mLoginView.setErrorMessage("Email not authenticated !",0)
                    }


                } else {
                    Log.d(login, "onError errorMessage : " + e.message)
                    mLoginView.isLoginSuccessful(false)
                }
            }

            override fun onComplete() {
                mLoginView.isLoginSuccessful(true)

            }
        }
    }
    fun login(email: String, password: String) {

        val tokenfirebase = FirebaseInstanceId.getInstance().token
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("tokenfirebase", tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_login("authenticate")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_login()))
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
private fun getObservable_register(typesearch: String): Observable<Response> {
    return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
            .setTag(register)
            .addJSONObjectBody(jsonObject)
            .build()

            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
                Log.d(register, " bytesSent : " + bytesSent)
                Log.d(register, " bytesReceived : " + bytesReceived)
                Log.d(register, " isFromCache : " + isFromCache)
            }

            .getObjectObservable(Response::class.java)
}
    private fun getDisposableObserver_register(type: Int): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
               if(type!=0){
                   mLoginView.getUserDetail(response.user!!)
               }
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    Log.d(register, "onError errorCode : " + e.errorCode)
                    Log.d(register, "onError errorBody : " + e.errorBody)
                    Log.d(register, e.errorDetail + " : " + e.message)
                    if(e.errorCode == 409)
                    {
                        mLoginView.setErrorMessage("User Already Registered !",0)
                    }else{
                        mLoginView.setErrorMessage(e.errorBody,0)
                    }
                    mLoginView.isLoginSuccessful(false)
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mLoginView.setErrorMessage(e.message!!,1)
                }
            }

            override fun onComplete() {
                mLoginView.isRegisterSuccessful(true, type)
            }
        }
    }
    fun register(user: User, type: Int) {

        try {
            if (type == Constants.FACEBOOK) {
                jsonObject.put("id", user.facebook!!.id)
                jsonObject.put("token", user.facebook!!.token)
                jsonObject.put("name", user.facebook!!.name)
                jsonObject.put("email", user.facebook!!.email)
                jsonObject.put("password", user.hashed_password)
                jsonObject.put("photoprofile", user.facebook!!.photoprofile)
                jsonObject.put("type", type)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            } else if (type == Constants.GOOGLE) {
                jsonObject.put("id", user.google!!.id)
                jsonObject.put("token", user.google!!.token)
                jsonObject.put("name", user.google!!.name)
                jsonObject.put("email", user.google!!.email)
                jsonObject.put("password", user.hashed_password)
                jsonObject.put("photoprofile", user.google!!.photoprofile)
                jsonObject.put("type", type)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            } else {
                jsonObject.put("name", user.name)
                jsonObject.put("email", user.email)
                jsonObject.put("type", type)
                jsonObject.put("password", user.hashed_password)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_register("users")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_register(type)))

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun getObservable_register_finish(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(register_finish)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register_finish, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(register_finish, " bytesSent : " + bytesSent)
                    Log.d(register_finish, " bytesReceived : " + bytesReceived)
                    Log.d(register_finish, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(Response::class.java)
    }
    private fun getDisposableObserver_register_finish(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                mLoginView.getUserDetail(response.user!!)
            }
            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(register, "onError errorCode : " + e.errorCode)
                        Log.d(register, "onError errorBody : " + e.errorBody)
                        Log.d(register, "onError errorDetail : " + e.errorDetail)
                        mLoginView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"), 2)
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(register, "onError errorDetail : " + e.errorDetail)
                        mLoginView.setErrorMessage(JSONObject(e.errorBody.toString()).getString("message"), 2)
                    }
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mLoginView.isRegisterSuccessful(false, 2)
                }
            }

            override fun onComplete() {
                mLoginView.isRegisterSuccessful(true,3)

            }
        }
    }

    fun register_finish(email: String, code: String) {

        try {
            jsonObject.put("email", email)
            jsonObject.put("code", code)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_register_finish("registerfinish")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_register_finish()))
    }
    fun cancelRequest() {
        Log.e("Cancel","Cancel Request")
        disposables.clear()
    }
    interface LoginView {

        fun isLoginSuccessful(isLoginSuccessful: Boolean)
        fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type: Int)
        fun getUserDetail(user: User)
    }
}
