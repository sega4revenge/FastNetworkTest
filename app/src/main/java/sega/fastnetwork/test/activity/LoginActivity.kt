package sega.fastnetwork.test.activity


import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircularAnim
import sega.fastnetwork.test.manager.AppAccountManager
import sega.fastnetwork.test.manager.SessionManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.LoginPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.util.Validation.validateFields
import sega.fastnetwork.test.view.LoginView


class LoginActivity : AppCompatActivity(), LoginView, GoogleApiClient.OnConnectionFailedListener {
    var mAccountManager: AccountManager? = null
    var account: Account? = null
    var user: User? = null
    var TAG = "Login Activity"
    private var callbackManager: CallbackManager? = null
    var session: SessionManager? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 7
    var mLoginPresenter: LoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        session = SessionManager(this)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)
        mAccountManager = AccountManager.get(this)

        val accountsFromFirstApp = mAccountManager!!.getAccountsByType(AppAccountManager.ACCOUNT_TYPE)
        if (accountsFromFirstApp.isNotEmpty()) {

            //            // User is already logged in. Take him to main activity
            val intent = Intent(this, HomeActivity::class.java)
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
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { _, response ->
                    // Application code
                    try {
                        Log.i("Response", response.toString())

                        val tokenfirebase = FirebaseInstanceId.getInstance().token


                        /*       session.setProfilepic(response.getJSONObject().getString("picture"));
                                String firstName = response.getJSONObject().getString("first_name");
                                String lastName = response.getJSONObject().getString("last_name");
                                gender = response.getJSONObject().getString("gender");*/
                        val id = response.jsonObject.getString("id")
                        val url = "https://graph.facebook.com/$id/picture?type=large"
                        val user = User()
                        user.facebook!!.name = response.jsonObject.getString("name")
                        user.facebook!!.email = response.jsonObject.getString("email")
                        user.facebook!!.id = id
                        user.facebook!!.token = AccessToken.getCurrentAccessToken().toString()
                        user.password = ""
                        user.facebook!!.photoprofile = (url)
                        user.tokenfirebase = (tokenfirebase)
                        mLoginPresenter!!.register(user, Constants.FACEBOOK)


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

            user.password = password.text.toString()
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
                    .colorOrImageRes(R.color.colorAccent)
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
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
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                finish()
                            }
                        })
            } else {
                CircularAnim.fullActivity(this@LoginActivity, btn_google)
                        .colorOrImageRes(R.color.white)
                        .go(object : CircularAnim.OnAnimationEndListener {
                            override fun onAnimationEnd() {
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                finish()
                            }
                        })
            }
        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
        }

    }

    override fun setErrorMessage(errorMessage: String) {

    }

    override fun getUserDetail(user: User) {
        account = Account(user.name, AppAccountManager.ACCOUNT_TYPE)
        if (mAccountManager!!.addAccountExplicitly(account, user.password, null)) {
            println("tao thanh cong")
            AppAccountManager.saveAccountUser(this, account!!, user)
        } else {
            account = AppAccountManager.getAppAccount(this)
            val accountManager = this.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
            accountManager.setUserData(account, AppAccountManager.USER_DATA_ID, user._id)
        }
        this.user = user
    }


    fun gotoregister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
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
            user.google!!.id = acct.id
            user.google!!.token = acct.idToken
            user.google!!.name = acct.displayName
            user.google!!.email = acct.email
            user.password = ""
            user.google!!.photoprofile = acct.photoUrl.toString()
            user.tokenfirebase = tokenfirebase
            mLoginPresenter!!.register(user, Constants.GOOGLE)
            Log.e(TAG, "Name: " + user.google!!.name + ", email: " + user.google!!.email
                    + ", Image: " + user.google!!.photoprofile)
        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + p0)
    }

    override fun onStart() {
        super.onStart()
        val opr: OptionalPendingResult<GoogleSignInResult> = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            val result: GoogleSignInResult = opr.get()
            handleSignInResult(result)
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback { googleSignInResult -> handleSignInResult(googleSignInResult); }
        }
    }

    private fun showSnackBarMessage(message: String?) {


        Snackbar.make(findViewById(R.id.root_login), message!!, Snackbar.LENGTH_SHORT).show()

    }

}




