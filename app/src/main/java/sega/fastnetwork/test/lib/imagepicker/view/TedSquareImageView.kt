package sega.fastnetwork.test.lib.imagepicker.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet

import sega.fastnetwork.test.R


/**
 * Created by Gil on 09/06/2014.
 */
class TedSquareImageView : android.support.v7.widget.AppCompatImageView {

    internal var fit_mode: String? = null
    private var fore: Drawable? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TedBottomPickerImageView,
                0, 0)

        val foreground = a.getDrawable(R.styleable.TedBottomPickerImageView_foreground)
        if (foreground != null) {
            setForeground(foreground)
        }


        try {
            fit_mode = a.getString(R.styleable.TedBottomPickerImageView_fit_mode)

        } finally {
            a.recycle()
        }
    }


    //Squares the thumbnail
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        if ("height" == fit_mode) {
            setMeasuredDimension(heightMeasureSpec, heightMeasureSpec)

        } else {
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)

        }


        if (fore != null) {
            fore!!.setBounds(0, 0, measuredWidth, measuredHeight)
            invalidate()
        }


    }


    /**
     * Supply a Drawable that is to be rendered on top of all of the child views
     * in the frame layout.

     * @param drawable The Drawable to be drawn on top of the children.
     */
    override fun setForeground(drawable: Drawable?) {
        if (fore === drawable) {
            return
        }
        if (fore != null) {
            fore!!.callback = null
            unscheduleDrawable(fore)
        }

        fore = drawable

        if (drawable != null) {
            drawable.callback = this
            if (drawable.isStateful) {
                drawable.state = drawableState
            }
        }
        requestLayout()
        invalidate()
    }


    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === fore
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (fore != null)
            fore!!.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (fore != null && fore!!.isStateful) {
            fore!!.state = drawableState
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (fore != null) {
            fore!!.setBounds(0, 0, w, h)
            invalidate()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (fore != null) {
            fore!!.draw(canvas)
        }
    }


}
