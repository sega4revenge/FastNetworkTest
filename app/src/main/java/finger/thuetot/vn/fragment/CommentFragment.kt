package finger.thuetot.vn.fragment


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
import finger.thuetot.vn.R
import finger.thuetot.vn.adapter.CommentAdapter
import finger.thuetot.vn.manager.AppManager
import finger.thuetot.vn.model.Comment
import finger.thuetot.vn.presenter.CommentPresenter
import finger.thuetot.vn.util.Constants
import kotlinx.android.synthetic.main.content_comments.*
import kotlinx.android.synthetic.main.toolbar_twoline.*


/**
 * A simple [Fragment] subclass.
 */
class CommentFragment : Fragment(), CommentAdapter.OncommentClickListener, CommentPresenter.CommentView {
    override fun getStatusAddComent(listcomment: ArrayList<Comment>) {
        if(listcomment.size == 0){
            no_cmt.visibility = View.VISIBLE
            comments_list.visibility = View.GONE
        }
        else{
            FirebaseMessaging.getInstance().subscribeToTopic(listcomment[(listcomment.size-1)]._id)
//            adapter!!.commentsList.clear()
//            adapter!!.commentsList = listcomment
//            adapter!!.notifyDataSetChanged()
//            comments_list.scrollToPosition(adapter!!.commentsList.size-1)
//            no_cmt.visibility = View.GONE
//            comments_list.visibility = View.VISIBLE
        }
    }


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
            alertDialogBuilder.setMessage(R.string.delete_comment)
            alertDialogBuilder.setPositiveButton(R.string.title_comment, { _, _ ->
                run {
//                    Toast.makeText(activity, "DELETED", Toast.LENGTH_LONG).show()
                    mCommentPresenter!!.deletecomment(adapter!!.commentsList[position]._id.toString(), id)
                }
            })
            alertDialogBuilder.setNegativeButton(R.string.no, { _, _ ->
                run {
//                    Toast.makeText(activity, "NO", Toast.LENGTH_LONG).show()
                }
            })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }

    override fun setErrorMessage(errorMessage: String) {
        if(errorMessage.equals("405")){
            var build = android.app.AlertDialog.Builder(activity)
            build.setMessage(R.string.product_notexit)
                    .setNegativeButton(R.string.btn_ok, { _, _ ->
                        run {
                        }
                    })
            var alert = build.create()
            alert.show()
        }else {
            println(errorMessage)
        }
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

        FirebaseMessaging.getInstance().subscribeToTopic(id)
        adapter = CommentAdapter(context, this,this.fragmentManager,0)
        val layoutManager = LinearLayoutManager(context)
        comments_list.layoutManager = layoutManager
        comments_list.setHasFixedSize(true)
        comments_list.adapter = adapter
        mCommentPresenter = CommentPresenter(this)
        mCommentPresenter!!.refreshcomment(id)

        buttoncomment.setOnClickListener {
            //            Log.e("cmtttt",AppAccountManager.)
            if(writecomment.text.trim().isEmpty())
            {
                writecomment.setText("")
            }
            else
            {
                mCommentPresenter!!.addcomment(AppManager.getAppAccountUserId(activity), id, writecomment.text.trim().toString())
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(writecomment.windowToken, 0)
                writecomment.setText("")
                writecomment.clearFocus()
            }


        }
        back_button.setOnClickListener {
            activity.finish()
            activity.overridePendingTransition(0,R.anim.fade_out)
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_comment, container, false)

//        var v = inflater!!.inflate(R.layout.fragment_comment, container, false) as View
    // Inflate the layout for this fragment

    override fun onResume() {
        super.onResume()
        mCommentPresenter!!.refreshcomment(id)
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

