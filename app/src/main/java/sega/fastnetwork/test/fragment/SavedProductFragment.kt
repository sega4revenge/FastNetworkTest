package sega.fastnetwork.test.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidnetworking.AndroidNetworking
import kotlinx.android.synthetic.main.tab_home.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.MainActivity
import sega.fastnetwork.test.activity.ProductDetailActivity
import sega.fastnetwork.test.activity.ProductDetailNeedActivity
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.lib.ShimmerRecycleView.OnLoadMoreListener
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.ProductListPresenter
import sega.fastnetwork.test.util.Constants

/**
 * Created by cc on 9/18/2017.
 */


class SavedProductFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListPresenter.ProductListView {
    override fun setErrorNotFound() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListSavedProduct(productsavedlist: User) {
        Log.e("Saved", productsavedlist.listsavedproduct!!.size.toString())
        if(productsavedlist.listsavedproduct!!.size>0){
            adapter!!.productList.removeAt(adapter!!.productList.size - 1)
            adapter!!.notifyItemRemoved(adapter!!.productList.size)
            if (isFirstLoad) {
                adapter!!.productList.clear()
                isFirstLoad = false
            }
            adapter!!.productList.addAll(productsavedlist.listsavedproduct!!)
            adapter!!.pageToDownload++
            adapter!!.isLoading = false
            adapter!!.isLoadingLocked = false
            onDownloadSuccessful()
        }
        else{
            onListNull()
        }

    }


    var mProductListPresenter: ProductListPresenter? = null
    private var isTablet: Boolean = false
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: ProductAdapter? = null
    var isFirstLoad = true
    var mCategory = 0
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mBundle = Bundle()
        mBundle = arguments
        mCategory = mBundle.getInt("Category",1)
        Log.d("Runnnnnnnnn",mCategory.toString()+"111")
        isTablet = resources.getBoolean(R.bool.is_tablet)
        mProductListPresenter = ProductListPresenter(this)
        product_recycleview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        product_recycleview.layoutManager = layoutManager
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(context, this, product_recycleview, layoutManager!!)
        product_recycleview.adapter = adapter
        searchbar.visibility = View.GONE
        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({


            adapter!!.pageToDownload = 1
            adapter!!.productList.clear()
            adapter!!.initShimmer()
            adapter!!.isLoading = false
            isFirstLoad = true
            AndroidNetworking.cancelAll()
            mProductListPresenter!!.getSavedProductList(Constants.BORROW, adapter!!.pageToDownload , AppManager.getAppAccountUserId(activity))

        })
        adapter!!.pageToDownload = 1
        adapter!!.initShimmer()



        adapter!!.setOnLoadMoreListener(OnLoadMoreListener {
            if (!isFirstLoad) {
                val a = Product()
                a.productname = ""
                adapter!!.productList.add(a)
                product_recycleview.post({
                    adapter!!.notifyItemInserted(adapter!!.productList.size - 1)
                })
            }

            mProductListPresenter!!.getSavedProductList(Constants.BORROW, adapter!!.pageToDownload , AppManager.getAppAccountUserId(activity))
        })
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.tab_home, container, false)
//        view.pager!!.adapter = TabsAdapter(childFragmentManager)
//
//        view.tab_layout!!.setupWithViewPager(view.pager)
        retainInstance = true
        setHasOptionsMenu(true)
        return view
    }
    override fun onDestroy() {
        mProductListPresenter?.stopRequest()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onDetach() {
        super.onDetach()
    }
    private fun onDownloadSuccessful() {

        Log.e("onDownloadSuccessful","ádasd")
        error_message.visibility = View.GONE

        swipe_refresh.isRefreshing = false
        swipe_refresh.isEnabled = true


        adapter?.notifyDataSetChanged()


    }
    private fun onListNull() {

        if (adapter!!.pageToDownload == 1) {


            swipe_refresh.isRefreshing = false
            swipe_refresh.visibility = View.GONE
            nodata.visibility = View.VISIBLE
        } else {
            adapter!!.productList.removeAt(adapter!!.productList.size - 1)
            adapter!!.notifyItemRemoved(adapter!!.productList.size)

            nodata.visibility = View.GONE

            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true
            adapter!!.isLoadingLocked = true
        }


    }
    private fun onDownloadFailed() {

        if (adapter!!.pageToDownload == 1) {


            swipe_refresh.isRefreshing = false
            swipe_refresh.visibility = View.GONE
            error_message.visibility = View.VISIBLE
        } else {
            adapter!!.productList.removeAt(adapter!!.productList.size - 1)
            adapter!!.notifyItemRemoved(adapter!!.productList.size)

            error_message.visibility = View.GONE

            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true
            adapter!!.isLoadingLocked = true
        }


    }

    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }

    override fun getListProduct(productlist: ArrayList<Product>) {



        adapter!!.productList.removeAt(adapter!!.productList.size - 1)
        adapter!!.notifyItemRemoved(adapter!!.productList.size)
        if (isFirstLoad) {
            adapter!!.productList.clear()
            isFirstLoad = false
        }
        adapter!!.productList.addAll(productlist)
        adapter!!.pageToDownload++
        adapter!!.isLoading = false
        adapter!!.isLoadingLocked = false
        onDownloadSuccessful()
    }

    override fun onproductClicked(position: Int) {

        if (isTablet) {
            //                Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();

            (activity as MainActivity).loadDetailFragmentWith(adapter!!.productList[position]._id!!)
        } else {
            //                Toast.makeText(getActivity(),"4",Toast.LENGTH_LONG).show();
//            val intent = Intent(context, ProductDetailActivity::class.java)
//            intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
//            startActivity(intent)
            if (adapter!!.productList[position].type == "1") {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
                intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
                startActivity(intent)
            } else {
                val intent = Intent(context, ProductDetailNeedActivity::class.java)
                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
                intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
                startActivity(intent)
            }
        }
    }

//    internal inner class TabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
//
//        override fun getCount(): Int {
//            return 2
//        }
//
//        override fun getItem(i: Int): Fragment? {
//            when (i) {
//                0 -> return ProductListFragment()
//                1 -> return ProductNeedListFragment()
//            }
//            return null
//        }
//
//        override fun getPageTitle(position: Int): CharSequence {
//            when (position) {
//                0 -> return "Cần thuê"
//                1 -> return "Cho thuê"
//            }
//            return ""
//        }
//    }


}