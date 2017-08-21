package sega.fastnetwork.test.manager

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.support.annotation.Nullable
import android.util.Log
import android.widget.Toast
import com.androidnetworking.error.ANError
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import sega.fastnetwork.test.MyApplication
import sega.fastnetwork.test.activity.LoginActivity
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants


/**
 * Created by sega4 on 27/07/2017.
 */

object AppManager {
    val ACCOUNT_TYPE = "sega.fastnetwork.test"
    val USER_DATA_ID = "USER_DATA_ID"
    val USER_TYPE = "USER_TYPE"
    val USER_DATA_USERNAME = "USER_DATA_USERNAME"
    val USER_DATA_VERSION = "USER_DATA_VERSION"
    val CURRENT_USER_DATA_VERSION = "1"
    val USER_DATA_EMAIL = "USER_DATA_EMAIL"
    val USER_DATA_GOOGLE_ID = "USER_DATA_GOOGLE_ID"
    val USER_DATA_GOOGLE_TOKEN = "USER_DATA_GOOGLE_TOKEN"
    val USER_DATA_GOOGLE_EMAIL = "USER_DATA_GOOGLE_EMAIL"
    val USER_DATA_GOOGLE_NAME = "USER_DATA_GOOGLE_NAME"
    val USER_DATA_FACEBOOK_ID = "USER_DATA_FACEBOOK_ID"
    val USER_DATA_FACEBOOK_TOKEN = "USER_DATA_FACEBOOK_TOKEN"
    val USER_DATA_FACEBOOK_EMAIL = "USER_DATA_FACEBOOK_EMAIL"
    val USER_DATA_FACEBOOK_NAME = "USER_DATA_FACEBOOK_NAME"
    fun getAppAccount(context: Context): Account? {
        val am = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accountsFromFirstApp = am.getAccountsByType(AppManager.ACCOUNT_TYPE)

        if (accountsFromFirstApp.size > 0) {
            return accountsFromFirstApp[0]
        }
        return null
    }

    /**
     * retrieve Tigo App Account User Id
     */
    fun getAppAccountUserId(context: Context): String {
        val am = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accountsFromFirstApp = am.getAccountsByType(AppManager.ACCOUNT_TYPE)

        try {
            return am.getUserData(accountsFromFirstApp[0], AppManager.USER_DATA_ID)
        } catch (e: IllegalArgumentException) {

            return ""
        }

    }

    fun removeAccount(context: Context) {
        val am = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accountsFromFirstApp = am.getAccountsByType(AppManager.ACCOUNT_TYPE)
        val type = am.getUserData(accountsFromFirstApp[0], AppManager.USER_TYPE)
        when(type) {
            "0" -> {
                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                val i = Intent(getApplicationContext(), LoginActivity::class.java)
                (context as Activity).finish()
                context.startActivity(i)
            }
            "1" -> {
                LoginManager.getInstance().logOut()
                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                val i = Intent(getApplicationContext(), LoginActivity::class.java)
                (context as Activity).finish()
                context.startActivity(i)
            }
            "2" -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                val mGoogleApiClient = GoogleApiClient.Builder(context)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build()
                mGoogleApiClient.connect()
                mGoogleApiClient.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(@Nullable bundle: Bundle?) {


                        if (mGoogleApiClient.isConnected) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                                // ...
                                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                                val i = Intent(getApplicationContext(), LoginActivity::class.java)
                                (context as Activity).finish()
                                context.startActivity(i)
                            }
                        }
                    }

                    override fun onConnectionSuspended(i: Int) {
                        Log.d("AppAcountManager", "Google API Client Connection Suspended")
                    }
                })

            }
        }
        am.removeAccount(accountsFromFirstApp[0], null, null)


    }

    /**
     * create Tigo calendar and retrieve calendar id
     * @param accountName user account name
     * *
     * @return created calendar id
     */


    fun saveAccountUser(context: Context, account: Account, user: User,type : Int) {


        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        accountManager.setUserData(account, AppManager.USER_DATA_ID, user._id)
        accountManager.setUserData(account, AppManager.USER_TYPE, type.toString())
        /* accountManager.setUserData(account, AppManager.USER_DATA_USERNAME, user.name)
         accountManager.setUserData(account, AppManager.USER_DATA_EMAIL, user.email)
         accountManager.setUserData(account, AppManager.USER_DATA_GOOGLE_ID, user.google?.id)
         accountManager.setUserData(account, AppManager.USER_DATA_GOOGLE_TOKEN, user.google?.token)
         accountManager.setUserData(account, AppManager.USER_DATA_GOOGLE_NAME, user.google?.name)
         accountManager.setUserData(account, AppManager.USER_DATA_GOOGLE_EMAIL, user.google?.email)
         accountManager.setUserData(account, AppManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)
         accountManager.setUserData(account, AppManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)
         accountManager.setUserData(account, AppManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)
         accountManager.setUserData(account, AppManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)*/
        accountManager.setUserData(account, AppManager.USER_DATA_VERSION, AppManager.CURRENT_USER_DATA_VERSION)
    }
    fun getSocket(application : Application) : Socket? {
        return (application as MyApplication).getSocket()
    }
    fun getUserDetail(userid: String) : User? {
        val userdetail = "USERDETAIL"
        var userinfo : User?=null
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

                        userinfo = user
                    }


                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(userdetail, "onError errorCode : " + anError.errorCode)
                                Log.d(userdetail, "onError errorBody : " + anError.errorBody)
                                Log.d(userdetail, "onError errorDetail : " + anError.errorDetail)

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(userdetail, "onError errorDetail : " + anError.errorDetail)

                            }
                        } else {
                            Log.d(userdetail, "onError errorMessage : " + e.message)

                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
        return userinfo
    }
}
