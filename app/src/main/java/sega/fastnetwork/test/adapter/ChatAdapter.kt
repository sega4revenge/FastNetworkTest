package sega.fastnetwork.test.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import sega.fastnetwork.test.R
import sega.fastnetwork.test.customview.CircleImageView
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.ChatMessager
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.util.Constants
import java.net.URL


/**
 * Created by VinhNguyen on 9/22/2017.
 */
class ChatAdapter (item: ArrayList<ChatMessager>?, context: Context,mUserTo: String?) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var mItems: ArrayList<ChatMessager>?  = item
    var mContext : Context? = context
    var user : User =  AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(mContext!!)!!)
    var imguserto  = mUserTo
    val options = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .diskCacheStrategy( DiskCacheStrategy.ALL )
            .skipMemoryCache( true )
            .override(550,550)
            .placeholder(R.drawable.logo)
            .error(R.drawable.server_unreachable)
            .priority(Priority.HIGH)
    fun avatacmt(link: String): String?{
        var result = ""
        if(!link.startsWith("http")){
            result = Constants.IMAGE_URL+link
        }
        return result
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = mItems?.get(position)
        Log.d("aaaaaaaaaa",item?.mStatus.toString())
        if(item?.email.equals(user.email))
        {
            if(item?.photoprofile.equals("")) {
                holder.imgAvatarFrom.visibility = View.GONE
                holder.LinFrom.visibility = View.VISIBLE
                holder.LinTo.visibility = View.GONE
                holder.imgSend.visibility = View.GONE
                holder.txtnameFrom.text = item?.name
                holder.txtmessFrom.text = item?.message
            }else{
                val url = URL(avatacmt(item?.photoprofile!!))
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//                var with = (bmp.width)/2
//                var height = (bmp.width)/2
                holder.imgAvatarFrom.visibility = View.GONE
                holder.LinFrom.visibility = View.VISIBLE
                holder.LinTo.visibility = View.GONE
                holder.imgSend.visibility = View.VISIBLE
//                holder.imgSend.maxWidth = with/2
//                holder.imgSend.maxHeight = height/2
                holder.txtnameFrom.text = item?.name
                holder.txtmessFrom.text = ""
            //    if(!item?.mStatus!!) {

                    Glide.with(mContext)
                            .load(avatacmt(item?.photoprofile!!))
                            .thumbnail(0.5f)
                            .apply(options)
                            .into(holder.imgSend)
            //    }
            }

        }else{
            if(item?.photoprofile.equals("")) {
                holder.imgAvatarTo.visibility = View.GONE
                holder.LinTo.visibility = View.VISIBLE
                holder.LinFrom.visibility = View.GONE
                holder.imgSendTo.visibility = View.GONE
                holder.txtnameTo.text = item?.name
                holder.txtmessTo.text = item?.message
            }else{
                val url = URL(avatacmt(item?.photoprofile!!))
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//                var with = (bmp.width)/2
//                var height = (bmp.width)/2
                holder.imgAvatarTo.visibility = View.GONE
                holder.LinTo.visibility = View.VISIBLE
                holder.LinFrom.visibility = View.GONE
                holder.imgSendTo.visibility = View.VISIBLE
//                holder.imgSendTo.maxWidth = with
//                holder.imgSendTo.maxHeight = height
                holder.txtnameTo.text = item?.name
                holder.txtmessTo.text = ""
             //   if(!item?.mStatus!!) {
                    Glide.with(mContext)
                            .load(avatacmt(item?.photoprofile!!))
                            .thumbnail(0.5f)
                            .apply(options)
                            .into(holder.imgSendTo)
              //  }
            }

        }
        mItems?.get(position)!!.mStatus = true
        Log.d("aaaaaaaaaa1111",mItems?.get(position)!!.mStatus.toString())
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
        val LinTo = view.findViewById<LinearLayout>(R.id.LinTo)
        val txtnameFrom = view.findViewById<TextView>(R.id.txtnameFrom)
        val txtnameTo = view.findViewById<TextView>(R.id.txtnameTo)
        val txtmessFrom = view.findViewById<TextView>(R.id.txtmessFrom)
        val txtmessTo = view.findViewById<TextView>(R.id.txtmessTo)
        val imgAvatarFrom = view.findViewById<CircleImageView>(R.id.imgAvatarFrom)
        val imgAvatarTo = view.findViewById<CircleImageView>(R.id.imgAvatarTo)
        val imgSend = view.findViewById<ImageView>(R.id.imgSend)
        val imgSendTo = view.findViewById<ImageView>(R.id.imgSendTo)
    }
}