package finger.thuetot.vn.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.facebook.FacebookSdk
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.iid.FirebaseInstanceId
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.ProductDetailActivity
import finger.thuetot.vn.activity.SearchActivity
import finger.thuetot.vn.adapter.ProductAdapter
import finger.thuetot.vn.customview.CircularAnim
import finger.thuetot.vn.customview.DividerItemDecoration
import finger.thuetot.vn.lib.ShimmerRecycleView.OnLoadMoreListener
import finger.thuetot.vn.lib.firewall.SafetyNetHelper
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.manager.PrefManager
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.Response
import finger.thuetot.vn.model.User
import finger.thuetot.vn.presenter.DrawerPresenter
import finger.thuetot.vn.presenter.ProductListPresenter
import finger.thuetot.vn.presenter.checkVersion
import finger.thuetot.vn.util.Constants
import finger.thuetot.vn.util.Validation
import kotlinx.android.synthetic.main.dialog_phonenumber.view.*
import kotlinx.android.synthetic.main.layout_error_message.*
import kotlinx.android.synthetic.main.tab_home.*




/**
 * Created by Admin on 3/15/2017.
 */

class HomeFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListPresenter.ProductListView, DrawerPresenter.DrawerView, checkVersion.IntroView {
    override fun getVersion(ver: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelreferralSuccess(response: Response) {
        manager!!.setValidation(true)
        Log.e("phoneeeeee", response.user?.totalreferralpoint.toString() + " / " + response.user?.email + " / " + response.user?.referral)
        mAleftdialog?.cancel()
        AppManager.onlyremoveAccount(context, response.user!!)
    }

    override fun referralSuccess(response: Response) {
        manager!!.setValidation(true)
        Log.e("phoneeeeee", response.user?.totalreferralpoint.toString() + " / " + response.user?.email + " / " + response.user?.referral)
        mAleftdialog?.cancel()
        showSnackBarMessage("Nhập mã giới thiệu thành công !")
        AppManager.onlyremoveAccount(context, response.user!!)
        val x = Intent()
        x.action = "mainactivity"
        x.putExtra("reload", response.user!!.totalreferralpoint.toString())
        activity.sendBroadcast(x)
//        navigation_view.getHeaderView(0).money_referral.text = response.user!!.totalreferralpoint.toString()
    }

    override fun setErrorPhonenumber(errorMessage: String) {
        accept?.isEnabled = true
        if (errorMessage.equals("")) {
            v!!.progressBar_phonenumber.visibility = View.GONE
            CircularAnim.show(v!!.btn_accept_phonenumber).go()
            v!!.edt_phonenumber.error = "Cảnh báo! Chúng tôi đã phát hiện thiết bị được sử dụng quá số lần cho phép"

        } else if(errorMessage.equals("block")){
            v!!.progressBar_phonenumber.visibility = View.GONE
            CircularAnim.show(v!!.btn_accept_phonenumber).go()
            v!!.edt_phonenumber.error = "Cảnh báo! mã giới thiệu này đã bị tạm khóa vì sai phạm điều lệ THUÊ TỐT. Hãy thử mã giới thiệu khác."

        }
        else{
            v!!.progressBar_phonenumber.visibility = View.GONE
            CircularAnim.show(v!!.btn_accept_phonenumber).go()
            v!!.edt_phonenumber.error = "Mã giới thiệu không đúng hoặc không hợp lệ"
        }


    }
    private var appendChatScreenMsgReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val b = intent.extras
            if (b != null) {
                if (b.getBoolean("blockuser")) {
                    FacebookSdk.sdkInitialize(activity)
                    AppManager.removeAccount(activity)
                }
            }
        }
    }
    override fun changeAvatarSuccess(t: Response) {
    }

    override fun getUserDetail(response: Response) {
        AppManager.onlyremoveAccount(context, response.user!!)
//        AppManager.saveAccountUser(context, response.user!!, 0)
        showSnackBarMessage(getString(R.string.update_phonenumber_success))
//        Toast.makeText(activity, "Update phone success!", Toast.LENGTH_SHORT).show()
    }

    override fun getListSavedProduct(productsavedlist: User) {
    }

    var manager : PrefManager? =null
    var mProductListPresenter: ProductListPresenter? = null
    var mCheckAndroidId: checkVersion? = null
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
    var accept: Button? = null
    var androidId = ""
    var mContext : Context?=null
    private var safetyNetHelper: SafetyNetHelper? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager = PrefManager(activity)
        mContext = activity
        user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        mCheckAndroidId = checkVersion(this)

        try{
             androidId = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID)

            if(!androidId.equals("") || androidId != null){
                mCheckAndroidId?.checkAndroidId(user?._id!!,androidId)
            }
        }catch (ex: Exception){
            System.out.print(ex.message)
        }


        if(manager!!.validationDevice())
        {
            if (user!!.referral.equals("") || user!!.referral == null) {
                dialog = AlertDialog.Builder(mContext!!)
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

                                        mDrawarPresenter!!.editphone(user!!._id!!, phonenumber.text.toString(), androidId)



                                    } else {
                                        progressBar.visibility = View.GONE
                                        CircularAnim.show(accept!!).go()

                                    }
                                }
                            })

                }
                cancel.setOnClickListener {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Bạn có thật sự muốn hủy bỏ?")
                            .setPositiveButton(R.string.btn_ok, { _, _ ->
                                //                    Toast.makeText(context, "ádasda",Toast.LENGTH_LONG).show()
                                mDrawarPresenter!!.cancelReferral(user!!._id!!)


                            })
                            .setNegativeButton(R.string.cancelnot, { _, _ ->
                            })
                            .show()
                }
            }
            else
            {
                manager!!.setShowWarning(true)
            }
        }
        else{
            if(!manager!!.isWarning())
            {
                val builder = AlertDialog.Builder(mContext!!)
                builder.setMessage("Chức năng nhập mã không hoạt động trên thiết bị đã root , sửa đổi bản rom , mở khóa bootloader , máy ảo , thiết bị đã bị chỉnh sửa , google service đã cũ hoặc không hoạt động")
                        .setPositiveButton(R.string.btn_ok, { _, _ ->
                        })

                        .show()
                manager!!.setShowWarning(true)
            }

        }

        var mBundle = Bundle()
        mBundle = arguments
        mCategory = mBundle.getInt("Category", 1)
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



        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({
            if (isLodaing) {
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
            if (!isFistTime) {
                adapter!!.pageToDownload++
            }
            if (isLodaing) {
                mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)
            }

        })
        mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)
    }

    private fun handleError(errorCode: Int, errorMsg: String) {
        Log.e("HomeFragment", errorMsg)

        val b = StringBuilder()

        when (errorCode) {
            SafetyNetHelper.SAFETY_NET_API_REQUEST_UNSUCCESSFUL -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
            SafetyNetHelper.RESPONSE_ERROR_VALIDATING_SIGNATURE -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: error\n")
            }
            SafetyNetHelper.RESPONSE_FAILED_SIGNATURE_VALIDATION -> {
                b.append("SafetyNet request: success\n")
                b.append("Response signature validation: fail\n")
            }
            SafetyNetHelper.RESPONSE_VALIDATION_FAILED -> {
                b.append("SafetyNet request: success\n")
                b.append("Response validation: fail\n")
            }
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                b.append("SafetyNet request: fail\n")
                b.append("\n*GooglePlayServices outdated*\n")
                try {
                    val v = activity.getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode
                    val vName = activity.getPackageManager().getPackageInfo("com.google.android.gms", 0).versionName.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]
                    b.append("You are running version:\n$vName $v\nSafetyNet requires minimum:\n7.3.27 7327000\n")
                } catch (NameNotFoundException: Exception) {
                    b.append("Could not find GooglePlayServices on this device.\nPackage com.google.android.gms missing.")
                }

            }
            else -> {
                b.append("SafetyNet request failed\n")
                b.append("(This could be a networking issue.)\n")
            }
        }
        Log.e("HomeFragment", b.toString())
    }

    fun RefreshListener() {
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
        try{
            if(appendChatScreenMsgReceiver != null){
                activity.unregisterReceiver(appendChatScreenMsgReceiver)
            }
        }catch (e: Exception){System.out.print(e.message)}

        mProductListPresenter?.stopRequest()
        mDrawarPresenter?.cancelRequest()
    }

    override fun onResume() {
        super.onResume()
        activity.registerReceiver(this.appendChatScreenMsgReceiver, IntentFilter("blockuser"))
        Log.e("Phone", "Phone: " + user!!.referral)

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
