package sega.fastnetwork.test.fragment

import android.content.Intent
import android.os.Bundle
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
import kotlinx.android.synthetic.main.tab_home.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.MainActivity
import sega.fastnetwork.test.activity.ProductDetailActivity
import sega.fastnetwork.test.activity.ProductDetailNeedActivity
import sega.fastnetwork.test.activity.SearchActivity
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.CircularAnim
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.lib.ShimmerRecycleView.OnLoadMoreListener
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DrawerPresenter
import sega.fastnetwork.test.presenter.ProductListPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.util.Validation


/**
 * Created by Admin on 3/15/2017.
 */

class HomeFragment : Fragment(), ProductAdapter.OnproductClickListener, ProductListPresenter.ProductListView, DrawerPresenter.DrawerView {

    override fun changeAvatarSuccess(t: Response) {
    }

    override fun getUserDetail(response: Response) {
        AppManager.saveAccountUser(context, response.user!!, 0)
        showSnackBarMessage("Update phone success!")
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
    var mCategory = 0
    var user: User? = null
    val mAleftdialog: AlertDialog? = null
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

        swipe_refresh.setColorSchemeResources(R.color.color_background_button)
        swipe_refresh.setOnRefreshListener({


            adapter!!.pageToDownload = 1
            adapter!!.productList.clear()
            adapter!!.initShimmer()
            adapter!!.isLoading = false
            isFirstLoad = true
            Log.d("ssssss","11111111")
            mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)

        })
        search_view.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
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

            Log.d("ssssss","222222222")
            mProductListPresenter!!.getProductList(Constants.BORROW, adapter!!.pageToDownload, mCategory)
        })
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.tab_home, container, false)
//        view.pager!!.adapter = TabsAdapter(childFragmentManager)
//
//        view.tab_layout!!.setupWithViewPager(view.pager)
        retainInstance = true
        setHasOptionsMenu(true)
        return view
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        mProductListPresenter?.stopRequest()
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

        adapter?.notifyDataSetChanged()
        user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        Log.e("Phone", "Name: " + user!!.name + "Email: " + user!!.email + "Phone: " + user!!.phone)
        if (user!!.phone.equals("") || user!!.phone == null) {
            Log.e("HERE", "Here")

                val dl_phonenumber = AlertDialog.Builder(activity)
                val inflater = layoutInflater
                val v = inflater.inflate(R.layout.dialog_phonenumber, null)
                val phonenumber = v.findViewById<EditText>(R.id.edt_phonenumber)
                val progressBar = v.findViewById<ProgressBar>(R.id.progressBar_phonenumber)
                val accept = v.findViewById<Button>(R.id.btn_accept_phonenumber)
                dl_phonenumber.setView(v)
                dl_phonenumber.setCancelable(false)
                val dg = dl_phonenumber.show()

                accept.setOnClickListener {
                    CircularAnim.hide(accept)
                            .endRadius((progressBar.height / 2).toFloat())
                            .go(object : CircularAnim.OnAnimationEndListener {
                                override fun onAnimationEnd() {
                                    progressBar.visibility = View.VISIBLE
                                    phonenumber!!.error = null
                                    var err = 0
                                    if (!Validation.validateFields(phonenumber.text.toString())) {

                                        err++
                                        phonenumber.error = "Old password should not be empty !"
                                    }

                                    if (err == 0) {
//                                    mChangePasswordPresenter!!.changepassword(user!!._id!!, oldpass.text.toString(), newpass.text.toString())
//                                        mDrawerPresenter!!.eidtInfoUser(user!!._id.toString(),newname.text.toString(),newphone.text.toString())
                                        mDrawarPresenter!!.editphonenumber(user!!._id!!, phonenumber.text.toString())
                                        dg.dismiss()

//                    val user = User()
//                    user.name = name.text.toString()
//                    user.password = password.text.toString()
//                    user.email = email.text.toString()
//                    user.tokenfirebase = FirebaseInstanceId.getInstance().token
//                    mRegisterPresenter!!.register(user,Constants.LOCAL)

                                    } else {
                                        progressBar.visibility = View.GONE
                                        CircularAnim.show(accept).go()
//                                    showSnackBarMessage("Enter Valid Details !")
                                    }                            }
                            })

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

    private fun onDownloadFailed() {

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
            Log.d("sssss","!!!!!")
            adapter!!.productList.clear()
            isFirstLoad = false
        }
        adapter!!.productList.addAll(productlist)
        adapter!!.pageToDownload++
        adapter!!.isLoading = false
        adapter!!.isLoadingLocked = false
        onDownloadSuccessful()
    }

    override fun onproductClicked(position: Int) {

        if (isTablet) {
            //                Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();

            (activity as MainActivity).loadDetailFragmentWith(adapter!!.productList[position]._id!!)
        } else {
            if (adapter!!.productList[position].type == "1") {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
                startActivity(intent)
            } else {
                val intent = Intent(context, ProductDetailNeedActivity::class.java)
                intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
                startActivity(intent)
            }
        }
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
