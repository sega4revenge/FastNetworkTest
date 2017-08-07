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


import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import sega.fastnetwork.test.activity.LoginActivity

class Authenticator(private val mContext: Context) : AbstractAccountAuthenticator(mContext) {

    @Throws(NetworkErrorException::class)
    override fun addAccount(response: AccountAuthenticatorResponse,
                            accountType: String, authTokenType: String,
                            requiredFeatures: Array<String>, options: Bundle): Bundle {


        Log.v(TAG, "addAccount()")

        val intent = Intent(mContext, LoginActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
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
