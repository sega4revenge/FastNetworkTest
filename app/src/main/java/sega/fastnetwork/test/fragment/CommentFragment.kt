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
import kotlinx.android.synthetic.main.toolbar_twoline.*
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
        if(listcomment.size == 0){
            no_cmt.visibility = View.VISIBLE
            comments_list.visibility = View.GONE
        }
        else{
            adapter!!.commentsList.clear()
            adapter!!.commentsList = listcomment
            adapter!!.notifyDataSetChanged()
            comments_list.scrollToPosition(adapter!!.commentsList.size-1)
            no_cmt.visibility = View.GONE
            comments_list.visibility = View.VISIBLE
        }


    }


    override fun isCommentSuccessful(isCommentSuccessful: Boolean) {

    }



    var mCommentPresenter: CommentPresenter? = null

    override fun oncommentClicked(position: Int) {
        Log.e("oncommentClicked","oncommentClicked")
//        Log.e("IDCMTUSER", adapter!!.commentsList[position]._id)
//        Log.e("IDCMTUSER", adapter!!.commentsList[position].user!!._id)
//        Log.e("IDUSER", AppManager.getAppAccountUserId(activity))
        if(AppManager.getAppAccountUserId(activity) == adapter!!.commentsList[position].user!!._id){
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setMessage("Are you sure you want to delete this comment?")
            alertDialogBuilder.setPositiveButton("DELETEE", { _, _ ->
                run {
//                    Toast.makeText(activity, "DELETED", Toast.LENGTH_LONG).show()
                    mCommentPresenter!!.deletecomment(adapter!!.commentsList[position]._id.toString(), id)
                }
            })
            alertDialogBuilder.setNegativeButton("NOOO", { _, _ ->
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
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments.getString(Constants.product_ID)
        product_name = arguments.getString(Constants.product_NAME)
        seller_name = arguments.getString(Constants.seller_name)

        toolbar_title.text = product_name
        toolbar_subtitle.text = seller_name
        Log.e("ASD", id)
        FirebaseMessaging.getInstance().subscribeToTopic(id)
        adapter = CommentAdapter(context, this,this.fragmentManager)
        val layoutManager = LinearLayoutManager(context)
        comments_list.layoutManager = layoutManager
        comments_list.setHasFixedSize(true)
        comments_list.adapter = adapter
        mCommentPresenter = CommentPresenter(this)
        mCommentPresenter!!.refreshcomment(id)

        buttoncomment.setOnClickListener {
            //            Log.e("cmtttt",AppAccountManager.)
            Log.e("cmtttt", AppManager.getAppAccountUserId(activity) + " " + id + " " + writecomment.text.toString())
            mCommentPresenter!!.addcomment(AppManager.getAppAccountUserId(activity), id, writecomment.text.toString())
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(writecomment.windowToken, 0)
            writecomment.setText("")
            writecomment.clearFocus()

        }
        back_button.setOnClickListener {
            activity.finish()
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_comment, container, false)

//        var v = inflater!!.inflate(R.layout.fragment_comment, container, false) as View
        // Inflate the layout for this fragment
    }

    override fun onResume() {
        super.onResume()
        activity.registerReceiver(this.appendChatScreenMsgReceiver, IntentFilter("commmentactivity"))
    }

    override fun onDestroy() {
        super.onDestroy()
        mCommentPresenter!!.cancelRequest()
        activity.unregisterReceiver(appendChatScreenMsgReceiver)
    }

    private var appendChatScreenMsgReceiver: BroadcastReceiver = object : BroadcastReceiver() {
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

