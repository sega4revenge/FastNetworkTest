package finger.thuetot.vn.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.ChatActivity
import finger.thuetot.vn.activity.ProductDetailActivity
import finger.thuetot.vn.adapter.ProductAdapter
import finger.thuetot.vn.customview.DividerItemDecoration
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.DetailProfilePressenter
import finger.thuetot.vn.util.Constants
import kotlinx.android.synthetic.main.profile_seller_layout.view.*
import java.util.*

/**
 * Created by VinhNguyen on 10/19/2017.
 */
class ProifileSellerFragment : BottomSheetDialogFragment(), DetailProfilePressenter.DetailProfileView, ProductAdapter.OnproductClickListener{
    var mContext: Context? = null
    var mUser: User? = null
    var detailprofile: DetailProfilePressenter? = null
    private var adapter: ProductAdapter? = null
    var productGive: ArrayList<Product> = ArrayList()
    var productNeed: ArrayList<Product> = ArrayList()
    private var layoutManager: GridLayoutManager? = null
    private var contentView: View? = null
    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {


            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAllowingStateLoss()

            }


        }


        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            hidePopup()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    fun show(fragmentManager: FragmentManager,user: User,context: Context) {
        mContext = context
        mUser = user
        val ft = fragmentManager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            super.onCreateDialog(savedInstanceState)

    override fun onViewCreated(contentView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)


    }

    private fun getSoftButtonsBarHeight(): Int {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val usableHeight = metrics.heightPixels
            activity.windowManager.defaultDisplay.getRealMetrics(metrics)
            val realHeight = metrics.heightPixels
            return if (realHeight > usableHeight)
                realHeight - usableHeight
            else
                0
        }
        return 0
    }
    private fun hidePopup() {
   //     contentView!!.chips_input.mFilterableListView!!.fadeOut()
    //    contentView!!.slidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        contentView = View.inflate(context, R.layout.profile_seller_layout, null)
        dialog.setContentView(contentView)
        val layoutParams = (contentView!!.parent as View?)?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            behavior.peekHeight = 1800

        }
        contentView!!.btn_share.setOnClickListener(){
            Snackbar.make( contentView!!.findViewById(R.id.overview_coordinator_layout), "Comming soon :))", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(3000)
                    .show()
        }
        contentView!!.btn_chat.setOnClickListener(){
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("avatar", mUser!!.photoprofile)
            intent.putExtra("iduser", mUser!!._id)
            startActivity(intent)
        }
        contentView!!.img_back.setOnClickListener(){
            dismissAllowingStateLoss()
        }
        contentView!!.btn_call.setOnClickListener(){

            val permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + mUser!!.phone)
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
        contentView!!.txtname.text = mUser!!.name
        contentView!!.txtemail.text = mUser!!.email
        contentView!!.layout_give.setOnClickListener {
            contentView!!.tv_give.setTextColor(resources.getColor(R.color.colorAccent))
            contentView!!.tv_need.setTextColor(resources.getColor(R.color.text_light))
            contentView!!.tv_gived.setTextColor(resources.getColor(R.color.text_light))
            contentView!!.tv_needed.setTextColor(resources.getColor(R.color.text_light))
            if (productGive.size != 0) {
                contentView!!.product_list.visibility = View.VISIBLE
                contentView!!.mess_notfound.visibility = View.GONE
                adapter?.productList = productGive
                adapter?.notifyDataSetChanged()
            } else {
                contentView!!.product_list.visibility = View.GONE
                contentView!!.mess_notfound.visibility = View.VISIBLE
            }
        }
        contentView!!.layout_need.setOnClickListener {
            contentView!!.tv_give.setTextColor(resources.getColor(R.color.text_light))
            contentView!!.tv_need.setTextColor(resources.getColor(R.color.actionBarColor))
            contentView!!.tv_gived.setTextColor(resources.getColor(R.color.text_light))
            contentView!!. tv_needed.setTextColor(resources.getColor(R.color.text_light))
            if (productNeed.size != 0) {
                contentView!!.product_list.visibility = View.VISIBLE
                contentView!!.mess_notfound.visibility = View.GONE
                adapter?.productList = productNeed
                adapter?.notifyDataSetChanged()
            } else {
                contentView!!.product_list.visibility = View.GONE
                contentView!!.mess_notfound.visibility = View.VISIBLE
            }
        }

        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
        Glide.with(this).load(avatacmt(mUser?.photoprofile!!))
                .thumbnail(0.1f)
                .apply(options)
                .into(contentView!!.imgAvatar)

        layoutManager = GridLayoutManager(mContext, 1)
        contentView!!.product_list.setHasFixedSize(true)
        contentView!!.product_list.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        contentView!!.product_list.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(mContext!!, this,contentView!!.product_list, layoutManager!!)
        adapter!!.user = mUser!!.name
        contentView!!.product_list.adapter = adapter
        detailprofile = DetailProfilePressenter(this)
        detailprofile!!.ConnectHttp(mUser!!._id!!)
    }
    fun avatacmt(link: String): String?{
        var result = ""
        if(!link.startsWith("http")){
            result = Constants.IMAGE_URL+link
        }else{
            result = link
        }
        return result
    }

    companion object {
        fun newInstance(): ProifileSellerFragment = ProifileSellerFragment()
    }

    interface Callbacks {
        fun onResult(result: Any?)
    }
    override fun getListProduct(productlist: ArrayList<Product>, user: User) {

        if (adapter!!.productList.size > 0) {
            adapter!!.productList.clear()
        }
        if (productGive.size != 0) {
            productGive.clear()
        }
        if (productNeed.size != 0) {
            productNeed.clear()
        }
        var numGive = 0
        var numNeed = 0
        for (i in productlist) {
            if (i.type?.toInt() == 1) {
                productGive.add(i)
                numGive++
            } else {
                productNeed.add(i)
                numNeed++
            }
        }

        contentView!!.tv_number_give.text = numGive.toString()
        contentView!!.tv_number_need.text = numNeed.toString()
        contentView!!.product_list.visibility = View.VISIBLE
        if (productGive.size != 0) {
            contentView!!.mess_notfound.visibility = View.GONE
            adapter?.productList = productGive
            adapter?.notifyDataSetChanged()
        } else {
            contentView!!.product_list.visibility = View.GONE
            contentView!!.mess_notfound.visibility = View.VISIBLE
        }

    }

    override fun getUser(user: User) {
        contentView!!.root_addproduct.visibility = View.VISIBLE
        contentView!!.product_list.visibility = View.GONE
        contentView!!.mess_notfound.visibility = View.VISIBLE

        if (user.listproduct!!.size > 0) {
            getListProduct(user.listproduct!!, user)
        }
    }

    override fun setErrorMessage(errorMessage: String) {

    }


    override fun onproductClicked(position: Int) {

        val intent = Intent(mContext, ProductDetailActivity::class.java)
        intent.putExtra(Constants.product_ID, adapter?.productList?.get(position)?._id)
        intent.putExtra(Constants.seller_ID, adapter?.productList?.get(position)?.user?._id)
        startActivity(intent)
    }
}


