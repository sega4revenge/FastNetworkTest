package sega.fastnetwork.test.lib.imagepicker.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Describe :自定义 Image 用来兼容 fresco
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
class GalleryImageView : android.support.v7.widget.AppCompatImageView {

    private val mOnImageViewListener: OnImageViewListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    interface OnImageViewListener {
        fun onDetach()

        fun onAttach()

        fun verifyDrawable(dr: Drawable): Boolean

        fun onDraw(canvas: Canvas)

        fun onTouchEvent(event: MotionEvent): Boolean
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mOnImageViewListener?.onDetach()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mOnImageViewListener?.onAttach()
    }

    override fun verifyDrawable(dr: Drawable): Boolean {
        if (mOnImageViewListener != null) {
            if (mOnImageViewListener.verifyDrawable(dr)) {
                return true
            }
        }
        return super.verifyDrawable(dr)
    }

    override fun onStartTemporaryDetach() {
        super.onStartTemporaryDetach()
        mOnImageViewListener?.onDetach()
    }

    override fun onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach()
        mOnImageViewListener?.onAttach()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mOnImageViewListener?.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mOnImageViewListener == null) {
            return super.onTouchEvent(event)
        }
        return mOnImageViewListener.onTouchEvent(event) || super.onTouchEvent(event)
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
