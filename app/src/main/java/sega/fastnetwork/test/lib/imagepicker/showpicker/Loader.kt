package sega.fastnetwork.test.lib.imagepicker.showpicker

import android.content.Context
import android.support.annotation.DrawableRes
import android.widget.ImageView

import com.bumptech.glide.Glide

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description:
 * Date: 2017/4/10
 */

class Loader : ImageLoader() {

    override fun displayImage(context: Context, path: String, imageView: ImageView) {
        Glide.with(context).load(path).into(imageView)

    }

    override fun displayImage(context: Context, @DrawableRes resId: Int?, imageView: ImageView) {
        imageView.setImageResource(resId!!)
    }

}
