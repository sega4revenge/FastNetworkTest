package sega.fastnetwork.test.lib.MaterialChips.views


import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.list_filterable_view.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.MaterialChips.ChipsInput
import sega.fastnetwork.test.lib.MaterialChips.adapter.FilterableAdapter
import sega.fastnetwork.test.lib.MaterialChips.model.ChipInterface
import sega.fastnetwork.test.lib.MaterialChips.util.ViewUtil


class FilterableListView(private val mContext: Context,var view : View,var distance : Int) : RelativeLayout(mContext) {
    // list
    private var mAdapter: FilterableAdapter? = null
    private var mFilterableList: List<ChipInterface>? = null
    // others
   var mChipsInput: ChipsInput? = null

    init {
        init()
    }

    private fun init() {
        // inflate layout
        val view = View.inflate(context, R.layout.list_filterable_view, this)
        // butter knife


        // recycler
        recycler_view.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)

        // hide on first
        visibility = View.GONE
    }

    fun build(filterableList: List<ChipInterface>, chipsInput: ChipsInput, backgroundColor: ColorStateList?, textColor: ColorStateList?) {
        mFilterableList = filterableList
        mChipsInput = chipsInput

        // adapter
        mAdapter = FilterableAdapter(mContext, recycler_view, filterableList, chipsInput, backgroundColor, textColor)
        recycler_view.adapter = mAdapter
        if (backgroundColor != null)
            recycler_view.background.setColorFilter(backgroundColor.defaultColor, PorterDuff.Mode.SRC_ATOP)

        // listen to change in the tree
        mChipsInput!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {

                // position
                val rootView = mChipsInput!!.rootView as ViewGroup

                // size
                val layoutParams = RelativeLayout.LayoutParams(
                        ViewUtil.getWindowWidth(mContext),
                        ViewGroup.LayoutParams.MATCH_PARENT)

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

                if (mContext.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutParams.bottomMargin = ViewUtil.getNavBarHeight(mContext)
                }


                // add view
                rootView.addView(this@FilterableListView, layoutParams)

                // remove the listener:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mChipsInput!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    mChipsInput!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

        })
    }

    fun filterList(text: CharSequence) {
        mAdapter!!.filter.filter(text) {
            // show if there are results
            if (mAdapter!!.itemCount > 0)
                fadeIn()
            else
                fadeOut()
        }
    }

    /**
     * Fade in
     */
    fun fadeIn() {
        if (visibility == View.VISIBLE)
            return

        // get visible window (keyboard shown)
        val rootView = rootView
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)

        val coord = IntArray(2)
        mChipsInput!!.getLocationInWindow(coord)
        val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = coord[1] + mChipsInput!!.height
        // height of the keyboard

        layoutParams.bottomMargin = distance
        setLayoutParams(layoutParams)

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 200
        startAnimation(anim)
        visibility = View.VISIBLE
    }

    /**
     * Fade out
     */
   internal fun fadeOut() {
        if (visibility == View.GONE)
            return

        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        startAnimation(anim)
        visibility = View.GONE
    }

    companion object {

        private val TAG = FilterableListView::class.java.toString()
    }
}
