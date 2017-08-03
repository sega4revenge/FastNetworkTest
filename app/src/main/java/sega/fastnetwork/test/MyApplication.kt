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
import android.graphics.BitmapFactory
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ConnectionQuality

/**
 * Created by amitshekhar on 22/03/16.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidNetworking.initialize(applicationContext)
        val options = BitmapFactory.Options()
        AndroidNetworking.setBitmapDecodeOptions(options)
        AndroidNetworking.enableLogging()

        AndroidNetworking.setConnectionQualityChangeListener { connectionQuality, _ ->
            if (connectionQuality == ConnectionQuality.EXCELLENT) {
                System.out.println("Tot")
                // do something
            } else if (connectionQuality == ConnectionQuality.POOR) {
                System.out.println("Duoc")
                // do something
            } else if (connectionQuality == ConnectionQuality.UNKNOWN) {
                System.out.println("Te")
                // do something
            }
        }


    }

    companion object {


        var instance: MyApplication? = null
            private set
    }

}
