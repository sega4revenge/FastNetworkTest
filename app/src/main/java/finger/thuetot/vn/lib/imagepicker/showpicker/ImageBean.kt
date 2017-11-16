package finger.thuetot.vn.lib.imagepicker.showpicker

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description:
 * Date: 2017/4/10
 */

class ImageBean(private val url: String) : ImageShowPickerBean() {

    private val resId: Int = 0


    override fun setImageShowPickerUrl(): String {
        return url
    }

    override fun setImageShowPickerDelRes(): Int {
        return resId
    }
}
