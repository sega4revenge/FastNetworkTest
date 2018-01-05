package finger.thuetot.vn.activity


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import finger.thuetot.vn.R
import finger.thuetot.vn.customview.CircularAnim
import finger.thuetot.vn.lib.smsverifycatcher.OnSmsCatchListener
import finger.thuetot.vn.lib.smsverifycatcher.SmsVerifyCatcher
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.LoginPresenter
import finger.thuetot.vn.util.Constants
import finger.thuetot.vn.util.Validation.validateEmail
import finger.thuetot.vn.util.Validation.validateFields
import finger.thuetot.vn.util.Validation.validatePassword
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.verify_phone_layout.view.*
import java.util.regex.Pattern

/**
 * Created by sega4 on 23/05/2017.
 */

class RegisterActivity : AppCompatActivity(), LoginPresenter.LoginView {
    override fun setCode(code: String) {
        Snackbar.make(findViewById(R.id.root_register), code!!, Snackbar.LENGTH_INDEFINITE)
                .setDuration(10000)
                .setAction("OK", {
                })
                .show()    }

    var user = User()

    var dialog: AlertDialog? = null
    var v: View? = null
    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int) {
        if (isRegisterSuccessful) {
//            Log.e("toi day roi", "ne")
//            name.visibility = View.GONE
//            email.visibility = View.GONE
////            password.visibility = View.GONE
////            repassword.visibility = View.GONE
//            btn_join.visibility = View.GONE
//
//            input_code.visibility = View.VISIBLE
//            btn_finish.visibility = View.VISIBLE
//
//            showSnackBarMessage(getString(R.string.txt_checkmail))
//
//            progressBar.visibility = View.GONE

//            CircularAnim.show(btn_join).go()
        }
        if (type == 3) {
//            showSnackBarMessage("Register Success")
//            progressBar.visibility = View.GONE
//            CircularAnim.show(btn_finish).go()
            CircularAnim.fullActivity(this@RegisterActivity, progressBar)
                    .colorOrImageRes(R.color.color_background_button)
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        }
                    })

        }
    }

    var smsVerifyCatcher: SmsVerifyCatcher? = null
    var mRegisterPresenter: LoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mRegisterPresenter = LoginPresenter(this)
        btn_join!!.setOnClickListener {
            CircularAnim.hide(btn_join)
                    .endRadius((progressBar.height / 2).toFloat())
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            progressBar.visibility = View.VISIBLE
                            /*
                                }*/
                            register()
                        }
                    })
        }

        btn_backtologin!!.setOnClickListener {
            gotologin()
        }
    }


    private fun register_finish() {
        setError()
        var err = 0
        if (!validatePassword( v!!.edit_verifycode.text.toString())) {
            err++
            v?.edit_verifycode!!.error = getString(R.string.st_errpass2)
        }
        if (err == 0) {
            mRegisterPresenter!!.register_finish(user,  v!!.edit_verifycode.text.toString(), 0)

        } else {
            v?.progressBar2?.visibility = View.GONE
            CircularAnim.show(v!!.login_verifycode).go()
            showSnackBarMessage(getString(R.string.enter_valid))
        }
    }


    private fun register() {

        setError()


        var err = 0

        if (!validateFields(txt_phone!!.text.toString())) {

            err++
            txt_phone!!.error = getString(R.string.st_errpass)
        }
        if (!validateFields(name!!.text.toString())) {

            err++
            name!!.error = getString(R.string.st_errpass)
        }

        if (!validateEmail(email!!.text.toString())) {

            err++
            email!!.error = getString(R.string.invalid_email)
        }

//        if (!validatePassword(password!!.text.toString())) {
//
//            err++
//            password!!.error = getString(R.string.st_errpass2)
//        }
//        if (password!!.text.toString() != repassword!!.text.toString() || repassword!!.text.toString() == "") {
//
//            err++
//
//            repassword!!.error = getString(R.string.err_repass)
//
//        }


        if (err == 0) {
//            progressBar.visibility = View.GONE
//            CircularAnim.show(btn_join).go()

            user.phone = txt_phone.text.toString()
            user.name = name.text.toString()
            user.hashed_password = ""
            user.email = email.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token

            mRegisterPresenter!!.linkaccount(user, 0)


        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_join).go()
            showSnackBarMessage(getString(R.string.enter_valid))
        }
    }

    private fun gotologin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun setError() {

        name!!.error = null
        email!!.error = null
//        password!!.error = null
//        repassword!!.error = null
        input_code!!.error = null
        txt_phone!!.error = null
    }


    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {
        progressBar.visibility = View.GONE
        CircularAnim.show(btn_join).go()
    }

    override fun setErrorMessage(errorMessage: String, type: Int) {

        if (errorMessage == "202") {


            val dl_verifycode = AlertDialog.Builder(this)
            dl_verifycode.setCancelable(false)
            val inflater = layoutInflater
            v = inflater.inflate(R.layout.verify_phone_layout, null)
            v!!.edit_phonenumber.setText(user.phone)
            v!!.txt_huongdan.visibility = View.VISIBLE
            v!!.progressBar_dialog.visibility = View.GONE
            v!!.login_verifycode.isEnabled = true
            dl_verifycode.setView(v)
            dialog = dl_verifycode.create()
            dialog?.show()
            //  val dg = dl_verifycode.show()
            v!!.btn_out.setOnClickListener() {
                dialog?.dismiss()
            }
            v!!.progressBar_dialog.visibility = View.VISIBLE

            v!!.send_code.visibility = View.GONE
            v!!.edit_phonenumber.isEnabled = false
            v!!.login_verifycode.setOnClickListener() {
                CircularAnim.hide(v!!.login_verifycode).go()
                v!!.progressBar2.visibility = View.VISIBLE
                register_finish()
            }

        }else if(errorMessage == "409"){
            showSnackBarMessage(getString(R.string.err_phonealready))
        }else if(errorMessage == "401"){
            dialog?.dismiss()
            showSnackBarMessage(getString(R.string.err_invalidcode))
        }



        //if (type == 1) {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_join).go()
      //  }


    }

    override fun getUserDetail(user: User) {
        AppManager.saveAccountUser(this, user, Constants.LOCAL)
        this.user = user
    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher = SmsVerifyCatcher(this, OnSmsCatchListener<String> { message ->
            Log.e("message", message)
            val code = parseCode(message)//Parse verification code
            Log.e("code", code)
            v!!.edit_verifycode.setText(code)//set code in edit text
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

    private fun showSnackBarMessage(message: String?) {


        Snackbar.make(findViewById(R.id.root_register), message!!, Snackbar.LENGTH_SHORT).show()

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

    public override fun onDestroy() {
        super.onDestroy()
        mRegisterPresenter?.cancelRequest()
    }
}