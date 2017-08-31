package sega.fastnetwork.test.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.SearchActivity
import sega.fastnetwork.test.lib.FabiousFilter.AAH_FabulousFragment
import java.util.*





/**
 * Created by sega4 on 28/08/2017.
 */

class FilterFragment : AAH_FabulousFragment() {


    internal var applied_filters: ArrayMap<String, MutableList<String>>? = ArrayMap()
    internal var textviews: MutableList<TextView> = ArrayList()

    internal var tabs_types: TabLayout? = null

    internal var imgbtn_refresh: ImageButton? = null
    internal var imgbtn_apply: ImageButton? = null
    private var mAdapter: SectionsPagerAdapter? = null
    private var metrics: DisplayMetrics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applied_filters = (activity as SearchActivity).getApplied_filters()
        metrics = this.resources.displayMetrics

        for ((key, value) in applied_filters!!) {
            Log.d("k9res", "from activity: " + key)
            for (s in value) {
                Log.d("k9res", "from activity val: " + s)

            }
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {

        val contentView = View.inflate(context, R.layout.filter_view, null)

        val rl_content = contentView.findViewById<View>(R.id.rl_content) as RelativeLayout
        val ll_buttons = contentView.findViewById<View>(R.id.ll_buttons) as LinearLayout
        imgbtn_refresh = contentView.findViewById<View>(R.id.imgbtn_refresh) as ImageButton
        imgbtn_apply = contentView.findViewById<View>(R.id.imgbtn_apply) as ImageButton
        val vp_types = contentView.findViewById<View>(R.id.vp_types) as ViewPager
        tabs_types = contentView.findViewById<View>(R.id.tabs_types) as TabLayout

        imgbtn_apply!!.setOnClickListener { closeFilter(applied_filters) }
        imgbtn_refresh!!.setOnClickListener {
            for (tv in textviews) {
                tv.tag = "unselected"
                tv.setBackgroundResource(R.drawable.chip_unselected)
                tv.setTextColor(ContextCompat.getColor(context, R.color.color_background_button))
            }
            applied_filters!!.clear()
        }





        mAdapter = SectionsPagerAdapter()
        vp_types.offscreenPageLimit = 4
        vp_types.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
        tabs_types!!.setupWithViewPager(vp_types)


        //params to set
        setAnimationDuration(400) //optional; default 500ms
        setPeekHeight(300) // optional; default 400dp
        setCallbacks(activity as AAH_FabulousFragment.Callbacks) //optional; to get back result
        setAnimationListener(activity as AAH_FabulousFragment.AnimationListener) //optional; to get animation callbacks
        setViewgroupStatic(ll_buttons) // optional; layout to stick at bottom on slide
        setViewPager(vp_types) //optional; if you use viewpager that has scrollview
        setViewMain(rl_content) //necessary; main bottomsheet view
        setMainContentView(contentView) // necessary; call at end before super
        super.setupDialog(dialog, R.style.DialogTheme) //call super at last
    }


    private inner class SectionsPagerAdapter : PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.view_filters_sorters, collection, false) as ViewGroup
            val fbl = layout.findViewById<FlexboxLayout>(R.id.fbl)
            when (position) {
                0 -> inflateLayoutWithFilters("Danh mục", fbl)
                1 -> inflateLayoutWithFilters("Địa điểm", fbl)
                2 -> inflateLayoutWithFilters("Lọc", fbl)
                3 -> inflateLayoutWithFilters("Sắp xếp", fbl)
            }
            collection.addView(layout)
            return layout

        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int = 4

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return "Danh mục"
                1 -> return "Địa điểm"
                2 -> return "Lọc"
                3 -> return "Sắp xếp"
            }
            return ""
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }

    private fun inflateLayoutWithFilters(filter_category: String, fbl: FlexboxLayout) {
        var keys: List<String> = ArrayList()
        when (filter_category) {
            "Danh mục" -> keys = Arrays.asList(*resources.getStringArray(R.array.mCategory))
            "Địa điểm" -> keys = Arrays.asList(*resources.getStringArray(R.array.mLocation))
            "Lọc" -> keys = Arrays.asList(*resources.getStringArray(R.array.mCategory))
            "Sắp xếp" -> keys = Arrays.asList(*resources.getStringArray(R.array.mCategory))
        }

        for (i in keys.indices) {
            val subchild = activity.layoutInflater.inflate(R.layout.single_chip, null)
            val tv = subchild.findViewById<View>(R.id.txt_title) as TextView
            tv.text = keys[i]
            val finalKeys = keys
            tv.setOnClickListener {
                if (tv.tag != null && tv.tag == "selected") {
                    tv.tag = "unselected"
                    tv.setBackgroundResource(R.drawable.chip_unselected)
                    tv.setTextColor(ContextCompat.getColor(context, R.color.color_background_button))
                    removeFromSelectedMap(filter_category, finalKeys[i])
                } else {
                    tv.tag = "selected"
                    tv.setBackgroundResource(R.drawable.chip_selected)
                    tv.setTextColor(ContextCompat.getColor(context, R.color.white))
                    addToSelectedMap(filter_category, finalKeys[i])
                }
            }
            try {
                Log.d("k9res", "key: " + filter_category + " |val:" + keys[i])
                Log.d("k9res", "applied_filters != null: " + (applied_filters != null))
                Log.d("k9res", "applied_filters.get(key) != null: " + (applied_filters!![filter_category] != null))
                Log.d("k9res", "applied_filters.get(key).contains(keys.get(finalI)): " + applied_filters!![filter_category]!!.contains(keys[i]))
            } catch (e: Exception) {

            }

            if (applied_filters != null && applied_filters!![filter_category] != null && applied_filters!![filter_category]!!.contains(keys[i])) {
                tv.tag = "selected"
                tv.setBackgroundResource(R.drawable.chip_selected)
                tv.setTextColor(ContextCompat.getColor(context, R.color.filters_header))
            } else {
                tv.setBackgroundResource(R.drawable.chip_unselected)
                tv.setTextColor(ContextCompat.getColor(context, R.color.filters_chips))
            }
            textviews.add(tv)

            fbl.addView(subchild)
        }


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
}
