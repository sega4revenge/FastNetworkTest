package finger.thuetot.vn.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import finger.thuetot.vn.R
import kotlinx.android.synthetic.main.intro_layout.*


/**
 * Created by VinhNguyen on 11/5/2017.
 */
class AppIntroActivity: Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_layout)

        val secondsDelayed0 = 1
        Handler().postDelayed(Runnable {
            logo.animate()
                    .alpha(10F)
                    .duration = 3*1000

        }, (secondsDelayed0 * 1000).toLong())

        val secondsDelayed = 2
        Handler().postDelayed(Runnable {
            txt_slogan.animate()
                    .alpha(10F)
                    .duration = 3*1000

        }, (secondsDelayed * 1000).toLong())

        val secondsDelayed3 = 3
        Handler().postDelayed(Runnable {
            txt_nameapp.animate()
                    .alpha(10F)
                    .duration = 3*1000
        }, (secondsDelayed3 * 1000).toLong())

        val secondsDelayed2 = 4
        Handler().postDelayed(Runnable {
            txt_slogan.animate().alpha(10F)
            startActivity(Intent(this@AppIntroActivity, MainActivity::class.java))
            finish()
        }, (secondsDelayed2 * 1000).toLong())
    }

}