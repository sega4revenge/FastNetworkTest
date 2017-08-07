package sega.fastnetwork.test.activity

import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.toolbar.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.fragment.HomeFragment
import sega.fastnetwork.test.manager.AppAccountManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.HomePresenter
import sega.fastnetwork.test.view.HomeView


/* Ismail Xebia */

class HomeActivity : AppCompatActivity(), HomeView, NavigationView.OnNavigationItemSelectedListener {


    private val mDrawerHandler = Handler()
    private var mPrevSelectedId: Int = 0
    private var mSelectedId: Int = 0
    var mHomePresenter: HomePresenter? = null
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mHomePresenter = HomePresenter(this)
        mHomePresenter!!.getUserDetail(AppAccountManager.getAppAccountUserId(this))
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        navigation_view!!.setNavigationItemSelectedListener(this)

        val mDrawerToggle = object : ActionBarDrawerToggle(this,
                drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                super.onDrawerSlide(drawerView, 0f)
            }

            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                super.onDrawerSlide(drawerView, 0f)
            }
        }
        drawer_layout!!.setDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        mSelectedId = navigation_view!!.menu.getItem(prefs.getInt("default_view", 0)).itemId
        mSelectedId = savedInstanceState?.getInt(SELECTED_ITEM_ID) ?: mSelectedId
        mPrevSelectedId = mSelectedId
        navigation_view!!.menu.findItem(mSelectedId).isChecked = true

        if (savedInstanceState == null) {
            mDrawerHandler.removeCallbacksAndMessages(null)
            mDrawerHandler.postDelayed({ navigate(mSelectedId) }, 250)

            val openDrawer = prefs.getBoolean("open_drawer", false)

            if (openDrawer)
                drawer_layout!!.openDrawer(GravityCompat.START)
            else
                drawer_layout!!.closeDrawers()
        }
    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
    }

    override fun getUserDetail(user: User) {
        this.user = user

        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
        Glide.with(this)
                .load(user!!.google!!.photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(avatar_header)

    }

    fun switchFragment(itemId: Int) {
        mSelectedId = navigation_view!!.menu.getItem(itemId).itemId
        navigation_view!!.menu.findItem(mSelectedId).isChecked = true
        mDrawerHandler.removeCallbacksAndMessages(null)
        mDrawerHandler.postDelayed({ navigate(mSelectedId) }, 250)
        drawer_layout!!.closeDrawers()
    }

    private fun navigate(itemId: Int) {
        /*  val elevation = findViewById(R.id.elevation)*/
        var navFragment: Fragment? = null
        when (itemId) {
            R.id.nav_1 -> {
                mPrevSelectedId = itemId
                toolbar_title.setText(R.string.nav_home)
                navFragment = HomeFragment()
            }
            R.id.nav_2 -> {
                mPrevSelectedId = itemId
                toolbar_title.setText(R.string.nav_category)
            }
        //case R.id.nav_5:
        //startActivity(new Intent(this, SettingsActivity.class));
        //navigation_view.getMenu().findItem(mPrevSelectedId).setChecked(true);
        //return;
            R.id.nav_6 -> {
                /*   startActivity(new Intent(this, AboutActivity.class));*/
                navigation_view!!.menu.findItem(mPrevSelectedId).isChecked = true
                return
            }
        }

        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(4f))

        if (navFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            try {
                transaction.replace(R.id.content_frame, navFragment).commit()

                //elevation shadow
                /*  if (elevation != null) {
                      params.topMargin = if (navFragment is HomeFragment) dp(48f) else 0
  
                      val a = object : Animation() {
                          override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                              elevation.layoutParams = params
                          }
                      }
                      a.duration = 150
                      elevation.startAnimation(a)
                  }*/
            } catch (ignored: IllegalStateException) {
            }

        }
    }

    fun dp(value: Float): Int {
        val density = applicationContext.resources.displayMetrics.density

        if (value == 0f) {
            return 0
        }
        return Math.ceil((density * value).toDouble()).toInt()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        mSelectedId = menuItem.itemId
        mDrawerHandler.removeCallbacksAndMessages(null)
        mDrawerHandler.postDelayed({ navigate(mSelectedId) }, 250)
        drawer_layout!!.closeDrawers()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_ITEM_ID, mSelectedId)
    }

    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        private val SELECTED_ITEM_ID = "SELECTED_ITEM_ID"
    }
}