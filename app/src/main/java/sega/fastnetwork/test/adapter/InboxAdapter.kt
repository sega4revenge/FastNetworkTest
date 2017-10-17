package sega.fastnetwork.test.adapter

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
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.ChatActivity
import sega.fastnetwork.test.customview.CircleImageView
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Chat
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants

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
                    holder.txtmessFrom.text = "bạn đã gửi 1 tin nhắn ảnh"
                }else{
                    holder.txtmessFrom.text = mess?.name+" đã gửi 1 tin nhắn ảnh"
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
        val LinFrom = view.findViewById<LinearLayout>(R.id.LinFrom)
        val txtnameFrom = view.findViewById<TextView>(R.id.txtnameFrom)
        val txtmessFrom = view.findViewById<TextView>(R.id.txtmessFrom)
        val imgAvatarFrom = view.findViewById<CircleImageView>(R.id.imgAvatarFrom)
        val txttime = view.findViewById<TextView>(R.id.txttime)
    }
    private fun timeAgo(time: String): CharSequence? {
        val time = DateUtils.getRelativeTimeSpanString(
                java.lang.Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
        return time
    }
}