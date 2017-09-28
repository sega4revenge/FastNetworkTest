package sega.fastnetwork.test.activity

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_change_password.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircularAnim
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.ChangePasswordPresenter
import sega.fastnetwork.test.util.Validation

class ChangePasswordActivity : AppCompatActivity(), ChangePasswordPresenter.ChangePasswordView {
    override fun setErrorMessage(errorMessage: String) {
        showSnackBarMessage(errorMessage)
        CircularAnim.show(btnChangePassword).go()
    }

    override fun getUserDetail(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isgetUserDetailSuccess(success: Boolean) {
//        finish()
        showSnackBarMessage("Change Success")
        CircularAnim.show(btnChangePassword).go()
    }

    var mChangePasswordPresenter: ChangePasswordPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar_changepass)
        toolbar_changepass.inflateMenu(R.menu.uploadproduct_menu)
        toolbar_changepass.setTitleTextColor(Color.WHITE)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.back_arrow)
        supportActionBar!!.title = "Change Password"
        mChangePasswordPresenter = ChangePasswordPresenter(this)
        btnChangePassword!!.setOnClickListener {
            CircularAnim.hide(btnChangePassword)
                    .endRadius((progressBar_changepassword.height / 2).toFloat())
                    .go(object : CircularAnim.OnAnimationEndListener {
                        override fun onAnimationEnd() {
                            progressBar_changepassword.visibility = View.VISIBLE
                            /*
                                }*/
                            changepassword()
                        }
                    })

        }
    }

    private fun changepassword() {
        setError()
        var err = 0


        if (!Validation.validateFields(oldPassword!!.text.toString())) {

            err++
            oldPassword!!.error = "Old password should not be empty !"
        }
        if (!Validation.validateFields(newPassword!!.text.toString())) {

            err++
            newPassword!!.error = "Password should not be empty !"
        }
        if (newPassword!!.text.toString() != confirmNewPassword!!.text.toString() || confirmNewPassword!!.text.toString() == "") {

            err++

            confirmNewPassword!!.error = "Password do not match or empty!"

        }
        if (err == 0) {
            Log.e("OK",AppManager.getAppAccountUserId(this) + " " + oldPassword.text.toString() + " " + newPassword.text.toString())
            mChangePasswordPresenter!!.changepassword(AppManager.getAppAccountUserId(this), oldPassword.text.toString(), newPassword.text.toString())

        } else {
            progressBar_changepassword.visibility = View.GONE
            CircularAnim.show(btnChangePassword).go()
            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun showSnackBarMessage(message: String?) {

        Snackbar.make(findViewById(R.id.root_changepassword), message!!, Snackbar.LENGTH_SHORT).show()

    }

    private fun setError() {
        oldPassword!!.error = null
        newPassword!!.error = null
        confirmNewPassword!!.error = null
    }
}
