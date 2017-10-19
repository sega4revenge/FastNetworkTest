package sega.fastnetwork.test.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import sega.fastnetwork.test.MyApplication
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircularAnim
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.LoginPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.util.Validation.validateFields


class LoginActivity : AppCompatActivity(), LoginPresenter.LoginView, GoogleApiClient.OnConnectionFailedListener {
    var mAccountManager: AccountManager? = null
    var account: Account? = null
    var user: User? = null
    var TAG = "Login Activity"
    private var callbackManager: CallbackManager? = null


    private val RC_SIGN_IN = 7
    var mLoginPresenter: LoginPresenter? = null
    var type: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)

        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)

        mAccountManager = AccountManager.get(this)

        val accountsFromFirstApp = mAccountManager!!.getAccountsByType(AppManager.ACCOUNT_TYPE)
        if (accountsFromFirstApp.isNotEmpty()) {

            //            // User is already logged in. Take him to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        AppEventsLogger.activateApp(this)
        mLoginPresenter = LoginPresenter(this)

        btn_singin!!.setOnClickListener {
            CircularAnim.hide(btn_singin)
                    .endRadius((progressBar.height / 2).toFloat())
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            progressBar.visibility = View.VISIBLE
                            /*
                                }*/
                            login()
                        }
                    })
        }
        btn_signup!!.setOnClickListener {
            gotoregister()
        }
//================================fogot==============================
        btn_forgot!!.setOnClickListener {
            gotoforgot()
        }
        //==============================================================


        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { _, response ->
                    // Application code
                    try {
                        Log.i("Response", response.toString())

                        val tokenfirebase = FirebaseInstanceId.getInstance().token
                        val id = response.jsonObject.getString("id")
                        val url = "https://graph.facebook.com/$id/picture?type=large"
                        val user = User()
                        user.facebook!!.name = response.jsonObject.getString("name")
                        user.facebook!!.email = response.jsonObject.getString("email")
                        user.facebook!!.id = id
                        user.facebook!!.token = AccessToken.getCurrentAccessToken().toString()
                        user.hashed_password = ""
                        user.facebook!!.photoprofile = (url)
                        user.tokenfirebase = (tokenfirebase)
                        type = Constants.FACEBOOK
                        mLoginPresenter!!.register(user, type)


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
                println("loi cmnr")
            }

            override fun onError(error: FacebookException) {
                Log.i("Error", error.message)
            }
        })
        btn_facebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        }



        btn_google.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(MyApplication.getGoogleApiHelper()?.googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun login() {

        setError()


        var err = 0
        if (!validateFields(email!!.text.toString())) {
            err++
            email!!.error = "Email should not be empty !"
        }
        if (!validateFields(password!!.text.toString())) {
            err++
            password!!.error = "Password should not be empty !"
        }
        if (err == 0) {
            val user = User()
            user.hashed_password = password.text.toString()
            user.email = email.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token
            mLoginPresenter!!.login(email.text.toString(), password.text.toString())


        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()

        }
    }

    private fun setError() {
        email.error = null
        password.error = null
    }

    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {
        if (isLoginSuccessful)
            CircularAnim.fullActivity(this@LoginActivity, progressBar)
                    .colorOrImageRes(R.color.color_background_button)
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    })
        else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
        }
    }

    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int) {
        if (isRegisterSuccessful) {
            if (type == 1) {
                CircularAnim.fullActivity(this@LoginActivity, btn_facebook)
                        .colorOrImageRes(R.color.white)
                        .go(object : CircularAnim.OnAnimationEndListener {
                            override fun onAnimationEnd() {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        })
            } else {
                CircularAnim.fullActivity(this@LoginActivity, btn_google)
                        .colorOrImageRes(R.color.white)
                        .go(object : CircularAnim.OnAnimationEndListener {
                            override fun onAnimationEnd() {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        })
            }
        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
        }

    }

    override fun setErrorMessage(errorMessage: String, type: Int) {
        showSnackBarMessage(errorMessage)
    }

    override fun getUserDetail(user: User) {
        AppManager.saveAccountUser(this, user, type)
        this.user = user
    }


    private fun gotoregister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)

        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)

    }

    private fun gotoforgot() {
        startActivityForResult(Intent(this@LoginActivity, ForgotPassword::class.java), Constants.FOTGOTPASSWORD)
//        finish()
        overridePendingTransition(0, 0)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            handleSignInResult(result)
        } else if (requestCode == Constants.FOTGOTPASSWORD) {
            Log.e("requestCode: ", "OK ne")
            if (resultCode == Activity.RESULT_OK) {
                showSnackBarMessage("Successsssss")

            }
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.

            val acct: GoogleSignInAccount? = result.signInAccount
            Log.e(TAG, "display name: " + acct!!.displayName)
            val tokenfirebase = FirebaseInstanceId.getInstance().token
            val user = User()
            user.google!!.id = acct.id
            user.google!!.token = acct.idToken
            user.google!!.name = acct.displayName
            user.google!!.email = acct.email
            user.hashed_password = ""
            user.google!!.photoprofile = acct.photoUrl.toString()
            user.tokenfirebase = tokenfirebase
            type = Constants.GOOGLE
            mLoginPresenter!!.register(user, type)
            Log.e(TAG, "Name: " + user.google!!.name + ", email: " + user.google!!.email
                    + ", Image: " + user.google!!.photoprofile)
        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + p0)
    }

    /* override fun onStart() {
         super.onStart()
         val opr: OptionalPendingResult<GoogleSignInResult> = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
         if (opr.isDone) {
             // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
             // and the GoogleSignInResult will be available instantly.
             Log.d(TAG, "Got cached sign-in")
             val result: GoogleSignInResult = opr.get()
             handleSignInResult(result)
         } else {
             // If the user has not previously signed in on this device or the sign-in has expired,
             // this asynchronous branch will attempt to sign in the user silently.  Cross-device
             // single sign-on will occur in this branch.

             opr.setResultCallback { googleSignInResult -> handleSignInResult(googleSignInResult); }
         }
     }
 */
    private fun showSnackBarMessage(message: String?) {


        Snackbar.make(findViewById(R.id.root_login), message!!, Snackbar.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        mLoginPresenter?.cancelRequest()
    }

    /*override fun onPause() {
        super.onPause()

        // Your application logic
        // ...
        // ...

     *//*   MyApplication.getGoogleApiHelper()?.googleApiClient?.disconnect()*//*
    }

    override fun onStop() {
        super.onStop()
        // stop GoogleApiClient
        if (MyApplication.getGoogleApiHelper()?.googleApiClient?.isConnected!!) {
            MyApplication.getGoogleApiHelper()?.googleApiClient?.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MyApplication.getGoogleApiHelper()?.googleApiClient?.isConnected!!) {
            MyApplication.getGoogleApiHelper()?.googleApiClient?.disconnect()
        }
    }*/

}




