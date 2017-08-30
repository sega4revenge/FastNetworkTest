package sega.fastnetwork.test.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidnetworking.AndroidNetworking
import kotlinx.android.synthetic.main.fragment_product_list.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.MainActivity
import sega.fastnetwork.test.activity.ProductDetailActivity
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.presenter.ProductListPresenter
import sega.fastnetwork.test.util.Constants


/**
 * Created by Admin on 5/25/2016.
 */
class ProductNeedListFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListPresenter.ProductListView {

    var mProductListPresenter: ProductListPresenter? = null
    private var isLoading: Boolean = false
    private var isLoadingLocked: Boolean = false
    private var pageToDownload: Int = 0
    internal var isTablet: Boolean = false
    private var layoutManager: GridLayoutManager? = null
    private var adapter: ProductAdapter? = null
    private var isDestroy = false
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isTablet = resources.getBoolean(R.bool.is_tablet)

        mProductListPresenter = ProductListPresenter(this)
        Log.e("haha", "need")

        layoutManager = GridLayoutManager(context, getNumberOfColumns())
        adapter = ProductAdapter(context, this, product_recycleview, layoutManager!!)
        product_recycleview.setHasFixedSize(true)
        product_recycleview.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        product_recycleview.adapter = adapter

        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({
            // Toggle visibility
            error_message.visibility = View.GONE


            // Remove cache

            // Download again
            pageToDownload = 1
            adapter!!.productList.clear()
            mProductListPresenter!!.getProductList(Constants.NEEDBORROW)
        })
        pageToDownload = 1
        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.product_LIST)) {

            mProductListPresenter!!.getProductList(Constants.NEEDBORROW)
        } else {
            adapter!!.productList = savedInstanceState.getParcelableArrayList(Constants.product_LIST)
            /*   pageToDownload = savedInstanceState.getInt(Constants.PAGE_TO_DOWNLOAD)
               isLoadingLocked = savedInstanceState.getBoolean(Constants.IS_LOCKED)
               isLoading = savedInstanceState.getBoolean(Constants.IS_LOADING)*/
            // Download again if stopped, else show list
            if (isLoading) {
                if (pageToDownload == 1) {

                    loading_more.visibility = View.GONE

                    swipe_refresh.visibility = View.GONE
                } else {

                    loading_more.visibility = View.VISIBLE

                    swipe_refresh.visibility = View.VISIBLE
                }

                mProductListPresenter!!.getProductList(Constants.NEEDBORROW)

            } else {
                onDownloadSuccessful()
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return inflater!!.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onDestroyView() {
        AndroidNetworking.cancelAll()
        isDestroy = true
        super.onDestroyView()

    }

    override fun onSaveInstanceState(outState: Bundle?) {

        if (layoutManager != null && adapter != null) {
            outState!!.putBoolean(Constants.IS_LOADING, isLoading)
            outState.putBoolean(Constants.IS_LOCKED, isLoadingLocked)
            outState.putInt(Constants.PAGE_TO_DOWNLOAD, pageToDownload)
            outState.putParcelableArrayList(Constants.product_LIST, adapter!!.productList)


        }
        super.onSaveInstanceState(outState)
    }

    fun refreshLayout() {
        val state = layoutManager!!.onSaveInstanceState()
        layoutManager = GridLayoutManager(context, getNumberOfColumns())
        product_recycleview.layoutManager = layoutManager
        layoutManager!!.onRestoreInstanceState(state)
        product_recycleview.performClick()


    }

    private fun onDownloadSuccessful() {
        if (!isDestroy) {
            if (isTablet && adapter?.productList?.size!! > 0) {
                /*(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))*/
            }
            isLoading = false

            error_message.visibility = View.GONE

            loading_more.visibility = View.GONE

            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true


            adapter?.notifyDataSetChanged()
        }


    }

    private fun onDownloadFailed() {
        if (!isDestroy) {
            isLoading = false
            if (pageToDownload == 1) {

                loading_more.visibility = View.GONE

                swipe_refresh.isRefreshing = false
                swipe_refresh.visibility = View.GONE
                error_message.visibility = View.VISIBLE
            } else {

                loading_more.visibility = View.GONE
                error_message.visibility = View.GONE

                swipe_refresh.visibility = View.VISIBLE
                swipe_refresh.isRefreshing = false
                swipe_refresh.isEnabled = true
                isLoadingLocked = true
            }
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
        println("da nhan")
        if (isTablet) {
            //                Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();

            (activity as MainActivity).loadDetailFragmentWith(adapter!!.productList[position]._id!!)
        } else {
            //                Toast.makeText(getActivity(),"4",Toast.LENGTH_LONG).show();
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
            startActivity(intent)
        }
    }


    fun getNumberOfColumns(): Int {
        // Get screen width
        val displayMetrics = resources.displayMetrics
        var widthPx = displayMetrics.widthPixels.toFloat()
        if (isTablet) {
            widthPx /= 3
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
