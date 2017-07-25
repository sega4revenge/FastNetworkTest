package sega.fastnetwork.test.util

import android.content.Context
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import com.androidnetworking.error.ANError

/**
 * Created by sega4 on 12/06/2017.
 */

object Utils {

    fun getRootDirPath(context: Context): String {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file = ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
            return file.absolutePath
        } else {
            return context.applicationContext.filesDir.absolutePath
        }
    }

    fun logError(TAG: String, e: Throwable) {
        if (e is ANError) {
            val anError = e
            if (anError.errorCode != 0) {
                // received ANError from server
                // error.getErrorCode() - the ANError code from server
                // error.getErrorBody() - the ANError body from server
                // error.getErrorDetail() - just a ANError detail
                Log.d(TAG, "onError errorCode : " + anError.errorCode)
                Log.d(TAG, "onError errorBody : " + anError.errorBody)
                Log.d(TAG, "onError errorDetail : " + anError.errorDetail)
            } else {
                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                Log.d(TAG, "onError errorDetail : " + anError.errorDetail)
            }
        } else {
            Log.d(TAG, "onError errorMessage : " + e.message)
        }
    }

}