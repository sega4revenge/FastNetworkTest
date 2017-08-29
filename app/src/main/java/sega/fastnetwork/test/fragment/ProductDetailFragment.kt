package sega.fastnetwork.test.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.content_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.view.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.layout_detail_backdrop.*
import kotlinx.android.synthetic.main.layout_detail_cast.*
import kotlinx.android.synthetic.main.layout_detail_cast.view.*
import kotlinx.android.synthetic.main.layout_detail_fab.*
import kotlinx.android.synthetic.main.layout_detail_fab.view.*
import kotlinx.android.synthetic.main.layout_detail_info.*
import kotlinx.android.synthetic.main.toolbar_twoline.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.ChatActivity
import sega.fastnetwork.test.activity.CommentActivity
import sega.fastnetwork.test.lib.SliderTypes.Animations.DescriptionAnimation
import sega.fastnetwork.test.lib.SliderTypes.BaseSliderView
import sega.fastnetwork.test.lib.SliderTypes.DefaultSliderView
import sega.fastnetwork.test.lib.SliderTypes.SliderLayout
import sega.fastnetwork.test.lib.SliderTypes.Tricks.ViewPagerEx
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.ProductDetailPresenter
import sega.fastnetwork.test.util.Constants
import java.text.DecimalFormat
import java.util.*


/**
 * Created by sega4 on 10/08/2017.
 */

class ProductDetailFragment : Fragment(), ProductDetailPresenter.ProductDetailView, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {


    internal var error: Boolean = false
/*    internal var commentslist = ArrayList<Comments>()*/


    internal var height: Int = 0
    internal var width: Int = 0
    private var format: String = ""

    private var id: String = ""
    private var product: Product? = null
    private var seller: User? = null
    internal var formatprice: DecimalFormat? = DecimalFormat("#0,000");
    var isTablet: Boolean = false
    var mProductDetailPresenter: ProductDetailPresenter? = null

    // Fragment lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v = inflater.inflate(R.layout.fragment_product_detail, container, false)
        isTablet = resources.getBoolean(R.bool.is_tablet)
        val displaymetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displaymetrics)
        height = displaymetrics.heightPixels
        width = displaymetrics.widthPixels
        val current = Locale("vi", "VN")
        val cur = Currency.getInstance(current)
        format = cur.symbol
        mProductDetailPresenter = ProductDetailPresenter(this)
//=============================see all comment=======================
        v.comments_see_all.setOnClickListener {
            gotoallcomment()
        }
//===================================================================
        // Download product details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(Constants.product_ID)
                && savedInstanceState.containsKey(Constants.product_OBJECT) && savedInstanceState.containsKey(Constants.seller_DETAIL))) {
            id = arguments.getString(Constants.product_ID)


            if (TextUtils.isEmpty(id)) {
                progress_circle.visibility = View.GONE
                toolbar_text_holder.visibility = View.GONE
                toolbar.title = ""
            } else {
                mProductDetailPresenter!!.getProductDetail(id, AppManager.getAppAccountUserId(activity))
            }

        } else {

            id = savedInstanceState.getString(Constants.product_ID)
            product = savedInstanceState.get(Constants.product_OBJECT) as Product
            Log.e("BBB",id + " " + product)
            onDownloadSuccessful()


        }

        v.product_detail_holder.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (oldScrollY < scrollY) {
                v.fab_menu.hideMenuButton(true)
            } else {
                v.fab_menu.showMenuButton(true)
            }
        }
        v.fab_messenger.setOnClickListener {
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)

        }
        return v

    }

    private fun gotoallcomment() {
       val intent = Intent(activity, CommentActivity::class.java)
        intent.putExtra(Constants.product_ID,id)
        intent.putExtra(Constants.product_NAME,product!!.productname)
        intent.putExtra(Constants.seller_name,product!!.user!!.name)
        startActivity(intent)
    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
        onDownloadFailed()
    }

    override fun getProductDetail(product: Product) {

        this.product = product
        onDownloadSuccessful()
    }

    private fun showAnimationBanner() {
        for (i in 0 until product!!.images!!.size) {
            val textSliderView = DefaultSliderView(activity)
            // initialize a SliderLayout
            textSliderView
                    .image(Constants.IMAGE_URL + product!!.images!![i])
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this)

            //add your extra information
            textSliderView.bundle(Bundle())
            textSliderView.bundle
                    ?.putInt("index", i)

            slider!!.addSlider(textSliderView)

        }
        if(product!!.images!!.size!=1)
        {
            slider!!.setPresetTransformer(SliderLayout.Transformer.Accordion)
            slider!!.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            slider!!.setCustomAnimation(DescriptionAnimation())
            slider!!.setDuration(4000)
            slider!!.addOnPageChangeListener(this)
        }


    }

    override fun onResume() {
        super.onResume()
        // Send screen name to analytics
        slider!!.startAutoCycle()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (product != null && id != null) {
            outState.putString(Constants.product_ID, id)
            outState.putParcelable(Constants.product_OBJECT, product)
            outState.putParcelable(Constants.seller_DETAIL, seller)
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

    }


    private fun onDownloadSuccessful() {
        // Toggle visibility
        progress_circle.visibility = View.GONE
        error_message.visibility = View.GONE
        product_detail_holder.visibility = View.VISIBLE
        fab_menu.visibility = View.VISIBLE

        // Set title and tagline
        if (TextUtils.isEmpty(product!!.productname)) {
            toolbar.title = product!!.productname
            toolbar_text_holder.visibility = View.GONE
        } else {
            toolbar.title = ""
            toolbar_text_holder.visibility = View.VISIBLE
            toolbar_title.text = product!!.productname
            toolbar_subtitle.text = product!!.user?.name
        }
        product_name.text = product!!.productname
        product_overview.text = product!!.description

        val temp = formatprice?.format(product!!.price?.toDouble()) + format
        product_price.text = temp

        println(product!!.user!!.name)
        product_user_name.text = product!!.user?.name
        product_user_email.text = product!!.user?.email
        product_user_address.text = product!!.address
        println(product!!._id)
        when (product!!.category) {
            "0" -> product_category.setImageResource(R.drawable.cate_vehicle)
            "1" -> product_category.setImageResource(R.drawable.cate_toy)
            "2" -> product_category.setImageResource(R.drawable.cate_electronic)
            "3" -> product_category.setImageResource(R.drawable.cate_furniture)
            "4" -> product_category.setImageResource(R.drawable.cate_fashion)
            "5" -> product_category.setImageResource(R.drawable.cate_home)
            "6" -> product_category.setImageResource(R.drawable.cate_education)
            "7" -> product_category.setImageResource(R.drawable.cate_music)
            "8" -> product_category.setImageResource(R.drawable.cate_machine)
            else -> { // Note the block
                product_category.setImageResource(R.drawable.cate_more)
            }
        }
//        FirebaseMessaging.getInstance().subscribeToTopic(product!!._id)
        /* val timeAgo = DateUtils.getRelativeTimeSpanString(
                 java.lang.Long.parseLong(product!!.productdate),
                 System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
         product_date.setText(timeAgo)*/
//=======================option Glide========================
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
//=======================0 cmt========================
        if (product!!.comment!!.size == 0) {
            comment_item1.visibility = View.GONE
            comment_item2.visibility = View.GONE
            comment_item3.visibility = View.GONE
            comments_see_all.visibility = View.GONE
        }
//=======================1 cmt========================

        else if (product!!.comment!!.size == 1) {
            comments_see_all.visibility = View.GONE
            comment_item2.visibility = View.GONE
            comment_item3.visibility = View.GONE
            Glide.with(this)
                    .load(product!!.comment!![0].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
        }
//=======================2 cmt========================

        else if (product!!.comment!!.size == 2) {
            comments_see_all.visibility = View.GONE
            comment_item3.visibility = View.GONE

            Glide.with(this)
                    .load(product!!.comment!![0].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
            Glide.with(this)
                    .load(product!!.comment!![1].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage2)
            usercomments2.text = product!!.comment!![1].user!!.name
            comments2.text = product!!.comment!![1].content
            datecomment2.text = timeAgo(product!!.comment!![1].time!!)
        }

//=======================3 cmt========================
        else if (product!!.comment!!.size == 3) {
            comments_see_all.visibility = View.GONE
            Glide.with(this)
                    .load(product!!.comment!![0].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
            Glide.with(this)
                    .load(product!!.comment!![1].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage2)
            usercomments2.text = product!!.comment!![1].user!!.name
            comments2.text = product!!.comment!![1].content
            datecomment2.text = timeAgo(product!!.comment!![1].time!!)
            Glide.with(this)
                    .load(product!!.comment!![2].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage3)
            usercomments3.text = product!!.comment!![2].user!!.name
            comments3.text = product!!.comment!![2].content
            datecomment3.text = timeAgo(product!!.comment!![2].time!!)
        } else {
            Glide.with(this)
                    .load(product!!.comment!![0].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
            Glide.with(this)
                    .load(product!!.comment!![1].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage2)
            usercomments2.text = product!!.comment!![1].user!!.name
            comments2.text = product!!.comment!![1].content
            datecomment2.text = timeAgo(product!!.comment!![1].time!!)
            Glide.with(this)
                    .load(product!!.comment!![2].user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage3)
            usercomments3.text = product!!.comment!![2].user!!.name
            comments3.text = product!!.comment!![2].content
            datecomment3.text = timeAgo(product!!.comment!![2].time!!)
            comments_see_all.visibility = View.VISIBLE
        }


        showAnimationBanner()
    }
    fun destroyfragment() {
        if(AppManager.getAppAccountUserId(activity) != product!!.user!!._id)
       FirebaseMessaging.getInstance().unsubscribeFromTopic(product!!._id)
    }
    private fun timeAgo(time: String): CharSequence? {
        val time = DateUtils.getRelativeTimeSpanString(
                java.lang.Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
        return time
    }

    private fun onDownloadFailed() {
        error_message.visibility = View.VISIBLE
        progress_circle.visibility = View.GONE
        product_detail_holder.visibility = View.GONE
        toolbar_text_holder.visibility = View.GONE
        toolbar.title = ""
    }


    // FAB related functions


    override fun onDestroy() {
        super.onDestroy()

        slider?.stopAutoCycle()
        slider?.removeAllSliders()


    }

    override fun onPause() {
        slider.stopAutoCycle()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                slider!!.stopAutoCycle()
                slider!!.removeAllSliders()
                /*downloadproductDetails(id)*/

            }
        }
    }

    override fun onStop() {

        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        slider.stopAutoCycle()
        super.onStop()
    }

    override fun onSliderClick(slider: BaseSliderView) {

        /*    val i = Intent(activity, Fullscreen::class.java)
            i.putExtra("pos", slider.bundle.getInt("index"))
            i.putExtra("data", product!!.productimage)
            startActivity(i)*/
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        Log.d("Slider Demo", "Page Changed: " + position)
    }

    override fun onPageScrollStateChanged(state: Int) {}


    companion object {
        private val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234
    }

}
