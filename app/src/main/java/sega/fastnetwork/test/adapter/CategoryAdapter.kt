package sega.fastnetwork.test.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
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
(private val context: Context, private val oncategoryClickListener: OncategoryClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var categoryList: ArrayList<Category> = ArrayList()


    init {

        categoryList.add(Category("Xe cộ","25",R.drawable.cate_vehicle,false))
        categoryList.add(Category("Đồ chơi","216",R.drawable.cate_toy,false))
        categoryList.add(Category("Điện tử","40",R.drawable.cate_electronic,false))
        categoryList.add(Category("Đồ gia dụng,nội thất","42",R.drawable.cate_furniture,false))
        categoryList.add(Category("Thời trang","105",R.drawable.cate_fashion,false))
        categoryList.add(Category("Nhà","6",R.drawable.cate_home,false))
        categoryList.add(Category("Giáo dục","15",R.drawable.cate_education,false))
        categoryList.add(Category("Giải trí","15",R.drawable.cate_music,false))
        categoryList.add(Category("Khác","8",R.drawable.cate_more,false))
    }

    // RecyclerView methods
    override fun getItemCount(): Int = categoryList.size
    fun refreshFilter(){
        for (tv in categoryList) {
            tv.selected = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductGridViewHolder {


        // GRID MODE
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.category_item_list, null)
        return ProductGridViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val category = categoryList[position]

        // GRID MODE
        if(category.selected)
        {
            viewHolder.itemView.selected.visibility = View.VISIBLE
          //  viewHolder.itemView.category_card.setBackgroundResource(R.drawable.category_selected)
            viewHolder.itemView.category_name.text = category.name
            viewHolder.itemView.category_card.category_name.setTextColor(ContextCompat.getColor(context, R.color.black))
/*            viewHolder.itemView.category_line.setBackgroundResource(category.color)*/
        }
        else
        {
            viewHolder.itemView.selected.visibility = View.GONE
          //  viewHolder.itemView.category_card.setBackgroundResource(R.drawable.category_unselected)
            viewHolder.itemView.category_name.text = category.name
            viewHolder.itemView.category_card.category_name.setTextColor(ContextCompat.getColor(context, R.color.black))
        /*    viewHolder.itemView.category_line.setBackgroundResource(category.color)*/

        }




        // Title and year

        //                categoryViewHolder.categoryRating.setText(category.price+"");
        //            }
        viewHolder.itemView.setOnClickListener { oncategoryClickListener.oncategoryClicked(position,viewHolder.itemView) }
    }


    // ViewHolders
    inner class ProductGridViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Click listener interface
    interface OncategoryClickListener {
        fun oncategoryClicked(position: Int,view : View)
    }
}