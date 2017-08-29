package sega.fastnetwork.test.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.detailprofile.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.EditProductActivity
import sega.fastnetwork.test.adapter.Product_ProfileAdapter
import sega.fastnetwork.test.customview.DividerItemDecoration
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DetailProfilePressenter
import sega.fastnetwork.test.view.DetailProfileView
/**
 * Created by VinhNguyen on 8/22/2017.
 */
class DetailProfileFragment: Fragment() ,DetailProfileView ,Product_ProfileAdapter.OnproductClickListener {

    private var layoutManager: GridLayoutManager? = null
    private var adapter: Product_ProfileAdapter? = null
    var detailprofile : DetailProfilePressenter? = null
    var productGive : ArrayList<Product> = ArrayList<Product>()
    var productNeed : ArrayList<Product> = ArrayList<Product>()
    var numType = 0
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = Product_ProfileAdapter(activity, this)
        layoutManager = GridLayoutManager(context, 1)
        product_list.setHasFixedSize(true)
        product_list.layoutManager = (layoutManager as RecyclerView.LayoutManager?)!!
        product_list.addItemDecoration(DividerItemDecoration(R.color.category_divider_color, 3))
        product_list.adapter = adapter
        detailprofile = DetailProfilePressenter(this)
        detailprofile!!.ConnectHttp(AppManager.getAppAccountUserId(activity))
        LinGive.setOnClickListener{
            lent_need.setTextColor(resources.getColor(R.color.black))
            lent_give.setTextColor(resources.getColor(R.color.white))
            LinGive.setBackgroundColor(resources.getColor(R.color.menu_background))
            LinNeed.setBackgroundColor(resources.getColor(R.color.white))
            if(productGive.size!= 0)
            {
                product_list.visibility = View.VISIBLE
                mess_notfound.visibility = View.GONE
                adapter?.productList = productGive
                adapter?.notifyDataSetChanged()
            }else{
                product_list.visibility = View.GONE
                mess_notfound.visibility = View.VISIBLE
            }
        }
        LinNeed.setOnClickListener{
            lent_give.setTextColor(resources.getColor(R.color.black))
            lent_need.setTextColor(resources.getColor(R.color.white))
            LinNeed.setBackgroundColor(resources.getColor(R.color.menu_background))
            LinGive.setBackgroundColor(resources.getColor(R.color.white))
            if(productNeed.size!= 0)
            {
                adapter?.productList = productNeed
                adapter?.notifyDataSetChanged()
            }else{
                product_list.visibility = View.GONE
                mess_notfound.visibility = View.VISIBLE
            }
        }


    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.detailprofile, container, false)
        return view
    }

    override fun setErrorMessage(errorMessage: String) {

    }

    override fun getListProduct(productlist: ArrayList<Product>,user: User) {
        getUser(user)
        if (adapter!!.productList.size > 0) {
            adapter!!.productList.clear()
            Log.d("test", "clear")
        }
        var numGive = 0
        var numNeed = 0
        for(i in productlist )
        {
            if(i.type?.toInt() == 1)
            {
                productGive.add(i)
                numGive++
            }else{
                productNeed.add(i)
                numNeed++
            }
        }
        adapter!!.mUser = user
        lent_give.text = numGive.toString()
        lent_need.text = numNeed.toString()
        product_list.visibility = View.VISIBLE
        if(productGive.size!= 0)
        {
            adapter?.productList = productGive
            adapter?.notifyDataSetChanged()
        }else{
            product_list.visibility = View.GONE
            mess_notfound.visibility = View.VISIBLE
        }

    }
    override fun getUser(user: User) {
        product_list.visibility = View.GONE
        mess_notfound.visibility = View.VISIBLE
        txt_name.text = user.name
        txt_mail.text = user.email
        if(user.photoprofile != null) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.img_error)
                    .priority(Priority.HIGH)
            Glide.with(activity)
                    .load(user.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(avatar_header)
        }else{
            Glide.with(activity)
                    .load(R.drawable.img_error)
                    .thumbnail(0.1f)
                    .into(avatar_header)
        }
    }
    override fun setMessagerNotFound() {

    }
    override fun onproductClicked(position: Int) {
        val intent = Intent(context, EditProductActivity::class.java)
        intent.putExtra("data",adapter!!.productList[position])
        intent.putExtra("imglist",adapter!!.productList[position].images)
        Log.d("tttttt2", adapter!!.productList[position].images?.size.toString())
        startActivity(intent)
    }
//    fun refresh() {
//
//        val fragment = childFragmentManager.findFragmentByTag(getFragmentTag(R.id.pager, 0)) as ProductListFragment
//        val fragment2 = childFragmentManager.findFragmentByTag(getFragmentTag(R.id.pager, 1)) as ProductListFragment
//        fragment.refreshLayout()
//        fragment2.refreshLayout()
//    }
//
//    private fun getFragmentTag(viewPagerId: Int, fragmentPosition: Int): String {
//        return "android:switcher:$viewPagerId:$fragmentPosition"
//    }
//
//    internal inner class TabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
//
//        override fun getCount(): Int {
//            return 2
//        }
//
//        override fun getItem(i: Int): Fragment? {
//            when (i) {
//                0 -> return ProductListFragment()
//                1 -> return ProductListFragment()
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
