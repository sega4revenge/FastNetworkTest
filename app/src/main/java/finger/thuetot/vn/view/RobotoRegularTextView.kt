package finger.thuetot.vn.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView



class RobotoRegularTextView : TextView {
    constructor(context: Context) : super(context) {
        applyCustomFont(context)
        isSelected = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyCustomFont(context)
        isSelected = true
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        applyCustomFont(context)
        isSelected = true
    }

    private fun applyCustomFont(context: Context) {
        val customFont = FontCache.getTypeface(FontCache.ROBOTO_REGULAR, context)
        typeface = customFont
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
        }
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        if (focused) {
            super.onWindowFocusChanged(focused)
        }
    }

    override fun isFocused(): Boolean {
        return true
    }
}