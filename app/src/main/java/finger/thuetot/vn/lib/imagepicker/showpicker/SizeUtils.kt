package finger.thuetot.vn.lib.imagepicker.showpicker

import android.content.Context

/**
 * Author: 姚智胜
 * Version: V1.0版本
 * Description: dp转换工具类
 * Date: 2017/4/17
 */

class SizeUtils {

    /**
     * dp转px

     * @param context 上下文
     * *
     * @param dpValue dp值
     * *
     * @return px值
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        val sizeUtils = SizeUtils()
    }

}
