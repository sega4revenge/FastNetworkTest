package finger.thuetot.vn.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
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
import finger.thuetot.vn.MyApplication
import finger.thuetot.vn.R
import finger.thuetot.vn.customview.CircularAnim
import finger.thuetot.vn.lib.smsverifycatcher.OnSmsCatchListener
import finger.thuetot.vn.lib.smsverifycatcher.SmsVerifyCatcher
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.LoginPresenter
import finger.thuetot.vn.util.Constants
import finger.thuetot.vn.util.Validation.validatePassword
import finger.thuetot.vn.util.Validation.validatePhone
import finger.thuetot.vn.util.Validation.validatePhone2
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.verify_phone_layout.view.*
import org.json.JSONException
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity(), LoginPresenter.LoginView, GoogleApiClient.OnConnectionFailedListener {
    override fun setCode(code: String) {
    }

    var mAccountManager: AccountManager? = null
    var account: Account? = null
    var user = User()
    var TAG = "Login Activity"
    var smsVerifyCatcher: SmsVerifyCatcher? = null

    var dialog: AlertDialog? = null

    private var callbackManager: CallbackManager? = null


    var v: View? = null
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
//        btn_forgot!!.setOnClickListener {
//            gotoforgot()
//        }
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
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(MyApplication.getGoogleApiHelper()!!.googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun login() {

        setError()


        var err = 0
        if (!validatePhone(phone_number!!.text.toString())) {
            err++
            phone_number!!.error = getString(R.string.st_errpass4)
        } else {
            if (!validatePhone2( phone_number.text.toString())!!) {
                err++
                phone_number.error = getString(R.string.st_errPhone)
            }
        }
        if (err == 0) {
            user.phone = phone_number.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token
            mLoginPresenter!!.login(user.phone!!)
        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()

        }
    }

    private fun setError() {
        phone_number.error = null

    }

    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {
        if (isLoginSuccessful) {
            dialog?.dismiss()
            CircularAnim.fullActivity(this@LoginActivity, progressBar)
                    .colorOrImageRes(R.color.color_background_button)
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    })
        } else {
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
            dialog?.dismiss()
        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
        }

    }

    override fun setErrorMessage(errorMessage: String, type: Int) {
        if (errorMessage.equals("404")) {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
            showSnackBarMessage(getString(R.string.err_usernotfound))

        }else if (errorMessage.equals("405")) {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
            showSnackBarMessage(getString(R.string.err_timeout))

        } else if (errorMessage.equals("401")) {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_singin).go()
            v!!.progressBar2.visibility = View.GONE
            v!!.progressBar3.visibility = View.GONE
            CircularAnim.show(v!!.login_verifycode).go()
            Snackbar.make(v!!.content_verify, getString(R.string.err_invalidcode), Snackbar.LENGTH_SHORT).show()
        }
        if (errorMessage == "201") {
            val dl_verifycode = AlertDialog.Builder(this)
            dl_verifycode.setCancelable(false)
            val inflater = layoutInflater
            v = inflater.inflate(R.layout.verify_phone_layout, null)
            dl_verifycode.setView(v)
            dialog = dl_verifycode.create()
            dialog?.show()
            //  val dg = dl_verifycode.show()
            v!!.btn_out.setOnClickListener() {
                progressBar.visibility = View.GONE
                CircularAnim.show(btn_singin).go()
                dialog?.dismiss()
            }
            v!!.send_code.setOnClickListener {
                v!!.edit_phonenumber!!.error = null
                var err = 0
                if (!validatePhone( v!!.edit_phonenumber.text.toString())) {
                    err++
                    v?.edit_phonenumber!!.error = getString(R.string.st_errpass4)
                } else {
                    if (!validatePhone2( v!!.edit_phonenumber.text.toString())!!) {
                        err++
                        v?.edit_phonenumber!!.error = getString(R.string.st_errPhone)
                    }
                }
                if (err == 0) {
                    CircularAnim.hide(v!!.send_code).go()
                    v!!.progressBar_dialog.visibility = View.VISIBLE
                    user.phone = v!!.edit_phonenumber.text.toString()
                    v!!.edit_phonenumber.isEnabled = false
                    v!!.edit_phonenumber.visibility = View.VISIBLE
                    mLoginPresenter!!.linkaccount(user, type)
                } else {
                    v!!.progressBar_dialog.visibility = View.GONE
                    CircularAnim.show(v!!.send_code).go()
                }
//                if (v!!.edit_phonenumber.toString().equals("")) {
//                    v!!.progressBar_dialog.visibility = View.GONE
//                    CircularAnim.show(v!!.send_code).go()
//                } else {
//                    CircularAnim.hide(v!!.send_code).go()
//                    v!!.progressBar_dialog.visibility = View.VISIBLE
//                    user.phone = v!!.edit_phonenumber.text.toString()
//                    v!!.edit_phonenumber.isEnabled = false
//                    v!!.edit_phonenumber.visibility = View.VISIBLE
//                    mLoginPresenter!!.linkaccount(user, type)
//                }
            }



            v!!.login_verifycode.setOnClickListener() {
                if(v!!.edit_verifycode.text.toString().equals("")){

                }else{
                    verifycode(v!!.edit_verifycode.text.toString())
                }
            }


        }
        if (errorMessage == "202") {
            if(type == 999)
            {
                val dl_verifycode = AlertDialog.Builder(this)
                dl_verifycode.setCancelable(false)
                val inflater = layoutInflater
                v = inflater.inflate(R.layout.verify_phone_layout, null)
                v!!.edit_phonenumber.setText(user.phone)
                v!!.edit_phonenumber.isEnabled = false
                v!!.send_code.visibility = View.GONE

                dl_verifycode.setView(v)
                dialog = dl_verifycode.create()
                dialog?.show()
                //  val dg = dl_verifycode.show()
                v!!.btn_out.setOnClickListener() {
                    progressBar.visibility = View.GONE
                    CircularAnim.show(btn_singin).go()
                    dialog?.dismiss()
                }
                v!!.send_code.visibility = View.GONE
                v!!.login_verifycode.setOnClickListener {
                    if(v!!.edit_verifycode.text.toString().equals("")){

                    }else{
                        verifycode(v!!.edit_verifycode.text.toString())
                    }

                }
                v!!.txt_huongdan.visibility = View.VISIBLE
                v!!.progressBar_dialog.visibility = View.GONE
                v!!.login_verifycode.isEnabled = true
            }
            else
            {
                v!!.txt_huongdan.visibility = View.VISIBLE
                v!!.progressBar_dialog.visibility = View.GONE
                v!!.login_verifycode.isEnabled = true
            }

           // showSnackBarMessage(errorMessage)
        }

        if (errorMessage == "0") {
            showSnackBarMessage(resources.getString(R.string.server_unreachable))
        }

    }

    private fun login_finish() {
        setError()
        var err = 0
        if (!validatePassword(v!!.edit_verifycode.text.toString())) {
            err++
            v!!.edit_verifycode.error = getString(R.string.st_errpass2)
        }
        if (err == 0) {
            mLoginPresenter!!.loginfinish(v!!.edit_phonenumber.text.toString(), v!!.edit_verifycode.text.toString())

        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(v!!.login_verifycode).go()
            showSnackBarMessage(getString(R.string.enter_valid))
        }
    }

    override fun getUserDetail(user: User) {
        AppManager.saveAccountUser(this, user, type)
        this.user = user
    }


    private fun gotoregister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
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
                showSnackBarMessage(getString(R.string.success))

            }
        }
    }
    private fun verifycode(code: String){
        CircularAnim.hide(v!!.login_verifycode).go()
        v!!.progressBar2.visibility = View.VISIBLE
        mLoginPresenter!!.register_finish(user,code,type)
    }
    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.status)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.

            val acct: GoogleSignInAccount? = result.signInAccount
            Log.e(TAG, "display name: " + acct!!.displayName)
            val tokenfirebase = FirebaseInstanceId.getInstance().token
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

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher = SmsVerifyCatcher(this@LoginActivity, OnSmsCatchListener<String> { message ->
            Log.e("message", message)
            val code = parseCode(message)//Parse verification code
            Log.e("code", code)
            v!!.edit_verifycode.setText(code)
            verifycode(code)
            // input_code.setText(code)//set code in edit text
            //then you can send verification code to server
        })
        smsVerifyCatcher?.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher?.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun parseCode(message: String): String {
        val p = Pattern.compile("\\b\\d{6}\\b")
        val m = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        return code
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




