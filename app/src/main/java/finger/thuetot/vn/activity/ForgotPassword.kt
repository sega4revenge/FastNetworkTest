package finger.thuetot.vn.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_forgot_password.*
import finger.thuetot.vn.R
import finger.thuetot.vn.customview.CircularAnim
import finger.thuetot.vn.lib.smsverifycatcher.OnSmsCatchListener
import finger.thuetot.vn.lib.smsverifycatcher.SmsVerifyCatcher
import finger.thuetot.vn.presenter.ForgotPwPresenter
import finger.thuetot.vn.util.Validation
import java.util.regex.Pattern

class ForgotPassword : AppCompatActivity(), ForgotPwPresenter.ForgotPwView {
    override fun setErrorMessage(errorMessage: String, type: Int) {
        showSnackBarMessage(errorMessage)
        forgot_progressBar.visibility = View.GONE
        if(type == 0){
            CircularAnim.show(forgot_action_email).go()
        }
        else
        CircularAnim.show(forgot_action_newpw).go()

    }

    override fun onStart() {
        super.onStart()
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

    override fun isFotgotPwSuccessful(isForgotPwSuccessful: Boolean, type: Int) {
        if(type == 0){
//            if (isForgotPwSuccessful) {
//                showSnackBarMessage(resources.getString(R.string.message_signin))
//            }
            forgot_action_newpw.visibility = View.VISIBLE
            forgot_newpw.visibility = View.VISIBLE
            forgot_code.visibility = View.VISIBLE
            forgot_repassword.visibility = View.VISIBLE

            forgot_action_email.visibility = View.GONE
            email.visibility = View.GONE
        }
        else {
            if (isForgotPwSuccessful) {
//                showSnackBarMessage("Success")
            }
            setResult(Activity.RESULT_OK,Intent())
//            startActivity(Intent(this@ForgotPassword,LoginActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
        }

    }
    var smsVerifyCatcher : SmsVerifyCatcher ?= null
    var mForgotPwPresenter: ForgotPwPresenter? = null
    var phone_number : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        smsVerifyCatcher = SmsVerifyCatcher(this, OnSmsCatchListener<String> { message ->
            Log.e("message", message)
            val code = parseCode(message)//Parse verification code
            Log.e("code", code)
            forgot_code.setText(code)//set code in edit text
            //then you can send verification code to server
        })
        mForgotPwPresenter = ForgotPwPresenter(this)
        forgot_action_newpw.visibility = View.GONE
        forgot_newpw.visibility = View.GONE
        forgot_code.visibility = View.GONE
        forgot_repassword.visibility = View.GONE
        forgot_action_email.setOnClickListener {
            CircularAnim.hide(forgot_action_email)
                    .endRadius((forgot_progressBar.height / 2).toFloat())
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            forgot_progressBar.visibility = View.VISIBLE
                            forgot()
                        }
                    })
        }
        forgot_action_newpw.setOnClickListener {
            CircularAnim.hide(forgot_action_newpw)
                    .endRadius((forgot_progressBar.height / 2).toFloat())
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            forgot_progressBar.visibility = View.VISIBLE
                            newpw()
                        }
                    })
        }
        forgot_btn_backtologin.setOnClickListener {
            backtologin()
        }
    }

    private fun backtologin() {
        startActivity(Intent(this@ForgotPassword,LoginActivity::class.java))
        finish()
        overridePendingTransition(0, 0)

    }

    override fun onDestroy() {
        super.onDestroy()
        mForgotPwPresenter?.cancelRequest()
    }

    private fun newpw() {
        var err = 0

        var newpw = forgot_newpw.text.toString()
        var code = forgot_code.text.toString()
        if (!Validation.validateFields(forgot_newpw!!.text.toString())) {
            err++
            forgot_newpw!!.error = getString(R.string.st_errpass)
        }
        if (!Validation.validateFields(forgot_code!!.text.toString())) {
            err++
            forgot_code!!.error = getString(R.string.st_errpass)
        }
        if (forgot_repassword!!.text.toString() != forgot_newpw!!.text.toString()||forgot_repassword!!.text.toString()=="") {
            err++
            forgot_repassword!!.error = getString(R.string.st_errpass)

        }
        if(err==0){
            mForgotPwPresenter!!.newpw(newpw, code, phone_number!!)
        }
        else {
            forgot_progressBar.visibility = View.GONE
            CircularAnim.show(forgot_action_newpw).go()
//            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun forgot() {
        var err = 0

        phone_number = email.text.toString()
        if (!Validation.validateFields(email!!.text.toString())) {
            err++
            email!!.error = getString(R.string.st_errpass)
        }
        if(err==0){
            mForgotPwPresenter!!.forgot(phone_number!!)
        }
        else {
            forgot_progressBar.visibility = View.GONE
            CircularAnim.show(forgot_action_email).go()
//            showSnackBarMessage("Enter Valid Details !")
        }

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
    private fun showSnackBarMessage(message: String?) {
        Snackbar.make(findViewById(R.id.root_forgotpw), message!!, Snackbar.LENGTH_SHORT).show()
    }
}
