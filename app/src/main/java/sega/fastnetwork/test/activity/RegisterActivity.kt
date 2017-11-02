package sega.fastnetwork.test.activity


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_register.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircularAnim
import sega.fastnetwork.test.lib.smsverifycatcher.OnSmsCatchListener
import sega.fastnetwork.test.lib.smsverifycatcher.SmsVerifyCatcher
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.LoginPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.util.Validation.validateEmail
import sega.fastnetwork.test.util.Validation.validateFields
import java.util.regex.Pattern

/**
 * Created by sega4 on 23/05/2017.
 */

class RegisterActivity : AppCompatActivity(), LoginPresenter.LoginView {

    var user = User()

    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int) {
        if(isRegisterSuccessful)
        {
            Log.e("toi day roi", "ne")
            name.visibility = View.GONE
           phone.visibility = View.GONE
            password.visibility = View.GONE
            repassword.visibility = View.GONE
            btn_join.visibility = View.GONE

            input_code.visibility = View.VISIBLE
            btn_finish.visibility = View.VISIBLE

            showSnackBarMessage("Check Phone")
            progressBar.visibility = View.GONE

//            CircularAnim.show(btn_join).go()
        }
        if(type == 3){
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
    var smsVerifyCatcher : SmsVerifyCatcher?= null
    var mRegisterPresenter: LoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        smsVerifyCatcher = SmsVerifyCatcher(this, OnSmsCatchListener<String> { message ->
            Log.e("message", message)
            val code = parseCode(message)//Parse verification code
            Log.e("code", code)
            input_code.setText(code)//set code in edit text
            //then you can send verification code to server
        })
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
        if (!validateFields(input_code!!.text.toString())) {
            err++
            password!!.error = getString(R.string.st_errpass)
        }
        if (err == 0) {
            mRegisterPresenter!!.register_finish(user,input_code.text.toString(),0)

        } else {
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_join).go()
            showSnackBarMessage(getString(R.string.enter_valid))
        }
    }


    private fun register() {

        setError()


        var err = 0

        if (!validateFields(name!!.text.toString())) {

            err++
            name!!.error = getString(R.string.st_errpass)
        }

        if (!validateEmail(phone!!.text.toString())) {

            err++
            phone!!.error = getString(R.string.invalid_email)
        }

        if (!validateFields(password!!.text.toString())) {

            err++
            password!!.error = getString(R.string.st_errpass)
        }
        if (password!!.text.toString() != repassword!!.text.toString()||repassword!!.text.toString()=="") {

            err++

            repassword!!.error = getString(R.string.st_errpass)

        }
        if (err == 0) {

            user.name = name.text.toString()
            user.hashed_password = password.text.toString()
            user.email = phone.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token
            btn_phone.visibility = View.VISIBLE
            input_phone.visibility = View.VISIBLE
            name.visibility = View.GONE
            phone.visibility = View.GONE
            password.visibility = View.GONE
            repassword.visibility = View.GONE
            btn_join.visibility = View.GONE
            btn_phone.setOnClickListener {
                user.phone = input_phone.text.toString()
                mRegisterPresenter!!.linkaccount(user,0)
            }

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
        phone!!.error = null
        password!!.error = null
        repassword!!.error = null
        input_code!!.error = null
    }



    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {
        progressBar.visibility = View.GONE
        CircularAnim.show(btn_join).go()
    }

    override fun setErrorMessage(errorMessage: String, type: Int) {
        if(errorMessage == "202") {
            input_code.visibility = View.VISIBLE
            btn_finish.visibility = View.VISIBLE
            input_phone.visibility = View.GONE
            btn_phone.visibility = View.GONE
            btn_finish!!.setOnClickListener {
                CircularAnim.hide(btn_finish)
                        .endRadius((progressBar.height / 2).toFloat())
                        .go(object : CircularAnim.OnAnimationEndListener {
                            override fun onAnimationEnd() {
                                progressBar.visibility = View.VISIBLE
                                /*
                                    }*/
                                register_finish()
                            }
                        })
            }
        }

        showSnackBarMessage(errorMessage)

        if(type == 1){
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_join).go()
        }
        else if (type == 2 ){
            progressBar.visibility = View.GONE
            CircularAnim.show(btn_finish).go()
        }



    }

    override fun getUserDetail(user: User) {
        AppManager.saveAccountUser(this, user, Constants.LOCAL)
        this.user = user
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