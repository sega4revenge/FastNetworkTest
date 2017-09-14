package sega.fastnetwork.test.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.product_item_compact.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants
import java.text.DecimalFormat
import java.util.*

/**
 * Created by VinhNguyen on 8/24/2017.
 */
class Product_ProfileAdapter
(private val context: Context, private val onproductClickListener: OnproductClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val format: String
    private val sharedPref: SharedPreferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
    var productList: ArrayList<Product>
    var mUser: User
    private val imageWidth: Int
    internal var formatprice: DecimalFormat? = DecimalFormat("#0,000");

    init {
        imageWidth = sharedPref.getInt(Constants.THUMBNAIL_SIZE,
                0)
        productList = ArrayList<Product>()
        mUser = User()
        val current = Locale("vi", "VN")
        val cur = Currency.getInstance(current)
        format = cur.symbol

    }


    override fun getItemCount(): Int {
        return productList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, mviewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.product_item_compact, parent, false) as ViewGroup
            return ProductCompactViewHolder(v)
    }



    override fun onBindViewHolder(viewHolderParent: RecyclerView.ViewHolder, position: Int) {
        val product = productList[position]
            // COMPACT MODE
            val viewHolder = viewHolderParent as ProductCompactViewHolder
            viewHolder.itemView.product_name_compact.text = product.productname
            viewHolder.itemView.timepost.text = product.created_at
            val timeAgo = DateUtils.getRelativeTimeSpanString(
                    java.lang.Long.parseLong(product.created_at),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
            viewHolder.itemView.timepost.text = timeAgo
            viewHolder.itemView.price_compact.text = product.price
            viewHolder.itemView.userpost.text = mUser.name
            viewHolder.itemView.area_compact.text = product.location!!.address
            viewHolder.itemView.area_compact.isSelected = true
        viewHolderParent.itemView.setOnClickListener { onproductClickListener.onproductClicked(position)}
    }



    inner class ProductCompactViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnproductClickListener {
        fun onproductClicked(position: Int)
    }

}