package sega.fastnetwork.test.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_comments.view.*
import kotlinx.android.synthetic.main.product_item_compact.view.*
import kotlinx.android.synthetic.main.toolbar_twoline.view.*

import sega.fastnetwork.test.R
import sega.fastnetwork.test.model.Comment
import sega.fastnetwork.test.util.Constants
import java.text.DecimalFormat
import java.util.*

/**
 * Created by cc on 8/16/2017.
 */
class CommentAdapter// Constructor
(private val context: Context, private val oncommentClickListener: OncommentClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val format: String
    private val sharedPref: SharedPreferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
    var commentsList: ArrayList<Comment>

    private val imageWidth: Int
    internal var formatprice: DecimalFormat? = DecimalFormat("#0,000");

    init {
        imageWidth = sharedPref.getInt(Constants.THUMBNAIL_SIZE,
                0)   // Load image width for grid view
        commentsList = ArrayList<Comment>()

        val current = Locale("vi", "VN")
        val cur = Currency.getInstance(current)
        format = cur.symbol

    }

    // RecyclerView methods
    override fun getItemCount(): Int {

        return commentsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return sharedPref.getInt(Constants.VIEW_MODE, Constants.VIEW_MODE_GRID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_comments, parent, false) as ViewGroup
            return CommentsViewHolder(v)
        
       

    }

    override fun onBindViewHolder(viewHolderParent: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(0)
        val comment = commentsList[position]
        

     
            // COMPACT MODE
            val viewHolder = viewHolderParent as CommentsViewHolder
        viewHolder.itemView.commentname.text = comment.user!!.name
            viewHolder.itemView.commentdata.text = comment.content
        viewHolder.itemView.commentdate.text = timeAgo(comment.time!!)
        val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.img_error)
                    .priority(Priority.HIGH)
            Glide.with(context)
                    .load(comment.user!!.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(viewHolder.itemView.commentpic)



        


        /* viewHolder.itemView.category_name.text = comment.name
         viewHolder.itemView.category_number.text = comment.number
         viewHolder.itemView.category_line.setBackgroundResource(comment.color)
         println(comment.color)*/
        // Title and year

        //                commentViewHolder.commentRating.setText(comment.price+"");
        //            }
        viewHolderParent.itemView.setOnClickListener { oncommentClickListener.oncommentClicked(position)}
    }


    // ViewHolders
  
    inner class CommentsViewHolder(view: View) : RecyclerView.ViewHolder(view)
    // Click listener interface
    interface OncommentClickListener {
        fun oncommentClicked(position: Int)
    }
    private fun timeAgo(time: String): CharSequence? {
        var time = DateUtils.getRelativeTimeSpanString(
                java.lang.Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
        return time
    }

}