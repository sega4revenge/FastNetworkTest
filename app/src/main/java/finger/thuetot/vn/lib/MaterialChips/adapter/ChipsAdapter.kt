package finger.thuetot.vn.lib.MaterialChips.adapter

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import finger.thuetot.vn.lib.MaterialChips.ChipView
import finger.thuetot.vn.lib.MaterialChips.ChipsInput
import finger.thuetot.vn.lib.MaterialChips.model.ChipInterface
import finger.thuetot.vn.lib.MaterialChips.util.ViewUtil
import finger.thuetot.vn.lib.MaterialChips.views.ChipsInputEditText
import finger.thuetot.vn.lib.MaterialChips.views.DetailedChipView
import finger.thuetot.vn.lib.MaterialChips.views.FilterableListView
import java.util.*


class ChipsAdapter(private val mContext: Context, private val mChipsInput: ChipsInput, private val mRecycler: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mChipList = ArrayList<ChipInterface>()
    private val mHintLabel: String?
    val mEditText: ChipsInputEditText?

    init {
        mHintLabel = mChipsInput.hint
        mEditText = mChipsInput.editText
        initEditText()
    }

    private inner class ItemViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        val chipView: ChipView

        init {
            chipView = view as ChipView
        }
    }

    private inner class EditTextViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        private val editText: EditText

        init {
            editText = view as EditText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EDIT_TEXT)
            EditTextViewHolder(mEditText!!)
        else
            ItemViewHolder(mChipsInput.chipView)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // edit text
        if (position == mChipList.size) {
            if (mChipList.size == 0)
                mEditText!!.hint = mHintLabel

            // auto fit edit text
            autofitEditText()
        } else if (itemCount > 1) {
            val itemViewHolder = holder as ItemViewHolder
            itemViewHolder.chipView.inflate(getItem(position))
            // handle click
            handleClickOnEditText(itemViewHolder.chipView, position)
        }// chip
    }

    override fun getItemCount(): Int {
        return mChipList.size + 1
    }

    private fun getItem(position: Int): ChipInterface {
        return mChipList[position]
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mChipList.size) TYPE_EDIT_TEXT else TYPE_ITEM

    }

    override fun getItemId(position: Int): Long {
        return mChipList[position].hashCode().toLong()
    }

    private fun initEditText() {
        mEditText!!.layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        mEditText.hint = mHintLabel
        mEditText.setBackgroundResource(android.R.color.transparent)
        // prevent fullscreen on landscape
        mEditText.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        mEditText.privateImeOptions = "nm"
        // no suggestion
        mEditText.inputType = InputType.TYPE_TEXT_VARIATION_FILTER or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        // handle back space
        mEditText.setOnKeyListener { v, keyCode, event ->
            // backspace
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                // remove last chip
                if (mChipList.size > 0 && mEditText.text.toString().length == 0)
                    removeChip(mChipList.size - 1)
            }
            false
        }

        // text changed
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mChipsInput.onTextChanged(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun autofitEditText() {
        // min width of edit text = 50 dp
        val params = mEditText!!.layoutParams
        params.width = ViewUtil.dpToPx(50)
        mEditText.layoutParams = params

        // listen to change in the tree
        mEditText.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                // get right of recycler and left of edit text
                val right = mRecycler.right
                val left = mEditText.left

                // edit text will fill the space
                val params = mEditText.layoutParams
                params.width = right - left - ViewUtil.dpToPx(8)
                mEditText.layoutParams = params

                // request focus
                mEditText.requestFocus()

                // remove the listener:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mEditText.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    mEditText.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

        })
    }

    private fun handleClickOnEditText(chipView: ChipView, position: Int) {
        // delete chip
        chipView.setOnDeleteClicked(View.OnClickListener { removeChip(position) })

        // show detailed chip
        if (mChipsInput.isShowChipDetailed()) {
            chipView.setOnChipClicked(View.OnClickListener { v ->
                // get chip position
                val coord = IntArray(2)
                v.getLocationInWindow(coord)

                val detailedChipView = mChipsInput.getDetailedChipView(getItem(position))
                setDetailedChipViewPosition(detailedChipView, coord)

                // delete button
                detailedChipView.setOnDeleteClicked(View.OnClickListener {
                    removeChip(position)
                    detailedChipView.fadeOut()
                })
            })
        }
    }

    private fun setDetailedChipViewPosition(detailedChipView: DetailedChipView, coord: IntArray) {
        // window width
        val rootView = mRecycler.rootView as ViewGroup
        val windowWidth = ViewUtil.getWindowWidth(mContext)

        // chip size
        val layoutParams = RelativeLayout.LayoutParams(
                ViewUtil.dpToPx(300),
                ViewUtil.dpToPx(100))

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

        // align left window
        if (coord[0] <= 0) {
            layoutParams.leftMargin = 0
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(13)
            detailedChipView.alignLeft()
        } else if (coord[0] + ViewUtil.dpToPx(300) > windowWidth + ViewUtil.dpToPx(13)) {
            layoutParams.leftMargin = windowWidth - ViewUtil.dpToPx(300)
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(13)
            detailedChipView.alignRight()
        } else {
            layoutParams.leftMargin = coord[0] - ViewUtil.dpToPx(13)
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(13)
        }// same position as chip
        // align right

        // show view
        rootView.addView(detailedChipView, layoutParams)
        detailedChipView.fadeIn()
    }

    fun setFilterableListView(filterableListView: FilterableListView) {
        if (mEditText != null)
            mEditText.filterableListView = filterableListView
    }

    fun addChip(chip: ChipInterface) {
        if (!listContains(mChipList, chip)) {
            mChipList.add(chip)
            // notify listener
            mChipsInput.onChipAdded(chip, mChipList.size)
            // hide hint
            mEditText!!.hint = null
            // reset text
            mEditText.text = null
            // refresh data
            notifyItemInserted(mChipList.size)
        }
    }

    fun removeChip(chip: ChipInterface) {
        val position = mChipList.indexOf(chip)
        mChipList.removeAt(position)
        // notify listener
        notifyItemRangeChanged(position, itemCount)
        // if 0 chip
        if (mChipList.size == 0)
            mEditText!!.hint = mHintLabel
        // refresh data
        notifyDataSetChanged()
    }

    fun removeChip(position: Int) {
        val chip = mChipList[position]
        // remove contact
        mChipList.removeAt(position)
        // notify listener
        mChipsInput.onChipRemoved(chip, mChipList.size)
        // if 0 chip
        if (mChipList.size == 0)
            mEditText!!.hint = mHintLabel
        // refresh data
        notifyDataSetChanged()
    }

    fun removeChipById(id: Any) {
        val iter = mChipList.listIterator()
        while (iter.hasNext()) {
            val chip = iter.next()
            if (chip.id != null && chip.id == id) {
                // remove chip
                iter.remove()
                // notify listener
                mChipsInput.onChipRemoved(chip, mChipList.size)
            }
        }
        // if 0 chip
        if (mChipList.size == 0)
            mEditText!!.hint = mHintLabel
        // refresh data
        notifyDataSetChanged()
    }

    fun removeChipByLabel(label: String) {
        val iter = mChipList.listIterator()
        while (iter.hasNext()) {
            val chip = iter.next()
            if (chip.label == label) {
                // remove chip
                iter.remove()
                // notify listener
                mChipsInput.onChipRemoved(chip, mChipList.size)
            }
        }
        // if 0 chip
        if (mChipList.size == 0)
            mEditText!!.hint = mHintLabel
        // refresh data
        notifyDataSetChanged()
    }

    fun removeChipByInfo(info: String) {
        val iter = mChipList.listIterator()
        while (iter.hasNext()) {
            val chip = iter.next()
            if (chip.info != null && chip.info == info) {
                // remove chip
                iter.remove()
                // notify listener
                mChipsInput.onChipRemoved(chip, mChipList.size)
            }
        }
        // if 0 chip
        if (mChipList.size == 0)
            mEditText!!.hint = mHintLabel
        // refresh data
        notifyDataSetChanged()
    }

    val chipList: List<ChipInterface>
        get() = mChipList

    private fun listContains(contactList: List<ChipInterface>, chip: ChipInterface): Boolean {

        if (mChipsInput.chipValidator != null) {
            for (item in contactList) {
                if (mChipsInput.chipValidator!!.areEquals(item, chip))
                    return true
            }
        } else {
            for (item in contactList) {
                if (chip.id != null && chip.id == item.id)
                    return true
                if (chip.label == item.label)
                    return true
            }
        }

        return false
    }

    companion object {

        private val TAG = ChipsAdapter::class.java.toString()
        private val TYPE_EDIT_TEXT = 0
        private val TYPE_ITEM = 1
    }
}
