package sega.fastnetwork.test.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.filter_product.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.SearchActivity
import sega.fastnetwork.test.adapter.CategoryAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.lib.FabiousFilter.AAH_FabulousFragment
import java.util.*


/**
 * Created by sega4 on 28/08/2017.
 */

class FilterFragment : AAH_FabulousFragment(), CategoryAdapter.OncategoryClickListener {
    var adapter: CategoryAdapter? = null
    private var keysCategory: List<String> = ArrayList()
    private var applied_filters: ArrayMap<String, MutableList<String>>? = ArrayMap()
    private var textviews: MutableList<TextView> = ArrayList()

    private var tabs_types: TabLayout? = null

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
            adapter!!.refreshFilter()
            adapter!!.notifyDataSetChanged()
            applied_filters!!.clear()
        }





        mAdapter = SectionsPagerAdapter()
        vp_types.offscreenPageLimit = 3
        vp_types.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
        tabs_types!!.setupWithViewPager(vp_types)


        //params to set

        setPeekHeight(300) // optional; default 400dp
        setCallbacks(activity as AAH_FabulousFragment.Callbacks) //optional; to get back result
        setViewgroupStatic(ll_buttons) // optional; layout to stick at bottom on slide
        setViewPager(vp_types) //optional; if you use viewpager that has scrollview
        setViewMain(rl_content) //necessary; main bottomsheet view
        setMainContentView(contentView) // necessary; call at end before super
        super.setupDialog(dialog, R.style.DialogTheme) //call super at last
    }


    private inner class SectionsPagerAdapter : PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): View? {
            val inflater = LayoutInflater.from(context)
            /*  val layout = inflater.inflate(R.layout.view_filters_sorters, collection, false) as ViewGroup
              val fbl = layout.findViewById<FlexboxLayout>(R.id.fbl)*/
            val resId: Int
            var view: View? = null
            when (position) {
                0 -> {
                    resId = R.layout.category_fragment
                    view = inflater.inflate(resId, collection, false)
                    val fbl = view.findViewById<RecyclerView>(R.id.category_grid)
                    inflateLayoutCategory(fbl)
                    (collection as ViewPager).addView(view, 0)
                }
                1 -> {
                    if (arguments.getBoolean("isMap")) {
                        resId = R.layout.filter_product
                        view = inflater.inflate(resId, collection, false)

                        inflateLayoutProduct(view)
                        (collection as ViewPager).addView(view, 0)
                    } else {
                        resId = R.layout.view_filters_sorters
                        view = inflater.inflate(resId, collection, false)
                        val fbl = view.findViewById<FlexboxLayout>(R.id.fbl)
                        inflateLayoutWithFilters(fbl)
                        (collection as ViewPager).addView(view, 0)
                    }
                }
                2 -> {
                    resId = R.layout.filter_product
                    view = inflater.inflate(resId, collection, false)
                    inflaterRangeProduct(view)
                    inflateLayoutProduct(view)
                    (collection as ViewPager).addView(view, 0)
                }
            }
            /*  when (position) {
                  0 -> inflateLayoutCategory("Danh mục", fbl)
                  1 -> inflateLayoutWithFilters("Địa điểm", fbl)
                  2 -> inflateLayoutWithFilters("Lọc", fbl)
                  3 -> inflateLayoutWithFilters("Sắp xếp", fbl)
              }
              collection.addView(layout)
              return layout*/

            return view

        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int =   if (arguments.getBoolean("isMap")) 2 else 3

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return "Danh mục"
                1 -> return   if (arguments.getBoolean("isMap")) "Lọc" else "Địa điểm"
                2 -> return "Lọc"
            }
            return ""
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }

    private fun inflateLayoutWithFilters(fbl: FlexboxLayout) {
        var keys: List<String> = Arrays.asList(*resources.getStringArray(R.array.mLocation))

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
                    removeFromSelectedMap("location", finalKeys[i])
                } else if (applied_filters!!["location"]?.size == 5) {
                    Toast.makeText(activity, " da du 5 truong", Toast.LENGTH_LONG).show()
                } else {
                    tv.tag = "selected"
                    tv.setBackgroundResource(R.drawable.chip_selected)
                    tv.setTextColor(ContextCompat.getColor(context, R.color.white))
                    addToSelectedMap("location", finalKeys[i])
                }
            }
            if (applied_filters != null && applied_filters!!["location"] != null && applied_filters!!["location"]!!.contains(keys[i])) {
                tv.tag = "selected"
                tv.setBackgroundResource(R.drawable.chip_selected)
                tv.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                tv.setBackgroundResource(R.drawable.chip_unselected)
                tv.setTextColor(ContextCompat.getColor(context, R.color.color_background_button))
            }
            textviews.add(tv)

            fbl.addView(subchild)
        }


    }

    private fun inflateLayoutCategory(recycleview: RecyclerView) {
        keysCategory = Arrays.asList(*resources.getStringArray(R.array.category))
        adapter = CategoryAdapter(context, this)

        val layoutManager = GridLayoutManager(context, getNumberOfColumns())
        recycleview.setHasFixedSize(true)
        recycleview.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!

        recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 1))
        recycleview.adapter = adapter

        for (i in keysCategory.indices) {
            if (applied_filters != null && applied_filters!!["category"] != null && applied_filters!!["category"]!!.contains(i.toString())) {
                adapter!!.categoryList[i].selected = true
                adapter!!.notifyItemChanged(i)

            }
        }
        adapter!!.notifyDataSetChanged()


    }

    private fun inflateLayoutProduct(view: View) {
        if (applied_filters != null && applied_filters!!["filter"] != null) {
            when (applied_filters!!["filter"]!![0]) {
                "1" -> view.filter_newimg.visibility = View.VISIBLE

                "2" -> view.filter_smallimg.visibility = View.VISIBLE

                "3" -> view.filter_hignimg.visibility = View.VISIBLE

                else -> view.filter_hotimg.visibility = View.VISIBLE
            }

        }

        view.filter_hot.setOnClickListener {

            val temp = ArrayList<String>()
            temp.add("0")
            applied_filters!!.remove("filter")
            applied_filters!!.put("filter", temp)
            view.filter_hotimg.visibility = View.VISIBLE
            view.filter_newimg.visibility = View.GONE
            view.filter_smallimg.visibility = View.GONE
            view.filter_hignimg.visibility = View.GONE
        }
        view.filter_new.setOnClickListener {
            val temp = ArrayList<String>()
            temp.add("1")
            applied_filters!!.remove("filter")
            applied_filters!!.put("filter", temp)
            view.filter_hotimg.visibility = View.GONE
            view.filter_newimg.visibility = View.VISIBLE
            view.filter_smallimg.visibility = View.GONE
            view.filter_hignimg.visibility = View.GONE

        }
        view.filter_small.setOnClickListener {
            val temp = ArrayList<String>()
            temp.add("2")
            applied_filters!!.remove("filter")
            applied_filters!!.put("filter", temp)
            view.filter_hotimg.visibility = View.GONE
            view.filter_newimg.visibility = View.GONE
            view.filter_smallimg.visibility = View.VISIBLE
            view.filter_hignimg.visibility = View.GONE
        }
        view.filter_hign.setOnClickListener {
            val temp = ArrayList<String>()
            temp.add("3")
            applied_filters!!.remove("filter")
            applied_filters!!.put("filter", temp)
            view.filter_hotimg.visibility = View.GONE
            view.filter_newimg.visibility = View.GONE
            view.filter_smallimg.visibility = View.GONE
            view.filter_hignimg.visibility = View.VISIBLE
        }


    }
    private fun inflaterRangeProduct(view: View) {
//        if (applied_filters != null && applied_filters!!["filter"] != null) {
//            when (applied_filters!!["filter"]!![0]) {
//                "1" -> view.filter_newimg.visibility = View.VISIBLE
//
//                "2" -> view.filter_smallimg.visibility = View.VISIBLE
//
//                "3" -> view.filter_hignimg.visibility = View.VISIBLE
//
//                else -> view.filter_hotimg.visibility = View.VISIBLE
//            }
//
//        }
        view.edt_range.text = SpannableStringBuilder(view.seek_range.progress.toString())
        view.seek_range.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                view.edt_range.text =SpannableStringBuilder(p1.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        view.edt_range.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                view.seek_range.progress = p0.toString().toInt()
            }

        })


    }

    private fun getNumberOfColumns(): Int {
        // Get screen width
        val displayMetrics = resources.displayMetrics
        val widthPx = displayMetrics.widthPixels.toFloat()
        /* if (isTablet) {
             widthPx = widthPx / 3
         }*/
        // Calculate desired width


        val desiredPx = resources.getDimensionPixelSize(R.dimen.product_card_width).toFloat()
        val columns = Math.round(widthPx / desiredPx)
        return if (columns > 2) columns else 2

    }

    override fun oncategoryClicked(position: Int, view: View) {

        if (adapter!!.categoryList[position].selected) {
            adapter!!.categoryList[position].selected = false
            adapter!!.notifyItemChanged(position)
            removeFromSelectedMap("category", position.toString())
        } else if (applied_filters!!["category"]?.size == 5) {
            Toast.makeText(activity, " da du 5 truong", Toast.LENGTH_LONG).show()
        } else {
            adapter!!.categoryList[position].selected = true
            adapter!!.notifyItemChanged(position)
            addToSelectedMap("category", position.toString())
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
