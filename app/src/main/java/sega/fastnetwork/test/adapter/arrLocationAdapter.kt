package sega.fastnetwork.test.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.item_row_onlytext.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.Search_FilterLocationSecondActivity

/**
 * Created by VinhNguyen on 8/10/2017.
 */
class arrLocationAdapter(item : Array<String>,context : Context) : RecyclerView.Adapter<arrLocationAdapter.ViewHolder>() {
    private var lastChecked: RadioButton? = null
    private val lastCheckedPos :Int = 0
    var mItems = item
    var mContext : Context? = context

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("aaaaa", mItems.get(position))

//        if(position==0){
//            holder.ra_bt.isChecked = false
//        }
        val item = mItems[position]
        holder.itemView.txtnameLocation.text = item
        if(position == lastCheckedPos && position != 0)
        {
            holder.itemView.ra_button.isChecked = true
        }else{
            if (holder.itemView.ra_button.isChecked)
                holder.itemView.ra_button.isChecked = false
        }
        holder.itemView.itemId.setOnClickListener {
            if(lastChecked!=null)
            {
                if(lastChecked!!.isChecked == true)
                    lastChecked!!.isChecked =false
            }
            holder.itemView.ra_button.isChecked = true
            lastChecked = holder.itemView.ra_button
            (mContext as Search_FilterLocationSecondActivity).selectLocation(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_row_onlytext, parent, false))
    }

    override fun getItemCount(): Int {
        return mItems.size -1
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}