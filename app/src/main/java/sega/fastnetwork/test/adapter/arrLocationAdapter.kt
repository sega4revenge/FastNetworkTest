package sega.fastnetwork.test.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
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
        val item = mItems.get(position)
        holder.nameLocation.text = item.toString()
        if(position == lastCheckedPos && position != 0)
        {
            holder.ra_bt.isChecked = true
        }else{
            if (holder.ra_bt.isChecked == true )
                holder.ra_bt.isChecked = false
        }
        holder.itemId.setOnClickListener(){
            if(lastChecked!=null)
            {
                if(lastChecked!!.isChecked == true)
                    lastChecked!!.isChecked =false
            }
            holder.ra_bt.isChecked = true
            lastChecked = holder.ra_bt
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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
          val nameLocation = view.findViewById(R.id.txtnameLocation) as TextView
          val ra_bt = view.findViewById(R.id.ra_button) as RadioButton
          val itemId = view.findViewById(R.id.itemId) as LinearLayout
    }
}