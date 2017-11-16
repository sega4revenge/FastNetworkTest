package finger.thuetot.vn.manager

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by VinhNguyen on 11/5/2017.
 */
class PrefManager(context: Context) {
    private var PRIVATE_MODE = 0
    // Shared preferences file name
    private val PREF_NAME = "Thuetot-welcome"
    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    private var pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private var editor= pref.edit()
    private var _context = context


    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor?.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor?.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref!!.getBoolean(IS_FIRST_TIME_LAUNCH, false)
    }
}