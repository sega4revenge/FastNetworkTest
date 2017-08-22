package sega.fastnetwork.test.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_forgot_password.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircularAnim
import sega.fastnetwork.test.presenter.ForgotPwPresenter
import sega.fastnetwork.test.util.Validation

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
            layout_forgot_email.visibility = View.GONE
        }
        else {
            if (isForgotPwSuccessful) {
                showSnackBarMessage("Success")
            }
            startActivity(Intent(this@ForgotPassword,LoginActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
        }

    }

    var mForgotPwPresenter: ForgotPwPresenter? = null
    var email : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
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

    private fun newpw() {
        var err = 0

        var newpw = forgot_newpw.text.toString()
        var code = forgot_code.text.toString()
        if (!Validation.validateFields(forgot_newpw!!.text.toString())) {
            err++
            forgot_newpw!!.error = "New password should not be empty !"
        }
        if (!Validation.validateFields(forgot_code!!.text.toString())) {
            err++
            forgot_code!!.error = "Code should not be empty !"
        }
        if (forgot_repassword!!.text.toString() != forgot_newpw!!.text.toString()||forgot_repassword!!.text.toString()=="") {
            err++
            forgot_repassword!!.error = "Repassword do not match or empty!"

        }
        if(err==0){
            mForgotPwPresenter!!.newpw(newpw, code, email!!)
        }
        else {
            forgot_progressBar.visibility = View.GONE
            CircularAnim.show(forgot_action_newpw).go()
//            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun forgot() {
        var err = 0

        email = forgot_email.text.toString()
        if (!Validation.validateFields(forgot_email!!.text.toString())) {
            err++
            forgot_email!!.error = "Email should not be empty !"
        }
        if (!Validation.validateEmail(forgot_email!!.text.toString())) {
            err++
            forgot_email!!.error = "Invalid email address !"
        }
        if(err==0){
            mForgotPwPresenter!!.forgot(email!!)
        }
        else {
            forgot_progressBar.visibility = View.GONE
            CircularAnim.show(forgot_action_email).go()
//            showSnackBarMessage("Enter Valid Details !")
        }

    }

    private fun showSnackBarMessage(message: String?) {
        Snackbar.make(findViewById(R.id.root_forgotpw), message!!, Snackbar.LENGTH_SHORT).show()
    }
}
