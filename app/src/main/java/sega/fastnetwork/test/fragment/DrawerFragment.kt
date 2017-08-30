package sega.fastnetwork.test.fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_drawer_main.*
import kotlinx.android.synthetic.main.header.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.AddActivity
import sega.fastnetwork.test.activity.SearchActivity
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants


/**
 * Created by sega4 on 08/08/2017.
 */

class DrawerFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    var viewtype: Int = 1
    private val mDrawerHandler = Handler()
    private var mPrevSelectedId: Int = 0
    private var mSelectedId: Int = 0

    private var isTablet: Boolean? = null

    var fragment: Fragment? = null

    private var preferences: SharedPreferences? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        preferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
        // Setup toolbar
        isTablet = resources.getBoolean(R.bool.is_tablet)

        if (view != null) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        navigation_view!!.setNavigationItemSelectedListener(this)

        val mDrawerToggle = object : ActionBarDrawerToggle(activity,
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

        drawer_layout!!.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
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


        addproduct.setOnClickListener {
            startActivity(Intent(this@DrawerFragment.activity, AddActivity::class.java))
        }
        var user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
        Glide.with(this)
                .load(user.photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(navigation_view.getHeaderView(0).avatar_header)
        navigation_view.getHeaderView(0).username_header.text = user.name
        navigation_view.getHeaderView(0).email_header.text  = user.email

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_drawer_main, container, false)


    fun isClosedDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()

    }
    fun setUserDetail(user : User){

    }


    fun switchFragment(itemId: Int) {
        mSelectedId = navigation_view!!.menu.getItem(itemId).itemId
        navigation_view!!.menu.findItem(mSelectedId).isChecked = true
        mDrawerHandler.removeCallbacksAndMessages(null)
        mDrawerHandler.postDelayed({ navigate(mSelectedId) }, 250)
        drawer_layout.closeDrawers()
    }

    private fun navigate(itemId: Int) {
        /*  val elevation = findViewById(R.id.elevation)*/

        when (itemId) {
            R.id.nav_1 -> {
                mPrevSelectedId = itemId
                toolbar_title.setText(R.string.nav_home)
                fragment = HomeFragment()
            }
            R.id.nav_2 -> {
                mPrevSelectedId = itemId
                toolbar_title.setText(R.string.nav_category)
                fragment = CategoryFragment()
            }
            R.id.nav_3 -> {
                mPrevSelectedId = itemId
                toolbar_title.setText(R.string.nav_category)
                fragment = DetailProfileFragment()
            }
            R.id.nav_5 -> {
                val intent = Intent((activity as AppCompatActivity), SearchActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_6 -> {
                /*   startActivity(new Intent(this, AboutActivity.class));*/
                navigation_view!!.menu.findItem(mPrevSelectedId).isChecked = true
                return
            }
            R.id.nav_8 -> {
                /*   startActivity(new Intent(this, AboutActivity.class));*/
                AppManager.removeAccount(activity)

                return
            }
        }

        /*  val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(4f))*/

        if (fragment != null) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            try {
                transaction.replace(R.id.content_frame, fragment).commit()

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
        val density = activity.resources.displayMetrics.density

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

        drawer_layout.closeDrawers()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_ITEM_ID, mSelectedId)
    }


    companion object {

        private val SELECTED_ITEM_ID = "SELECTED_ITEM_ID"
    }


    private fun setSelectedDrawerItem() {


        // Create and setup bundle args

        fragment = ProductListFragment()


        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment, Constants.TAG_GRID_FRAGMENT)
        transaction.commitAllowingStateLoss()
        // Save selected position to preference


    }

}


