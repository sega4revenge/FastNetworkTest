package sega.fastnetwork.test.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.CategoryAdapter


/**
 * Created by Admin on 3/15/2017.
 */

class CategoryFragment : Fragment(), CategoryAdapter.OncategoryClickListener {

    private var layoutManager: GridLayoutManager? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.category_fragment, container, false)
        val adapter = CategoryAdapter(context, this)

        layoutManager = GridLayoutManager(context, getNumberOfColumns())
       /* view.category_grid.setHasFixedSize(true)
        view.category_grid.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!

        view.category_grid.addItemDecoration(DividerItemDecoration(R.color.category_divider_color,1))
        view.category_grid.adapter = adapter
        adapter.notifyDataSetChanged()*/
        return view
    }

    override fun oncategoryClicked(position: Int,view : View) {

    }

    fun getNumberOfColumns(): Int {
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
}
