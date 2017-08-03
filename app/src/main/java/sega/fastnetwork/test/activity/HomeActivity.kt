package sega.fastnetwork.test.activity


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions

import kotlinx.android.synthetic.main.activity_home.*


import sega.fastnetwork.test.R
import sega.fastnetwork.test.manager.AppAccountManager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.HomePresenter
import sega.fastnetwork.test.view.HomeView


class HomeActivity : AppCompatActivity(), HomeView, AppBarLayout.OnOffsetChangedListener {


    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val ALPHA_ANIMATIONS_DURATION = 200
    var user: User? = null
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    var mHomePresenter: HomePresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mHomePresenter = HomePresenter(this)
        appbar.addOnOffsetChangedListener(this)

        toolbar.inflateMenu(R.menu.menu_product)
        startAlphaAnimation(textview_title, 0, View.INVISIBLE)
        mHomePresenter!!.getUserDetail(AppAccountManager.getAppAccountUserId(this))


    }

    override fun getUserDetail(user: User) {
        this.user = user
    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
    }

    override fun isgetUserDetailSuccess(success: Boolean) {
        if (success) {
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
                    .into(avatar)
        }

    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appBarLayout!!.totalScrollRange
        val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textview_title, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                mIsTheTitleVisible = true
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textview_title, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(framelayout_title, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                mIsTheTitleContainerVisible = false
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(framelayout_title, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                mIsTheTitleContainerVisible = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_add) {
            System.out.println("add")
            startActivity(Intent(applicationContext, AddActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    public override fun onDestroy() {
        super.onDestroy()


    }

    fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

}

