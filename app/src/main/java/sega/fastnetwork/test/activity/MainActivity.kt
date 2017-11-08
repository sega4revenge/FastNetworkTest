package sega.fastnetwork.test.activity


import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.fragment.DrawerFragment
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User


/**
 * Created by sega4 on 08/08/2017.
 */

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var isTablet: Boolean = false
    internal var fragment: Fragment? = null
    private var mSocket: Socket? = null
    var user: User? = null
    var type: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_main)

//       var prefManager = PrefManager(this)
//        if(!prefManager.isFirstTimeLaunch())
//        {
//            val intent = Intent(this, AppIntroActivity::class.java)
//            startActivity(intent)
//        }else{
            if (AppManager.getAppAccount(this) != null) {

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
                try{
                    user = AppManager.getUserDatafromAccount(this, AppManager.getAppAccount(this)!!)
                    type = AppManager.getTypeuser(this, AppManager.getAppAccount(this)!!)
                    Log.e("Adasd", "ACCOUNT_TYPE" + type + "PW " + user?._id)
                }catch (e:Exception){
                    print(e.message)}


                mSocket = AppManager.getSocket(application)
                mSocket!!.emit("connected", user?._id)
            }
      //  }




    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Do nothing because we only want to pre-load map.

    }

    override fun onBackPressed() {


        (main_fragment as DrawerFragment).isClosedDrawer()

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

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.emit("disconnected", user?._id)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }


}
