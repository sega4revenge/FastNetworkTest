package sega.fastnetwork.test.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import org.json.JSONArray
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircleImageView
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.User

/**
 * Created by VinhNguyen on 9/22/2017.
 */
class ChatAdapter (item: JSONArray?, context: Context,mUserTo: User?) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var mItems: JSONArray?  = item
    var mContext : Context? = context
    var user : User =  AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(mContext!!)!!)
    var userto : User? = mUserTo

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = mItems?.getJSONObject(position)
        if(item?.getString("email").equals(user.email))
        {
            holder.LinFrom.visibility = View.VISIBLE
            holder.LinTo.visibility = View.GONE
            holder.txtnameFrom.text = item?.getString("name")
            holder.txtmessFrom.text = item?.getString("message")
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.server_unreachable)
                    .priority(Priority.HIGH)
            Glide.with(mContext)
                    .load(user.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(holder.imgAvatarFrom)
        }else{
            holder.LinTo.visibility = View.VISIBLE
            holder.LinFrom.visibility = View.GONE
            holder.txtnameTo.text = item?.getString("name")
            holder.txtmessTo.text = item?.getString("message")
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.server_unreachable)
                    .priority(Priority.HIGH)
            Glide.with(mContext)
                    .load(userto?.photoprofile)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(holder.imgAvatarTo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_chat_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return mItems?.length()!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val LinFrom = view.findViewById<LinearLayout>(R.id.LinFrom)
        val LinTo = view.findViewById<LinearLayout>(R.id.LinTo)
        val txtnameFrom = view.findViewById<TextView>(R.id.txtnameFrom)
        val txtnameTo = view.findViewById<TextView>(R.id.txtnameTo)
        val txtmessFrom = view.findViewById<TextView>(R.id.txtmessFrom)
        val txtmessTo = view.findViewById<TextView>(R.id.txtmessTo)
        val imgAvatarFrom = view.findViewById<CircleImageView>(R.id.imgAvatarFrom)
        val imgAvatarTo = view.findViewById<CircleImageView>(R.id.imgAvatarTo)
    }
}