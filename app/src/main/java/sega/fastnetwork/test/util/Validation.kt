package sega.fastnetwork.test.util

import android.text.TextUtils
import android.util.Patterns

/**
 * Created by Sega on 23/03/2017.
 */


object Validation {

    fun validateFields(name: String?): Boolean {

        return !(TextUtils.isEmpty(name) || name!!.trim { it <= ' ' } == "")
    }

    fun validateEmail(string: String?): Boolean {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches() || string!!.trim { it <= ' ' } == "")
    }
}
