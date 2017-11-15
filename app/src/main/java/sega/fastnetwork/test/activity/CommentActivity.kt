package sega.fastnetwork.test.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import sega.fastnetwork.test.R
import sega.fastnetwork.test.fragment.CommentFragment
import sega.fastnetwork.test.util.Constants

class CommentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        if (savedInstanceState == null) {
            val fragment = CommentFragment()

//            val id = savedInstanceState!!.getString(Constants.product_ID)
//            Log.e("CMT",id)
            val id = intent.extras.getString(Constants.product_ID)
            val product_name = intent.extras.getString(Constants.product_NAME)
            val seller_name = intent.extras.getString(Constants.seller_name)
            Log.e("CMT",id)
            val arg = Bundle()
            arg.putString(Constants.product_ID,id)
            arg.putString(Constants.product_NAME,product_name)
            arg.putString(Constants.seller_name,seller_name)
            fragment.arguments = arg
            supportFragmentManager.beginTransaction().replace(R.id.comment_container,fragment).commit()



//            val fragment = CommentFragment()
//            val args = Bundle()
//            args.putString("productid", id)
//            fragment.setArguments(args)
//            supportFragmentManager.beginTransaction().replace(R.id.comment_container, fragment).commit()

        }
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
    }
}
