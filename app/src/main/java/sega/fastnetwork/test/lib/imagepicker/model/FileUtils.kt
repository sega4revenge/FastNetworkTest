package sega.fastnetwork.test.lib.imagepicker.model

import android.graphics.BitmapFactory

/**
 * Describe :
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-4.
 */
class FileUtils {

    private val IMAGE_STORE_FILE_NAME = "IMG_%s.jpg"

    companion object {

        private val TAG = "FileUtils"

        /**
         * 检查文件是否损坏
         * Check if the file is corrupted

         * @param filePath
         * *
         * @return
         */
        fun checkImgCorrupted(filePath: String): Boolean {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            return options.mCancel || options.outWidth == -1
                    || options.outHeight == -1
        }
    }


}

/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */
