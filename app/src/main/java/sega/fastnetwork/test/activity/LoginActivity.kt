package sega.fastnetwork.test.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.firebase.iid.FirebaseInstanceId
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.R
import sega.fastnetwork.test.manager.SessionManager
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.util.Utils


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    var TAG = "Login Activity"
    private var callbackManager: CallbackManager? = null
    var session: SessionManager? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 7
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        session = SessionManager(this)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)
        AppEventsLogger.activateApp(this)


        btn_singin!!.setOnClickListener {
            loginprocess()
        }
        btn_signup!!.setOnClickListener {
            register()
        }
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { `object`, response ->
                    // Application code
                    try {
                        Log.i("Response", response.toString())
                        val tokenfirebase = FirebaseInstanceId.getInstance().token
                        var email = response.jsonObject.getString("email")
                        if (email == "")
                            email = response.jsonObject.getString("id")
                        /*       session.setProfilepic(response.getJSONObject().getString("picture"));
                                String firstName = response.getJSONObject().getString("first_name");
                                String lastName = response.getJSONObject().getString("last_name");
                                gender = response.getJSONObject().getString("gender");*/
                        val name = response.jsonObject.getString("name")
                        val tk = response.jsonObject.getString("id")
                        val url = "https://graph.facebook.com/$tk/picture?type=large"
                        val user = User()
                        user.name = name
                        user.email = email
                        user.password = ""
                        user.phone = ("")
                        user.photoprofile = (url)
                        user.type = ("1")
                        user.tokenfirebase = (tokenfirebase)
                        /*mProgressbar.setVisibility(View.VISIBLE);*/
                        registerProcess(user)


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,email,first_name,last_name,gender, birthday, name,picture")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {}
        })
        btn_facebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        btn_google.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    fun loginprocess() {

        val tokenfirebase = FirebaseInstanceId.getInstance().token
        val jsonObject = JSONObject()
        try {

            jsonObject.put("email", email.text)
            jsonObject.put("password", password.text)
            jsonObject.put("tokenfirebase", tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "authenticate")

                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(localClassName, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(localClassName, " bytesSent : " + bytesSent)
                    Log.d(localClassName, " bytesReceived : " + bytesReceived)
                    Log.d(localClassName, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(localClassName, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                        session!!.createLoginSession(email.text.toString(), "", "", response!!.user!!._id!!)
                        System.out.println(response.user!!.name)
                    }


                    override fun onComplete() {


                        val intent = Intent(this@LoginActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(localClassName, e)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })

    }

    private fun registerProcess(user: User) {


        val jsonObject = JSONObject()
        try {

            jsonObject.put("name", user.name)
            jsonObject.put("email", user.email)
            jsonObject.put("password", user.password)
            jsonObject.put("photoprofile", user.photoprofile)
            jsonObject.put("type", user.type)
            jsonObject.put("tokenfirebase", user.tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "users")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(localClassName, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(localClassName, " bytesSent : " + bytesSent)
                    Log.d(localClassName, " bytesReceived : " + bytesReceived)
                    Log.d(localClassName, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onNext(response: Response?) {
                        Log.d(localClassName, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    }


                    override fun onComplete() {

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(localClassName, e)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }

    fun register() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            handleSignInResult(result)
        }
    }

    fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess);
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.

            val acct: GoogleSignInAccount? = result.signInAccount
            Log.e(TAG, "display name: " + acct!!.displayName)
            val tokenfirebase = FirebaseInstanceId.getInstance().token
            val user = User()
            user.name = acct.displayName
            user.email = acct.email
            user.password = ""
            user.phone = ""
            user.photoprofile = acct.photoUrl.toString()
            user.type = "1"
            user.tokenfirebase = tokenfirebase

            registerProcess(user)


            Log.e(TAG, "Name: " + user.name + ", email: " + user.email
                    + ", Image: " + user.photoprofile)


        } else {
            // Signed out, show unauthenticated UI.

        }
    }
    override fun onConnectionFailed(p0: ConnectionResult) {

        Log.d(TAG, "onConnectionFailed:" + p0)
    }

    override fun onStart() {
        super.onStart()
        val opr : OptionalPendingResult<GoogleSignInResult> =  Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            val result : GoogleSignInResult = opr.get()
            handleSignInResult(result)
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback { googleSignInResult -> handleSignInResult(googleSignInResult); }
        }
    }
}




