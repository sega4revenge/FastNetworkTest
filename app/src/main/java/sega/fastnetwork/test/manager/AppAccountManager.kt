package sega.fastnetwork.test.manager

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import sega.fastnetwork.test.model.User

/**
 * Created by sega4 on 27/07/2017.
 */

object AppAccountManager {
    val ACCOUNT_TYPE = "sega.fastnetwork.test"
    val USER_DATA_ID = "USER_DATA_ID"
    val USER_DATA_USERNAME = "USER_DATA_USERNAME"
    val USER_DATA_VERSION = "USER_DATA_VERSION"
    val CURRENT_USER_DATA_VERSION = "1"
    val USER_DATA_EMAIL = "USER_DATA_EMAIL"
    val USER_DATA_GOOGLE_ID = "USER_DATA_GOOGLE_ID"
    val USER_DATA_GOOGLE_TOKEN = "USER_DATA_GOOGLE_TOKEN"
    val USER_DATA_GOOGLE_EMAIL = "USER_DATA_GOOGLE_EMAIL"
    val USER_DATA_GOOGLE_NAME = "USER_DATA_GOOGLE_NAME"
    val USER_DATA_FACEBOOK_ID = "USER_DATA_FACEBOOK_ID"
    val USER_DATA_FACEBOOK_TOKEN = "USER_DATA_FACEBOOK_TOKEN"
    val USER_DATA_FACEBOOK_EMAIL = "USER_DATA_FACEBOOK_EMAIL"
    val USER_DATA_FACEBOOK_NAME = "USER_DATA_FACEBOOK_NAME"
    fun getAppAccount(context: Context): Account? {
        val am = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accountsFromFirstApp = am.getAccountsByType(AppAccountManager.ACCOUNT_TYPE)

        if (accountsFromFirstApp.size > 0) {
            return accountsFromFirstApp[0]
        }
        return null
    }

    /**
     * retrieve Tigo App Account User Id
     */
    fun getAppAccountUserId(context: Context): String {
        val am = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accountsFromFirstApp = am.getAccountsByType(AppAccountManager.ACCOUNT_TYPE)

        try {
            return am.getUserData(accountsFromFirstApp[0], AppAccountManager.USER_DATA_ID)
        } catch (e: IllegalArgumentException) {

            return ""
        }

    }

    /**
     * create Tigo calendar and retrieve calendar id
     * @param accountName user account name
     * *
     * @return created calendar id
     */


    fun saveAccountUser(context: Context, account: Account, user: User) {


        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        accountManager.setUserData(account, AppAccountManager.USER_DATA_ID, user._id)
        /* accountManager.setUserData(account, AppAccountManager.USER_DATA_USERNAME, user.name)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_EMAIL, user.email)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_GOOGLE_ID, user.google?.id)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_GOOGLE_TOKEN, user.google?.token)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_GOOGLE_NAME, user.google?.name)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_GOOGLE_EMAIL, user.google?.email)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)
         accountManager.setUserData(account, AppAccountManager.USER_DATA_FACEBOOK_EMAIL, user.facebook?.email)*/
        accountManager.setUserData(account, AppAccountManager.USER_DATA_VERSION, AppAccountManager.CURRENT_USER_DATA_VERSION)
    }


}
