package sega.fastnetwork.test.lib.MaterialChips.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.lib_detailed_chip_view.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.MaterialChips.model.ChipInterface
import sega.fastnetwork.test.lib.MaterialChips.util.ColorUtil
import sega.fastnetwork.test.lib.MaterialChips.util.LetterTileProvider


class DetailedChipView : RelativeLayout {
    // context
    private var mContext: Context? = null
    // xml elements
    // attributes
    private var mBackgroundColor: ColorStateList? = null

    constructor(context: Context) : super(context) {
        mContext = context
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init(attrs)
    }

    /**
     * Inflate the view according to attributes
     *
     * @param attrs the attributes
     */
    private fun init(attrs: AttributeSet?) {
        // inflate layout
        val rootView = View.inflate(context, R.layout.lib_detailed_chip_view, this)
        // butter knife

        // letter tile provider
        mLetterTileProvider = LetterTileProvider(mContext)

        // hide on first
        visibility = View.GONE
        // hide on touch outside
        hideOnTouchOutside()
    }

    /**
     * Hide the view on touch outside of it
     */
    private fun hideOnTouchOutside() {
        // set focusable
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
    }

    /**
     * Fade in
     */
    fun fadeIn() {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 200
        startAnimation(anim)
        visibility = View.VISIBLE
        // focus on the view
        requestFocus()
    }

    /**
     * Fade out
     */
    fun fadeOut() {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        startAnimation(anim)
        visibility = View.GONE
        // fix onclick issue
        clearFocus()
        isClickable = false
    }

    fun setAvatarIcon(icon: Drawable?) {
        avatar_icon!!.setImageDrawable(icon)
    }

    fun setAvatarIcon(icon: Bitmap) {
        avatar_icon!!.setImageBitmap(icon)
    }

    fun setAvatarIcon(icon: Uri?) {
        avatar_icon!!.setImageURI(icon)
    }

    fun setName(nameuser: String?) {
        name!!.text = nameuser
    }

    fun setInfo(infodetail: String?) {
        if (infodetail != null) {
            info!!.visibility = View.VISIBLE
            info!!.text = infodetail
        } else {
            info!!.visibility = View.GONE
        }
    }

    fun setTextColor(color: ColorStateList?) {
        name!!.setTextColor(color)
        info!!.setTextColor(ColorUtil.alpha(color!!.defaultColor, 150))
    }

    fun setBackGroundcolor(color: ColorStateList?) {
        mBackgroundColor = color
        content!!.background.setColorFilter(color!!.defaultColor, PorterDuff.Mode.SRC_ATOP)
    }

    val backgroundColor: Int
        get() = if (mBackgroundColor == null) ContextCompat.getColor(mContext!!, R.color.colorAccent) else mBackgroundColor!!.defaultColor

    fun setDeleteIconColor(color: ColorStateList?) {
        delete_button!!.drawable.mutate().setColorFilter(color!!.defaultColor, PorterDuff.Mode.SRC_ATOP)
    }

    fun setOnDeleteClicked(onClickListener: View.OnClickListener) {
        delete_button!!.setOnClickListener(onClickListener)
    }

    fun alignLeft() {
        val params = content!!.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = 0
        content!!.layoutParams = params
    }

    fun alignRight() {
        val params = content!!.layoutParams as RelativeLayout.LayoutParams
        params.rightMargin = 0
        content!!.layoutParams = params
    }

    class Builder(val context: Context) {
        var avatarUri: Uri? = null
        var avatarDrawable: Drawable? = null
        var name: String? = null
        var info: String? = null
        var textColor: ColorStateList? = null
        var backgroundColor: ColorStateList? = null
        var deleteIconColor: ColorStateList? = null

        fun avatar(avatarUri: Uri): Builder {
            this.avatarUri = avatarUri
            return this
        }

        fun avatar(avatarDrawable: Drawable): Builder {
            this.avatarDrawable = avatarDrawable
            return this
        }

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun info(info: String): Builder {
            this.info = info
            return this
        }

        fun chip(chip: ChipInterface): Builder {
            this.avatarUri = chip.getAvatarUri()
            this.avatarDrawable = chip.getAvatarDrawable()
            this.name = chip.getLabel()
            this.info = chip.getInfo()
            return this
        }

        fun textColor(textColor: ColorStateList?): Builder {
            this.textColor = textColor
            return this
        }

        fun backgroundColor(backgroundColor: ColorStateList?): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun deleteIconColor(deleteIconColor: ColorStateList?): Builder {
            this.deleteIconColor = deleteIconColor
            return this
        }

        fun build(): DetailedChipView {
            return DetailedChipView.newInstance(this)
        }
    }

    companion object {

        private val TAG = DetailedChipView::class.java.toString()
        // letter tile provider
        private var mLetterTileProvider: LetterTileProvider? = null

        private fun newInstance(builder: Builder): DetailedChipView {
            val detailedChipView = DetailedChipView(builder.context)
            // avatar
            if (builder.avatarUri != null)
                detailedChipView.setAvatarIcon(builder.avatarUri)
            else if (builder.avatarDrawable != null)
                detailedChipView.setAvatarIcon(builder.avatarDrawable)
            else
                detailedChipView.setAvatarIcon(mLetterTileProvider!!.getLetterTile(builder.name))

            // background color
            if (builder.backgroundColor != null)
                detailedChipView.setBackGroundcolor(builder.backgroundColor)

            // text color
            if (builder.textColor != null)
                detailedChipView.setTextColor(builder.textColor)
            else if (ColorUtil.isColorDark(detailedChipView.backgroundColor))
                detailedChipView.setTextColor(ColorStateList.valueOf(Color.WHITE))
            else
                detailedChipView.setTextColor(ColorStateList.valueOf(Color.BLACK))

            // delete icon color
            if (builder.deleteIconColor != null)
                detailedChipView.setDeleteIconColor(builder.deleteIconColor)
            else if (ColorUtil.isColorDark(detailedChipView.backgroundColor))
                detailedChipView.setDeleteIconColor(ColorStateList.valueOf(Color.WHITE))
            else
                detailedChipView.setDeleteIconColor(ColorStateList.valueOf(Color.BLACK))

            detailedChipView.setName(builder.name)
            detailedChipView.setInfo(builder.info)
            return detailedChipView
        }
    }
}
