package sega.fastnetwork.test.customview

import android.content.Context
import android.graphics.Typeface
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.ViewGroup




/**
 * Created by sega4 on 07/08/2017.
 */
class CustomTabLayout : TabLayout {
    private var mTypeface: Typeface? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mTypeface = Typeface.createFromAsset(context.assets, "fonts/myriadpro-regular.otf") // here you will provide fully qualified path for fonts
    }

    override fun setupWithViewPager(viewPager: ViewPager?) {
        super.setupWithViewPager(viewPager)

        if (mTypeface != null) {
            this.removeAllTabs()
            val slidingTabStrip = getChildAt(0) as ViewGroup

            val adapter = viewPager!!.adapter

            var i = 0
            val count = adapter.count
            while (i < count) {
                val tab = this.newTab()
                this.addTab(tab.setText(adapter.getPageTitle(i)))
                val view = (slidingTabStrip.getChildAt(i) as ViewGroup).getChildAt(1) as AppCompatTextView
                view.setTypeface(mTypeface, Typeface.NORMAL)
                i++
            }
        }
    }

}