package sega.fastnetwork.test.fragment


import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.layout_detail_backdrop.*
import kotlinx.android.synthetic.main.layout_detail_crew.*
import kotlinx.android.synthetic.main.layout_detail_info.*
import kotlinx.android.synthetic.main.layout_detail_overview.*
import kotlinx.android.synthetic.main.toolbar_twoline.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.SliderTypes.Animations.DescriptionAnimation
import sega.fastnetwork.test.lib.SliderTypes.BaseSliderView
import sega.fastnetwork.test.lib.SliderTypes.SliderLayout
import sega.fastnetwork.test.lib.SliderTypes.TextSliderView
import sega.fastnetwork.test.lib.SliderTypes.Tricks.ViewPagerEx
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.ProductDetailPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.ProductDetailView
import java.text.DecimalFormat
import java.util.*


/**
 * Created by sega4 on 10/08/2017.
 */

class ProductDetailFragment : Fragment(), ProductDetailView, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {


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


        // Download product details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(Constants.product_ID)
                && savedInstanceState.containsKey(Constants.product_OBJECT) && savedInstanceState.containsKey(Constants.seller_DETAIL))) {
            id = arguments.getString(Constants.product_ID)


            if (TextUtils.isEmpty(id)) {
                progress_circle.visibility = View.GONE
                toolbar_text_holder.visibility = View.GONE
                toolbar.title = ""
            } else {


                mProductDetailPresenter!!.getProductDetail(id)
            }

        } else {

            id = savedInstanceState.getString(Constants.product_ID)
            product = savedInstanceState.get(Constants.product_OBJECT) as Product
            onDownloadSuccessful()


        }


        return v
    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
        onDownloadFailed()
    }

    override fun getProductDetail(product: Product) {

        this.product = product
        onDownloadSuccessful()
    }

    fun showAnimationBanner() {
        for (i in 0..product!!.images!!.size - 1) {
            val textSliderView = TextSliderView(activity)
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
        slider!!.setPresetTransformer(SliderLayout.Transformer.Accordion)
        slider!!.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        slider!!.setCustomAnimation(DescriptionAnimation())
        slider!!.setDuration(4000)
        slider!!.addOnPageChangeListener(this)
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
        /*floatingActionsMenu.setVisibility(View.VISIBLE)*/

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
        /* val timeAgo = DateUtils.getRelativeTimeSpanString(
                 java.lang.Long.parseLong(product!!.productdate),
                 System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
         product_date.setText(timeAgo)*/



        showAnimationBanner()
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
