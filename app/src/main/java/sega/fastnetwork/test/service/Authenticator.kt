/**
 * Copyright (c) 2012-2013, Gerald Garcia

 * This file is part of Andoid Caldav Sync Adapter Free.

 * Andoid Caldav Sync Adapter Free is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or at your option any later version.

 * Andoid Caldav Sync Adapter Free is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Andoid Caldav Sync Adapter Free.
 * If not, see <http:></http:>//www.gnu.org/licenses/>.

 */

package sega.fastnetwork.test.service


import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import sega.fastnetwork.test.activity.LoginActivity
import sega.fastnetwork.test.manager.AppAccountManager



class Authenticator(private val mContext: Context) : AbstractAccountAuthenticator(mContext) {


    @Throws(NetworkErrorException::class)
    override fun addAccount(response: AccountAuthenticatorResponse,
                            accountType: String,
                            authTokenType: String?,
                            requiredFeatures: Array<String>?,
                            options: Bundle?): Bundle? {
        val bundle = Bundle()
        val accountManager = mContext.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        val accounts = accountManager.getAccountsByType(AppAccountManager.ACCOUNT_TYPE)
        if (accounts.isEmpty()) {
            Log.v(TAG, "addAccount()")

            val intent = Intent(mContext, LoginActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

            bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        }
        else{
            val h = Handler(Looper.getMainLooper())
            h.post {Toast.makeText(mContext,"Already Login",Toast.LENGTH_LONG).show() }


        }


        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(response: AccountAuthenticatorResponse,
                                    account: Account, options: Bundle): Bundle? {
        // TODO Auto-generated method stub
        return null
    }

    override fun editProperties(response: AccountAuthenticatorResponse,
                                accountType: String): Bundle? {
        // TODO Auto-generated method stub
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(response: AccountAuthenticatorResponse,
                              account: Account, authTokenType: String, options: Bundle): Bundle? {
        // TODO Auto-generated method stub
        return null
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        // TODO Auto-generated method stub
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(response: AccountAuthenticatorResponse,
                             account: Account, features: Array<String>): Bundle? {
        // TODO Auto-generated method stub
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(response: AccountAuthenticatorResponse,
                                   account: Account, authTokenType: String, options: Bundle): Bundle? {
        // TODO Auto-generated method stub
        return null
    }

    companion object {

        private val TAG = "Authenticator"
    }

}
