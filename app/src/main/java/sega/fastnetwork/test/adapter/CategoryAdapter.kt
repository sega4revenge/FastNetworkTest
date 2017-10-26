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
(context: Context, private val oncategoryClickListener: OncategoryClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var categoryList: ArrayList<Category> = ArrayList()


    init {

        categoryList.add(Category(0,context.getString(R.string.cate_vehicle), context.resources.getDrawable(R.drawable.cate_vehicle), false))
        categoryList.add(Category(1,context.getString(R.string.cate_electronic), context.resources.getDrawable(R.drawable.cate_electronic), false))
        categoryList.add(Category(2,context.getString(R.string.cate_fashion), context.resources.getDrawable(R.drawable.cate_fashion), false))
        categoryList.add(Category(3,context.getString(R.string.cate_house), context.resources.getDrawable(R.drawable.cate_home), false))
        categoryList.add(Category(4,context.getString(R.string.cate_motherandbaby), context.resources.getDrawable(R.drawable.mother), false))
        categoryList.add(Category(5,context.getString(R.string.cate_furniture), context.resources.getDrawable(R.drawable.cate_furniture), false))
        categoryList.add(Category(6,context.getString(R.string.cate_entertaiment), context.resources.getDrawable(R.drawable.cup), false))
        categoryList.add(Category(7,context.getString(R.string.cate_office), context.resources.getDrawable(R.drawable.printer), false))
        categoryList.add(Category(8,context.getString(R.string.cate_other), context.resources.getDrawable(R.drawable.cate_more), false))
    }

    // RecyclerView methods
    override fun getItemCount(): Int = categoryList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductGridViewHolder {


        // GRID MODE
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.category_item_list, null)
        return ProductGridViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val category = categoryList[position]
        viewHolder.itemView.category_name.text = category.name
        viewHolder.itemView.category_icon.background = category.avatar
        // GRID MODE
        if (category.selected) {
            viewHolder.itemView.selected.visibility = View.VISIBLE


        } else {
            viewHolder.itemView.selected.visibility = View.GONE


        }



        viewHolder.itemView.setOnClickListener { oncategoryClickListener.oncategoryClicked(position, viewHolder.itemView) }
    }


    // ViewHolders
    inner class ProductGridViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Click listener interface
    interface OncategoryClickListener {
        fun oncategoryClicked(position: Int, view: View)
    }
}