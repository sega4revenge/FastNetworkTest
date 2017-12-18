package finger.thuetot.vn.lib.MaterialChips


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import finger.thuetot.vn.R
import finger.thuetot.vn.lib.MaterialChips.model.ChipInterface
import finger.thuetot.vn.lib.MaterialChips.util.LetterTileProvider
import finger.thuetot.vn.lib.MaterialChips.util.ViewUtil
import kotlinx.android.synthetic.main.lib_chip_view.view.*

class ChipView : RelativeLayout {
    // context
    private var mContext: Context? = null
    private var mLabel: String? = null
    private var mLabelColor: ColorStateList? = null
    private var mHasAvatarIcon = false
    private var mAvatarIconDrawable: Drawable? = null
    private var mAvatarIconUri: Uri? = null
    private var mDeletable = false
    private var mDeleteIcon: Drawable? = null
    private var mDeleteIconColor: ColorStateList? = null
    private var mBackgroundColor: ColorStateList? = null
    // letter tile provider
    private var mLetterTileProvider: LetterTileProvider? = null
    // chip
    private var mChip: ChipInterface? = null

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
        val rootView = View.inflate(context, R.layout.lib_chip_view, this)
        // butter knife

        // letter tile provider
        mLetterTileProvider = LetterTileProvider(mContext)

        // attributes
        if (attrs != null) {
            val a = mContext!!.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.ChipView,
                    0, 0)

            try {
                // label
                mLabel = a.getString(R.styleable.ChipView_label)
                mLabelColor = a.getColorStateList(R.styleable.ChipView_labelColor)
                // avatar icon
                mHasAvatarIcon = a.getBoolean(R.styleable.ChipView_hasAvatarIcon, false)
                val avatarIconId = a.getResourceId(R.styleable.ChipView_avatarIcon, NONE)
                if (avatarIconId != NONE) mAvatarIconDrawable = ContextCompat.getDrawable(mContext!!, avatarIconId)
                if (mAvatarIconDrawable != null) mHasAvatarIcon = true
                // delete icon
                mDeletable = a.getBoolean(R.styleable.ChipView_deletable, false)
                mDeleteIconColor = a.getColorStateList(R.styleable.ChipView_deleteIconColor)
                val deleteIconId = a.getResourceId(R.styleable.ChipView_deleteIcon, NONE)
                if (deleteIconId != NONE) mDeleteIcon = ContextCompat.getDrawable(mContext!!, deleteIconId)
                // background color
                mBackgroundColor = a.getColorStateList(R.styleable.ChipView_backgroundColor)
            } finally {
                a.recycle()
            }
        }

        // inflate
        inflateWithAttributes()
    }

    /**
     * Inflate the view
     */
    private fun inflateWithAttributes() {
        // label
        labelstring = mLabel
        if (mLabelColor != null)
            setLabelColor(mLabelColor!!)

        // avatar
        setHasAvatarIcon(mHasAvatarIcon)

        // delete button
        setDeletable(mDeletable)

        // background color
        if (mBackgroundColor != null)
            setChipBackgroundColor(mBackgroundColor!!)
    }

    fun inflate(chip: ChipInterface) {
        mChip = chip
        // label
        mLabel = mChip!!.label
        // icon
        mAvatarIconUri = mChip!!.avatarUri
        mAvatarIconDrawable = mChip!!.avatarDrawable

        // inflate
        inflateWithAttributes()
    }

    /**
     * Get label
     *
     * @return the label
     */
    /**
     * Set label
     *
     * @param label the label to set
     */
    var labelstring: String?
        get() = mLabel
        set(labelstring) {
            mLabel = labelstring
            label.text = labelstring
        }

    /**
     * Set label color
     *
     * @param color the color to set
     */
    private fun setLabelColor(color: ColorStateList) {
        mLabelColor = color
        label.setTextColor(color)
    }

    /**
     * Set label color
     *
     * @param color the color to set
     */
    fun setLabelColor(@ColorInt color: Int) {
        mLabelColor = ColorStateList.valueOf(color)
        label.setTextColor(color)
    }

    /**
     * Show or hide avatar icon
     *
     * @param hasAvatarIcon true to show, false to hide
     */
    private fun setHasAvatarIcon(hasAvatarIcon: Boolean) {
        mHasAvatarIcon = hasAvatarIcon

        if (!mHasAvatarIcon) {
            // hide icon
            icon.visibility = View.GONE
            // adjust padding
            if (delete_button.visibility == View.VISIBLE)
                label.setPadding(ViewUtil.dpToPx(12), 0, 0, 0)
            else
                label.setPadding(ViewUtil.dpToPx(12), 0, ViewUtil.dpToPx(12), 0)

        } else {
            // show icon
            icon.visibility = View.VISIBLE
            // adjust padding
            if (delete_button.visibility == View.VISIBLE)
                label.setPadding(ViewUtil.dpToPx(8), 0, 0, 0)
            else
                label.setPadding(ViewUtil.dpToPx(8), 0, ViewUtil.dpToPx(12), 0)

            // set icon
            if (mAvatarIconUri != null)
                icon.setImageURI(mAvatarIconUri)
            else if (mAvatarIconDrawable != null)
                icon.setImageDrawable(mAvatarIconDrawable)
            else
                icon.setImageBitmap(mLetterTileProvider!!.getLetterTile(labelstring))
        }
    }

    /**
     * Set avatar icon
     *
     * @param avatarIcon the icon to set
     */
    fun setAvatarIcon(avatarIcon: Drawable) {
        mAvatarIconDrawable = avatarIcon
        mHasAvatarIcon = true
        inflateWithAttributes()
    }

    /**
     * Set avatar icon
     *
     * @param avatarUri the uri of the icon to set
     */
    fun setAvatarIcon(avatarUri: Uri) {
        mAvatarIconUri = avatarUri
        mHasAvatarIcon = true
        inflateWithAttributes()
    }

    /**
     * Show or hide delte button
     *
     * @param deletable true to show, false to hide
     */
    fun setDeletable(deletable: Boolean) {
        mDeletable = deletable
        if (!mDeletable) {
            // hide delete icon
            delete_button.setVisibility(View.GONE)
            // adjust padding
            if (icon.getVisibility() == View.VISIBLE)
                label.setPadding(ViewUtil.dpToPx(8), 0, ViewUtil.dpToPx(12), 0)
            else
                label.setPadding(ViewUtil.dpToPx(12), 0, ViewUtil.dpToPx(12), 0)
        } else {
            // show icon
            delete_button.setVisibility(View.VISIBLE)
            // adjust padding
            if (icon.getVisibility() == View.VISIBLE)
                label.setPadding(ViewUtil.dpToPx(8), 0, 0, 0)
            else
                label.setPadding(ViewUtil.dpToPx(12), 0, 0, 0)

            // set icon
            if (mDeleteIcon != null)
                delete_button.setImageDrawable(mDeleteIcon)
            if (mDeleteIconColor != null)
                delete_button.getDrawable().mutate().setColorFilter(mDeleteIconColor!!.defaultColor, PorterDuff.Mode.SRC_ATOP)
        }
    }

    /**
     * Set delete icon color
     *
     * @param color the color to set
     */
    fun setDeleteIconColor(color: ColorStateList) {
        mDeleteIconColor = color
        mDeletable = true
        inflateWithAttributes()
    }

    /**
     * Set delete icon color
     *
     * @param color the color to set
     */
    fun setDeleteIconColor(@ColorInt color: Int) {
        mDeleteIconColor = ColorStateList.valueOf(color)
        mDeletable = true
        inflateWithAttributes()
    }

    /**
     * Set delete icon
     *
     * @param deleteIcon the icon to set
     */
    fun setDeleteIcon(deleteIcon: Drawable) {
        mDeleteIcon = deleteIcon
        mDeletable = true
        inflateWithAttributes()
    }

    /**
     * Set background color
     *
     * @param color the color to set
     */
    fun setChipBackgroundColor(color: ColorStateList) {
        mBackgroundColor = color
        setChipBackgroundColor(color.defaultColor)
    }

    /**
     * Set background color
     *
     * @param color the color to set
     */
    fun setChipBackgroundColor(@ColorInt color: Int) {
        mBackgroundColor = ColorStateList.valueOf(color)
        content.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    /**
     * Set the chip object
     *
     * @param chip the chip
     */
    fun setChip(chip: ChipInterface) {
        mChip = chip
    }

    /**
     * Set OnClickListener on the delete button
     *
     * @param onClickListener the OnClickListener
     */
    fun setOnDeleteClicked(onClickListener: View.OnClickListener) {
        delete_button.setOnClickListener(onClickListener)
    }

    /**
     * Set OnclickListener on the entire chip
     *
     * @param onClickListener the OnClickListener
     */
    fun setOnChipClicked(onClickListener: View.OnClickListener) {
        content.setOnClickListener(onClickListener)
    }

    /**
     * Builder class
     */
    class Builder(val context: Context) {
        var label: String? = null
        var labelColor: ColorStateList? = null
        var hasAvatarIcon = false
        var avatarIconUri: Uri? = null
        var avatarIconDrawable: Drawable? = null
        var deletable = false
        var deleteIcon: Drawable? = null
        var deleteIconColor: ColorStateList? = null
        var backgroundColor: ColorStateList? = null
        var chip: ChipInterface? = null

        fun label(label: String): Builder {
            this.label = label
            return this
        }

        fun labelColor(labelColor: ColorStateList?): Builder {
            this.labelColor = labelColor
            return this
        }

        fun hasAvatarIcon(hasAvatarIcon: Boolean): Builder {
            this.hasAvatarIcon = hasAvatarIcon
            return this
        }

        fun avatarIcon(avatarUri: Uri): Builder {
            this.avatarIconUri = avatarUri
            return this
        }

        fun avatarIcon(avatarIcon: Drawable): Builder {
            this.avatarIconDrawable = avatarIcon
            return this
        }

        fun deletable(deletable: Boolean): Builder {
            this.deletable = deletable
            return this
        }

        fun deleteIcon(deleteIcon: Drawable?): Builder {
            this.deleteIcon = deleteIcon
            return this
        }

        fun deleteIconColor(deleteIconColor: ColorStateList?): Builder {
            this.deleteIconColor = deleteIconColor
            return this
        }

        fun backgroundColor(backgroundColor: ColorStateList?): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun chip(chip: ChipInterface): Builder {
            this.chip = chip
            this.label = chip.label
            this.avatarIconDrawable = chip.avatarDrawable
            this.avatarIconUri = chip.avatarUri
            return this
        }

        fun build(): ChipView = newInstance(this)
    }

    companion object {

        private val TAG = ChipView::class.java.toString()
        // xml elements

        // attributes
        private val NONE = -1

        private fun newInstance(builder: Builder): ChipView {
            val chipView = ChipView(builder.context)
            chipView.mLabel = builder.label
            chipView.mLabelColor = builder.labelColor
            chipView.mHasAvatarIcon = builder.hasAvatarIcon
            chipView.mAvatarIconUri = builder.avatarIconUri
            chipView.mAvatarIconDrawable = builder.avatarIconDrawable
            chipView.mDeletable = builder.deletable
            chipView.mDeleteIcon = builder.deleteIcon
            chipView.mDeleteIconColor = builder.deleteIconColor
            chipView.mBackgroundColor = builder.backgroundColor
            chipView.mChip = builder.chip
            chipView.inflateWithAttributes()

            return chipView
        }
    }
}
