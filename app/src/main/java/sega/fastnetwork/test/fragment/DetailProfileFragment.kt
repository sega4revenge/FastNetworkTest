package sega.fastnetwork.test.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.detailprofile.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.EditProductActivity
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailProfilePressenter

/**
 * Created by VinhNguyen on 8/22/2017.
 */
class DetailProfileFragment : Fragment(), DetailProfilePressenter.DetailProfileView, ProductAdapter.OnproductClickListener {

    private var layoutManager: GridLayoutManager? = null
    private var adapter: ProductAdapter? = null
    var detailprofile: DetailProfilePressenter? = null
    var productGive: ArrayList<Product> = ArrayList<Product>()
    var productNeed: ArrayList<Product> = ArrayList<Product>()
    var numType = 0
    val options = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .placeholder(R.drawable.logo)
            .error(R.drawable.img_error)
            .priority(Priority.HIGH)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        layoutManager = GridLayoutManager(context, 1)


        product_list.setHasFixedSize(true)
        product_list.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_list.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(this.activity, this, product_list, layoutManager!!)
        adapter!!.user = AppManager.getUserDatafromAccount(activity, AppManager.getAppAccount(activity)!!).name
        product_list.adapter = adapter

        detailprofile = DetailProfilePressenter(this)
        layout_give.setOnClickListener {
            tv_give.setTextColor(resources.getColor(R.color.colorAccent))
            tv_need.setTextColor(resources.getColor(R.color.text_light))
            tv_gived.setTextColor(resources.getColor(R.color.text_light))
            tv_needed.setTextColor(resources.getColor(R.color.text_light))
            if (productGive.size != 0) {
                product_list.visibility = View.VISIBLE
                mess_notfound.visibility = View.GONE
                adapter?.productList = productGive
                adapter?.notifyDataSetChanged()
            } else {
                product_list.visibility = View.GONE
                mess_notfound.visibility = View.VISIBLE
            }
        }
        layout_need.setOnClickListener {
            tv_give.setTextColor(resources.getColor(R.color.text_light))
            tv_need.setTextColor(resources.getColor(R.color.actionBarColor))
            tv_gived.setTextColor(resources.getColor(R.color.text_light))
            tv_needed.setTextColor(resources.getColor(R.color.text_light))
            if (productNeed.size != 0) {
                product_list.visibility = View.VISIBLE
                mess_notfound.visibility = View.GONE
                adapter?.productList = productNeed
                adapter?.notifyDataSetChanged()
            } else {
                product_list.visibility = View.GONE
                mess_notfound.visibility = View.VISIBLE
            }
        }
        //    detailprofile!!.ConnectHttp(AppManager.getAppAccountUserId(activity))


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater!!.inflate(R.layout.detailprofile, container, false)
    }

    override fun setErrorMessage(errorMessage: String) {

    }

    override fun getListProduct(productlist: ArrayList<Product>, user: User) {

//        getUser(user)
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
        Log.d("SearchTag", productlist.size.toString() + " give" + productGive.size + " need" + productNeed.size)

        tv_number_give.text = numGive.toString()
        tv_number_need.text = numNeed.toString()
        product_list.visibility = View.VISIBLE
        if (productGive.size != 0) {
            mess_notfound.visibility = View.GONE
            adapter?.productList = productGive
            adapter?.notifyDataSetChanged()
        } else {
            product_list.visibility = View.GONE
            mess_notfound.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        tv_give.setTextColor(resources.getColor(R.color.colorAccent))
        tv_need.setTextColor(resources.getColor(R.color.text_light))
        tv_gived.setTextColor(resources.getColor(R.color.text_light))
        tv_needed.setTextColor(resources.getColor(R.color.text_light))
        detailprofile!!.ConnectHttp(AppManager.getAppAccountUserId(activity))
    }

    override fun getUser(user: User) {
        root_addproduct.visibility = View.VISIBLE
        product_list.visibility = View.GONE
        mess_notfound.visibility = View.VISIBLE

        if (user.listproduct!!.size > 0) {
            getListProduct(user.listproduct!!, user)
        }
    }


    override fun onDestroy() {
        detailprofile?.cancelRequest()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 909) {
            if (resultCode == 0) {
                Snackbar.make(activity.findViewById(R.id.root_addproduct), "Delete Product successfully", Snackbar.LENGTH_SHORT).show()
            } else if (resultCode == 1) {
                Snackbar.make(activity.findViewById(R.id.root_addproduct), "Edit Product successfully", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onproductClicked(position: Int) {
        var mtype = 0
        val intent = Intent(context, EditProductActivity::class.java)
        if (adapter!!.productList[position].type!!.equals("2")) {
            mtype = 2
        } else {
            mtype = 1
            intent.putExtra("imglist", adapter!!.productList[position].images)
        }
        intent.putExtra("type", mtype)
        intent.putExtra("data", adapter!!.productList[position])
        startActivityForResult(intent, 909)

    }

}
