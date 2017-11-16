package finger.thuetot.vn.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.FragmentManager
import android.support.v4.util.ArrayMap
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.filter_layout.view.*
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.SearchActivity
import finger.thuetot.vn.adapter.CategoryAdapter
import finger.thuetot.vn.adapter.FilterAdapter
import finger.thuetot.vn.lib.MaterialChips.ChipsInput
import finger.thuetot.vn.lib.MaterialChips.model.ChipInterface
import finger.thuetot.vn.model.Category
import finger.thuetot.vn.model.LocationChip
import java.util.*
import kotlin.collections.ArrayList


class FilterFragment : BottomSheetDialogFragment(), CategoryAdapter.OncategoryClickListener {

    private var mLocationList: MutableList<LocationChip>? = ArrayList()
    private var contentView: View? = null
    private var applied_filters: ArrayMap<String, MutableList<String>>? = ArrayMap()
    private var keysCategory: List<String> = ArrayList()
    private var category_adapter: CategoryAdapter? = null
    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        private var countremoveloca = 0

        override fun onStateChanged(bottomSheet: View, newState: Int) {


            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAllowingStateLoss()

            }


        }


        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            hidePopup()

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applied_filters = (activity as SearchActivity).getApplied_filters()

    }


    fun show(fragmentManager: FragmentManager) {

        val ft = fragmentManager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            super.onCreateDialog(savedInstanceState)

    override fun onViewCreated(contentView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)


    }

    private fun getSoftButtonsBarHeight(): Int {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val usableHeight = metrics.heightPixels
            activity.windowManager.defaultDisplay.getRealMetrics(metrics)
            val realHeight = metrics.heightPixels
            return if (realHeight > usableHeight)
                realHeight - usableHeight
            else
                0
        }
        return 0
    }

    private fun inflateLayoutWithFilters() {
        val keys: List<String> = Arrays.asList(*resources.getStringArray(R.array.mLocation))
        for (i in keys.indices) {
            val chip = LocationChip(keys[i], keys[i])
            // add contact to the list
            mLocationList!!.add(chip)
            if (applied_filters != null && applied_filters!!["location"] != null && applied_filters!!["location"]!!.contains(keys[i])) {
                contentView!!.chips_input.addChip(chip)
            }
        }
        contentView!!.chips_input.setFilterableList(mLocationList!!, contentView!!, getSoftButtonsBarHeight())
        contentView!!.chips_input.setChipDeletable(true)
        contentView!!.chips_input.setShowChipDetailed(false)
        contentView!!.chips_input.addChipsListener(object : ChipsInput.ChipsListener {
            override fun onChipAdded(chip: ChipInterface, newSize: Int) {
                addToSelectedMap("location", chip.label)
                if (applied_filters?.get("location")?.size == 5) {
                    Toast.makeText(activity, R.string.max_city, Toast.LENGTH_LONG).show()
                    contentView!!.chips_input.setEdittextEnable(false)
                } else
                    contentView!!.chips_input.setEdittextEnable(true)


            }

            override fun onChipRemoved(chip: ChipInterface, newSize: Int) {
                println(chip.label)
                println("dang xoa")
                removeFromSelectedMap("location", chip.label)
            }

            override fun onTextChanged(text: CharSequence) {

            }
        })
    }

    private fun inflateLayoutCategory() {
        contentView!!.chips_input_category.editText.isFilterableListVisible = false
        contentView!!.chips_input_category.setEdittextEnable(false)
        contentView!!.chips_input_category.setChipDeletable(true)
        contentView!!.chips_input_category.setShowChipDetailed(false)



        for (i in keysCategory.indices) {
            if (applied_filters != null && applied_filters!!["category"] != null && applied_filters!!["category"]!!.contains(i.toString())) {
                category_adapter!!.categoryList[i].selected = true
                contentView!!.chips_input_category.addChip(Category(i,category_adapter!!.categoryList[i].name, category_adapter!!.categoryList[i].avatar, true))
                category_adapter!!.notifyItemChanged(i)
                println("co du lieu2")


            }

            // add contact to the list

        }
        contentView!!.chips_input_category.addChipsListener(object : ChipsInput.ChipsListener {
            override fun onChipAdded(chip: ChipInterface, newSize: Int) {
                addToSelectedMap("category", chip.id.toString())
            }

            override fun onChipRemoved(chip: ChipInterface, newSize: Int) {
                removeFromSelectedMap("category", chip.id.toString())
                if (category_adapter!!.categoryList[chip.id.toString().toInt()].selected) {
                    category_adapter!!.categoryList[chip.id.toString().toInt()].selected = false
                    category_adapter!!.notifyItemChanged(chip.id.toString().toInt())
                }
             /*   category_adapter!!.categoryList.indexOf(chip.label).selected = false
                contentView!!.chips_input_category.removeChipByLabel(category_adapter!!.categoryList[position].name)

                category_adapter!!.notifyItemChanged(position)*/
            }

            override fun onTextChanged(text: CharSequence) {

            }
        })

        /*   for (i in keysCategory.indices) {

               onClickCategory(linearlayout[i], keysCategory, i)
               if (applied_filters != null && applied_filters!!["category"] != null && applied_filters!!["category"]!!.contains(keysCategory[i])) {
                   linearlayout[i].tag = "selected"
                   linearlayout[i].setBackgroundResource(R.drawable.category_selected)


               } else {

                   linearlayout[i].setBackgroundResource(R.drawable.category_unselected)
               }

           }*/


    }

    override fun oncategoryClicked(position: Int, view: View) {

        if (!category_adapter!!.categoryList[position].selected) {
            if (applied_filters?.get("category")?.size == 5)
                Toast.makeText(activity, R.string.max_cate, Toast.LENGTH_LONG).show()
            else {
                category_adapter!!.categoryList[position].selected = true
                contentView!!.chips_input_category.addChip(Category(position,category_adapter!!.categoryList[position].name, category_adapter!!.categoryList[position].avatar, true))
                category_adapter!!.notifyItemChanged(position)
            }

        } else {

            category_adapter!!.categoryList[position].selected = false
            contentView!!.chips_input_category.removeChipByLabel(category_adapter!!.categoryList[position].name)

            category_adapter!!.notifyItemChanged(position)
        }
    }

    private fun hidePopup() {
        contentView!!.chips_input.mFilterableListView!!.fadeOut()
        contentView!!.slidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        contentView = View.inflate(context, R.layout.filter_layout, null)
        dialog.setContentView(contentView)
        val layoutParams = (contentView!!.parent as View?)?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            behavior.peekHeight = 1800

        }
        keysCategory = Arrays.asList(*resources.getStringArray(R.array.category))
        contentView!!.filter_list.layoutManager = LinearLayoutManager(activity)
        val filterAdapter = FilterAdapter(activity)
        contentView!!.filter_list.adapter = filterAdapter
        category_adapter = CategoryAdapter(activity, this)
        contentView!!.category_list.setHasFixedSize(true)

        contentView!!.category_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        contentView!!.category_list.adapter = category_adapter
        contentView!!.btn_refresh.setOnClickListener {
            try{
                val arrloca = applied_filters!!["location"]!!
                var locationSelected =  arrloca

                for(i in 0..(locationSelected.size)){
                    println(locationSelected.size)
                    println(locationSelected.get(0))
                    var remo = locationSelected.get(0)
                    contentView!!.chips_input.removeChipByLabel(remo)
                }
            }
            catch (e: Exception){
                Log.e("ASDASd",e.toString())
            }

            var categorySelected = contentView!!.chips_input_category.selectedChipList

            Log.e("categorySelected: ", "categorySelected: "+categorySelected)
            for (i in 0..(categorySelected.size-1)) {
                Log.e("removecate: ", "removecate: "+categorySelected.size)
                Log.e("removecate: ", "removecate: "+categorySelected[0].label)
                var id = (categorySelected[0].id).toString().toInt()
                Log.e("removecate: ", "removecate: id"+id)
                contentView!!.chips_input_category.removeChipByLabel(categorySelected[0].label)
                if (category_adapter!!.categoryList[id].selected) {
                    category_adapter!!.categoryList[id].selected = false
                    category_adapter!!.notifyItemChanged(id)
                }
            }
            contentView!!.filter.text = filterAdapter.getItem(0).label
            filterAdapter.changeSelect(0)
            filterAdapter.notifyItemChanged(0)
            val temp = ArrayList<String>()
            temp.add("0")
            applied_filters!!.remove("filter")
            applied_filters!!.put("filter", temp)


        }
        contentView!!.btn_done!!.setOnClickListener {
            (activity as Callbacks).onResult(applied_filters)
            dismissAllowingStateLoss()
        }

        if (applied_filters != null && applied_filters!!["filter"] != null) {
            when (applied_filters!!["filter"]!![0]) {
                "0" -> {contentView!!.filter.text = filterAdapter.getItem(0).label
                    filterAdapter.changeSelect(0)
                    filterAdapter.notifyItemChanged(0)
                }

                "1" ->{contentView!!.filter.text = filterAdapter.getItem(1).label
                    filterAdapter.changeSelect(1)
                    filterAdapter.notifyItemChanged(1)
                }


                "2" -> {contentView!!.filter.text = filterAdapter.getItem(2).label
                    filterAdapter.changeSelect(2)
                    filterAdapter.notifyItemChanged(2)}



                else -> {contentView!!.filter.text = filterAdapter.getItem(3).label
                    filterAdapter.changeSelect(3)
                    filterAdapter.notifyItemChanged(3)}

            }


        }
        else{
            contentView!!.filter.text = filterAdapter.getItem(0).label
            val temp = ArrayList<String>()
            temp.add("0")
            applied_filters!!.remove("filter")
            applied_filters!!.put("filter", temp)

        }
        filterAdapter.setOnItemClickListener(object : FilterAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                contentView!!.slidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                contentView!!.filter.text = filterAdapter.getItem(position).label
                val temp = ArrayList<String>()
                temp.add(position.toString())
                applied_filters!!.remove("filter")
                applied_filters!!.put("filter", temp)
            }


        })




        inflateLayoutCategory()
        inflateLayoutWithFilters()

    }




    private fun addToSelectedMap(key: String, value: String) {
        if (applied_filters!![key] != null && !applied_filters!![key]!!.contains(value)) {
            applied_filters!![key]!!.add(value)
        } else {
            val temp = ArrayList<String>()
            temp.add(value)
            applied_filters!!.put(key, temp)
        }
    }

    private fun removeFromSelectedMap(key: String, value: String) {
        if (applied_filters!![key]!!.size == 1) {
            applied_filters!!.remove(key)
        } else {
            applied_filters!![key]!!.remove(value)
        }
    }

    companion object {
        fun newInstance(): FilterFragment = FilterFragment()
    }

    interface Callbacks {
        fun onResult(result: Any?)
    }


}