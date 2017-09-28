package sega.fastnetwork.test.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.content_product_detail_need.*
import kotlinx.android.synthetic.main.content_product_detail_need.view.*
import kotlinx.android.synthetic.main.fragment_product_need_detail.*
import kotlinx.android.synthetic.main.fragment_product_need_detail.view.*
import kotlinx.android.synthetic.main.layout_detail_cast.*
import kotlinx.android.synthetic.main.layout_detail_cast.view.*
import kotlinx.android.synthetic.main.layout_detail_fab.*
import kotlinx.android.synthetic.main.layout_detail_fab.view.*
import kotlinx.android.synthetic.main.layout_detail_need_info.*
import kotlinx.android.synthetic.main.layout_detail_need_info.view.*
import kotlinx.android.synthetic.main.toolbar_twoline.*
import kotlinx.android.synthetic.main.toolbar_twoline.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.ChatActivity
import sega.fastnetwork.test.activity.CommentActivity
import sega.fastnetwork.test.lib.SliderTypes.BaseSliderView
import sega.fastnetwork.test.lib.SliderTypes.Tricks.ViewPagerEx
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.ProductDetailPresenter
import sega.fastnetwork.test.util.Constants
import java.lang.Double
import java.text.DecimalFormat
import java.util.*

/**
 * Created by cc on 9/21/2017.
 */
class ProductDetailNeedFragment : Fragment(), ProductDetailPresenter.ProductDetailView, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, OnMapReadyCallback {
    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        // For showing a move to my location button
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap!!.isMyLocationEnabled = true

        // For dropping a marker at a point on the Map
        val sydney = LatLng(Double.parseDouble(product!!.location!!.coordinates!![1].toString()), Double.parseDouble(product!!.location!!.coordinates!![0].toString()))
        Log.e("sydney: ",sydney.toString())
        googleMap!!.addMarker(MarkerOptions().position(sydney).title(product!!.productname).snippet(product!!.location!!.address))

        // For zooming automatically to the location of the marker
        val cameraPosition = CameraPosition.Builder().target(sydney).zoom(16f).build()
        googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    internal var error: Boolean = false
/*    internal var commentslist = ArrayList<Comments>()*/


    internal var height: Int = 0
    internal var width: Int = 0
    private var format: String = ""

    private var id: String = ""
    private var product: Product? = null
    private var seller: User? = null
    internal var formatprice: DecimalFormat? = DecimalFormat("#0,000")
    var isTablet: Boolean = false
    var mProductDetailPresenter: ProductDetailPresenter? = null
    var s = 0
    var mTypeSave = "0"
    var doubleClick = false
    var statussave = false
    var photoprofile : String? = null
    var googleMap : GoogleMap? = null
    val options = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .placeholder(R.drawable.logo)
            .error(R.drawable.img_error)
            .priority(Priority.HIGH)
    // Fragment lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v = inflater.inflate(R.layout.fragment_product_need_detail, container, false)
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
        //=============================add comment=======================
        v.add_comment.setOnClickListener {
            gotoallcomment()
        }
        //==============================back button=================
        v.back_button.setOnClickListener {
            //            slider?.stopAutoCycle()
//            slider?.removeAllSliders()
            activity.finish()
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
            Log.e("BBB", id + " " + product)
            onDownloadSuccessful()


        }

        v.product_detail_holder.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (oldScrollY < scrollY) {
                println("len")
                v.fab_menu.hideMenuButton(true)
            } else {
                v.fab_menu.showMenuButton(true)
            }
        }
        v.fab_messenger.setOnClickListener {
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)

        }

        // share
        v.im_share.setOnClickListener {
            val sendIntent = Intent()
            val linkapp = "https://www.facebook.com/groups/727189854084530/"
            val numberFormat = DecimalFormat("###,###")
            val gia = numberFormat.format(product!!.price!!.toLong()).toString()

            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "- Productname(tên s?n ph?m): ${product!!.productname}\n"+
                            "- Category(th? lo?i): ${product!!.category}\n"+
                            "- Price(giá): ${gia} VNÐ\n"+
                            "- Number(S? lu?ng): ${product!!.price}\n"+
                            "- Address(d?a ch?): ${product!!.location!!.address}\n"+
                            "- Time(th?i gian): ${product!!.time} gi?\n"+
                            "- Description(Mô t?): ${product!!.description}\n"+
                            "- Tham kh?o thêm t?i: ${linkapp}")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        // star
        v.im_star.setOnClickListener{
            //   s = s + 1
            if(statussave){
                mTypeSave = "1"
                mProductDetailPresenter!!.SaveProduct(id, AppManager.getAppAccountUserId(activity),mTypeSave)
            }else if(!statussave){
                mTypeSave = "0"
                mProductDetailPresenter!!.SaveProduct(id, AppManager.getAppAccountUserId(activity),mTypeSave)
            }
        }
        v.layout_detail_user.setOnClickListener{
            //            val intent = Intent(activity, DetailProfileActivity::class.java)
////            intent.putExtra(Constants.product_ID, id)
////            intent.putExtra(Constants.product_NAME, product!!.productname)
////            intent.putExtra(Constants.seller_name, product!!.user!!.name)
//            intent.putExtra("data",product!!.user!!)
//            startActivity(intent)
        }

        return v

    }

    private fun gotoallcomment() {
        val intent = Intent(activity, CommentActivity::class.java)
        intent.putExtra(Constants.product_ID, id)
        intent.putExtra(Constants.product_NAME, product!!.productname)
        intent.putExtra(Constants.seller_name, product!!.user!!.name)
        startActivity(intent)
    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
        onDownloadFailed()
    }
    override fun getStatusSave(bool: Boolean) {

        //  if(bool)
        //   { doubleClick = true}
        statussave = !statussave
        if (statussave) {
            im_star.setImageResource(R.drawable.ic_start_on)
        } else {
            im_star.setImageResource(R.drawable.ic_start_off)
        }

    }
    override fun getProductDetail(response: Response) {
        try {
            statussave = response.statussave!!
            Log.e("getProductDetail", statussave.toString())

            if (statussave) {
                im_star.setImageResource(R.drawable.ic_start_on)
            } else {
                im_star.setImageResource(R.drawable.ic_start_off)
            }
        }catch (e: Exception){
            Log.e("getProductDetail", "saidjasd")
            im_star.visibility = View.GONE
        }
        this.product = response.product
        onDownloadSuccessful()
    }


    override fun onResume() {
        super.onResume()
        // Send screen name to analytics
//        slider!!.startAutoCycle()

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
        // status save product


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

//        val temp = formatprice?.format(product!!.price?.toDouble()) + format
//        product_price.text = temp

        println(product!!.user!!.name)
        if(product!!.user!!.photoprofile!!.startsWith("http")){
            photoprofile = product!!.user!!.photoprofile
        }
        else{
            photoprofile = Constants.IMAGE_URL+product!!.user!!.photoprofile
        }
        Glide.with(this)
                .load(photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(productdetail_avatar)
        product_user_name.text = product!!.user?.name
        product_user_email.text = product!!.user?.email
        product_user_address.text = product!!.location!!.address
        product_date.text = timeAgo(product!!.created_at.toString())
        product_review.text = product!!.view.toString()
        println(product!!._id)
        Log.e("LatLog", product!!.location!!.coordinates.toString())
//        when (product!!.time){
//            "0" -> product_rentime.text = "1 gi?"
//            "1" -> product_rentime.text = "1 ngày"
//            "2" -> product_rentime.text = "1 tu?n"
//            "3" -> product_rentime.text = "1 tháng"
//            else -> {
//                product_rentime.text = "1 nam"
//            }
//
//        }
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

//=======================0 cmt========================
        if (product!!.comment!!.size == 0) {
            comment_item1.visibility = View.GONE
            comment_item2.visibility = View.GONE
            comment_item3.visibility = View.GONE
            comments_see_all.visibility = View.GONE
            no_cmt.visibility = View.VISIBLE
        }
//=======================1 cmt========================

        else if (product!!.comment!!.size == 1) {
            comments_see_all.visibility = View.GONE
            comment_item2.visibility = View.GONE
            comment_item3.visibility = View.GONE
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![0].user!!.photoprofile!!))
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
                    .load(avatacmt(product!!.comment!![0].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![1].user!!.photoprofile!!))
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
                    .load(avatacmt(product!!.comment!![0].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![1].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage2)
            usercomments2.text = product!!.comment!![1].user!!.name
            comments2.text = product!!.comment!![1].content
            datecomment2.text = timeAgo(product!!.comment!![1].time!!)
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![2].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage3)
            usercomments3.text = product!!.comment!![2].user!!.name
            comments3.text = product!!.comment!![2].content
            datecomment3.text = timeAgo(product!!.comment!![2].time!!)
        } else {
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![0].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
            usercomments1.text = product!!.comment!![0].user!!.name
            comments1.text = product!!.comment!![0].content
            datecomment1.text = timeAgo(product!!.comment!![0].time!!)
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![1].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage2)
            usercomments2.text = product!!.comment!![1].user!!.name
            comments2.text = product!!.comment!![1].content
            datecomment2.text = timeAgo(product!!.comment!![1].time!!)
            Glide.with(this)
                    .load(avatacmt(product!!.comment!![2].user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage3)
            usercomments3.text = product!!.comment!![2].user!!.name
            comments3.text = product!!.comment!![2].content
            datecomment3.text = timeAgo(product!!.comment!![2].time!!)
            comments_see_all.visibility = View.VISIBLE
            comments_see_all.text = (product!!.comment!!.size - 3).toString() + " more comments..."
        }
        val mapView_location = childFragmentManager.findFragmentById(R.id.mapView_location) as SupportMapFragment
        mapView_location.getMapAsync(this)
//        showAnimationBanner()
    }

    fun destroyfragment() {
        if (AppManager.getAppAccountUserId(activity) != product!!.user!!._id)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(product!!._id)
    }

    private fun timeAgo(time: String): CharSequence? {
        val time = DateUtils.getRelativeTimeSpanString(
                java.lang.Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
        return time
    }
    fun avatacmt(link: String): CharSequence?{
        if(link.startsWith("http")){
            photoprofile = link
        }
        else{
            photoprofile = Constants.IMAGE_URL+link
        }
        return photoprofile
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

//        slider?.stopAutoCycle()
//        slider?.removeAllSliders()


    }

    override fun onPause() {
//        slider.stopAutoCycle()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
//                slider!!.stopAutoCycle()
//                slider!!.removeAllSliders()
                /*downloadproductDetails(id)*/

            }
        }
    }

    override fun onStop() {

        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
//        slider.stopAutoCycle()
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