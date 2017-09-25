package sega.fastnetwork.test.lib.MaterialChips


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import kotlinx.android.synthetic.main.lib_chips_input.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.MaterialChips.adapter.ChipsAdapter
import sega.fastnetwork.test.lib.MaterialChips.model.Chip
import sega.fastnetwork.test.lib.MaterialChips.model.ChipInterface
import sega.fastnetwork.test.lib.MaterialChips.util.ActivityUtil
import sega.fastnetwork.test.lib.MaterialChips.util.MyWindowCallback
import sega.fastnetwork.test.lib.MaterialChips.util.ViewUtil
import sega.fastnetwork.test.lib.MaterialChips.views.ChipsInputEditText
import sega.fastnetwork.test.lib.MaterialChips.views.DetailedChipView
import sega.fastnetwork.test.lib.MaterialChips.views.FilterableListView
import sega.fastnetwork.test.lib.MaterialChips.views.ScrollViewMaxHeight
import java.util.*

class ChipsInput : ScrollViewMaxHeight {
    // context
    private var mContext: Context? = null
    // xml element
    // adapter
    private var mChipsAdapter: ChipsAdapter? = null
    var hint: String? = null
    private var mHintColor: ColorStateList? = null
    private var mTextColor: ColorStateList? = null
    private var mMaxRows = 2
    private var mChipLabelColor: ColorStateList? = null
    private var mChiphasEdittext = false
    private var mChipHasAvatarIcon = true
    private var mChipDeletable = false
    private var mChipDeleteIcon: Drawable? = null
    private var mChipDeleteIconColor: ColorStateList? = null
    private var mChipBackgroundColor: ColorStateList? = null
    private var mShowChipDetailed = true
    private var mChipDetailedTextColor: ColorStateList? = null
    private var mChipDetailedDeleteIconColor: ColorStateList? = null
    private var mChipDetailedBackgroundColor: ColorStateList? = null
    private var mFilterableListBackgroundColor: ColorStateList? = null
    private var mFilterableListTextColor: ColorStateList? = null
    // chips listener
    private var mChipList: List<ChipInterface>? = null
    private val mChipsListenerList = ArrayList<ChipsListener>()
    private var mChipsListener: ChipsListener? = null
    // chip list


    fun setFilterableList(list: List<ChipInterface>,view : View,distanceNavigation : Int) {
        mChipList = list
        mFilterableListView = FilterableListView(mContext!!,view,distanceNavigation)
        println(mFilterableListView)
        println(mChipList)
        println(mFilterableListBackgroundColor)
        println(mFilterableListTextColor)
        mFilterableListView!!.build(mChipList!!, this, mFilterableListBackgroundColor, mFilterableListTextColor)
        mChipsAdapter!!.setFilterableListView(mFilterableListView!!)
    }

    public var mFilterableListView: FilterableListView? = null
    // chip validator
    var chipValidator: ChipValidator? = null

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
        val rootView = View.inflate(context, R.layout.lib_chips_input, this)
        // butter knife


        // attributes
        if (attrs != null) {
            val a = mContext!!.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.ChipsInput,
                    0, 0)

            try {
                // hint
                hint = a.getString(R.styleable.ChipsInput_hint)
                mHintColor = a.getColorStateList(R.styleable.ChipsInput_hintColor)
                mTextColor = a.getColorStateList(R.styleable.ChipsInput_textColor)
                mMaxRows = a.getInteger(R.styleable.ChipsInput_maxRows, 2)
                setMaxHeight(ViewUtil.dpToPx(40 * mMaxRows + 8))
                //setVerticalScrollBarEnabled(true);
                // chip label color
                mChipLabelColor = a.getColorStateList(R.styleable.ChipsInput_chip_labelColor)
                // chip avatar icon
                mChipHasAvatarIcon = a.getBoolean(R.styleable.ChipsInput_chip_hasAvatarIcon, true)
                // chip delete icon
                mChipDeletable = a.getBoolean(R.styleable.ChipsInput_chip_deletable, false)
                mChipDeleteIconColor = a.getColorStateList(R.styleable.ChipsInput_chip_deleteIconColor)
                val deleteIconId = a.getResourceId(R.styleable.ChipsInput_chip_deleteIcon, NONE)
                if (deleteIconId != NONE) mChipDeleteIcon = ContextCompat.getDrawable(mContext!!, deleteIconId)
                // chip background color
                mChipBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_chip_backgroundColor)
                // show chip detailed
                mShowChipDetailed = a.getBoolean(R.styleable.ChipsInput_showChipDetailed, true)

                // chip detailed text color
                mChipDetailedTextColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_textColor)
                mChipDetailedBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_backgroundColor)
                mChipDetailedDeleteIconColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_deleteIconColor)
                // filterable list
                mFilterableListBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_filterable_list_backgroundColor)
                mFilterableListTextColor = a.getColorStateList(R.styleable.ChipsInput_filterable_list_textColor)
            } finally {
                a.recycle()
            }
        }

        // adapter
        mChipsAdapter = ChipsAdapter(mContext!!, this, chips_recycler)
        val chipsLayoutManager = ChipsLayoutManager.newBuilder(mContext)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build()
        chips_recycler!!.layoutManager = chipsLayoutManager
        chips_recycler!!.isNestedScrollingEnabled = false
        chips_recycler!!.adapter = mChipsAdapter

        // set window callback
        // will hide DetailedOpenView and hide keyboard on touch outside
        val activity = ActivityUtil.scanForActivity(mContext) ?: throw ClassCastException("android.view.Context cannot be cast to android.app.Activity")

        val mCallBack = activity.window.callback
        activity.window.callback = MyWindowCallback(mCallBack, activity)
    }

    fun addChip(chip: ChipInterface) {
        mChipsAdapter!!.addChip(chip)
    }
    fun setEdittextEnable(bol: Boolean) {
        mChipsAdapter?.mEditText?.isEnabled = bol
    }

    fun addChip(id: Any, icon: Drawable, label: String, info: String) {
        val chip = Chip(id, icon, label, info)
        mChipsAdapter!!.addChip(chip)
    }

    fun addChip(icon: Drawable?, label: String, info: String) {
        val chip = Chip(icon, label, info)
        mChipsAdapter!!.addChip(chip)
    }

    fun addChip(id: Any, iconUri: Uri, label: String, info: String) {
        val chip = Chip(id, iconUri, label, info)
        mChipsAdapter!!.addChip(chip)
    }

    fun addChip(iconUri: Uri?, label: String, info: String) {
        val chip = Chip(iconUri,label,info)
        mChipsAdapter!!.addChip(chip)
    }

    fun addChip(label: String, info: String) {
        val chip = Chip(label, info)
        mChipsAdapter!!.addChip(chip)
    }

    fun removeChip(chip: ChipInterface) {
        mChipsAdapter!!.removeChip(chip)
    }

    fun removeChipById(id: Any) {
        mChipsAdapter!!.removeChipById(id)
    }

    fun removeChipByLabel(label: String) {
        mChipsAdapter!!.removeChipByLabel(label)
    }

    fun removeChipByInfo(info: String) {
        mChipsAdapter!!.removeChipByInfo(info)
    }

    val chipView: ChipView
        get() {
            val padding = ViewUtil.dpToPx(4)
            val chipView = ChipView.Builder(mContext!!)
                    .labelColor(mChipLabelColor)
                    .hasAvatarIcon(mChipHasAvatarIcon)
                    .deletable(mChipDeletable)
                    .deleteIcon(mChipDeleteIcon)
                    .deleteIconColor(mChipDeleteIconColor)
                    .backgroundColor(mChipBackgroundColor)
                    .build()

            chipView.setPadding(padding, padding, padding, padding)

            return chipView
        }

    val editText: ChipsInputEditText
        get() {
            val editText = ChipsInputEditText(mContext!!)
            if (mHintColor != null)
                editText.setHintTextColor(mHintColor)
            if (mTextColor != null)
                editText.setTextColor(mTextColor)

            return editText
        }

    fun getDetailedChipView(chip: ChipInterface): DetailedChipView {
        return DetailedChipView.Builder(mContext!!)
                .chip(chip)
                .textColor(mChipDetailedTextColor)
                .backgroundColor(mChipDetailedBackgroundColor)
                .deleteIconColor(mChipDetailedDeleteIconColor)
                .build()
    }

    fun addChipsListener(chipsListener: ChipsListener) {
        mChipsListenerList.add(chipsListener)
        mChipsListener = chipsListener
    }

    fun onChipAdded(chip: ChipInterface, size: Int) {
        for (chipsListener in mChipsListenerList) {
            chipsListener.onChipAdded(chip, size)
        }
    }

    fun onChipRemoved(chip: ChipInterface, size: Int) {
        for (chipsListener in mChipsListenerList) {
            chipsListener.onChipRemoved(chip, size)
        }
    }

    fun onTextChanged(text: CharSequence) {
        if (mChipsListener != null) {
            for (chipsListener in mChipsListenerList) {
                chipsListener.onTextChanged(text)
            }
            // show filterable list
            if (mFilterableListView != null) {
                if (text.length > 0)
                    mFilterableListView!!.filterList(text)
                else
                    mFilterableListView!!.fadeOut()
            }
        }
    }

    val selectedChipList: List<ChipInterface>
        get() = mChipsAdapter!!.chipList

    fun setHintColor(mHintColor: ColorStateList) {
        this.mHintColor = mHintColor
    }

    fun setTextColor(mTextColor: ColorStateList) {
        this.mTextColor = mTextColor
    }

    fun setMaxRows(mMaxRows: Int): ChipsInput {
        this.mMaxRows = mMaxRows
        return this
    }

    fun setChipLabelColor(mLabelColor: ColorStateList) {
        this.mChipLabelColor = mLabelColor
    }

    fun setChipHasAvatarIcon(mHasAvatarIcon: Boolean) {
        this.mChipHasAvatarIcon = mHasAvatarIcon
    }

    fun chipHasAvatarIcon(): Boolean {
        return mChipHasAvatarIcon
    }

    fun setChipDeletable(mDeletable: Boolean) {
        this.mChipDeletable = mDeletable
    }

    fun setChipDeleteIcon(mDeleteIcon: Drawable) {
        this.mChipDeleteIcon = mDeleteIcon
    }

    fun setChipDeleteIconColor(mDeleteIconColor: ColorStateList) {
        this.mChipDeleteIconColor = mDeleteIconColor
    }

    fun setChipBackgroundColor(mBackgroundColor: ColorStateList) {
        this.mChipBackgroundColor = mBackgroundColor
    }

    fun setShowChipDetailed(mShowChipDetailed: Boolean): ChipsInput {
        this.mShowChipDetailed = mShowChipDetailed
        return this
    }


    fun isShowChipDetailed(): Boolean {
        return mShowChipDetailed
    }

    fun setChipDetailedTextColor(mChipDetailedTextColor: ColorStateList) {
        this.mChipDetailedTextColor = mChipDetailedTextColor
    }

    fun setChipDetailedDeleteIconColor(mChipDetailedDeleteIconColor: ColorStateList) {
        this.mChipDetailedDeleteIconColor = mChipDetailedDeleteIconColor
    }

    fun setChipDetailedBackgroundColor(mChipDetailedBackgroundColor: ColorStateList) {
        this.mChipDetailedBackgroundColor = mChipDetailedBackgroundColor
    }

    interface ChipsListener {
        fun onChipAdded(chip: ChipInterface, newSize: Int)
        fun onChipRemoved(chip: ChipInterface, newSize: Int)
        fun onTextChanged(text: CharSequence)
    }

    interface ChipValidator {
        fun areEquals(chip1: ChipInterface, chip2: ChipInterface): Boolean
    }

    companion object {

        private val TAG = ChipsInput::class.java.toString()
        // attributes
        private val NONE = -1
    }
}
