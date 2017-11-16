package finger.thuetot.vn.util

import android.text.TextUtils
import android.util.Patterns

/**
 * Created by Sega on 23/03/2017.
 */


object Validation {

    fun validateFields(name: String?): Boolean {

        return !(TextUtils.isEmpty(name) || name!!.trim { it <= ' ' } == "")
    }
    fun validatePassword(name: String?): Boolean {

        return !(TextUtils.isEmpty(name) || name!!.trim { it <= ' ' } == "" || name.length < 6)
    }

    fun validateEmail(string: String?): Boolean {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches() || string!!.trim { it <= ' ' } == "")
    }
}
