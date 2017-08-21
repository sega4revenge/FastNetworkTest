package sega.fastnetwork.test.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.product_item_compact.view.*
import kotlinx.android.synthetic.main.product_item_grid.view.*
import kotlinx.android.synthetic.main.product_item_list.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.util.Constants
import java.text.DecimalFormat
import java.util.*

/**
 * Created by sega4 on 07/08/2017.
 */
class ProductAdapter// Constructor
(private val context: Context, private val onproductClickListener: OnproductClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val format: String
    private val sharedPref: SharedPreferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
    var productList: ArrayList<Product>


    private val imageWidth: Int
    internal var formatprice: DecimalFormat? = DecimalFormat("#0,000");

    init {
        imageWidth = sharedPref.getInt(Constants.THUMBNAIL_SIZE,
                0)   // Load image width for grid view
        productList = ArrayList<Product>()

        val current = Locale("vi", "VN")
        val cur = Currency.getInstance(current)
        format = cur.symbol

    }

    // RecyclerView methods
    override fun getItemCount(): Int {

        return productList.size
    }

    override fun getItemViewType(position: Int): Int {
        return sharedPref.getInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == Constants.VIEW_MODE_GRID) {
            // GRID MODE
            val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.product_item_grid, parent, false) as ViewGroup
            val viewTreeObserver = v.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                // Update width integer and save to storage for next use
                                val width = v.findViewById(R.id.product_poster_grid).width
                                if (width > imageWidth) {
                                    val editor = sharedPref.edit()
                                    editor.putInt(Constants.THUMBNAIL_SIZE, width)
                                    editor.apply()
                                }
                                // Unregister LayoutListener
                                if (Build.VERSION.SDK_INT >= 16) {
                                    v.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                } else {
                                    v.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                }
                            }
                        })
            }
            return ProductGridViewHolder(v)
        } else if (viewType == Constants.VIEW_MODE_LIST) {
            // LIST MODE
            val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.product_item_list, parent, false) as ViewGroup
            return ProductListViewHolder(v)
        } else {
            // COMPACT MODE
            val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.product_item_compact, parent, false) as ViewGroup
            return ProductCompactViewHolder(v)
        }
        // GRID MODE
        /*  val v = LayoutInflater.from(parent.context).inflate(
                  R.layout.category_item_list, null)
          return ProductGridViewHolder(v)*/

    }



    override fun onBindViewHolder(viewHolderParent: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(0)
        val product = productList[position]

        // GRID MODE
        if (viewType == Constants.VIEW_MODE_GRID) {
            // GRID MODE
            val viewHolder = viewHolderParent as ProductGridViewHolder
            viewHolder.itemView.product_name_grid.text = product.productname
            val temp = formatprice?.format(product.price?.toDouble()) + format
            viewHolder.itemView.price_grid.text = temp
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.img_error)
                    .priority(Priority.HIGH)
            Glide.with(context)
                    .load(Constants.BASE_URL + "?image=" + product.images?.get(0))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(viewHolder.itemView.product_poster_grid)

            //            }
        } else if (viewType == Constants.VIEW_MODE_LIST) {
            val viewHolder = viewHolderParent as ProductListViewHolder
            // LIST MODE
            viewHolder.itemView.product_name_list.text = product.productname
            viewHolder.itemView.price_list.text = product.number
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.img_error)
                    .priority(Priority.HIGH)
            Glide.with(context)
                    .load(Constants.BASE_URL + "?image=" + product.images?.get(0))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(viewHolder.itemView.product_poster_list)
        } else {
            // COMPACT MODE
            val viewHolder = viewHolderParent as ProductCompactViewHolder
            viewHolder.itemView.product_name_compact.text = product.productname
            viewHolder.itemView.timepost.text = product.created_at
            val timeAgo = DateUtils.getRelativeTimeSpanString(
                    java.lang.Long.parseLong(product.created_at),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
            viewHolder.itemView.timepost.text = timeAgo
            viewHolder.itemView.price_compact.text = product.price
            viewHolder.itemView.userpost.text = product.user!!.name
            viewHolder.itemView.area_compact.text = product.address
            viewHolder.itemView.area_compact.isSelected = true



        }


        /* viewHolder.itemView.category_name.text = product.name
         viewHolder.itemView.category_number.text = product.number
         viewHolder.itemView.category_line.setBackgroundResource(product.color)
         println(product.color)*/
        // Title and year

        //                productViewHolder.productRating.setText(product.price+"");
        //            }
        viewHolderParent.itemView.setOnClickListener { onproductClickListener.onproductClicked(position)}
    }


    // ViewHolders
    inner class ProductGridViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class ProductListViewHolder(view: View) : RecyclerView.ViewHolder(view)
    inner class ProductCompactViewHolder(view: View) : RecyclerView.ViewHolder(view)
    // Click listener interface
    interface OnproductClickListener {
        fun onproductClicked(position: Int)
    }

}