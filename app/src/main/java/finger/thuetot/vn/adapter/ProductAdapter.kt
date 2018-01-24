package finger.thuetot.vn.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import finger.thuetot.vn.R
import finger.thuetot.vn.customview.SlantedTextView
import finger.thuetot.vn.lib.ShimmerRecycleView.OnLoadMoreListener
import finger.thuetot.vn.lib.ShimmerRecycleView.ShimmerViewHolder
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.util.Constants
import kotlinx.android.synthetic.main.layout_demo_grid.view.*
import kotlinx.android.synthetic.main.product_item_compact.view.*
import java.text.DecimalFormat
import java.util.*

/**
 * Created by sega4 on 07/08/2017.
 */
internal class ProductAdapter// Constructor
(private val context: Context, private val onproductClickListener: OnproductClickListener, mRecyclerView: RecyclerView, linnerlayout: LinearLayoutManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    var productList: ArrayList<Product> = ArrayList()
    var isLoading: Boolean = false
    var isLoadingLocked: Boolean = false
    private var lastVisibleItem: LinearLayoutManager = linnerlayout
    private var totalItemCount: Int = 0
    var pageToDownload: Int = 0
    private val TOTAL_PAGES = 999
    internal var formatprice: DecimalFormat? = null
    var user: String? = null

    private var format: String?

    init {

        formatprice = DecimalFormat("###,###,###")
        val current = Locale("vi", "VN")
        val cur = Currency.getInstance(current)
        format = cur.symbol
      //  println(mRecyclerView.layoutManager)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = lastVisibleItem.findLastVisibleItemPosition()

           //     Log.d("aaaaaaaaaa","asd"+totalItemCount+" "+totalItemCount+" "+productList.size)
                if (!isLoading && !isLoadingLocked && totalItemCount == productList.size -1 ) {

                    if (mOnLoadMoreListener != null && pageToDownload < TOTAL_PAGES) {
                        mOnLoadMoreListener!!.onLoadMore()
                        println(pageToDownload.toString() + " tong so")
                    }
                    isLoading = true

                }
            }
        })
    }

    // RecyclerView methods
    override fun getItemCount(): Int = productList.size

    override fun getItemViewType(position: Int): Int =
            if (productList[position].productname.isNullOrEmpty()) Constants.VIEW_MODE_LIST else Constants.VIEW_MODE_GRID


    override fun onCreateViewHolder(parent: ViewGroup, mviewType: Int): RecyclerView.ViewHolder {

        return if (mviewType == Constants.VIEW_MODE_LIST) {

            val inflater = LayoutInflater.from(parent.context)
            val shimmerViewHolder = ShimmerViewHolder(inflater, parent, R.layout.layout_demo_grid)
            shimmerViewHolder.setShimmerColor(R.color.shimmer_color)
            shimmerViewHolder.setShimmerAngle(20)
            shimmerViewHolder.setShimmerViewHolderBackground(context.resources.getDrawable(R.drawable.bg_card))
            shimmerViewHolder
        } else {
            val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.product_item_compact, parent, false) as ViewGroup
            ProductCompactViewHolder(v)
        }


    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }


    override fun onBindViewHolder(viewHolderParent: RecyclerView.ViewHolder, position: Int) {

        if (viewHolderParent is ShimmerViewHolder) {


            viewHolderParent.itemView.shimmer_layout.startShimmerAnimation()


        } else if (viewHolderParent is ProductCompactViewHolder) {
            val product = productList[position]
            if (product.type.equals("1")) {
                viewHolderParent.itemView.product_name_compact.text = product.productname
            } else {
                viewHolderParent.itemView.product_name_compact.text = product.productname
            }

            viewHolderParent.itemView.timepost.text = product.created_at
            if (product.created_at != null) {
                val timeAgo = DateUtils.getRelativeTimeSpanString(
                        java.lang.Long.parseLong(product.created_at),
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
                viewHolderParent.itemView.timepost.text = timeAgo
            }

            /*    if (!product.price.isNullOrEmpty())
                {
                    viewHolderParent.itemView.price_compact.text = product.price!!
                    println(formatprice!!.format(product.price!!.toInt()) + format + " cho " + context.resources.getStringArray(R.array.timeid)[product.time!!.toInt()] + " a " + product.productname)
                }*/
            if (product.user?.name.isNullOrEmpty())
                viewHolderParent.itemView.userpost.text = user
            else
            viewHolderParent.itemView.userpost.text = product.user!!.name
            viewHolderParent.itemView.area_compact.isSelected = true
            try{
                var num = product.location!!.address?.split(", ")
                if(num?.size!! > 2){
                    viewHolderParent.itemView.area_compact.text = num[(num?.size-2)]
                }else{
                    viewHolderParent.itemView.area_compact.text = num[0]
                }
            }catch (e: Exception){

            }

            if(product.status == "0"){

            if (product.type == "1") {

                viewHolderParent.itemView.type_view.setText(R.string.rent)
                        .setTextColor(Color.WHITE)
                        .setSlantedBackgroundColor(context.resources.getColor(R.color.btn_login))
                        .setTextSize(15)
                        .setSlantedLength(60).mode = SlantedTextView.MODE_RIGHT_TRIANGLE
                viewHolderParent.itemView.price_compact.text = formatprice!!.format(product.price!!.toDouble()) + format + " "+context.getString(R.string.txt_per)+" " + context.resources.getStringArray(R.array.timeid)[product.time!!.toInt()]
            } else {
                viewHolderParent.itemView.type_view.setText(R.string.need_rent)
                        .setTextColor(Color.WHITE)
                        .setSlantedBackgroundColor(context.resources.getColor((R.color.actionBarColor)))
                        .setTextSize(15)
                        .setSlantedLength(60).mode = SlantedTextView.MODE_RIGHT_TRIANGLE
                viewHolderParent.itemView.price_compact.text = ""
            }
            } else {
                viewHolderParent.itemView.type_view.setText(R.string.need_rent1)
                        .setTextColor(Color.WHITE)
                        .setSlantedBackgroundColor(context.resources.getColor((R.color.wasRented)))
                        .setTextSize(15)
                        .setSlantedLength(60).mode = SlantedTextView.MODE_RIGHT_TRIANGLE
                viewHolderParent.itemView.price_compact.text = ""
            }
            when (product.category) {
                "0" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_vehicle)
                "1" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_electronic)
                "2" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_fashion)
                "3" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_home)
                "4" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_toy)
                "5" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_furniture)
                "6" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cup)
                "7" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.printer)
                "8" -> viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_more)
                else -> { // Note the block
                    viewHolderParent.itemView.product_poster_compact.setImageResource(R.drawable.cate_more)
                }
            }

        }
        viewHolderParent.itemView.setOnClickListener {
            onproductClickListener.onproductClicked(position)
        }


    }


    inner class ProductCompactViewHolder(view: View) : RecyclerView.ViewHolder(view)
    // Click listener interface
    interface OnproductClickListener {
        fun onproductClicked(position: Int)
    }

    fun initShimmer() {
        Collections.addAll(productList, Product(), Product(), Product())
    }


}