package sega.fastnetwork.test.lib.imagepicker.showpicker

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description: picker点击事件监听
 * Date: 2017/4/15
 */

interface ImageShowPickerListener {

    fun addOnClickListener(remainNum: Int)

    fun picOnClickListener(list: List<ImageShowPickerBean>, position: Int, remainNum: Int)

    fun delOnClickListener(position: Int, remainNum: Int)
}
