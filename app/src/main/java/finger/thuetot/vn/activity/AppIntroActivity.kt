package finger.thuetot.vn.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.intro_layout.*
import finger.thuetot.vn.R


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
                    .duration = 5*1000

        }, (secondsDelayed0 * 1000).toLong())

        val secondsDelayed = 3
        Handler().postDelayed(Runnable {
            txt_slogan.animate()
                    .alpha(10F)
                    .duration = 5*1000

        }, (secondsDelayed * 1000).toLong())

        val secondsDelayed3 = 4
        Handler().postDelayed(Runnable {
            txt_nameapp.animate()
                    .alpha(10F)
                    .duration = 5*1000
        }, (secondsDelayed3 * 1000).toLong())

        val secondsDelayed2 = 5
        Handler().postDelayed(Runnable {
            txt_slogan.animate().alpha(10F)
            startActivity(Intent(this@AppIntroActivity, MainActivity::class.java))
            finish()
        }, (secondsDelayed2 * 1000).toLong())
    }

}