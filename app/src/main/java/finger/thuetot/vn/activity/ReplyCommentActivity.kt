package finger.thuetot.vn.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import finger.thuetot.vn.R
import finger.thuetot.vn.fragment.ReplyCommentFragment
import finger.thuetot.vn.util.Constants

/**
 * Created by VinhNguyen on 11/21/2017.
 */
class ReplyCommentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        if (savedInstanceState == null) {

            val fragment = ReplyCommentFragment()
            val id = intent.extras.getString(Constants.comment_ID)
            val product_name = intent.extras.getString(Constants.product_NAME)
            val seller_name = intent.extras.getString(Constants.seller_name)

            Log.e("CMT",id)
            val arg = Bundle()
            arg.putString(Constants.comment_ID,id)
            arg.putString(Constants.product_NAME,product_name)
            arg.putString(Constants.seller_name,seller_name)
            fragment.arguments = arg
            supportFragmentManager.beginTransaction().replace(R.id.comment_container,fragment).commit()


        }
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
    }
}
