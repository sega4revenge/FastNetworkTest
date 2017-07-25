package sega.fastnetwork.test.lib.imagepicker.showpicker

import android.content.Context
import android.widget.ImageView

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description: 加载图片方式
 * Date: 2017/4/6
 */
abstract class ImageLoader : ImageLoaderInterface<ImageView> {

    override fun createImageView(context: Context): ImageView {
        return ImageView(context)
    }

}
