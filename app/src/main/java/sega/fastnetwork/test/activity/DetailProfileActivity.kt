package sega.fastnetwork.test.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_profile.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.ProductAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailProfilePressenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.DetailProfileView
import java.util.*

class DetailProfileActivity : Activity(), DetailProfileView, ProductAdapter.OnproductClickListener {

    var mUser: User? = null
    var detailprofile: DetailProfilePressenter? = null
    private var adapter: ProductAdapter? = null
    var productGive: ArrayList<Product> = ArrayList()
    var productNeed: ArrayList<Product> = ArrayList()
    private var layoutManager: GridLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_profile)

        // PUT_EXTRA
        mUser = intent.getParcelableExtra("data")

        // USER
        txtname.text = mUser!!.name
        txtemail.text = mUser!!.email
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

        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
        Glide.with(this).load(mUser?.photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(imgAvatar)

        layoutManager = GridLayoutManager(application, 1)
        product_list.setHasFixedSize(true)
        product_list.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_list.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        adapter = ProductAdapter(this.applicationContext, this,product_list, layoutManager!!)
        adapter!!.user = mUser!!.name
        product_list.adapter = adapter
        detailprofile = DetailProfilePressenter(this)
        detailprofile!!.ConnectHttp(mUser!!._id!!)


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

    override fun getUser(user: User) {
        root_addproduct.visibility = View.VISIBLE
        product_list.visibility = View.GONE
        mess_notfound.visibility = View.VISIBLE

        if (user.listproduct!!.size > 0) {
            getListProduct(user.listproduct!!, user)
        }
    }

    override fun setErrorMessage(errorMessage: String) {
    }

    override fun setMessagerNotFound() {
    }

    override fun onproductClicked(position: Int) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra(Constants.product_ID, adapter!!.productList[position]._id!!)
        intent.putExtra(Constants.seller_ID, adapter!!.productList[position].user!!._id)
        startActivity(intent)
    }


}

