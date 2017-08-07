package sega.fastnetwork.test.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import sega.fastnetwork.test.activity.LoginActivity
import java.util.*

/**
 * Created by Sega on 23/03/2017.
 */


class SessionManager
(
        // Editor for Shared preferences


        // Context
        internal var _context: Context) {

    // Shared Preferences
    internal var pref: SharedPreferences

    // Shared pref mode
    internal var PRIVATE_MODE = 0

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

    }
    val name: String
        get() = pref.getString(KEY_NAME, "sega")
    val token: String
        get() = pref.getString(KEY_TOKEN, null)
    val profilePic: String
        get() = pref.getString(KEY_PHOTO, null)

    fun setName(name: String) {
        pref.edit().putString(KEY_NAME, name)
        pref.edit().commit()
    }

    fun setEmail(email: String) {

        pref.edit().putString(KEY_EMAIL, email)
        pref.edit().commit()
    }

    fun setPhone(phone: String) {

        pref.edit().putString(KEY_PHONE, phone)
        pref.edit().commit()
    }

    fun setProfilepic(profilepic: String) {

        pref.edit().putString(KEY_PHOTO, profilepic)
        pref.edit().commit()
    }
    /* public void createInfoUser(String age, String phone, String location){

        pref.edit().putBoolean(IS_LOGIN, true);
        pref.edit().putString(KEY_AGE, age);
        pref.edit().putString(KEY_PHONE, phone);
        pref.edit().putString(KEY_LOCATION, location);

        pref.edit().commit();
    }*/
    /**
     * Create login session
     */
    fun createLoginSession(name: String, email: String, logo: String, id: String) {
        // Storing login value as TRUE
        pref.edit().putBoolean(IS_LOGIN, true)

        // Storing name in pref
        pref.edit().putString(KEY_NAME, name)

        // Storing email in pref
        pref.edit().putString(KEY_EMAIL, email)
        pref.edit().putString(KEY_PHOTO, logo)
        pref.edit().putString(KEY_ID, id)


        // commit changes
        pref.edit().commit()
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    fun checkLogin(): Boolean {
        // Check login status
        var p = false
        if (!this.isLoggedIn) {
            p = true
            //            // user is not logged in redirect him to Login Activity
            //            Intent i = new Intent(_context, DetailActivity.class);
            //            // Closing all the Activities
            //            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //
            //            // Add new Flag to start new Activity
            //            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //
            //            // Staring Login Activity
            //            _context.startActivity(i);
        }
        return p

    }


    /**
     * Get stored session data
     */
    // return user
    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()


            user.put(KEY_NAME, pref.getString(KEY_NAME, null))
            user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null))
            user.put(KEY_PHOTO, pref.getString(KEY_PHOTO, null))
            user.put(KEY_ID, pref.getString(KEY_ID, null))
            return user
        }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        pref.edit().clear()
        pref.edit().commit()

        // After logout redirect user to Loing Activity
        val i = Intent(_context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Staring Login Activity
        _context.startActivity(i)  // Closing all the Activities

    }

    /**
     * Quick check for login
     */
    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    companion object {

        // Sharedpref file name
        private val PREF_NAME = "JobFindPref"

        // All Shared Preferences Keys
        private val IS_LOGIN = "IsLoggedIn"

        // User name (make variable public to access from outside)
        val KEY_NAME = "name"
        val KEY_TOKEN = "token"
        // Email address (make variable public to access from outside)
        val KEY_EMAIL = "email"
        val KEY_PHOTO = "photo"
        val KEY_ID = "id"
        val KEY_PHONE = "phone"

    }


}