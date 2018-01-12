package finger.thuetot.vn.fragment

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.google.firebase.iid.FirebaseInstanceId
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.ProductDetailActivity
import finger.thuetot.vn.activity.SearchActivity
import finger.thuetot.vn.adapter.ProductAdapter
import finger.thuetot.vn.customview.CircularAnim
import finger.thuetot.vn.customview.DividerItemDecoration
import finger.thuetot.vn.lib.DetectEmulator_Sensor
import finger.thuetot.vn.lib.DetectResult
import finger.thuetot.vn.lib.ShimmerRecycleView.OnLoadMoreListener
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.DrawerPresenter
import finger.thuetot.vn.presenter.ProductListPresenter
import finger.thuetot.vn.util.Constants
import finger.thuetot.vn.util.Validation
import kotlinx.android.synthetic.main.dialog_phonenumber.view.*
import kotlinx.android.synthetic.main.layout_error_message.*
import kotlinx.android.synthetic.main.tab_home.*


/**
 * Created by Admin on 3/15/2017.
 */

class HomeFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListPresenter.ProductListView, DrawerPresenter.DrawerView, DetectResult {
    override fun Result(isRealDevice: Boolean) {
        println("day la " +getEmulatorName(activity))

       if(isRealDevice&&TextUtils.isEmpty(getEmulatorName(activity)))
       {
           if (user!!.referral.equals("") || user!!.referral == null) {
               Log.e("HEREEEEEEE", "Here")

               dialog = AlertDialog.Builder(activity)
               val inflater = layoutInflater
               v = inflater.inflate(R.layout.dialog_phonenumber, null)
               val phonenumber = v!!.findViewById<EditText>(R.id.edt_phonenumber)
               val progressBar = v!!.findViewById<ProgressBar>(R.id.progressBar_phonenumber)
               accept = v!!.findViewById<Button>(R.id.btn_accept_phonenumber)
               val cancel = v!!.findViewById<Button>(R.id.btn_cancel_phonenumber)
               dialog!!.setView(v)
               dialog!!.setCancelable(false)

               mAleftdialog = dialog!!.show()

               accept?.setOnClickListener {
                   Log.e("Click button","Click buttonnnnnnnnnnnn")
                   accept?.isEnabled = false
                   val tokenfirebase = FirebaseInstanceId.getInstance().token
                   CircularAnim.hide(accept!!)
                           .endRadius((progressBar.height / 2).toFloat())
                           .go(object : CircularAnim.OnAnimationEndListener {
                               override fun onAnimationEnd() {
                                   progressBar.visibility = View.VISIBLE
                                   phonenumber!!.error = null
                                   var err = 0
                                   if (!Validation.validateFields(phonenumber.text.toString())) {
                                       accept?.isEnabled = true
                                       err++
                                       phonenumber.error = getString(R.string.st_errpass)
                                   }

                                   if (err == 0) {
//                                    mChangePasswordPresenter!!.changepassword(user!!._id!!, oldpass.text.toString(), newpass.text.toString())
//                                        mDrawerPresenter!!.eidtInfoUser(user!!._id.toString(),newname.text.toString(),newphone.text.toString())
                                       mDrawarPresenter!!.editphonenumber(user!!._id!!, phonenumber.text.toString(),tokenfirebase!!)

//                    val user = User()
//                    user.name = name.text.toString()
//                    user.password = password.text.toString()
//                    user.email = email.text.toString()
//                    user.tokenfirebase = FirebaseInstanceId.getInstance().token
//                    mRegisterPresenter!!.register(user,Constants.LOCAL)

                                   } else {
                                       progressBar.visibility = View.GONE
                                       CircularAnim.show(accept!!).go()
//                                    showSnackBarMessage("Enter Valid Details !")
                                   }                            }
                           })

               }
               cancel.setOnClickListener{
                   val builder = AlertDialog.Builder(activity)
                   builder.setTitle("Bạn có thật sự muốn hủy bỏ?")
                           .setPositiveButton(R.string.btn_ok, { _, _ ->
                               //                    Toast.makeText(context, "ádasda",Toast.LENGTH_LONG).show()
                               mDrawarPresenter!!.cancelReferral(user!!._id!!)

                           })
                           .setNegativeButton(R.string.cancelnot, { _, _ ->
                               //                            Toast.makeText(context, "ádasda",Toast.LENGTH_LONG).show()
//                            mAleftdialog!!.dismiss()

                           })
                           .show()
               }
//            val aleftdialog = AlertDialog.Builder(activity)
//            aleftdialog.setMessage("Enter phone number:")
//            val input = EditText(activity)
//            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
//            input.layoutParams = lp
//            input.inputType = InputType.TYPE_CLASS_NUMBER
//            aleftdialog.setView(input)
//            aleftdialog.setIcon(R.drawable.phone_call)
//            aleftdialog.setPositiveButton("OK", null)
//            val mAleftdialog = aleftdialog.create()
//            mAleftdialog.setOnShowListener {
//                val b = mAleftdialog.getButton(AlertDialog.BUTTON_POSITIVE)
//                b.setOnClickListener {
//                    if (!Validation.validateFields(input.text.toString())) {
//                        input.error = "Should not be empty !"
//                    } else {
//                        mDrawarPresenter!!.editphonenumber(user!!._id!!, input.text.toString())
//                        mAleftdialog.dismiss()
//                    }
//                }
//            }
//            mAleftdialog.setCancelable(false)
//            mAleftdialog.show()

           }
       }
        else{
           val builder = AlertDialog.Builder(activity)
           builder.setMessage("Chức năng giới thiệu người dùng không áp dụng cho thiết bị này !")
                   .setPositiveButton(R.string.btn_ok, { _, _ ->
                   })

                   .show()
       }
    }

    fun getEmulatorName(context: Context): String {
        var sEmulatorName: String? = null
        val packageManager = context.packageManager
        val packages = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            val packageName = packageInfo.packageName
            if (packageName.startsWith("com.vphone.") || packageName.startsWith("com.bignox.")) {
                sEmulatorName = context.getString(R.string.emulator_name_yeshen)
            } else if (packageName.startsWith("me.haima.")) {
                sEmulatorName = context.getString(R.string.emulator_name_haimawan)
            } else if (packageName.startsWith("com.bluestacks.")) {
                sEmulatorName = context.getString(R.string.emulator_name_bluestacks)
            } else if (packageName.startsWith("cn.itools.") && (Build.PRODUCT.startsWith("iToolsAVM") || Build.MANUFACTURER.startsWith("iToolsAVM") || Build.DEVICE.startsWith("iToolsAVM") || Build.MODEL.startsWith("iToolsAVM") || Build.BRAND.startsWith("generic") || Build.HARDWARE.startsWith("vbox86"))) {
                sEmulatorName = context.getString(R.string.emulator_name_itools)
            } else if (packageName.startsWith("com.kop.") || packageName.startsWith("com.kaopu.")) {
                sEmulatorName = context.getString(R.string.emulator_name_tiantian)
            } else if (packageName.startsWith("com.microvirt.")) {
                sEmulatorName = context.getString(R.string.emulator_name_xiaoyao)
            } else if (packageName == "com.google.android.launcher.layouts.genymotion") {
                sEmulatorName = getString(R.string.emulator_name_genymotion)
            }
        }
        if (sEmulatorName == null) {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val serviceInfos = manager.getRunningServices(30)
            for (serviceInfo in serviceInfos) {
                val serviceName = serviceInfo.service.className
                if (serviceName.startsWith("com.bluestacks.")) {
                    sEmulatorName = context.getString(R.string.emulator_name_bluestacks)
                }
            }
        }
        if (sEmulatorName == null && Build.PRODUCT.startsWith("sdk_google")) {
            sEmulatorName = context.getString(R.string.emulator_name_android)
        }
        return if (sEmulatorName == null) "" else sEmulatorName
    }


    override fun cancelreferralSuccess(response: Response) {
        Log.e("phoneeeeee",response.user?.totalreferralpoint.toString() + " / " + response.user?.email + " / " + response.user?.referral)
        mAleftdialog?.cancel()
        AppManager.onlyremoveAccount(context,response.user!!)
    }

    override fun referralSuccess(response: Response) {
        Log.e("phoneeeeee",response.user?.totalreferralpoint.toString() + " / " + response.user?.email + " / " + response.user?.referral)
        mAleftdialog?.cancel()
        showSnackBarMessage("Nhập mã giới thiệu thành công !")
        AppManager.onlyremoveAccount(context,response.user!!)
        val x = Intent()
        x.action = "mainactivity"
        x.putExtra("reload", response.user!!.totalreferralpoint.toString())
        activity.sendBroadcast(x)
//        navigation_view.getHeaderView(0).money_referral.text = response.user!!.totalreferralpoint.toString()
    }

    override fun setErrorPhonenumber(errorMessage: String) {
        accept?.isEnabled  = true
        if(errorMessage.equals("")){
            v!!.progressBar_phonenumber.visibility = View.GONE
            CircularAnim.show(v!!.btn_accept_phonenumber).go()
            v!!.edt_phonenumber.error = "Cảnh báo! Chúng tôi đã phát hiện thiết bị được sử quá số lần cho phép"

        }else{
            v!!.progressBar_phonenumber.visibility = View.GONE
            CircularAnim.show(v!!.btn_accept_phonenumber).go()
            v!!.edt_phonenumber.error = "Không tìm thấy số điện thoại này"
        }


    }

    override fun changeAvatarSuccess(t: Response) {
    }

    override fun getUserDetail(response: Response) {
        AppManager.onlyremoveAccount(context,response.user!!)
//        AppManager.saveAccountUser(context, response.user!!, 0)
        showSnackBarMessage(getString(R.string.update_phonenumber_success))
//        Toast.makeText(activity, "Update phone success!", Toast.LENGTH_SHORT).show()
    }

    override fun getListSavedProduct(productsavedlist: User) {
    }


    var mProductListPresenter: ProductListPresenter? = null
    var mDrawarPresenter: DrawerPresenter? = null
    private var isTablet: Boolean = false
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: ProductAdapter? = null
    var isFirstLoad = true
    var isLodaing = true
    var isFistTime = true
    var mCategory = 0
    var user: User? = null
    var mAleftdialog: AlertDialog? = null
    var v: View? = null
    var dialog: AlertDialog.Builder? = null
    var accept : Button ?= null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mBundle = Bundle()
        mBundle = arguments
        mCategory = mBundle.getInt("Category",1)
        isTablet = resources.getBoolean(R.bool.is_tablet)
        mProductListPresenter = ProductListPresenter(this)
        mDrawarPresenter = DrawerPresenter(this)
        user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        product_recycleview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        product_recycleview.layoutManager = layoutManager
        product_recycleview.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(context, this, product_recycleview, layoutManager!!)
        product_recycleview.adapter = adapter

        adapter!!.pageToDownload = 1
        adapter!!.initShimmer()
        // mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)


        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({
            if(isLodaing){
                RefreshListener()
            }
        })
        search_view.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }

        try_again.setOnClickListener {
            error_message.visibility = View.GONE
            swipe_refresh.visibility = View.VISIBLE
            adapter!!.pageToDownload = 1
            adapter!!.productList.clear()
            adapter!!.initShimmer()
            adapter!!.isLoading = false
            isFirstLoad = true
            mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)

        }
        adapter!!.setOnLoadMoreListener(OnLoadMoreListener {

            if (!isFirstLoad) {
                val a = Product()
                a.productname = ""
                adapter!!.productList.add(a)
                product_recycleview.post({
                    adapter!!.notifyItemInserted(adapter!!.productList.size - 1)
                })
            }
            if(!isFistTime){
                adapter!!.pageToDownload++
            }
            if(isLodaing){
                mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)
            }

        })
    }
    fun RefreshListener(){
        isLodaing = false
        // product_recycleview.isNestedScrollingEnabled = false
        swipe_refresh.isEnabled = false
        adapter!!.productList.clear()
        adapter!!.pageToDownload = 1
        adapter!!.initShimmer()
        adapter!!.isLoading = false
        isFirstLoad = true
        mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)

    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.tab_home, container, false)
        retainInstance = true
        setHasOptionsMenu(true)
        return view
    }
    override fun onDestroy() {
        super.onDestroy()

        mProductListPresenter?.stopRequest()
        mDrawarPresenter?.cancelRequest()
    }

    override fun onResume() {
        super.onResume()
        user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        Log.e("Phone","Phone: " + user!!.referral)
        val ds = DetectEmulator_Sensor(activity)
        ds.setShowLog(true)
        ds.Detect(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onDetach() {
        super.onDetach()
    }
    private fun onDownloadSuccessful() {
        if (isTablet && adapter?.productList?.size!! > 0) {
            /*(activity as ProductActivity).loadDetailFragmentWith(adapter.productList[0].productid + "", String.valueOf(adapter.productList[0].userid))*/
        }
        error_message.visibility = View.GONE

        swipe_refresh.isRefreshing = false
        swipe_refresh.isEnabled = true
        swipe_refresh.visibility = View.VISIBLE

        adapter?.notifyDataSetChanged()
        isLodaing = true
        isFistTime = false

    }

    private fun onDownloadFailed() {
        // product_recycleview.isNestedScrollingEnabled = true
        isLodaing = true
        isFistTime = false
        if (adapter!!.pageToDownload == 1) {


            swipe_refresh.isRefreshing = false
            swipe_refresh.visibility = View.GONE
            error_message.visibility = View.VISIBLE
        } else {
            adapter!!.productList.removeAt(adapter!!.productList.size - 1)
            adapter!!.notifyItemRemoved(adapter!!.productList.size)

            error_message.visibility = View.GONE

            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true
            adapter!!.isLoadingLocked = true
        }


    }

    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }
    override fun setErrorNotFound() {
        if (adapter!!.pageToDownload == 1) {
            swipe_refresh.isRefreshing = false
            swipe_refresh.visibility = View.GONE
            error_message.visibility = View.GONE
            nodata.visibility = View.VISIBLE
        } else {
            adapter!!.productList.removeAt(adapter!!.productList.size - 1)
            adapter!!.notifyItemRemoved(adapter!!.productList.size)
            nodata.visibility = View.GONE
            error_message.visibility = View.GONE
            swipe_refresh.visibility = View.VISIBLE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true
            adapter!!.isLoadingLocked = true
        }

    }
    override fun getListProduct(productlist: ArrayList<Product>) {
        adapter!!.productList.removeAt(adapter!!.productList.size - 1)
        adapter!!.notifyItemRemoved(adapter!!.productList.size)
        if (isFirstLoad) {
            adapter!!.productList.clear()
            isFirstLoad = false
        }
        adapter!!.productList.addAll(productlist)
        adapter!!.isLoading = false
        adapter!!.isLoadingLocked = false
        swipe_refresh.isEnabled = true
        onDownloadSuccessful()
    }

    override fun onproductClicked(position: Int) {


//            if (adapter!!.productList[position].type == "1") {
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
        intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
        startActivity(intent)
//            } else {
//                val intent = Intent(context, ProductDetailNeedActivity::class.java)
//                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
//                intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
//                startActivity(intent)
//            }

    }
    private fun showSnackBarMessage(message: String?) {

        Snackbar.make(activity.findViewById(R.id.root_home), message!!, Snackbar.LENGTH_INDEFINITE)
                .setDuration(10000)
                .setAction("OK", {
                })
                .show()

    }
//    internal inner class TabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
//
//        override fun getCount(): Int {
//            return 2
//        }
//
//        override fun getItem(i: Int): Fragment? {
//            when (i) {
//                0 -> return ProductListFragment()
//                1 -> return ProductNeedListFragment()
//            }
//            return null
//        }
//
//        override fun getPageTitle(position: Int): CharSequence {
//            when (position) {
//                0 -> return "Cần thuê"
//                1 -> return "Cho thuê"
//            }
//            return ""
//        }
//    }


}
