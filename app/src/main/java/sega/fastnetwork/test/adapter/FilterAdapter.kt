package sega.fastnetwork.test.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_filter.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.model.Filter
import java.util.*

/**
 * Created by Sega on 21/09/2017.
 */
class FilterAdapter(val context: Context) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {
    private var selected: Int = 0
    private val filters = ArrayList<Filter>()


    init {
        filters.add(Filter("Mới nhất","Sắp xếp sản phẩm theo ngày đăng gần nhất",R.drawable.price_icon))
        filters.add(Filter("Phổ biến nhất","Sắp xếp sản phẩm theo lượt xem nhiều nhất",R.drawable.price_icon))
        filters.add(Filter("Giá thấp nhất","Sắp xếp sản phẩm theo giá thuê đồ thấp nhất",R.drawable.price_icon))
        filters.add(Filter("Giá cao nhất","Sắp xếp sản phẩm theo giá thuê đồ caonhất",R.drawable.price_icon))

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, null)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {

        if (selected == position) {
            holder.itemView.photo_gallery_select.setImageResource(R.drawable.select_icon)
        } else {
            holder.itemView.photo_gallery_select.setImageBitmap(null)
        }
        holder.itemView.label.text = getItem(position).label
        holder.itemView.description.text = getItem(position).description

        Glide.with(context).load(getItem(position).icon).into(holder.itemView.imageView)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (holder.itemView.id == R.id.photo_gallery_rl) {
                if (onItemClickListener != null) {
                    changeSelect(position)
                    onItemClickListener!!.onClick(position)
                }
            }
        }


    }
    override fun getItemCount(): Int = filters.size

    fun getItem(position: Int): Filter = this.filters[position]

    private fun changeSelect(position: Int) {
        this.selected = position
        notifyDataSetChanged()
    }

    class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view)


    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

}
