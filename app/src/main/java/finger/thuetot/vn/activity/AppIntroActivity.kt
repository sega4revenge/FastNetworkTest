package finger.thuetot.vn.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import finger.thuetot.vn.R
import finger.thuetot.vn.lib.firewall.SafetyNetHelper
import finger.thuetot.vn.lib.firewall.Utils
import finger.thuetot.vn.manager.PrefManager
import finger.thuetot.vn.presenter.checkVersion
import kotlinx.android.synthetic.main.intro_layout.*




/**
 * Created by VinhNguyen on 11/5/2017.
 */
class AppIntroActivity : Activity() ,checkVersion.IntroView {
    private var versionPressenter: checkVersion? =null
    override fun getVersion(ver: String) {
        if(version.equals("")){

        }else{
            if(version.equals(ver)){
                startActivity(Intent(this@AppIntroActivity, MainActivity::class.java))
                finish()
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle(resources.getString(R.string.txt_checkver_title) + " "+version)
                builder.setMessage(resources.getString(R.string.txt_checkver)+" "+ver)
                        .setPositiveButton(R.string.txt_checkver_button, { _, _ ->
                            val appPackageName = packageName
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
                            } catch (anfe: android.content.ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
                            }

                        })
                        .show()
            }
        }
    }

    private var safetyNetHelper: SafetyNetHelper? = null
    var version = ""
    var manager: PrefManager? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_layout)

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
             version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        versionPressenter = checkVersion(this)
        manager = PrefManager(this)
        safetyNetHelper = SafetyNetHelper("AIzaSyDbZnEq9-lpTvAk41v_fSe_ijKRIIj6R6Y")
        Log.d("HomeFragment", "AndroidAPIKEY: " + Utils.getSigningKeyFingerprint(this) + ";" + getPackageName())
        if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)) {
            handleError(0, "GooglePlayServices is not available on this device.\n\nThis SafetyNet test will not work")
            manager!!.setValidation(false)
        }
        safetyNetHelper!!.requestTest(this, object : SafetyNetHelper.SafetyNetWrapperCallback {
            override fun error(errorCode: Int, errorMessage: String) {

                handleError(errorCode, errorMessage)
                versionPressenter?.getVersionServer()
            }

            override fun success(ctsProfileMatch: Boolean, basicIntegrity: Boolean) {

                if (ctsProfileMatch) {
                    manager!!.setValidation(true)
                } else {
                    manager!!.setValidation(false)
                }
                versionPressenter?.getVersionServer()

            }
        })

        val secondsDelayed0 = 1
        Handler().postDelayed(Runnable {
            logo.animate()
                    .alpha(10F)
                    .duration = 3 * 1000

        }, (secondsDelayed0 * 1000).toLong())

        val secondsDelayed = 2
        Handler().postDelayed(Runnable {
            txt_slogan.animate()
                    .alpha(10F)
                    .duration = 3 * 1000

        }, (secondsDelayed * 1000).toLong())

        val secondsDelayed3 = 3
        Handler().postDelayed(Runnable {
            txt_nameapp.animate()
                    .alpha(10F)
                    .duration = 3 * 1000
        }, (secondsDelayed3 * 1000).toLong())

        val secondsDelayed2 = 4
        Handler().postDelayed(Runnable {
            txt_slogan.animate().alpha(10F)

        }, (secondsDelayed2 * 1000).toLong())
    }

    private fun handleError(errorCode: Int, errorMsg: String) {
        Log.e("HomeFragment", errorMsg)

        val b = StringBuilder()

        when (errorCode) {
            SafetyNetHelper.SAFETY_NET_API_REQUEST_UNSUCCESSFUL -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
            SafetyNetHelper.RESPONSE_ERROR_VALIDATING_SIGNATURE -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: error\n")
            }
            SafetyNetHelper.RESPONSE_FAILED_SIGNATURE_VALIDATION -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: fail\n")
            }
            SafetyNetHelper.RESPONSE_VALIDATION_FAILED -> {
                b.append("SafetyNet request: success\n")
                b.append("Response validation: fail\n")
            }
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                b.append("SafetyNet request: fail\n")
                b.append("\n*GooglePlayServices outdated*\n")
                try {
                    val v = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode
                    val vName = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionName.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]
                    b.append("You are running version:\n$vName $v\nSafetyNet requires minimum:\n7.3.27 7327000\n")
                } catch (NameNotFoundException: Exception) {
                    b.append("Could not find GooglePlayServices on this device.\nPackage com.google.android.gms missing.")
                }

            }
            else -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
        }
        Log.e("HomeFragment", b.toString())
    }
}