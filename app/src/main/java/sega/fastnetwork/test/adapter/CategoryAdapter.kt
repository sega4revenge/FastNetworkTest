package sega.fastnetwork.test.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.category_item_list.view.*
import sega.fastnetwork.test.R
import java.text.DecimalFormat
import java.util.*

/**
 * Created by sega4 on 07/08/2017.
 */
class CategoryAdapter// Constructor
(private val context: Context, private val onproductClickListener: OnproductClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val format: String

    var productList: ArrayList<String>
    internal var formatprice: DecimalFormat

    init {
        println("chay1")
       productList = ArrayList<String>()
        productList.add("abc")
       productList.add("abc2")
       productList.add("abc3")
       productList.add("abc4")
        productList.add("abc5")
        productList.add("abc6")
        formatprice = DecimalFormat("#0,000")
        val current = Locale("vi", "VN")
        val cur = Currency.getInstance(current)
        format = cur.symbol
    }

    // RecyclerView methods
    override fun getItemCount(): Int {
        println(productList.size)
        return productList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductGridViewHolder {

        println("cha3")
        // GRID MODE
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.category_item_list, null)
        return ProductGridViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(0)
        val product = productList[position]
        println("chay2")
        // GRID MODE





        viewHolder.itemView.product_name.text = "abc"
        // Title and year

        //                productViewHolder.productRating.setText(product.price+"");
        //            }
    }


    // ViewHolders
    inner class ProductGridViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Click listener interface
    interface OnproductClickListener {
        fun onproductClicked(position: Int)
    }
}