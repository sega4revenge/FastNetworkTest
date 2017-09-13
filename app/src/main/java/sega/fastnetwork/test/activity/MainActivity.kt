package sega.fastnetwork.test.activity

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.fragment.DrawerFragment


/**
 * Created by sega4 on 08/08/2017.
 */

class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    private var isTablet: Boolean = false
    internal var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(Runnable {
            try {
                val mf = SupportMapFragment.newInstance()
                supportFragmentManager.beginTransaction()
                        .add(R.id.dummy_map_view, mf)
                        .commit()
                runOnUiThread { mf.getMapAsync(this@MainActivity) }
            } catch (ignored: Exception) {

            }
        }).start()


        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "oke")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        if (isTablet && savedInstanceState == null) {
           /* if (session!!.lastpage == "setting") {
                loadSettingFragment()
                println(session!!.lastpage)
            } else {*/

                loadDetailFragmentWith("null")

        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Do nothing because we only want to pre-load map.

    }
    override fun onBackPressed() {


        (main_fragment as DrawerFragment).isClosedDrawer()

    }
    fun loadDetailFragmentWith(productId: String) {
        /*   ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID, productUserId);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().remove(fragment).commit();
        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();*/
    }

    fun loadSettingFragment() {
        /*  fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();*/


    }

    fun loadDetailFragmentUser(productId: String, productUserId: String) {
        /*    fragment = new ProductDetailFragmentUser();
        Bundle args = new Bundle();
        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID, productUserId);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();*/
    }

    public override fun onResume() {
        super.onResume()

    }

    public override fun onStart() {
        super.onStart()

    }

    public override fun onStop() {
        super.onStop()

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }


}
