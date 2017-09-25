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
import sega.fastnetwork.test.adapter.Product_ProfileAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailProfilePressenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.DetailProfileView
import java.util.*

class DetailProfile_Activity : Activity(), DetailProfileView, Product_ProfileAdapter.OnproductClickListener {

    var mUser: User? = null
    var detailprofile: DetailProfilePressenter? = null
    private var adapter: Product_ProfileAdapter? = null
    var productGive_ct: ArrayList<Product> = ArrayList<Product>()
    var productNeed_ct: ArrayList<Product> = ArrayList<Product>()
    private var layoutManager: GridLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_profile)

        // PUT_EXTRA
        mUser = intent.getParcelableExtra("data")

        // USER
        txtname.setText(mUser!!.name)
        txtemail.setText(mUser!!.email)
        LinGive_ct.setOnClickListener{
            if(productGive_ct.size!= 0)
            {
                product_list_ct.visibility = View.VISIBLE
                mess_notfound_ct.visibility = View.GONE
                adapter?.productList = productGive_ct
                adapter?.notifyDataSetChanged()
            }else{
                product_list_ct.visibility = View.GONE
                mess_notfound_ct.visibility = View.VISIBLE
            }
        }
        LinNeed_ct.setOnClickListener{
            if(productNeed_ct.size!= 0)
            {
                product_list_ct.visibility = View.VISIBLE
                mess_notfound_ct.visibility = View.GONE
                adapter?.productList = productNeed_ct
                adapter?.notifyDataSetChanged()
            }else{
                product_list_ct.visibility = View.GONE
                mess_notfound_ct.visibility = View.VISIBLE
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
        adapter = Product_ProfileAdapter(this.applicationContext, this)
        layoutManager = GridLayoutManager(application, 1)
        product_list_ct.setHasFixedSize(true)
        product_list_ct.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_list_ct.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        product_list_ct.adapter = adapter
        detailprofile = DetailProfilePressenter(this)
        detailprofile!!.ConnectHttp(mUser!!._id!!)


    }


    override fun getListProduct(productlist: ArrayList<Product>, user: User) {
        //        getUser(user)
        if (adapter!!.productList.size > 0) {
            adapter!!.productList.clear()
        }
        if (productGive_ct.size != 0) {
            productGive_ct.clear()
        }
        if (productNeed_ct.size != 0) {
            productNeed_ct.clear()
        }
        var numGive = 0
        var numNeed = 0
        for (i in productlist) {
            if (i.type?.toInt() == 1) {
                productGive_ct.add(i)
                numGive++
            } else {
                productNeed_ct.add(i)
                numNeed++
            }
        }
        Log.d("SearchTag", productlist.size.toString() + " give" + productGive_ct.size + " need" + productNeed_ct.size)
        adapter!!.mUser = user
        lent_give_ct.text = numGive.toString()
        lent_need_ct.text = numNeed.toString()
        product_list_ct.visibility = View.VISIBLE
        if (productGive_ct.size != 0) {
            mess_notfound_ct.visibility = View.GONE
            adapter?.productList = productGive_ct
            adapter?.notifyDataSetChanged()
        } else {
            product_list_ct.visibility = View.GONE
            mess_notfound_ct.visibility = View.VISIBLE
        }

    }

    override fun getUser(user: User) {
        root_addproduct_ct.visibility = View.VISIBLE
        product_list_ct.visibility = View.GONE
        mess_notfound_ct.visibility = View.VISIBLE

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
        startActivity(intent)
    }


}

