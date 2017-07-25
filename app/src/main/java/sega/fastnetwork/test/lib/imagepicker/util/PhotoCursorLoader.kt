package sega.fastnetwork.test.lib.imagepicker.util

import android.net.Uri
import android.provider.MediaStore

import android.provider.MediaStore.MediaColumns.MIME_TYPE

class PhotoCursorLoader {

    var uri: Uri
    var projection: Array<String>? = null
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    var sortOrder: String? = null

    var isShowGif: Boolean = false
        set(showGif) {
            field = showGif
            if (showGif) {
                selectionArgs = arrayOf(IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF)
            } else {
                selectionArgs = arrayOf(IMAGE_JPEG, IMAGE_PNG)
            }
        }
    private val IMAGE_PROJECTION = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.SIZE)

    init {
        //default ，默认配置
        isShowGif = true//展示gif
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        projection = IMAGE_PROJECTION
        selection = MIME_TYPE + "=? or " + MIME_TYPE + "=? " + if (isShowGif) "or $MIME_TYPE=?" else ""
        isShowGif = isShowGif
        selectionArgs = selectionArgs
        sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
        //        setSortOrder(MediaStore.Images.Media.DATE_MODIFIED);
    }


    companion object {

        private val IMAGE_JPEG = "image/jpeg"
        private val IMAGE_PNG = "image/png"
        private val IMAGE_GIF = "image/gif"
    }
}
