package sega.fastnetwork.test.activity


import android.Manifest
import android.app.Activity
import android.content.*
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
import sega.fastnetwork.test.lib.FabiousFilter.AAH_FabulousFragment
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.presenter.SearchPresenterImp
import sega.fastnetwork.test.service.LocationService
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.SearchView


/**
 * Created by VinhNguyen on 8/9/2017.
 */
class SearchActivity : AppCompatActivity(), OnMapReadyCallback, SearchView, ProductAdapter.OnproductClickListener, AAH_FabulousFragment.Callbacks {


    private var mLocationPicker: Marker? = null
    internal var mLocation: Marker? = null
    var SearchView: SearchPresenterImp? = null
    private var mMap: GoogleMap? = null
    var mCategory = 0
    var mFilter = 0
    internal var circle: Circle? = null
    var Location = ""

    var dialogFrag: FilterFragment? = null
    private var myOldLocation: LatLng? = null
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

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                val mLocationRequestwithBalanced = LocationRequest()
                mLocationRequestwithBalanced.interval = 30000
                mLocationRequestwithBalanced.fastestInterval = 10000
                mLocationRequestwithBalanced.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequestwithBalanced)
                val result = LocationServices.SettingsApi.checkLocationSettings(MyApplication.getGoogleApiHelper()?.googleApiClient, builder.build())

                result.setResultCallback { result ->
                    val status = result.status

                    result.locationSettingsStates
                    when (status.statusCode) {
                        LocationSettingsStatusCodes.SUCCESS -> {
                            startService(Intent(this@SearchActivity, LocationService::class.java))
                            Toast.makeText(this@SearchActivity, "started", Toast.LENGTH_SHORT).show()
                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->

                            try {

                                status.startResolutionForResult(
                                        this@SearchActivity,
                                        REQUEST_CHECK_SETTINGS)
                            } catch (e: IntentSender.SendIntentException) {

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

        isTablet = resources.getBoolean(R.bool.is_tablet)
        SearchView = SearchPresenterImp(this)
        layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(this, this, product_recycleview, layoutManager!!)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fab_search.setOnClickListener {
            dialogFrag = FilterFragment.newInstance()
            val inputManager = this
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            //check if no view has focus:
            ed_search.clearFocus()
            val v = this.currentFocus
            if (v != null)
                inputManager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            dialogFrag!!.show(supportFragmentManager, dialogFrag!!.tag)
        }
        nestedScrollView.visibility = View.VISIBLE
        action_grid.setOnClickListener {

            when (isMap) {
                false -> {
                    println("ban do")
                    isMap = true
                    nestedScrollView.visibility = View.GONE
                    layout_map.visibility = View.VISIBLE
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
        ed_search.setIconifiedByDefault(false)
        ed_search.isIconified = false
        ed_search.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                SearchView!!.cancelRequest()
                SearchView!!.ConnectHttp(ed_search.query.toString(), Location, mCategory, mFilter)
                ed_search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean = true


        })

    }

    override fun onMapReady(map: GoogleMap?) {
        /*  if(!isMap)
              layout_map.visibility = View.GONE
          map!!.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))
          mMap = map
          if (mPerth != null) {
              mPerth!!.remove()
          }
          mMap!!.setOnMapClickListener { latLng ->
              if (mLocationPicker != null) {
                  mLocationPicker!!.remove()
              }


              mMap!!.clear()

              val circleOptions = CircleOptions().center(latLng).radius(10000.0).fillColor(Color.argb(100, 78, 200, 156)).strokeColor(Color.BLUE).strokeWidth(8f)

              circle = mMap!!.addCircle(circleOptions)
          }*/
        mMap = map
        mLocation = mMap!!.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("My Location"))


    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


            val latitude = intent.getStringExtra("latutide")
            val longitude = intent.getStringExtra("longitude")

            myLocation = LatLng(latitude.toDouble(), longitude.toDouble())




            mMap!!.animateCamera(myLocation?.let { CameraUpdateFactory.newLatLngZoom(myLocation, 17.0f) })

            val circleOptions = CircleOptions().center(myLocation).radius(10000.0).fillColor(Color.argb(100, 78, 200, 156)).strokeColor(Color.BLUE).strokeWidth(8f)
            circle?.remove()
            circle = mMap!!.addCircle(circleOptions)
            mLocation!!.position = myLocation



            Toast.makeText(this@SearchActivity, latitude + " " + longitude, Toast.LENGTH_SHORT).show();

        }
    }

    override fun onResult(result: Any?) {
        Log.d("k9res", "onResult: " + result.toString())
        println(applied_filters["category"])
        println(applied_filters["location"])
        println(applied_filters["filter"])
        SearchView!!.ConnectHttp(ed_search.query.toString(), Location, mCategory, mFilter)

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


        product_recycleview.visibility = View.VISIBLE

        txt_notfound.visibility = View.GONE
        adapter?.notifyDataSetChanged()

    }

    private fun onDownloadFailed() {
        isLoading = false
        if (pageToDownload == 1) {


            product_recycleview.visibility = View.GONE

            error_message.visibility = View.VISIBLE
        } else {

            error_message.visibility = View.GONE
            product_recycleview.visibility = View.VISIBLE

            isLoadingLocked = true
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
                    startService(Intent(this@SearchActivity, LocationService::class.java))
                    Toast.makeText(this@SearchActivity, "started", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(this@SearchActivity, "loi roi", Toast.LENGTH_SHORT).show()
            }


        }
    }

}