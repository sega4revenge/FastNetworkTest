package sega.fastnetwork.test.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tab1.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.presenter.ProductListPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.ProductListView


/**
 * Created by Admin on 5/25/2016.
 */
class ProductListFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListView {

    var mProductListPresenter: ProductListPresenter? = null
    private var isLoading: Boolean = false
    private var isLoadingLocked: Boolean = false
    private var pageToDownload: Int = 0
    internal var isTablet: Boolean = false
    private var layoutManager: GridLayoutManager? = null
    private var adapter: ProductAdapter? = null
    var v: View? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater!!.inflate(R.layout.fragment_tab1, container, false)
        isTablet = resources.getBoolean(R.bool.is_tablet)
        adapter = ProductAdapter(context, this)
        mProductListPresenter = ProductListPresenter(this)

        //        floatingActionsMenu.setVisibility(View.VISIBLE);
        layoutManager = GridLayoutManager(context, getNumberOfColumns())
        v!!.product_recycleview.setHasFixedSize(true)
        v!!.product_recycleview.layoutManager = layoutManager
        v!!.product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 1))
        v!!.product_recycleview.adapter = adapter


        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.product_LIST)) {

            mProductListPresenter!!.getProductList()
        } else {
            adapter!!.productList = savedInstanceState.get(Constants.product_LIST) as ArrayList<Product>
            /*   pageToDownload = savedInstanceState.getInt(Constants.PAGE_TO_DOWNLOAD)
               isLoadingLocked = savedInstanceState.getBoolean(Constants.IS_LOCKED)
               isLoading = savedInstanceState.getBoolean(Constants.IS_LOADING)*/
            // Download again if stopped, else show list
            if (isLoading) {
                if (pageToDownload == 1) {
                    v!!.progress_circle.visibility = View.VISIBLE
                    v!!.loading_more.setVisibility(View.GONE)
                    v!!.product_recycleview.setVisibility(View.GONE)
                    v!!.swipe_refresh.setVisibility(View.GONE)
                } else {
                    v!!.progress_circle.setVisibility(View.GONE)
                    v!!.loading_more.setVisibility(View.VISIBLE)
                    v!!.product_recycleview.setVisibility(View.VISIBLE)
                    v!!.swipe_refresh.setVisibility(View.VISIBLE)
                }

                mProductListPresenter!!.getProductList()

            } else {
                onDownloadSuccessful()
            }
        }

        return v
    }

    private fun onDownloadSuccessful() {
        if (isTablet && adapter?.productList?.size!! > 0) {
            /*(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))*/
        }
        isLoading = false
        v!!.error_message.setVisibility(View.GONE)
        v!!.progress_circle.setVisibility(View.GONE)
        v!!.loading_more.setVisibility(View.GONE)
        v!!.product_recycleview.setVisibility(View.VISIBLE)
        v!!.swipe_refresh.setVisibility(View.VISIBLE)
        v!!.swipe_refresh.setRefreshing(false)
        v!!.swipe_refresh.setEnabled(true)
        adapter?.notifyDataSetChanged()


    }

    private fun onDownloadFailed() {
        isLoading = false
        if (pageToDownload == 1) {
            v!!.progress_circle.setVisibility(View.GONE)
            v!!.loading_more.setVisibility(View.GONE)
            v!!.product_recycleview.setVisibility(View.GONE)
            v!!.swipe_refresh.setRefreshing(false)
            v!!.swipe_refresh.setVisibility(View.GONE)
            v!!.error_message.setVisibility(View.VISIBLE)
        } else {
            v!!.progress_circle.setVisibility(View.GONE)
            v!!.loading_more.setVisibility(View.GONE)
            v!!.error_message.setVisibility(View.GONE)
            v!!.product_recycleview.setVisibility(View.VISIBLE)
            v!!.swipe_refresh.setVisibility(View.VISIBLE)
            v!!.swipe_refresh.setRefreshing(false)
            v!!.swipe_refresh.setEnabled(true)
            isLoadingLocked = true
        }
    }

    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }

    override fun getListProduct(productlist: ArrayList<Product>) {
        adapter!!.productList = productlist
        onDownloadSuccessful()
    }

    override fun onproductClicked(position: Int) {

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun getNumberOfColumns(): Int {
        // Get screen width
        val displayMetrics = resources.displayMetrics
        var widthPx = displayMetrics.widthPixels.toFloat()
        if (isTablet) {
            widthPx = widthPx / 3
        }
        // Calculate desired width
        val preferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
        if (preferences.getInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID) == Constants.VIEW_MODE_GRID) {
            val desiredPx = resources.getDimensionPixelSize(R.dimen.product_card_width).toFloat()
            val columns = Math.round(widthPx / desiredPx)
            return if (columns > 2) columns else 2
        } else {
            val desiredPx = resources.getDimensionPixelSize(R.dimen.product_list_card_width).toFloat()
            val columns = Math.round(widthPx / desiredPx)
            return if (columns > 1) columns else 1
        }

    }
}
