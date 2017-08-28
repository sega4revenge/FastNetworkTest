package sega.fastnetwork.test.fragment


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.content_comments.*
import kotlinx.android.synthetic.main.content_comments.view.*
import kotlinx.android.synthetic.main.toolbar_twoline.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.CommentAdapter
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Comment
import sega.fastnetwork.test.presenter.CommentPresenter
import sega.fastnetwork.test.util.Constants


/**
 * A simple [Fragment] subclass.
 */
class CommentFragment : Fragment(), CommentAdapter.OncommentClickListener, CommentPresenter.CommentView {



    override fun getCommentDetail(listcomment: ArrayList<Comment>) {

        adapter!!.commentsList.clear()
        adapter!!.commentsList = listcomment
        adapter!!.notifyDataSetChanged()
        comments_list.scrollToPosition(adapter!!.commentsList.size-1)

    }


    override fun isCommentSuccessful(isCommentSuccessful: Boolean) {

    }



    var mCommentPresenter: CommentPresenter? = null

    override fun oncommentClicked(position: Int) {
//        Log.e("IDCMTUSER", adapter!!.commentsList[position]._id)
//        Log.e("IDCMTUSER", adapter!!.commentsList[position].user!!._id)
//        Log.e("IDUSER", AppManager.getAppAccountUserId(activity))
        if(AppManager.getAppAccountUserId(activity) == adapter!!.commentsList[position].user!!._id){
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setMessage("Are you sure you want to delete this comment?")
            alertDialogBuilder.setPositiveButton("DELETE", { _, _ ->
                run {
//                    Toast.makeText(activity, "DELETED", Toast.LENGTH_LONG).show()
                    mCommentPresenter!!.deletecomment(adapter!!.commentsList[position]._id.toString(), id)
                }
            })
            alertDialogBuilder.setNegativeButton("NO", { _, _ ->
                run {
//                    Toast.makeText(activity, "NO", Toast.LENGTH_LONG).show()
                }
            })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }

    override fun setErrorMessage(errorMessage: String) {
        println(errorMessage)
    }


    private var id: String = ""
    private var product_name: String = ""
    private var seller_name: String = ""

    var adapter : CommentAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var v = inflater!!.inflate(R.layout.fragment_comment, container, false) as View
        // Inflate the layout for this fragment
        id = arguments.getString(Constants.product_ID)
        product_name = arguments.getString(Constants.product_NAME)
        seller_name = arguments.getString(Constants.seller_name)
        Log.e ("AAAAA",product_name + " " + seller_name)
        v.toolbar_title.text = product_name
        v.toolbar_subtitle.text = seller_name
        Log.e("ASD", id)
        FirebaseMessaging.getInstance().subscribeToTopic(id)
        adapter = CommentAdapter(context, this)
        val layoutManager = LinearLayoutManager(context)
        v.comments_list.layoutManager = layoutManager
        v.comments_list.setHasFixedSize(true)
        v.comments_list.adapter = adapter
        mCommentPresenter = CommentPresenter(this)
        mCommentPresenter!!.refreshcomment(id)
        v.buttoncomment.setOnClickListener {
            //            Log.e("cmtttt",AppAccountManager.)
            Log.e("cmtttt", AppManager.getAppAccountUserId(activity) + " " + id + " " + v.writecomment.text.toString())
            mCommentPresenter!!.addcomment(AppManager.getAppAccountUserId(activity), id, v.writecomment.text.toString())
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(writecomment.windowToken, 0)
            v.writecomment.setText("")
            v.writecomment.clearFocus()

        }

        return v


    }

    override fun onResume() {
        super.onResume()
        activity.registerReceiver(this.appendChatScreenMsgReceiver, IntentFilter("commmentactivity"))
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unregisterReceiver(appendChatScreenMsgReceiver)
    }

    var appendChatScreenMsgReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val b = intent.extras
            if (b != null) {
                if (b.getBoolean("reload")) {
                    mCommentPresenter!!.refreshcomment(id)
                }
            }
        }
    }
}// Required empty public constructor
