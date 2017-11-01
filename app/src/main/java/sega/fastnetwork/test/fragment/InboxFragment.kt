package sega.fastnetwork.test.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.inboxlayout.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.InboxAdapter
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Chat
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.InboxPressenter


/**
 * Created by VinhNguyen on 9/24/2017.
 */
class InboxFragment : Fragment(), InboxPressenter.InboxListView {

    var mItemsData: ArrayList<Chat>?  = ArrayList()
    var user : User?=null
    var userTo : User?=null
    var layoutManager: LinearLayoutManager? = null
    var adapter: InboxAdapter? = null
    var mInboxPressenter: InboxPressenter? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        mInboxPressenter = InboxPressenter(this)
        product_recycleview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        product_recycleview.layoutManager = this.layoutManager

        mInboxPressenter?.getProductList(user?._id!!)


    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.inboxlayout, container, false)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mInboxPressenter?.cancelRequest()
    }

    private fun onDownloadSuccessful() {
        Log.e("onDownloadSuccessful",mItemsData?.size.toString())
        error_message.visibility = View.GONE
        nodata.visibility = View.GONE
        swipe_refresh.isRefreshing = false
        swipe_refresh.isEnabled = false //true
        adapter = InboxAdapter(mItemsData,activity,userTo)
        product_recycleview.adapter = adapter
    }
    override fun getListInbox(mChat: ArrayList<Chat>?) {
        if(mChat?.size!!>0)
        {
            mItemsData = mChat
            onDownloadSuccessful()
        }else{
            setErrorMessage("")
        }

    }
    private fun onDownloadFailed() {
            nodata.visibility = View.VISIBLE
            swipe_refresh.visibility = View.GONE
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = true

    }

    override fun setErrorMessage(errorMessage: String) {
        onDownloadFailed()
    }






}