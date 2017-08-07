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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class AuthenticationService : Service() {

    private var mAuthenticator: Authenticator? = null

    override fun onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "SampleSyncAdapter Authentication Service started.")
        }
        mAuthenticator = Authenticator(this)
    }

    override fun onDestroy() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "SampleSyncAdapter Authentication Service stopped.")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent " + intent)
        }
        return mAuthenticator!!.iBinder
    }

    companion object {

        private val TAG = "AuthenticationService"
    }
}