package finger.thuetot.vn.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import finger.thuetot.vn.MyApplication
import finger.thuetot.vn.R
import finger.thuetot.vn.adapter.ProductAdapter
import finger.thuetot.vn.customview.DividerItemDecoration
import finger.thuetot.vn.fragment.FilterFragment
import finger.thuetot.vn.lib.ShimmerRecycleView.OnLoadMoreListener
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.presenter.SearchPresenterImp
import finger.thuetot.vn.service.LocationService
import finger.thuetot.vn.util.Constants
import kotlinx.android.synthetic.main.layout_error_message.*
import kotlinx.android.synthetic.main.searchmain_layout.*


/**
 * Created by VinhNguyen on 8/9/2017.
 */
class SearchActivity : AppCompatActivity() , SearchPresenterImp.SearchView, ProductAdapter.OnproductClickListener, FilterFragment.Callbacks {


    internal var mLocation: Marker? = null
    var SearchView: SearchPresenterImp? = null
    private var mMap: GoogleMap? = null
    var mCategory = 0
    var mFilter = 0
    internal var circle: Circle? = null
    var Location = ""
    var loca = ""
    var cate = ""
    val mLocationRequestwithBalanced = LocationRequest()
    var isFirstLoad = true
    var isLodaing = true
    var isFistTime = true
    internal var listProductMaker = java.util.ArrayList<Marker>()
    private var myLocation: LatLng? = null
    private var isLoading: Boolean = false
    private var isLoadingLocked: Boolean = false
    private var pageToDownload: Int = 0
    private var isTablet: Boolean = false
    private val applied_filters = ArrayMap<String, MutableList<String>>()
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: ProductAdapter? = null
    private var isMap: Boolean = false
    private var REQUEST_CHECK_SETTINGS = 1000


    fun getApplied_filters(): ArrayMap<String, MutableList<String>> = applied_filters



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchmain_layout)
        isTablet = resources.getBoolean(R.bool.is_tablet)
        SearchView = SearchPresenterImp(this)
        layoutManager = LinearLayoutManager(this)



       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        fab_search.setOnClickListener {

            val dialogFrag = FilterFragment.newInstance()
            val args = Bundle()
            args.putBoolean("isMap", isMap)
            dialogFrag.arguments = args
            val inputManager = this
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            //check if no view has focus:+a

            val v = this.currentFocus
            if (v != null)
                inputManager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            dialogFrag.show(supportFragmentManager, dialogFrag.tag)
        }
        nestedScrollView.visibility = View.VISIBLE
        back_button.setOnClickListener {
            finish()
        }
        action_grid.setOnClickListener {

            when (isMap) {
                false -> {
                    println("ban do")
                    isMap = true
                    nestedScrollView.visibility = View.GONE
                    layout_map.visibility = View.VISIBLE
                    val permissionlistener = object : PermissionListener, OnMapReadyCallback {
                        @SuppressLint("MissingPermission")
                        override fun onMapReady(map: GoogleMap?) {
                            if (!isMap)
                                layout_map.visibility = View.GONE

                            var iconMe = BitmapDescriptorFactory.fromResource(R.drawable.man)
                            mMap = map

                            mLocation = mMap!!.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("My Location"))
                            mMap!!.setOnMapClickListener { latLng ->
                                Log.d("taG: asdasd","sssssssssssss")
                                var iconpick = BitmapDescriptorFactory.fromResource(R.drawable.pin)
                                mMap!!.clear()
                                mMap!!.addMarker(MarkerOptions()
                                        .position(latLng)
                                        .icon(iconpick)
                                        .title(getString(R.string.location)))
                                if(myLocation!=null)
                                {
                                    mMap!!.addMarker(MarkerOptions().position(myLocation!!).title("Me"))
                                }

                                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.5f))
                                val circleOptions = CircleOptions().center(latLng).radius(10000.0).fillColor(Color.argb(100, 78, 200, 156)).strokeColor(Color.BLUE).strokeWidth(8f)

                                circle = mMap!!.addCircle(circleOptions)

                                listProductMaker.clear()
                                SearchView!!.searchWithMap(ed_search.query.toString(), latLng, cate, 10)
                            }
                            mMap!!.isMyLocationEnabled = true
                            mMap!!.setOnMyLocationButtonClickListener({

                                try{
                                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17.0f))
                                }catch (e: Exception){
                                    print(e.message.toString())
                                }

                                true
                            })

                        }

                        override fun onPermissionGranted() {

                                try{
                                    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                                    mapFragment.getMapAsync(this)
                                    //  Toast.makeText(this@SearchActivity, "ok", Toast.LENGTH_SHORT).show()
                                    mLocationRequestwithBalanced.interval = 30000
                                    mLocationRequestwithBalanced.fastestInterval = 10000
                                    mLocationRequestwithBalanced.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                                    val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequestwithBalanced)
                                    val result = LocationServices.SettingsApi.checkLocationSettings(MyApplication.getGoogleApiHelper()!!.googleApiClient, builder.build())

                                    result.setResultCallback { result ->
                                        val status = result.status

                                        result.locationSettingsStates
                                        when (status.statusCode) {
                                            LocationSettingsStatusCodes.SUCCESS -> {
                                                val intent = Intent(this@SearchActivity, LocationService::class.java)
                                                intent.putExtra("locationrequest", mLocationRequestwithBalanced)
                                                startService(intent)
                                                Toast.makeText(this@SearchActivity, getString(R.string.started), Toast.LENGTH_SHORT).show()
                                            }
                                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->

                                                try {


                                                    status.startResolutionForResult(
                                                            this@SearchActivity,
                                                            REQUEST_CHECK_SETTINGS)
                                                } catch (e: Throwable) {

                                                }

                                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                            }
                                        }
                                    }
                                }
                                catch (e : Exception){
                                    Log.e("error",e.toString())
                                }
                        }


                        override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>) {
                            try{
                                Toast.makeText(this@SearchActivity, getString(R.string.per_deni) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()

                            }
                            catch (e : Exception){
                                Log.e("error",e.toString())
                            }
                        }



                    }
                    TedPermission.with(this@SearchActivity)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage(getString(R.string.per_turnon))
                            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                            .check()

                }
                else -> {
                    println("list")
                    isMap = false
                   nestedScrollView.visibility = View.VISIBLE
                    layout_map.visibility = View.GONE
                }
            }

        }
      //  product_recycleview.visibility = View.GONE
        product_recycleview.setHasFixedSize(true)
        product_recycleview.layoutManager = layoutManager
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(this, this, product_recycleview, layoutManager!!)
        product_recycleview.adapter = adapter
//        product_recycleview.addOnScrollListener(object: RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                Log.d("aaaaaaaaaa","xyz "+dx+" "+dy)
//            }
//        })
        adapter!!.pageToDownload = 1
       // adapter!!.initShimmer()

        adapter!!.setOnLoadMoreListener(OnLoadMoreListener {

            if (!isFirstLoad) {
                val a = Product()
                a.productname = ""
                adapter!!.productList.add(a)
                product_recycleview.post({
                    adapter!!.notifyItemInserted(adapter!!.productList.size - 1)
                })
            }
            if (!isFistTime) {
                adapter!!.pageToDownload++
            }
            if (isLodaing) {
                isLodaing = false
                SearchView?.searchWithListLoadMore(ed_search.query.toString(), loca, cate, mFilter,adapter?.pageToDownload!!)
                //  mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)
            }

        })

        ed_search.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                isFirstLoad = false
                adapter!!.pageToDownload = 1
                ed_search.setQuery("",true)
                ed_search.onActionViewCollapsed()
                SearchView?.searchWithListLoadMore("", loca, cate, mFilter,adapter?.pageToDownload!!)
              return true
            }

        })
        ed_search.setOnClickListener(){
            ed_search.isIconified = false
        }
        ed_search.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                isFirstLoad = false
                adapter!!.pageToDownload = 1
                SearchView!!.cancelRequest()
                SearchView?.searchWithListLoadMore(ed_search.query.toString(), loca, cate, mFilter,adapter?.pageToDownload!!)

                ed_search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                if(ed_search.query.toString() == "" || ed_search.query.toString().equals("") )
//                {
//                    SearchView!!.searchWithList("", loca, cate, mFilter)
//                }
                return true
            }


        })

//        ed_search.setOnClickListener(){
//            ed
//        }
//        ed_search.addTextChangedListener(object : TextWatcher{
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//        })
        SearchView?.searchWithListLoadMore(ed_search.query.toString(), loca, cate, mFilter,adapter?.pageToDownload!!)


        try_again.setOnClickListener {
            isFirstLoad = false
            SearchView?.searchWithListLoadMore(ed_search.query.toString(), loca, cate, mFilter,adapter?.pageToDownload!!)

        }
    }

    override fun onBackPressed() {
        if (!ed_search.isIconified())
        {
            ed_search.setIconified(true);
            ed_search.onActionViewCollapsed();
        }
        else
        {
            super.onBackPressed();
        }

    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(myLocation == null)
            {
                val latitude = intent.getStringExtra("latutide")
                val longitude = intent.getStringExtra("longitude")

                myLocation = LatLng(latitude.toDouble(), longitude.toDouble())
                try{
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17.0f))
                    SearchView!!.searchWithMap(ed_search.query.toString(), myLocation!!, cate, 10)
                }catch (e: Exception){
                    print(e.message.toString())
                }

            }
            else
            {
                val latitude = intent.getStringExtra("latutide")
                val longitude = intent.getStringExtra("longitude")

                myLocation = LatLng(latitude.toDouble(), longitude.toDouble())

            }
            mLocation?.position = myLocation


          //  if(intent.getBooleanExtra("first",false))
         //       try{
         //           mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation?.position, 17.0f))
        //        }catch (e: Exception){
         //           print(e.message.toString())
        //        }

        //    Toast.makeText(this@SearchActivity, latitude + " " + longitude, Toast.LENGTH_SHORT).show();

        }
    }

    override fun onResult(result: Any?) {
        loca = ""
        cate = ""
        adapter?.pageToDownload = 1
        isFirstLoad = false
        if (result.toString() != "swiped_down") {
            println(applied_filters["category"])
            println(applied_filters["location"])
            println(applied_filters["filter"])
            if (applied_filters["category"]?.size != null && applied_filters["category"]?.size!! > 0) {
                for (i in 0..(applied_filters["category"]?.size!! - 1)) {
                    cate = if (cate == "") {
                        applied_filters["category"]!![i]
                    } else {

                        cate + " , " + applied_filters["category"]!![i]
                    }
                }
            }


            if (!isMap) {
                if (applied_filters["location"]?.size != null && applied_filters["location"]?.size!! > 0) {
                    for (i in 0..(applied_filters["location"]?.size!! - 1)) {
                        loca = if (loca == "") {
                            applied_filters["location"]!![i]
                        } else {

                            loca + " , " + applied_filters["location"]!![i]
                        }
                    }
                }
                SearchView?.searchWithListLoadMore(ed_search.query.toString(), loca, cate, applied_filters["filter"]?.get(0)!!.toInt(),adapter?.pageToDownload!!)

             //   SearchView!!.searchWithList(ed_search.query.toString(), loca, cate, applied_filters["filter"]?.get(0)!!.toInt())
                Log.d("k9res", loca + cate)
            }

        }


    }


    //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //  }
    override fun setMessagerNotFound() {
        if(adapter?.pageToDownload!! == 1)
        {
            product_recycleview.visibility = View.GONE
            layout_map.visibility = View.GONE
            txt_notfound.visibility = View.VISIBLE
        }


    }

    override fun onproductClicked(position: Int) {
        println("da nhan")
        if (isTablet) {

        } else {
//            if (adapter!!.productList[position].type == "1") {
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
                intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
                startActivity(intent)
//            } else {
//                val intent = Intent(this, ProductDetailNeedActivity::class.java)
//                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
//                intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
//                startActivity(intent)
//            }
        }
    }

    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }
   fun geticonCategory(cate: String): BitmapDescriptor{
       var iconMe: BitmapDescriptor? = null
        when(cate)
        {
            "0" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_1)
            "1" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_2)
            "2" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_3)
            "3" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_4)
            "4" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_5)
            "5" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_6)
            "6" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_7)
            "7" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_8)
            "8" -> iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_9)
            else -> { // Note the block
                iconMe = BitmapDescriptorFactory.fromResource(R.drawable.maker_9)
            }
        }

       return iconMe
   }
    override fun getListProduct(productlist: ArrayList<Product>) {
        if (isMap) {

            for ( i: Int in productlist.indices) {
                Log.d("taG: position ", i.toString())
                Log.d("taG:  productname", productlist[i].productname)
                var iconCate: BitmapDescriptor? = null
                iconCate= geticonCategory(productlist[i]?.category!!)
                val PERTH = LatLng(productlist[i].location!!.coordinates!![1], productlist[i].location!!.coordinates!![0])
                mMap!!.addMarker(MarkerOptions()
                        .icon(iconCate)
                        .position(PERTH)
                        .snippet(productlist[i].category)
                        .snippet(productlist[i].price)
                        .title(productlist[i].productname))
                        .tag = i
            }
            mMap!!.setOnInfoWindowClickListener { marker ->
                if(marker.tag.toString().equals("null") && marker.tag.toString() == null){
                        Log.d("taG:  ", marker.tag.toString())
                }else{
                    Log.d("taG:  ", marker.tag.toString())
                    Log.d("taG:  ", productlist.get(marker.tag.toString().toInt()).productname)
                    val intent = Intent(this, ProductDetailActivity::class.java)
                    intent.putExtra(Constants.product_ID, productlist.get(marker.tag.toString().toInt())._id)
                    intent.putExtra(Constants.seller_ID, productlist.get(marker.tag.toString().toInt()).user?._id)
                    startActivity(intent)
                }

            }

        } else {
//            if (adapter!!.productList.size > 0) {
//                adapter!!.productList.clear()
//                Log.d("test search ", "clear")
//
//            }
//            adapter!!.productList.removeAt(adapter!!.productList.size - 1)
//            adapter!!.notifyItemRemoved(adapter!!.productList.size)
            if (!isFirstLoad) {
                adapter!!.productList.clear()
                isFirstLoad = true
            }
            adapter!!.productList.addAll(productlist)
            adapter!!.isLoading = false
            adapter!!.isLoadingLocked = false
         //   adapter!!.productList = productlist
            onDownloadSuccessful()
        }

    }

    private fun onDownloadSuccessful() {
        if (isTablet && adapter?.productList?.size!! > 0) {
            //(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))
        }
      //  isLoading = true
        product_recycleview.visibility = View.VISIBLE
        error_message.visibility = View.GONE
        txt_notfound.visibility = View.GONE
        adapter?.notifyDataSetChanged()
        isLodaing = true
        isFistTime = false

    }

    private fun onDownloadFailed() {
     //   isLoading = false
        if(!isMap)
        {
            if (pageToDownload == 1) {


                product_recycleview.visibility = View.GONE
                layout_map.visibility=View.GONE
                error_message.visibility = View.VISIBLE
            } else {

                error_message.visibility = View.GONE
                product_recycleview.visibility = View.VISIBLE

                isLoadingLocked = true
            }
        }

    }


    public override fun onResume() {
        super.onResume()
        // Determine screen name
        registerReceiver(this.broadcastReceiver, IntentFilter("servicetutorial.service.receiver"))

    }

    public override fun onDestroy() {
        super.onDestroy()
        SearchView?.cancelRequest()
        unregisterReceiver(this.broadcastReceiver)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var states = LocationSettingsStates.fromIntent(intent)

        when (requestCode) {
            1000 -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val intent = Intent(this@SearchActivity, LocationService::class.java)
                    intent.putExtra("locationrequest", mLocationRequestwithBalanced)
                    startService(intent)
                    Toast.makeText(this@SearchActivity, getString(R.string.started), Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(this@SearchActivity, getString(R.string.err), Toast.LENGTH_SHORT).show()
            }


        }
    }

}


