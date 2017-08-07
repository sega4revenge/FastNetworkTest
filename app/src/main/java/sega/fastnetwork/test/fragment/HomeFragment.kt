package sega.fastnetwork.test.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tab_home.view.*
import sega.fastnetwork.test.R


/**
 * Created by Admin on 3/15/2017.
 */

class HomeFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.tab_home, container, false)
        view.pager!!.adapter = TabsAdapter(childFragmentManager)
        view.tab_layout!!.setupWithViewPager(view.pager)

        setHasOptionsMenu(true)

        return view
    }

    internal inner class TabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(i: Int): Fragment? {
            when (i) {
                0 -> return TabGeoCone()
                1 -> return TabGeoCone()
            }
            return null
        }

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return "Cần thuê"
                1 -> return "Cho thuê"
            }
            return ""
        }
    }


}