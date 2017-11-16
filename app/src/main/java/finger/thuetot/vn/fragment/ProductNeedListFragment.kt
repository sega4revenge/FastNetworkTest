package finger.thuetot.vn.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_product_list.*
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.DetailNeedProductActivity
import finger.thuetot.vn.adapter.ProductAdapter
import finger.thuetot.vn.customview.DividerItemDecoration
import finger.thuetot.vn.lib.ShimmerRecycleView.OnLoadMoreListener
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.ProductListPresenter
import finger.thuetot.vn.util.Constants


/**
 * Created by Admin on 5/25/2016.
 */
class ProductNeedListFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListPresenter.ProductListView {
    override fun setErrorNotFound() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListSavedProduct(productsavedlist: User) {
    }

    private var mProductListPresenter: ProductListPresenter? = null


    private var isTablet: Boolean = false
    private var layoutManager: LinearLayoutManager? = null
    var isFirstLoad = true
    private var adapter: ProductAdapter? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isTablet = resources.getBoolean(R.bool.is_tablet)

        mProductListPresenter = ProductListPresenter(this)
        Log.e("haha", "need")

        layoutManager = LinearLayoutManager(activity)
        adapter = ProductAdapter(context, this, product_recycleview, layoutManager!!.findLastVisibleItemPosition())
        product_recycleview.setHasFixedSize(true)
        product_recycleview.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        product_recycleview.adapter = adapter

        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({
            // Toggle visibility
            error_message.visibility = View.GONE

            adapter!!.pageToDownload = 1
            adapter!!.productList.clear()
            adapter!!.initShimmer()
            adapter!!.isLoading = false
            isFirstLoad = true
            mProductListPresenter!!.getProductList(Constants.NEEDBORROW,adapter!!.pageToDownload,0)
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

            mProductListPresenter!!.getProductList(Constants.NEEDBORROW, adapter!!.pageToDownload,0)
        })

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return inflater!!.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onDestroy() {
        super.onDestroy()
        mProductListPresenter?.stopRequest()

    }

    private fun onDownloadSuccessful() {

            if (isTablet && adapter?.productList?.size!! > 0) {
                /*(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))*/
            }
            adapter!!.isLoading = false

            error_message.visibility = View.GONE



            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true


            adapter?.notifyDataSetChanged()



    }

    private fun onDownloadFailed() {

        adapter!!.isLoading = false
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
        println("da nhan")

             val intent = Intent(context, DetailNeedProductActivity::class.java)
             intent.putExtra("product", adapter!!.productList[position])
             startActivity(intent)
        }






}
