package finger.thuetot.vn.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.*
import finger.thuetot.vn.adapter.ProductAdapter
import finger.thuetot.vn.customview.DividerItemDecoration
import finger.thuetot.vn.lib.SliderTypes.Animations.DescriptionAnimation
import finger.thuetot.vn.lib.SliderTypes.BaseSliderView
import finger.thuetot.vn.lib.SliderTypes.DefaultSliderView
import finger.thuetot.vn.lib.SliderTypes.SliderLayout
import finger.thuetot.vn.lib.SliderTypes.Tricks.ViewPagerEx
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.Comment
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.CommentPresenter
import finger.thuetot.vn.presenter.ProductDetailPresenter
import finger.thuetot.vn.presenter.ProductListPresenter
import finger.thuetot.vn.util.Constants
import kotlinx.android.synthetic.main.content_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_detail.view.*
import kotlinx.android.synthetic.main.layout_detail_backdrop.*
import kotlinx.android.synthetic.main.layout_detail_cast.*
import kotlinx.android.synthetic.main.layout_detail_cast.view.*
import kotlinx.android.synthetic.main.layout_detail_info.*
import kotlinx.android.synthetic.main.layout_detail_info.view.*
import kotlinx.android.synthetic.main.layout_error_message.view.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class ProductDetailFragment : Fragment(), ProductAdapter.OnproductClickListener,ProductDetailPresenter.ProductDetailView, ProductListPresenter.ProductListView, CommentPresenter.CommentView, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    override fun setErrorNotFound() {
        likenew.visibility = View.GONE
    }

    override fun getListProduct(productlist: ArrayList<Product>) {
        Log.d("sizeeeeeeeeeee",productlist.size.toString()+"aaaaaaassss")
        if(productlist?.size >0)
        {
            adapter!!.productList.addAll(productlist)
            adapter?.notifyDataSetChanged()
        }else{
            likenew.visibility = View.GONE
        }

    }

    override fun getListSavedProduct(productsavedlist: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStatusAddComent(listcomment: ArrayList<Comment>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal var error: Boolean = false
/*    internal var commentslist = ArrayList<Comments>()*/

    var mMap: GoogleMap? = null

    internal var height: Int = 0
    internal var width: Int = 0
    private var format: String = ""
    private var userCreateProduct: String = ""
    private var Myid: String = ""
    private var id: String = ""
    private var id_user: String = ""
    private var product: Product? = null
    private var seller: User? = null
    internal var formatprice: DecimalFormat? = DecimalFormat("###,###,###")
    var isTablet: Boolean = false
    var mProductDetailPresenter: ProductDetailPresenter? = null
    var mProductListPresenter: ProductListPresenter? = null
    var mCommentPresenter: CommentPresenter? = null
    var s = 0
    var userCreate: User? = null
    var mTypeSave = "0"
    var doubleClick = false
    var statussave = false
    var stt = false
    var stt2 = false
    var stt3 = false
    var photoprofile: String? = null
    private var isMap: Boolean = false
    val options = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .placeholder(R.drawable.logo)
            .error(R.drawable.img_error)
            .priority(Priority.HIGH)
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: ProductAdapter? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product_recycleview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        product_recycleview.layoutManager = layoutManager
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(context, this, product_recycleview, layoutManager!!)
        product_recycleview.adapter = adapter

    }

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
        Myid = AppManager.getAppAccountUserId(activity.applicationContext)
        mProductDetailPresenter = ProductDetailPresenter(this)
        mCommentPresenter = CommentPresenter(this)
        mProductListPresenter = ProductListPresenter(this)

//        layoutManager = LinearLayoutManager(activity)
//        v.product_recycleview.layoutManager = layoutManager
//        adapter = ProductAdapter(context, this, v.product_recycleview, layoutManager!!)
//        v.product_recycleview.adapter = adapter
        //========================refresh comment======================
//        v.refresh_comment.setOnClickListener {
//            doubleClick = true
//            mCommentPresenter!!.refreshcomment(id)
//        }
        //=============================see all comment=======================
        v.add_comment.setOnClickListener {
            gotoallcomment()
        }

        v.img_like.setOnClickListener(){
            var num: Int = 0
            if(v.txt_num_like.text.toString().equals("") || v.txt_num_like.text.toString().equals("0")){
                num = 0
            }else{
                num = v.txt_num_like.text.toString().toInt()

            }
            if(stt){
                mCommentPresenter?.likecomment(product!!.comment!![0]._id!!,Myid,1.toString())
                v.img_like.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
                if((num-1)!=0){
                    v.txt_num_like.text = (num-1).toString()
                }else{
                    v.txt_num_like.text = ""
                }
                stt = false
            }else{
                mCommentPresenter?.likecomment(product!!.comment!![0]._id!!,Myid,0.toString())
                v.img_like.setImageDrawable(resources.getDrawable(R.drawable.icon_liked))
                v.txt_num_like.text = (num+1).toString()
                stt = true
            }
        }
        v.img_like2.setOnClickListener(){
            var num: Int = 0
            if(v.txt_num_like2.text.toString().equals("") || v.txt_num_like2.text.toString().equals("0")){
                num = 0
            }else{
                num = v.txt_num_like2.text.toString().toInt()

            }
            if(stt2){
                mCommentPresenter?.likecomment(product!!.comment!![1]._id!!,Myid,1.toString())
                v.img_like2.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
                if((num-1)!=0){
                    v.txt_num_like2.text = (num-1).toString()
                }else{
                    v.txt_num_like2.text = ""
                }
                stt2 = false
            }else{
                mCommentPresenter?.likecomment(product!!.comment!![1]._id!!,Myid,0.toString())
                v.img_like2.setImageDrawable(resources.getDrawable(R.drawable.icon_liked))
                v.txt_num_like2.text = (num+1).toString()
                stt2 = true
            }
        }

        v.img_like3.setOnClickListener {
            var num: Int
            if(v.txt_num_like3.text.toString().equals("") || v.txt_num_like3.text.toString().equals("0")){
                num = 0
            }else{
                num = v.txt_num_like3.text.toString().toInt()

            }
            if(stt3){
                mCommentPresenter?.likecomment(product!!.comment!![2]._id!!,Myid,1.toString())
                v.img_like3.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
                if((num-1)!=0){
                    v.txt_num_like3.text = (num-1).toString()
                }else{
                    v.txt_num_like3.text = ""
                }

                stt3 = false
            }else{

                mCommentPresenter?.likecomment(product!!.comment!![2]._id!!,Myid,0.toString())
                v.img_like3.setImageDrawable(resources.getDrawable(R.drawable.icon_liked))
                v.txt_num_like3.text = (num+1).toString()
                stt3 = true
            }
        }
        v.layout_image1.setOnClickListener {
            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(product!!.comment!![0].user!!._id)) {
                val dialogFrag = ProifileSellerFragment.newInstance()
                val args = Bundle()
                args.putBoolean("isMap", isMap)
                dialogFrag.arguments = args
                dialogFrag.show(activity.supportFragmentManager, product!!.comment!![0].user!!, activity.applicationContext)
            }
        }
        v.layout_image2.setOnClickListener {

            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(product!!.comment!![1].user!!._id)) {
                val dialogFrag = ProifileSellerFragment.newInstance()
                val args = Bundle()
                args.putBoolean("isMap", isMap)
                dialogFrag.arguments = args
                dialogFrag.show(activity.supportFragmentManager, product!!.comment!![1].user!!, activity.applicationContext)
            }
        }
        v.layout_image3.setOnClickListener {
            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(product!!.comment!![2].user!!._id)) {
                val dialogFrag = ProifileSellerFragment.newInstance()
                val args = Bundle()
                args.putBoolean("isMap", isMap)
                dialogFrag.arguments = args
                dialogFrag.show(activity.supportFragmentManager, product!!.comment!![2].user!!, activity.applicationContext)

            }
        }
        v.layout_detail_user.setOnClickListener() {
            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(userCreate!!._id)) {
                val dialogFrag = ProifileSellerFragment.newInstance()
                val args = Bundle()
                args.putBoolean("isMap", isMap)
                dialogFrag.arguments = args
                dialogFrag.show(activity.supportFragmentManager, userCreate!!, activity.applicationContext)
            }

        }
        //=============================try again=======================
        v.try_again.setOnClickListener {
            error_message.visibility = View.GONE
            product_detail_holder.visibility = View.VISIBLE
//        toolbar_text_holder.visibility = View.GONE
//        toolbar.title = ""
            layout_detail_header.visibility = View.VISIBLE
            mProductDetailPresenter!!.getProductDetail(id, AppManager.getAppAccountUserId(activity))
        }
//        v.userimage1.setOnClickListener() {
//            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(product!!.comment!![0].user!!._id)) {
//                val dialogFrag = ProifileSellerFragment.newInstance()
//                val args = Bundle()
//                args.putBoolean("isMap", isMap)
//                dialogFrag.arguments = args
//                dialogFrag.show(activity.supportFragmentManager, product!!.comment!![0].user!!, activity.applicationContext)
//            }
//        }
//        v.userimage2.setOnClickListener() {
//            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(product!!.comment!![0].user!!._id)) {
//                val dialogFrag = ProifileSellerFragment.newInstance()
//                val args = Bundle()
//                args.putBoolean("isMap", isMap)
//                dialogFrag.arguments = args
//                dialogFrag.show(activity.supportFragmentManager, product!!.comment!![1].user!!, activity.applicationContext)
//            }
//        }
//        v.userimage3.setOnClickListener() {
//            if (!AppManager.getAppAccountUserId(activity.applicationContext).equals(product!!.comment!![0].user!!._id)) {
//                val dialogFrag = ProifileSellerFragment.newInstance()
//                val args = Bundle()
//                args.putBoolean("isMap", isMap)
//                dialogFrag.arguments = args
//                dialogFrag.show(activity.supportFragmentManager, product!!.comment!![2].user!!, activity.applicationContext)
//
//            }
//        }

        v.change_map.setOnClickListener {
            when (isMap) {
                false -> {
                    println("ban do")
                    isMap = true

                    map()
                    /////////////
//                    isMap = true
//                    change_map.background = resources.getDrawable(R.drawable.ic_map)
//                    slider.visibility = View.GONE
//                    val mapView_location = childFragmentManager.findFragmentById(R.id.mapView_location) as SupportMapFragment
//                    mapView_location.getMapAsync(this)
                }
                else -> {
                    isMap = false
                    change_map.background = resources.getDrawable(R.drawable.ic_location_white)
                    slider.visibility = View.VISIBLE
                }


            }
        }
        //==============================back button=================

        v.back_detail.setOnClickListener {
            if (activity.intent.data == null) {
                slider?.stopAutoCycle()
                slider?.removeAllSliders()
                activity.finish()
            } else {
                val i = Intent(activity, MainActivity::class.java)
                startActivity(i)
                activity.finish()
            }

        }
//===================================================================
        // Download product details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(Constants.product_ID)
                && savedInstanceState.containsKey(Constants.product_OBJECT) && savedInstanceState.containsKey(Constants.seller_DETAIL))) {
            id = arguments.getString(Constants.product_ID)
            id_user = arguments.getString(Constants.seller_ID)
//            Log.e("id + userid",id + " "+id_user)


            if (TextUtils.isEmpty(id)) {

//                toolbar_text_holder.visibility = View.GONE
//                toolbar.title = ""
            } else {
                mProductDetailPresenter!!.getProductDetail(id, AppManager.getAppAccountUserId(activity))
            }

        } else {

            id = savedInstanceState.getString(Constants.product_ID)
            id_user = savedInstanceState.getString(Constants.seller_ID)

            product = savedInstanceState.get(Constants.product_OBJECT) as Product
            Log.e("BBB", id + " " + product)
            onDownloadSuccessful()


        }


        v.layout_chat.setOnClickListener {

            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("avatar", photoprofile)
            intent.putExtra("iduser", userCreateProduct)
            startActivity(intent)

        }
        v.layout_call.setOnClickListener {
            val permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + product!!.user?.phone)
                    try {
                        startActivity(callIntent)
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(activity, R.string.err, Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>) =
                        Toast.makeText(activity, getString(R.string.per_deni) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()


            }
            TedPermission.with(activity)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(R.string.per_turnon)
                    .setPermissions(Manifest.permission.CALL_PHONE)
                    .check()
        }
        v.layout_sms.setOnClickListener {
            val permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    val smsIntent = Intent(Intent.ACTION_VIEW)

                    smsIntent.data = Uri.parse("smsto:")
                    smsIntent.type = "vnd.android-dir/mms-sms"
                    smsIntent.putExtra("address", product!!.user?.phone)
                    try {
                        startActivity(smsIntent)
                        Log.i("Finished sending SMS...", "")
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(activity,
                                R.string.sms_fail, Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>) =
                        Toast.makeText(activity, getString(R.string.per_deni) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()


            }
            TedPermission.with(activity)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(getString(R.string.per_turnon))
                    .setPermissions(Manifest.permission.SEND_SMS)
                    .check()
        }
        v.layout_share.setOnClickListener {

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND

            if (product?.price == null || product?.price == "") {
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "- Productname(tên sản phẩm): ${product!!.productname}\n" +
                                "- Category(thể loại): ${product!!.category}\n" +
                                "- Address(địa chỉ): ${product!!.location!!.address}\n" +
                                "- Time(thời gian): ${product!!.time} giờ\n" +
                                "- Description(Mô tả): ${product!!.description}\n" +
                                "- Tham khảo thêm tại: ${Constants.BASE_URL + "link?productid=" + product?._id + "&userid=" + product?.user?._id}")
            } else {
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "- Productname(tên sản phẩm): ${product!!.productname}\n" +
                                "- Category(thể loại): ${product!!.category}\n" +
                                "- Price(giá): ${formatprice?.format(product!!.price!!.toLong())} VNĐ\n" +
                                "- Address(địa chỉ): ${product!!.location!!.address}\n" +
                                "- Time(thời gian): ${product!!.time} giờ\n" +
                                "- Description(Mô tả): ${product!!.description}\n" +
                                "- Tham khảo thêm tại: ${Constants.BASE_URL + "link?productid=" + product?._id + "&userid=" + product?.user?._id}")

            }
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        v.btn_save.setOnClickListener {

            if (statussave) {
                mTypeSave = "1"
                mProductDetailPresenter!!.SaveProduct(id, AppManager.getAppAccountUserId(activity), mTypeSave)
            } else if (!statussave) {
                mTypeSave = "0"
                mProductDetailPresenter!!.SaveProduct(id, AppManager.getAppAccountUserId(activity), mTypeSave)
            }
        }
        v.img_save.setOnClickListener {

            if (statussave) {
                mTypeSave = "1"
                mProductDetailPresenter!!.SaveProduct(id, AppManager.getAppAccountUserId(activity), mTypeSave)
            } else if (!statussave) {
                mTypeSave = "0"
                mProductDetailPresenter!!.SaveProduct(id, AppManager.getAppAccountUserId(activity), mTypeSave)
            }
        }


        return v

    }

    private fun gotoallcomment() {
        val intent = Intent(activity, CommentActivity::class.java)
        intent.putExtra(Constants.product_ID, id)
        intent.putExtra(Constants.product_NAME, product!!.productname)
        intent.putExtra(Constants.seller_name, product!!.user!!.name)
        startActivityForResult(intent,105)
        activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
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
            btn_save.setImageResource(R.drawable.favorite_checked)
            img_save.setImageResource(R.drawable.favorite_checked)
            txt_save.text = getString(R.string.saved)
        } else {
            btn_save.setImageResource(R.drawable.favorite_unchecked)
            img_save.setImageResource(R.drawable.favorite_unchecked)
            txt_save.text = getString(R.string.save)
        }

    }

    override fun getProductDetail(response: Response) {
        product = null
        try {
            statussave = response.statussave!!
            if (statussave) {
                btn_save.setImageResource(R.drawable.favorite_checked)
                img_save.setImageResource(R.drawable.favorite_checked)
                txt_save.text = getString(R.string.saved)
            } else {
                btn_save.setImageResource(R.drawable.favorite_unchecked)
                img_save.setImageResource(R.drawable.favorite_unchecked)
                txt_save.text = getString(R.string.save)
            }
        } catch (e: Exception) {
            print(e.message)
            btn_save.visibility = View.GONE
            layout_favorite.visibility = View.GONE
        }
        userCreate = response.product?.user
        userCreateProduct = response.product?.user?._id.toString()
        this.product = response.product

        mProductListPresenter?.getProductListLikeNew(response.product?.category!!.toInt())

        if (userCreateProduct.equals(AppManager.getAppAccountUserId(activity.applicationContext))) {
            layout_contact.visibility  = View.GONE
        } else {
            layout_contact.visibility = View.VISIBLE
        }
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
            textSliderView.bundle?.putInt("index", i)
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
        mCommentPresenter!!.refreshcomment(id)
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


    private fun onDownloadSuccessful() {
        change_map.visibility = View.VISIBLE
        // status save product


        // Toggle visibility

        layout_detail_info_demo.visibility = View.GONE
        layout_detail_info.visibility = View.VISIBLE
        layout_detail_header.visibility = View.VISIBLE
        // Set title and tagline
        product_name.text = product!!.productname
        product_overview.text = product!!.description
        toolbar_productname.text = product!!.productname
        toolbar_username.text = product!!.user!!.name
        println(product!!.user!!.name)
        if (product!!.user!!.photoprofile!!.startsWith("http")) {
            photoprofile = product!!.user!!.photoprofile
        } else {
            photoprofile = Constants.IMAGE_URL + product!!.user!!.photoprofile
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
        product_view.text = product!!.view.toString() +" "+ getString(R.string.view)
        println(product!!._id)
        Log.e("status",product?.status)
        if(product?.status == "1"){
            edt_rented.visibility = View.VISIBLE
        }
        if (product!!.status == "1")
            Log.e("time: ", product!!.time)
        when (product!!.time) {
            "0" -> img_bagde.background = resources.getDrawable(R.drawable.bagde_hour)
            "1" -> img_bagde.background = resources.getDrawable(R.drawable.bagde_day)
            "2" -> img_bagde.background = resources.getDrawable(R.drawable.bagde_week)
            "3" -> img_bagde.background = resources.getDrawable(R.drawable.bagde_month)
            else -> {
                img_bagde.background = resources.getDrawable(R.drawable.bagde_year)
            }

        }
        if (product!!.type == "1") {
            product_status.text = getString(R.string.rent)
            product_status.setTextColor(Color.WHITE)
            product_status.background = resources.getDrawable(R.drawable.roundtextview)
            val temp = formatprice?.format(product!!.price?.toDouble()) + format
            product_price.text = temp
            when (product!!.category) {
                "0" -> {
                    icon_category.setImageResource(R.drawable.cate_vehicle_round)
                    txt_category.text = getString(R.string.cate_vehicle)
                }

                "1" -> {
                    icon_category.setImageResource(R.drawable.cate_electronic_round)
                    txt_category.text = getString(R.string.cate_electronic)
                }
                "2" -> {
                    icon_category.setImageResource(R.drawable.cate_fashion_round)
                    txt_category.text = getString(R.string.cate_fashion)
                }
                "3" -> {
                    icon_category.setImageResource(R.drawable.cate_home_round)
                    txt_category.text = getString(R.string.cate_house)
                }
                "4" -> {
                    icon_category.setImageResource(R.drawable.cate_mother_round)
                    txt_category.text = getString(R.string.cate_motherandbaby)
                }
                "5" -> {
                    icon_category.setImageResource(R.drawable.cate_furniture_round)
                    txt_category.text = getString(R.string.cate_furniture)
                }
                "6" -> {
                    icon_category.setImageResource(R.drawable.cate_cup_round)
                    txt_category.text = getString(R.string.cate_entertaiment)
                }
                "7" -> {
                    icon_category.setImageResource(R.drawable.cate_printer_round)
                    txt_category.text = getString(R.string.cate_office)
                }
                else -> { // Note the block
                    icon_category.setImageResource(R.drawable.cate_more_round)
                    txt_category.text = getString(R.string.cate_other)
                }
            }
            product_status.setOnClickListener {

            }
        } else {

            product_status.text = getString(R.string.need_rent)
            product_status.setTextColor(Color.WHITE)
            product_status.background = resources.getDrawable(R.drawable.roundtextview2)
            price_and_bagde.visibility = View.GONE
            icon_calendar.setImageResource(R.drawable.calendar1)
            icon_view.setImageResource(R.drawable.eye1)
            when (product!!.category) {
                "0" -> {
                    icon_category.setImageResource(R.drawable.cate_vehicle_round1)
                    txt_category.text = getString(R.string.cate_vehicle)
                }

                "1" -> {
                    icon_category.setImageResource(R.drawable.cate_electronic_round1)
                    txt_category.text = getString(R.string.cate_electronic)
                }
                "2" -> {
                    icon_category.setImageResource(R.drawable.cate_fashion_round1)
                    txt_category.text = getString(R.string.cate_fashion)
                }
                "3" -> {
                    icon_category.setImageResource(R.drawable.cate_home_round1)
                    txt_category.text = getString(R.string.cate_house)
                }
                "4" -> {
                    icon_category.setImageResource(R.drawable.cate_mother_round1)
                    txt_category.text = getString(R.string.cate_motherandbaby)
                }
                "5" -> {
                    icon_category.setImageResource(R.drawable.cate_furniture_round1)
                    txt_category.text = getString(R.string.cate_furniture)
                }
                "6" -> {
                    icon_category.setImageResource(R.drawable.cate_cup_round1)
                    txt_category.text = getString(R.string.cate_entertaiment)
                }
                "7" -> {
                    icon_category.setImageResource(R.drawable.cate_printer_round)
                    txt_category.text = getString(R.string.cate_office)
                }
                else -> { // Note the block
                    icon_category.setImageResource(R.drawable.cate_more_round1)
                    txt_category.text = getString(R.string.cate_other)
                }
            }
        }

        mCommentPresenter!!.refreshcomment(id)


        val listUserComments = ArrayList<String>()
        try{
            product?.comment!!
                    .asSequence()
                    .filterNot { listUserComments.contains(it.user!!.photoprofile) }
                    .forEach {
                        listUserComments.add(it.user!!.photoprofile!!)
                        println(it.user!!.photoprofile)
                    }
        }catch (e: Exception){
            System.out.print(e.message)
        }

        when {
            listUserComments.size == 0 -> {

            }
            listUserComments.size == 1 -> {
                head1.visibility = View.VISIBLE
                Glide.with(this)
                        .load(avatacmt(listUserComments[0]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head1)
            }
            listUserComments.size == 2 -> {
                head1.visibility = View.VISIBLE
                head2.visibility = View.VISIBLE
                Glide.with(this)
                        .load(avatacmt(listUserComments[0]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head1)
                Glide.with(this)
                        .load(avatacmt(listUserComments[1]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head2)
            }
            listUserComments.size == 3 -> {
                head1.visibility = View.VISIBLE
                head2.visibility = View.VISIBLE
                head3.visibility = View.VISIBLE
                Glide.with(this)
                        .load(avatacmt(listUserComments[0]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head1)
                Glide.with(this)
                        .load(avatacmt(listUserComments[1]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head2)
                Glide.with(this)
                        .load(avatacmt(listUserComments[2]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head3)
            }
            listUserComments.size == 4 -> {
                head1.visibility = View.VISIBLE
                head2.visibility = View.VISIBLE
                head3.visibility = View.VISIBLE
                head4.visibility = View.VISIBLE
                Glide.with(this)
                        .load(avatacmt(listUserComments[0]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head1)
                Glide.with(this)
                        .load(avatacmt(listUserComments[1]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head2)
                Glide.with(this)
                        .load(avatacmt(listUserComments[2]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head3)
                Glide.with(this)
                        .load(avatacmt(listUserComments[3]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head4)
            }
            else -> {
                head1.visibility = View.VISIBLE
                head2.visibility = View.VISIBLE
                head3.visibility = View.VISIBLE
                head4.visibility = View.VISIBLE
                head5.visibility = View.VISIBLE
                Glide.with(this)
                        .load(avatacmt(listUserComments[0]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head1)
                Glide.with(this)
                        .load(avatacmt(listUserComments[1]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head2)
                Glide.with(this)
                        .load(avatacmt(listUserComments[1]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head3)
                Glide.with(this)
                        .load(avatacmt(listUserComments[1]))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(head4)

                head5.text = "+" + (listUserComments.size - 4)
                head5.setSolidColor("#EC407A")


            }


        }
        if (product?.images?.size == 0) {
            slider.visibility = View.GONE
            change_map.visibility = View.GONE
            isMap = false
            map()
        } else {
            showAnimationBanner()
        }

    }
    fun setDatafromComment1(mProduct: Comment?){
        stt = false
        // ======= Check like ==============/
        if(mProduct?.listlike!!.size != 0){
            txt_num_like.text = mProduct?.listlike!!.size.toString()

            for(i in 0..(mProduct?.listlike!!.size-1)){
                if(Myid.equals(mProduct?.listlike!![i])){
                    stt = true
                    break
                }
            }

        }else{

            txt_num_like.text = ""
            img_like.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
        }
        if(stt) {
            img_like.setImageDrawable(resources.getDrawable(R.drawable.icon_liked))
        }else{
            img_like.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
        }
        //==================================/

        comment_item1.visibility = View.VISIBLE
        comment_item2.visibility = View.GONE
        comment_item3.visibility = View.GONE
        if(!mProduct?.user?.photoprofile!!.equals("") && mProduct?.user?.photoprofile!! != null)
        {
            Glide.with(this)
                    .load(avatacmt(mProduct?.user!!.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage1)
        }

        usercomments1.text = mProduct?.user!!.name
        Log.e("Email: ", mProduct?.user!!.email)
//        email_comment1.text = mProduct?.user!!.email
        comments1.text = mProduct?.content
        datecomment1.text = timeAgo(mProduct?.time!!)
        //====================check reply =================================//
        txt_reply.setOnClickListener(){
            val intent = Intent(activity, ReplyCommentActivity::class.java)
            intent.putExtra(Constants.comment_ID, mProduct._id)
            intent.putExtra(Constants.product_NAME, mProduct.content)
            intent.putExtra(Constants.seller_name, mProduct.user?.name)
            activity.startActivity(intent)
        }

        if(mProduct.listreply?.size!! > 0){
            var photoprofile = ""
            commemtreply.visibility = View.VISIBLE
            if(!mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile.equals("")){
                if(mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile?.startsWith("http")!!){
                    photoprofile = mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile!!
                }
                else{
                    photoprofile = Constants.IMAGE_URL+mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile
                }
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.server_unreachable)
                        .priority(Priority.HIGH)
                Glide.with(context)
                        .load(photoprofile)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(commentpic)
            }else{

            }

            commentname.text = mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.name
            commentdata.text = mProduct.listreply!![(mProduct.listreply!!.size-1)].content

        }else{
            commemtreply.visibility = View.GONE
        }

    }
    fun setDatafromComment2(mProduct: Comment?){
        stt2 = false
        if(mProduct?.listlike!!.size != 0){
            txt_num_like2.text = mProduct?.listlike!!.size.toString()

            for(i in 0..(mProduct?.listlike!!.size-1)){
                if(Myid.equals(mProduct?.listlike!![i])){
                    stt2 = true
                    break
                }
            }

        }else{
            txt_num_like2.text = ""
            img_like2.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
        }
        if(stt2) {
            img_like2.setImageDrawable(resources.getDrawable(R.drawable.icon_liked))
        }else{
            img_like2.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
        }
        comment_item2.visibility = View.VISIBLE
        comment_item3.visibility = View.GONE

        if(!mProduct?.user?.photoprofile!!.equals("") && mProduct?.user?.photoprofile!! != null)
        {
            Glide.with(this)
                    .load(avatacmt(mProduct?.user?.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage2)
        }

        usercomments2.text = mProduct?.user!!.name
        email_comment2.text = mProduct?.user!!.email
        comments2.text = mProduct?.content
        datecomment2.text = timeAgo(mProduct?.time!!)

        //====================check reply =================================//
        txt_reply2.setOnClickListener(){
            val intent = Intent(activity, ReplyCommentActivity::class.java)
            intent.putExtra(Constants.comment_ID, mProduct._id)
            intent.putExtra(Constants.product_NAME, mProduct.content)
            intent.putExtra(Constants.seller_name, mProduct.user?.name)
            activity.startActivity(intent)
        }
        if(mProduct.listreply?.size!! > 0){
            var photoprofile = ""
            commemtreply2.visibility = View.VISIBLE
            if(!mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile.equals("")){
                if(mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile?.startsWith("http")!!){
                    photoprofile = mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile!!
                }
                else{
                    photoprofile = Constants.IMAGE_URL+mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile
                }
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.server_unreachable)
                        .priority(Priority.HIGH)
                Glide.with(context)
                        .load(photoprofile)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(commentpic2)
            }else{

            }

            commentname2.text = mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.name
            commentdata2.text = mProduct.listreply!![(mProduct.listreply!!.size-1)].content

        }else{
            commemtreply2.visibility = View.GONE
        }
    }
    fun setDatafromComment3(mProduct: Comment?){
        stt3 = false
        if(mProduct?.listlike!!.size != 0){
            txt_num_like3.text = mProduct?.listlike!!.size.toString()

            for(i in 0..(mProduct?.listlike!!.size-1)){
                if(Myid.equals(mProduct?.listlike!![i])){
                    stt3 = true
                    break
                }
            }

        }else{
            txt_num_like3.text = ""
            img_like3.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
        }
        if(stt3) {
            img_like3.setImageDrawable(resources.getDrawable(R.drawable.icon_liked))
        }else{
            img_like3.setImageDrawable(resources.getDrawable(R.drawable.icon_like))
        }
        comment_item3.visibility = View.VISIBLE
        if(!mProduct?.user?.photoprofile!!.equals("") && mProduct?.user?.photoprofile!! != null)
        {
            Glide.with(this)
                    .load(avatacmt(mProduct?.user?.photoprofile!!))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(userimage3)
        }
        usercomments3.text = mProduct?.user!!.name
        email_comment3.text = mProduct?.user!!.email
        comments3.text = mProduct?.content
        datecomment3.text = timeAgo(mProduct?.time!!)
        //====================check reply =================================//
        txt_reply3.setOnClickListener(){
            val intent = Intent(activity, ReplyCommentActivity::class.java)
            intent.putExtra(Constants.comment_ID, mProduct._id)
            intent.putExtra(Constants.product_NAME, mProduct.content)
            intent.putExtra(Constants.seller_name, mProduct.user?.name)
            activity.startActivity(intent)
        }
        if(mProduct.listreply?.size!! > 0){
            var photoprofile = ""
            commemtreply3.visibility = View.VISIBLE
            if(!mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile.equals("")){
                if(mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile?.startsWith("http")!!){
                    photoprofile = mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile!!
                }
                else{
                    photoprofile = Constants.IMAGE_URL+mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.photoprofile
                }
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.server_unreachable)
                        .priority(Priority.HIGH)
                Glide.with(context)
                        .load(photoprofile)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(commentpic3)
            }else{

            }

            commentname3.text = mProduct.listreply!![(mProduct.listreply!!.size-1)].user?.name
            commentdata3.text = mProduct.listreply!![(mProduct.listreply!!.size-1)].content

        }else{
            commemtreply3.visibility = View.GONE
        }
    }
    fun destroyfragment() {
//        mProductDetailPresenter!!.cancelRequest()
        if (AppManager.getAppAccountUserId(activity) != id_user)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(id)


    }

    private fun timeAgo(time: String): CharSequence? {
        val time = DateUtils.getRelativeTimeSpanString(
                java.lang.Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
        return time
    }

    fun avatacmt(link: String): CharSequence? {
        if (link.startsWith("http")) {
            photoprofile = link
        } else {
            photoprofile = Constants.IMAGE_URL + link
        }
        return photoprofile
    }

    private fun onDownloadFailed() {
        try{
            error_message.visibility = View.VISIBLE

            product_detail_holder.visibility = View.GONE
//        toolbar_text_holder.visibility = View.GONE
//        toolbar.title = ""
            layout_detail_header.visibility = View.GONE
        }catch (e: Exception){
            System.out.print(e.message)
        }

    }


    // FAB related functions
    fun map() {
        val permissionlistener = object : PermissionListener, OnMapReadyCallback {
            @SuppressLint("MissingPermission")
            override fun onMapReady(map: GoogleMap?) {

                change_map.background = resources.getDrawable(R.drawable.ic_gallery)
                slider.visibility = View.GONE
                mMap = map
                mMap!!.isMyLocationEnabled = true

                // For dropping a marker at a point on the Map
                val sydney = LatLng((product!!.location!!.coordinates!![1].toString()).toDouble(), (product!!.location!!.coordinates!![0].toString()).toDouble())
                Log.e("sydney: ", sydney.toString())
                mMap!!.addMarker(MarkerOptions().position(sydney).title(product!!.productname).snippet(product!!.location!!.address)).showInfoWindow()

                // For zooming automatically to the location of the marker
                val cameraPosition = CameraPosition.Builder().target(sydney).zoom(16f).build()
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f))
//                            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }

            override fun onPermissionGranted() {
                try{
                    val mapView_location = childFragmentManager.findFragmentById(R.id.mapView_location) as SupportMapFragment
                    mapView_location.getMapAsync(
                            this)
                }
                catch (e : Exception){
                    Log.e("error",e.toString())
                }

            }


            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>) {
                try{
                    Toast.makeText(activity, getString(R.string.per_deni) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()

                }
                catch (e : Exception){
                    Log.e("error",e.toString())
                }
            }


        }
        TedPermission.with(activity)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(getString(R.string.per_turnon))
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }
    override fun onproductClicked(position: Int) {

        var intent2 = Intent(activity, ProductDetailActivity::class.java)
        intent2.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
        intent2.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
        activity.startActivityForResult(intent2,105)
        activity.finish()

    }
    override fun onDestroy() {
        super.onDestroy()
        mProductDetailPresenter!!.cancelRequest()
        mCommentPresenter!!.cancelRequest()
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
        }else if(requestCode == 105){
            mCommentPresenter!!.refreshcomment(id)
        }
    }

    override fun onStop() {

        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        slider.stopAutoCycle()
        super.onStop()
    }

    override fun onSliderClick(slider: BaseSliderView) {

        val i = Intent(activity, Fullscreen::class.java)
        i.putExtra("pos", slider.bundle.getInt("index"))
        i.putExtra("data", product?.images)
        startActivity(i)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        Log.d("Slider Demo", "Page Changed: " + position)
    }

    override fun onPageScrollStateChanged(state: Int) {}


    companion object {
        private val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234
    }

    override fun isCommentSuccessful(isCommentSuccessful: Boolean) {
    }

    override fun getCommentDetail(listcomment: ArrayList<Comment>) {
        no_cmt.visibility = View.GONE

//=======================0 cmt========================
        if (listcomment.size == 0) {
            comment_item1.visibility = View.GONE
            comment_item2.visibility = View.GONE
            comment_item3.visibility = View.GONE

            no_cmt.visibility = View.VISIBLE
        }
//=======================1 cmt========================
        else if (listcomment.size == 1) {
            product?.comment?.add(0,listcomment.get(0))
            setDatafromComment1(listcomment.get(0))
        }
//=======================2 cmt========================

        else if (listcomment.size == 2) {
            product?.comment?.add(0,listcomment.get(1))
            product?.comment?.add(1,listcomment.get(0))
            setDatafromComment1(listcomment.get(1))
            setDatafromComment2(listcomment.get(0))
        }


//=======================3 cmt========================
        else if (listcomment.size == 3) {
            product?.comment?.add(0,listcomment.get(2))
            product?.comment?.add(1,listcomment.get(1))
            product?.comment?.add(2,listcomment.get(0))
            setDatafromComment1(listcomment.get(2))
            setDatafromComment2(listcomment.get(1))
            setDatafromComment3(listcomment.get(0))
        }else{
            product?.comment?.add(0,listcomment.get(listcomment.size-1))
            product?.comment?.add(1,listcomment.get(listcomment.size-2))
            product?.comment?.add(2,listcomment.get(listcomment.size-3))
            setDatafromComment1(listcomment.get(listcomment.size-1))
            setDatafromComment2(listcomment.get(listcomment.size-2))
            setDatafromComment3(listcomment.get(listcomment.size-3))
            add_comment.text = getString(R.string.more_comment)+" ("+(listcomment.size -3)+")".toString()
        }
    }
}
