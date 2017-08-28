package sega.fastnetwork.test.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.filter_product.view.*
import kotlinx.android.synthetic.main.searchmain_layout.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.presenter.SearchPresenterImp
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.SearchView




/**
 * Created by VinhNguyen on 8/9/2017.
 */
class SearchActivity : AppCompatActivity(),SearchView,ProductAdapter.OnproductClickListener {


    private val INTENT_DATA_AllLOCATION = 100
    private val INTENT_DATA_CATEGORY = 10001
    private val INTENT_DATA_LOCATION = 1000
    var SearchView : SearchPresenterImp? = null
    var typeLocation = 0
    var mLocation = 0
    var mCategory = 0
    var mFilter = 0
    var styleList = 0
    var viewtype: Int = 1
    var Location = ""
    var thread: Thread? =null
    private  var countdowntime: CountDownTimer? =null
    private  var bool_search = false
    private var preferences: SharedPreferences? = null
    private var isLoading: Boolean = false
    private var isLoadingLocked: Boolean = false
    private var pageToDownload: Int = 0
    internal var isTablet: Boolean = false
    private var layoutManager: GridLayoutManager? = null
    private var adapter: ProductAdapter? = null
    private  var handler : Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchmain_layout)
        preferences = this.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
        isTablet = resources.getBoolean(R.bool.is_tablet)
        SearchView = SearchPresenterImp(this)
        adapter = ProductAdapter(this, this)
        layoutManager = GridLayoutManager(this, getNumberOfColumns())
        tabLocation.setOnClickListener {
            ShowLocation()
        }
        tabCategory.setOnClickListener {
            ShowCategory()
        }
        tabArrange.setOnClickListener {
            ShowArrange()
        }
        product_recycleview.setHasFixedSize(true)
        product_recycleview.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        product_recycleview.adapter = adapter
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener({
            error_message.visibility = View.GONE
            progress_circle.visibility = View.GONE
            product_recycleview.visibility = View.GONE
            pageToDownload = 1
            SearchView!!.ConnectHttp(ed_search.text.toString() , Location,mCategory,mFilter)

        })
        pageToDownload = 1
        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.product_LIST)) {
              progress_circle.visibility = View.VISIBLE
            SearchView!!.ConnectHttp(ed_search.text.toString(),"",mCategory,mFilter)
        } else {
            adapter!!.productList = savedInstanceState.getParcelableArrayList(Constants.product_LIST)

            if (isLoading) {
                if (pageToDownload == 1) {
                    progress_circle.visibility = View.VISIBLE
                    loading_more.visibility = View.GONE
                    product_recycleview.visibility = View.GONE
                    swipe_refresh.visibility = View.GONE
                } else {
                    progress_circle.visibility = View.GONE
                    loading_more.visibility = View.VISIBLE
                    product_recycleview.visibility = View.VISIBLE
                    swipe_refresh.visibility = View.VISIBLE
                }

                  progress_circle.visibility = View.VISIBLE
                SearchView!!.ConnectHttp(ed_search.text.toString() ,Location,mCategory,mFilter)

            } else {
                onDownloadSuccessful()
            }
        }
        stylelist.setOnClickListener {

            when (viewtype) {
                1 -> {

                    preferences!!.edit().putInt(Constants.VIEW_MODE, Constants.VIEW_MODE_LIST).apply()

                    onRefreshToolbarMenu()
                    onRefreshListProduct()


                }
                2 -> {
                    preferences!!.edit().putInt(Constants.VIEW_MODE, Constants.VIEW_MODE_COMPACT).apply()
                    onRefreshToolbarMenu()
                    onRefreshListProduct()


                }
                3 -> {
                    preferences!!.edit().putInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID).apply()
                    onRefreshToolbarMenu()
                    onRefreshListProduct()


                }
            }
        }

        ed_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("ed_search: ","afterTextChanged")
                if(bool_search){
                    countdowntime?.cancel()
                }

                 countdowntime = object : CountDownTimer(1500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        bool_search = true
                    }
                    override fun onFinish() {
                        Log.d("ed_search: ","finish")
                        SearchView!!.ConnectHttp(ed_search.text.toString() , Location,mCategory,mFilter)
                        bool_search = false
                    }
                }.start()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("ed_search: ","beforeTextChanged:"+p0.toString() +","+ p1 + "," + p2 + "," + p3)
                if(progress_circle.visibility != View.VISIBLE)
                {progress_circle.visibility = View.VISIBLE}
                loading_more.visibility = View.GONE
                product_recycleview.visibility = View.GONE
                swipe_refresh.isRefreshing = false
                swipe_refresh.visibility = View.GONE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("ed_search: ","onTextChanged:"+p0.toString() +","+ p1 + "," + p2 + "," + p3)
            }
        })
    }

    private fun onRefreshListProduct() {
        adapter?.notifyDataSetChanged()
    }

    private fun onRefreshToolbarMenu() {
        viewtype = preferences?.getInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID)!!
        if (viewtype == Constants.VIEW_MODE_GRID) {
            // Change from grid to list

            stylelist.setImageResource(R.drawable.action_grid)


        } else if (viewtype == Constants.VIEW_MODE_LIST) {

            stylelist.setImageResource(R.drawable.action_list)


        } else {
            // Change from compact to grid

            stylelist.setImageResource(R.drawable.action_compact)


        }
    }
    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
    override fun ShowLocation() {
        val intent = Intent(this@SearchActivity, Search_FilterLocationActivity::class.java)
        startActivityForResult(intent, INTENT_DATA_LOCATION)
    }

    override fun ShowCategory() {
        val intent = Intent(this@SearchActivity, Search_FliterCategory_Activity::class.java)
        startActivityForResult(intent, INTENT_DATA_CATEGORY)
    }
    //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //  }
    override fun setMessagerNotFound() {
       progress_circle.visibility = View.GONE
       loading_more.visibility = View.GONE
       product_recycleview.visibility = View.GONE
       swipe_refresh.isRefreshing = false
       swipe_refresh.visibility = View.GONE
       txt_notfound.visibility = View.VISIBLE
    }
    override fun ShowArrange() {
        val builder = AlertDialog.Builder(this@SearchActivity)
        var view = layoutInflater.inflate(R.layout.filter_product,null)
        builder.setView(view)
        val dialog = builder.create()
        when(mFilter)
        {
            1 -> view.filter_newimg.visibility = View.VISIBLE

            2 -> view.filter_smallimg.visibility = View.VISIBLE

            3 -> view.filter_hignimg.visibility = View.VISIBLE

            else -> view.filter_hotimg.visibility = View.VISIBLE
        }
        view.filter_hot.setOnClickListener {
            mFilter = 0
            dialog.dismiss()
              progress_circle.visibility = View.VISIBLE
            SearchView!!.ConnectHttp(ed_search.text.toString() , Location,mCategory,mFilter)
        }
        view.filter_new.setOnClickListener {
            mFilter = 1
            dialog.dismiss()
              progress_circle.visibility = View.VISIBLE
            SearchView!!.ConnectHttp(ed_search.text.toString() ,Location,mCategory,mFilter)
        }
        view.filter_small.setOnClickListener {
            mFilter = 2
            dialog.dismiss()
              progress_circle.visibility = View.VISIBLE
            SearchView!!.ConnectHttp(ed_search.text.toString() ,Location,mCategory,mFilter)
        }
        view.filter_hign.setOnClickListener {
            mFilter = 3
            dialog.dismiss()
              progress_circle.visibility = View.VISIBLE
            SearchView!!.ConnectHttp(ed_search.text.toString() ,Location,mCategory,mFilter)
        }
        dialog.show()


    }
    override fun onproductClicked(position: Int) {
        println("da nhan")
        if (isTablet) {

        } else {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(Constants.product_ID,adapter!!.productList[position]._id!!)
            startActivity(intent)
        }
    }
    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }

    override fun getListProduct(productlist: ArrayList<Product>) {
            if (adapter!!.productList.size > 0) {
                adapter!!.productList.clear()
                Log.d("test search ", "clear")

            }
            adapter!!.productList = productlist
            onDownloadSuccessful()
    }
    private fun onDownloadSuccessful() {
        if (isTablet && adapter?.productList?.size!! > 0) {
            //(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))
        }
        isLoading = false
        error_message.visibility = View.GONE
        progress_circle.visibility = View.GONE
        loading_more.visibility = View.GONE
        product_recycleview.visibility = View.VISIBLE
        swipe_refresh.visibility = View.VISIBLE
        swipe_refresh.isRefreshing = false
        swipe_refresh.isEnabled = true
        txt_notfound.visibility = View.GONE
        adapter?.notifyDataSetChanged()

    }

    private fun onDownloadFailed() {
        isLoading = false
        if (pageToDownload == 1) {
            progress_circle.visibility = View.GONE
            loading_more.visibility = View.GONE
            product_recycleview.visibility = View.GONE
            swipe_refresh.isRefreshing = false
            swipe_refresh.visibility = View.GONE
            error_message.visibility = View.VISIBLE
        } else {
            progress_circle.visibility = View.GONE
            loading_more.visibility = View.GONE
            error_message.visibility = View.GONE
            product_recycleview.visibility = View.VISIBLE
            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true
            isLoadingLocked = true
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
         preferences = this.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
        if (preferences!!.getInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID) == Constants.VIEW_MODE_GRID) {
            val desiredPx = resources.getDimensionPixelSize(R.dimen.product_card_width).toFloat()
            val columns = Math.round(widthPx / desiredPx)
            return if (columns > 2) columns else 2
        } else {
            val desiredPx = resources.getDimensionPixelSize(R.dimen.product_list_card_width).toFloat()
            val columns = Math.round(widthPx / desiredPx)
            return if (columns > 1) columns else 1
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTENT_DATA_CATEGORY) {
            mCategory = data.getIntExtra("typeCategory",1)
            var arrCategory  = resources.getStringArray(R.array.mCategory)
            txt_namecategory.text = arrCategory[mCategory]
              progress_circle.visibility = View.VISIBLE
            SearchView!!.ConnectHttp(ed_search.text.toString() ,Location,mCategory,mFilter)
        }else if (requestCode == INTENT_DATA_LOCATION){
            typeLocation = data.getIntExtra("typeLocation",1)
            if(typeLocation != 404 && typeLocation != 405)
            {
                mLocation = data.getIntExtra("position",1)
                if(typeLocation == 1)
                {
                    var arrLoca = resources.getStringArray(R.array.mLocation_Bac)
                    txt_namelocation.text = arrLoca[mLocation]
                }else  if(typeLocation == 2){
                    var arrLoca = resources.getStringArray(R.array.mLocation_Trung)
                    txt_namelocation.text = arrLoca[mLocation]
                }else if(typeLocation == 3){
                    var  arrLoca = resources.getStringArray(R.array.mLocation_Nam)
                    txt_namelocation.text = arrLoca[mLocation]
                }
                Location = txt_namelocation.text.toString()
                  progress_circle.visibility = View.VISIBLE
                SearchView!!.ConnectHttp(ed_search.text.toString() , Location,mCategory,mFilter)

            }else  if(typeLocation == 405){
                txt_namelocation.text = "Toàn quốc"
                Location = ""
                mLocation =999
                  progress_circle.visibility = View.VISIBLE
                SearchView!!.ConnectHttp(ed_search.text.toString() ,Location,mCategory,mFilter)
            }

        }

    }
}