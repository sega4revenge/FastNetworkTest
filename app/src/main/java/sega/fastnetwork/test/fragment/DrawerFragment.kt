package sega.fastnetwork.test.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
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
import kotlinx.android.synthetic.main.fragment_drawer_main.view.*
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.manager.AppAccountManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DrawerPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.DrawerView


/**
 * Created by sega4 on 08/08/2017.
 */

class DrawerFragment : Fragment(), DrawerView, NavigationView.OnNavigationItemSelectedListener {
    var viewtype: Int = 1
    private val mDrawerHandler = Handler()
    private var mPrevSelectedId: Int = 0
    private var mSelectedId: Int = 0
    var mDrawerPresenter: DrawerPresenter? = null
    internal var isTablet: Boolean? = null
    var user: User? = null
    var fragment: Fragment? = null
    var mDrawerLayout: DrawerLayout? = null
    private var preferences: SharedPreferences? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        preferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
        // Setup toolbar
        isTablet = resources.getBoolean(R.bool.is_tablet)
        mDrawerPresenter = DrawerPresenter(this)
        mDrawerPresenter!!.getUserDetail(AppAccountManager.getAppAccountUserId(activity))
        if (view != null) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        navigation_view!!.setNavigationItemSelectedListener(this)
        mDrawerLayout = view?.findViewById(R.id.drawer_layout) as DrawerLayout
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

        onRefreshToolbarMenu()
        action_grid.setOnClickListener {

            when (viewtype) {
                1 -> {
                    val editor1 = preferences!!.edit()
                    editor1.putInt(Constants.VIEW_MODE, Constants.VIEW_MODE_LIST)
                    editor1.apply()
                    onRefreshToolbarMenu()
                    onRefreshFragmentLayout()


                }
                2 -> {
                    val editor1 = preferences!!.edit()
                    editor1.putInt(Constants.VIEW_MODE, Constants.VIEW_MODE_COMPACT)
                    editor1.apply()
                    onRefreshToolbarMenu()
                    onRefreshFragmentLayout()


                }
                3 -> {
                    val editor1 = preferences!!.edit()
                    editor1.putInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID)
                    editor1.apply()
                    onRefreshToolbarMenu()
                    onRefreshFragmentLayout()


                }
            }


        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater!!.inflate(R.layout.fragment_drawer_main, container, false)
    }

    fun isClosedDrawer() {

        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()

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
                .load(user.google!!.photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(view!!.avatar_header)

        view!!.username_header.text = user.name
        view!!.email_header.text = user.email
    }

    fun switchFragment(itemId: Int) {
        mSelectedId = view!!.navigation_view!!.menu.getItem(itemId).itemId
        view!!.navigation_view!!.menu.findItem(mSelectedId).isChecked = true
        mDrawerHandler.removeCallbacksAndMessages(null)
        mDrawerHandler.postDelayed({ navigate(mSelectedId) }, 250)
        mDrawerLayout!!.closeDrawers()
    }

    private fun navigate(itemId: Int) {
        /*  val elevation = findViewById(R.id.elevation)*/

        when (itemId) {
            R.id.nav_1 -> {
                mPrevSelectedId = itemId
                view!!.toolbar_title.setText(R.string.nav_home)
                fragment = HomeFragment()
            }
            R.id.nav_2 -> {
                mPrevSelectedId = itemId
                view!!.toolbar_title.setText(R.string.nav_category)
                fragment = CategoryFragment()
            }
        //case R.id.nav_5:
        //startActivity(new Intent(this, SettingsActivity.class));
        //navigation_view.getMenu().findItem(mPrevSelectedId).setChecked(true);
        //return;
            R.id.nav_6 -> {
                /*   startActivity(new Intent(this, AboutActivity.class));*/
                view!!.navigation_view!!.menu.findItem(mPrevSelectedId).isChecked = true
                return
            }
            R.id.nav_8 -> {
                /*   startActivity(new Intent(this, AboutActivity.class));*/
                AppAccountManager.removeAccount(activity)

                return
            }
        }

        /*  val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(4f))*/

        if (fragment != null) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
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

        mDrawerLayout!!.closeDrawers()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_ITEM_ID, mSelectedId)
    }


    companion object {

        private val SELECTED_ITEM_ID = "SELECTED_ITEM_ID"
    }

    private fun onRefreshToolbarMenu() {
        viewtype = preferences?.getInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID)!!
        if (viewtype == Constants.VIEW_MODE_GRID) {
            // Change from grid to list

            action_grid.setImageResource(R.drawable.action_grid)


        } else if (viewtype == Constants.VIEW_MODE_LIST) {

            action_grid.setImageResource(R.drawable.action_list)


        } else {
            // Change from compact to grid

            action_grid.setImageResource(R.drawable.action_compact)


        }
    }


    private fun onRefreshFragmentLayout() {
        if (fragment is HomeFragment) {

            (fragment as HomeFragment).refresh()
        } /*else if (fragment instanceof productSavedFragment) {
            ((productSavedFragment) fragment).refreshLayout();
        }*/
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


