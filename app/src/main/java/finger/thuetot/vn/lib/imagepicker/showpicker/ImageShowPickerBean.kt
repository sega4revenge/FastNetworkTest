package finger.thuetot.vn.lib.imagepicker.showpicker

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description: 显示数据类的父类，必须继承于该类
 * Date: 2017/4/10
 */

abstract class ImageShowPickerBean {

    val imageShowPickerUrl: String
        get() = setImageShowPickerUrl()

    val imageShowPickerDelRes: Int
        get() = setImageShowPickerDelRes()

    /**
     * 为URL赋值，必须重写方法

     * @return
     */
    abstract fun setImageShowPickerUrl(): String

    /**
     * 为删除label赋值，必须重写方法

     * @return
     */
    abstract fun setImageShowPickerDelRes(): Int


}
