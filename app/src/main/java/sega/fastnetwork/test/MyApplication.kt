/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sega.fastnetwork.test

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.support.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ConnectionQuality
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import sega.fastnetwork.test.manager.GoogleApiHelper
import java.util.concurrent.TimeUnit


/**
 * Created by amitshekhar on 22/03/16.
 */
class MyApplication : Application() {
    private var mSocket: Socket? = null
    private var googleApiHelper: GoogleApiHelper? = null

    fun getSocket(): Socket? = mSocket
    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this);
        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build()
        AndroidNetworking.initialize(applicationContext, okHttpClient)
        val options = BitmapFactory.Options()
        AndroidNetworking.setBitmapDecodeOptions(options)
        AndroidNetworking.enableLogging()
        mSocket = IO.socket("http://45.77.36.109:8080").connect()
        AndroidNetworking.setConnectionQualityChangeListener { connectionQuality, _ ->
            when (connectionQuality) {
                ConnectionQuality.EXCELLENT -> System.out.println("Tot")

                ConnectionQuality.POOR -> System.out.println("Duoc")
                else -> {
                    System.out.println("Te")
                }
            }
        }
        googleApiHelper = GoogleApiHelper(instance!!)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    fun getGoogleApiHelperInstance(): GoogleApiHelper? = this.googleApiHelper

    companion object {
        var instance: MyApplication? = null
            private set

        fun getGoogleApiHelper(): GoogleApiHelper? = instance!!.getGoogleApiHelperInstance()
    }
}
