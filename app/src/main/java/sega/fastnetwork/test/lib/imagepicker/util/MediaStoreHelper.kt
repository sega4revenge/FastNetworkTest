package sega.fastnetwork.test.lib.imagepicker.util


import android.content.Context
import sega.fastnetwork.test.lib.imagepicker.model.Data
import sega.fastnetwork.test.lib.imagepicker.model.PhotoDirectory


object MediaStoreHelper {

    fun getPhotoDirs(context: Context, resultCallback: PhotosResultCallback?, checkImageStatus: Boolean = true) {
        Thread(Runnable {
            val loader = PhotoCursorLoader()
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(loader.uri, loader.projection, loader.selection, loader.selectionArgs, loader.sortOrder) ?: return@Runnable

            val directories = Data.getDataFromCursor(context, cursor, checkImageStatus)
            cursor.close()
            resultCallback?.onResultCallback(directories)
        }).start()
    }




    interface PhotosResultCallback {
        fun onResultCallback(directories: List<PhotoDirectory>)
    }

}
/**
 * 第一种方式

 * @param context        Activity
 * *
 * @param resultCallback PhotosResultCallback
 */
