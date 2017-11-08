package sega.fastnetwork.test.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import sega.fastnetwork.test.R


/**
 * Created by VinhNguyen on 11/5/2017.
 */
class AppIntroActivity: Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_layout)

        val secondsDelayed = 2
        Handler().postDelayed(Runnable {
            startActivity(Intent(this@AppIntroActivity, MainActivity::class.java))
            finish()
        }, (secondsDelayed * 1000).toLong())
    }

}