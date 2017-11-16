package finger.thuetot.vn.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import finger.thuetot.vn.R
import finger.thuetot.vn.activity.ChatActivity
import finger.thuetot.vn.customview.CircleImageView
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.Chat
import finger.thuetot.vn.model.User
import finger.thuetot.vn.util.Constants

/**
 * Created by VinhNguyen on 9/25/2017.
 */
class InboxAdapter (item: ArrayList<Chat>?, context: Context, mUserTo: User?) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    var mItems: ArrayList<Chat>?  = item
    var mContext : Context? = context
    var user : User =  AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(mContext!!)!!)
    var userto : User? = mUserTo

    fun avatacmt(link: String): String?{
        var result = ""
        if(!link.startsWith("http")){
            result = Constants.IMAGE_URL+link
        }
        return result
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = mItems?.get(position)
        var max = item?.messages?.size
        var mess = item?.messages?.get(max!!-1)

        var name = ""
        var photo = ""
        var id = ""
        if(!item?.userfrom?.name!!.equals(user.name))
        {
            name = item?.userfrom?.name!!
            photo = item?.userfrom?.photoprofile!!
            id =  item?.userfrom?._id!!
        }else{
            name = item?.userto?.name!!
            photo = item?.userto?.photoprofile!!
            id =  item?.userto?._id!!
        }

            holder.LinFrom.visibility = View.VISIBLE
            holder.txtnameFrom.visibility = View.VISIBLE
            holder.txttime.text = timeAgo(mess?.created_at!!)
            holder.txtnameFrom.text = name
            holder.txtmessFrom.visibility = View.VISIBLE
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.useravatar)
                    .error(R.drawable.useravatar)
                    .priority(Priority.HIGH)
            Glide.with(mContext)
                    .load(avatacmt(photo))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(holder.imgAvatarFrom)
            if(mess?.photoprofile.equals(""))
            {
                holder.txtmessFrom.text = mess?.message
            }else{
                if(mess?.email.equals(user.email))
                {
                    holder.txtmessFrom.text = mContext?.resources?.getString(R.string.inbox_send1)
                }else{
                    holder.txtmessFrom.text = mess?.name+" "+mContext?.resources?.getString(R.string.inbox_send)
                }

            }


        holder.LinFrom.setOnClickListener(){
            val intent = Intent((mContext as AppCompatActivity), ChatActivity::class.java)
            intent.putExtra("iduser",id)
            intent.putExtra("avatar","")
            mContext?.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_chat_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return mItems?.size!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val LinFrom = view.findViewById<LinearLayout>(R.id.LinTo)
        val txtnameFrom = view.findViewById<TextView>(R.id.txtnameTo)
        val txtmessFrom = view.findViewById<TextView>(R.id.txtmessTo)
        val imgAvatarFrom = view.findViewById<CircleImageView>(R.id.imgAvatarTo)
        val txttime = view.findViewById<TextView>(R.id.txttime)
    }
    private fun timeAgo(time: String): CharSequence? {
        val time = DateUtils.getRelativeTimeSpanString(
                java.lang.Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
        return time
    }
}