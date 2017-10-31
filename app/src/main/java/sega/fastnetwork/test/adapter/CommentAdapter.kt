package sega.fastnetwork.test.adapter


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_comments.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.fragment.ProifileSellerFragment
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Comment
import sega.fastnetwork.test.util.Constants
import java.lang.Long
import java.text.DecimalFormat
import java.util.*

/**
 * Created by cc on 8/16/2017.
 */
class CommentAdapter// Constructor
(private val context: Context, private val oncommentClickListener: OncommentClickListener,fragment: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val format: String
    private val sharedPref: SharedPreferences = context.getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE)
    var commentsList: ArrayList<Comment>
    var photoprofile : String? = null
    var mFragmentManager: FragmentManager = fragment
    private val imageWidth: Int
    internal var formatprice: DecimalFormat? = DecimalFormat("#0,000")

    init {
        imageWidth = sharedPref.getInt(Constants.THUMBNAIL_SIZE,
                0)   // Load image width for grid view
        commentsList = ArrayList()

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
        viewHolder.itemView.email_comment.text = comment.user?.email
        viewHolder.itemView.commentdate.text = timeAgo(comment.time!!)
        if(comment.user?.photoprofile?.startsWith("http")!!){
            photoprofile = comment.user!!.photoprofile
        }
        else{
            photoprofile = Constants.IMAGE_URL+comment.user!!.photoprofile
        }
        val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.server_unreachable)
                    .priority(Priority.HIGH)
            Glide.with(context)
                    .load(photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(viewHolder.itemView.commentpic)
        viewHolder.itemView.commentpic.setOnClickListener(){
            if(!AppManager.getAppAccountUserId(context.applicationContext).equals(comment.user!!._id)) {
                val dialogFrag = ProifileSellerFragment.newInstance()
                val args = Bundle()
                dialogFrag.show(mFragmentManager, comment.user!!, context.applicationContext)
            }
        }


        


        /* viewHolder.itemView.category_name.text = comment.name
         viewHolder.itemView.category_number.text = comment.number
         viewHolder.itemView.category_line.setBackgroundResource(comment.color)
         println(comment.color)*/
        // Title and year

        //                commentViewHolder.commentRating.setText(comment.price+"");
        //            }
        viewHolderParent.itemView.comment_item1.setOnLongClickListener {
            Log.e("oncommentClicked2","oncommentClicked2")
            oncommentClickListener.oncommentClicked(position)
            return@setOnLongClickListener true
        }
    }


    // ViewHolders
  
    inner class CommentsViewHolder(view: View) : RecyclerView.ViewHolder(view)
    // Click listener interface
    interface OncommentClickListener {
        fun oncommentClicked(position: Int)
    }
    private fun timeAgo(time: String): CharSequence? {
        return DateUtils.getRelativeTimeSpanString(
                Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
    }

}