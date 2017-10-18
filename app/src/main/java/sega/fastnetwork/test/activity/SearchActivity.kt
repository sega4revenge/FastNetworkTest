package sega.fastnetwork.test.activity


import android.Manifest
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
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.searchmain_layout.*
import sega.fastnetwork.test.MyApplication
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.fragment.FilterFragment
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.presenter.SearchPresenterImp
import sega.fastnetwork.test.service.LocationService
import sega.fastnetwork.test.util.Constants


/**
 * Created by VinhNguyen on 8/9/2017.
 */
class SearchActivity : AppCompatActivity(), SearchPresenterImp.SearchView, ProductAdapter.OnproductClickListener, FilterFragment.Callbacks {


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
        val inputManager = this
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //check if no view has focus:
        ed_search.clearFocus()
        val v = this.currentFocus
        if (v != null)
            inputManager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        isTablet = resources.getBoolean(R.bool.is_tablet)
        SearchView = SearchPresenterImp(this)
        layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(this, this, product_recycleview, layoutManager!!)
        fab_search.setOnClickListener {

            val dialogFrag = FilterFragment.newInstance()
            val args = Bundle()
            args.putBoolean("isMap", isMap)
            dialogFrag.arguments = args
            val inputManager = this
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //check if no view has focus:
            ed_search.clearFocus()
            val v = this.currentFocus
            if (v != null)
                inputManager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            dialogFrag.show(supportFragmentManager, dialogFrag.tag)
        }
        nestedScrollView.visibility = View.VISIBLE
        action_grid.setOnClickListener {

            when (isMap) {
                false -> {
                    println("ban do")
                    isMap = true
                    nestedScrollView.visibility = View.GONE
                    layout_map.visibility = View.VISIBLE
                    val permissionlistener = object : PermissionListener, OnMapReadyCallback {
                        override fun onMapReady(map: GoogleMap?) {
                            if (!isMap)
                                layout_map.visibility = View.GONE
                            mMap = map
                            mLocation = mMap!!.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("My Location"))
                            mMap!!.setOnMapClickListener { latLng ->


                                mMap!!.clear()
                                mMap!!.addMarker(MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .title("Location"))
                                mMap!!.addMarker(MarkerOptions().position(myLocation!!).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("My Location"))
                                val circleOptions = CircleOptions().center(latLng).radius(10000.0).fillColor(Color.argb(100, 78, 200, 156)).strokeColor(Color.BLUE).strokeWidth(8f)

                                circle = mMap!!.addCircle(circleOptions)

                                listProductMaker.clear()
                                SearchView!!.searchWithMap(ed_search.query.toString(), latLng, cate, 10)
                            }
                            mMap!!.isMyLocationEnabled = true
                            mMap!!.setOnMyLocationButtonClickListener({

                                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17.0f))
                                true
                            })

                        }

                        override fun onPermissionGranted() {
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
                                        Toast.makeText(this@SearchActivity, "started", Toast.LENGTH_SHORT).show()
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


                        override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>) =
                                Toast.makeText(this@SearchActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()


                    }
                    TedPermission.with(this@SearchActivity)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
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
        product_recycleview.setHasFixedSize(true)
        product_recycleview.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        product_recycleview.adapter = adapter
        pageToDownload = 1
        ed_search.isFocusable = false
        ed_search.setIconifiedByDefault(false)
        ed_search.isIconified = false
        ed_search.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                SearchView!!.cancelRequest()
                SearchView!!.searchWithList(ed_search.query.toString(), loca, cate, mFilter)
                ed_search.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean = true


        })
        SearchView!!.searchWithList(ed_search.query.toString(), loca, cate, mFilter)
    }


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


            val latitude = intent.getStringExtra("latutide")
            val longitude = intent.getStringExtra("longitude")

            myLocation = LatLng(latitude.toDouble(), longitude.toDouble())
            mLocation?.position = myLocation
            if(intent.getBooleanExtra("first",false))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation?.position, 17.0f))
            Toast.makeText(this@SearchActivity, latitude + " " + longitude, Toast.LENGTH_SHORT).show();

        }
    }

    override fun onResult(result: Any?) {
        loca = ""
        cate = ""
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
                SearchView!!.searchWithList(ed_search.query.toString(), loca, cate, 0)
                Log.d("k9res", loca + cate)
            }

        }


    }


    //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //  }
    override fun setMessagerNotFound() {
        product_recycleview.visibility = View.GONE
        txt_notfound.visibility = View.VISIBLE
    }

    override fun onproductClicked(position: Int) {
        println("da nhan")
        if (isTablet) {

        } else {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
            startActivity(intent)
        }
    }

    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }

    override fun getListProduct(productlist: ArrayList<Product>) {
        if (isMap) {


            for (i in productlist.indices) {

                val PERTH = LatLng(productlist[i].location!!.coordinates!![1], productlist[i].location!!.coordinates!![0])
                mMap!!.addMarker(MarkerOptions()
                        .position(PERTH)
                        .snippet(productlist[i].category)
                        .snippet(productlist[i].price)
                        .title(productlist[i].productname))

            }
        } else {
            if (adapter!!.productList.size > 0) {
                adapter!!.productList.clear()
                Log.d("test search ", "clear")

            }
            adapter!!.productList = productlist
            onDownloadSuccessful()
        }

    }

    private fun onDownloadSuccessful() {
        if (isTablet && adapter?.productList?.size!! > 0) {
            //(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))
        }
        isLoading = false
        product_recycleview.visibility = View.VISIBLE
        error_message.visibility = View.GONE
        txt_notfound.visibility = View.GONE
        adapter?.notifyDataSetChanged()

    }

    private fun onDownloadFailed() {
        isLoading = false
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
                    Toast.makeText(this@SearchActivity, "started", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(this@SearchActivity, "loi roi", Toast.LENGTH_SHORT).show()
            }


        }
    }

}