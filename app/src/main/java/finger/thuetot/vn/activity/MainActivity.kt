package finger.thuetot.vn.activity


import android.app.Fragment
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import finger.thuetot.vn.R
import finger.thuetot.vn.fragment.DrawerFragment
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.User
import io.fabric.sdk.android.Fabric
import io.socket.client.Socket
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by sega4 on 08/08/2017.
 */

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var isTablet: Boolean = false
    internal var fragment: Fragment? = null
    private var mSocket: Socket? = null
    var user: User? = null
    var type: String = ""
    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        Fabric.with(this, Crashlytics())

        setContentView(R.layout.activity_main)
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo("sega.fastnetwork.test", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }

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
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()

            (supportFragmentManager.findFragmentById(R.id.main_fragment) as DrawerFragment).isClosedDrawer()
            return
        }

        this.doubleBackToExitPressedOnce = true
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false

        }, 2000)

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
