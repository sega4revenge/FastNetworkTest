package finger.thuetot.vn.lib.imagepicker.model

import android.content.Context
import android.database.Cursor

import java.util.ArrayList



import android.provider.BaseColumns._ID
import android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.Images.ImageColumns.BUCKET_ID
import android.provider.MediaStore.MediaColumns.DATA
import android.provider.MediaStore.MediaColumns.DATE_ADDED
import android.provider.MediaStore.MediaColumns.SIZE
import finger.thuetot.vn.R

object Data {
    val INDEX_ALL_PHOTOS = 0

    fun getDataFromCursor(context: Context, data: Cursor, checkImageStatus: Boolean): List<PhotoDirectory> {
        val directories = ArrayList<PhotoDirectory>()
        val photoDirectoryAll = PhotoDirectory()
        photoDirectoryAll.name = context.getString(R.string.all_photo)
        photoDirectoryAll.setId("ALL")

        while (data.moveToNext()) {

            val imageId = data.getInt(data.getColumnIndexOrThrow(_ID))
            val bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID))
            val name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME))
            val path = data.getString(data.getColumnIndexOrThrow(DATA))
            val size = data.getLong(data.getColumnIndexOrThrow(SIZE))

            if (checkImageStatus) {
                if (!FileUtils.checkImgCorrupted(path)) {
                    val photoDirectory = PhotoDirectory()
                    photoDirectory.setId(bucketId)
                    photoDirectory.name = name

                    if (!directories.contains(photoDirectory)) {
                        photoDirectory.coverPath = path
                        photoDirectory.addPhoto(imageId, path)
                        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)))
                        directories.add(photoDirectory)
                    } else {
                        directories[directories.indexOf(photoDirectory)].addPhoto(imageId, path, size)
                    }

                    photoDirectoryAll.addPhoto(imageId, path, size)
                }
            } else {

                val photoDirectory = PhotoDirectory()
                photoDirectory.setId(bucketId)
                photoDirectory.name = name

                if (!directories.contains(photoDirectory)) {
                    photoDirectory.coverPath = path
                    photoDirectory.addPhoto(imageId, path)
                    photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)))
                    directories.add(photoDirectory)
                } else {
                    directories[directories.indexOf(photoDirectory)].addPhoto(imageId, path, size)
                }

                photoDirectoryAll.addPhoto(imageId, path, size)
            }


        }
        if (photoDirectoryAll.photoPaths.size > 0) {
            photoDirectoryAll.coverPath = photoDirectoryAll.photoPaths[0]
        }
        directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll)

        return directories
    }

}
