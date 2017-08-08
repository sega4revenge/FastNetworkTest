package sega.fastnetwork.test.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.category_item_list.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.model.Category
import java.util.*

/**
 * Created by sega4 on 07/08/2017.
 */
class CategoryAdapter// Constructor
(private val context: Context, private val onproductClickListener: OnproductClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var productList: ArrayList<Category>


    init {

       productList = ArrayList<Category>()
      productList.add(Category("Xe cộ","25",R.color.menu_background))
        productList.add(Category("Đồ chơi","40",R.color.abc_tint_spinner))
        productList.add(Category("Điện tử","105",R.color.md_styled_accent))
        productList.add(Category("Gia dụng","6",R.color.md_styled_primary))
        productList.add(Category("Thời trang","216",R.color.dot_light_screen1))
        productList.add(Category("Nhà cửa","42",R.color.dot_light_screen2))
        productList.add(Category("Vợ","15",R.color.dot_light_screen3))
        productList.add(Category("Người yêu","8",R.color.dot_light_screen4))
    }

    // RecyclerView methods
    override fun getItemCount(): Int {

        return productList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductGridViewHolder {


        // GRID MODE
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.category_item_list, null)
        return ProductGridViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(0)
        val product = productList[position]

        // GRID MODE





        viewHolder.itemView.category_name.text = product.name
        viewHolder.itemView.category_number.text = product.number
        viewHolder.itemView.category_line.setBackgroundResource(product.color)
        println(product.color)
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