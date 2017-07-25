package sega.fastnetwork.test.lib.imagepicker.showpicker

import android.content.Context
import android.support.annotation.DrawableRes
import android.widget.ImageView

import java.io.Serializable

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description: 加载图片接口
 * Date: 2017/4/6
 */
interface ImageLoaderInterface<T : ImageView> : Serializable {

    fun displayImage(context: Context, path: String, imageView: ImageView)

    fun displayImage(context: Context, @DrawableRes resId: Int?, imageView: ImageView)

    fun createImageView(context: Context): T
}
