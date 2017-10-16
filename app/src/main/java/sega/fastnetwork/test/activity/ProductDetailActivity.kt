package sega.fastnetwork.test.activity

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import sega.fastnetwork.test.R
import sega.fastnetwork.test.fragment.ProductDetailFragment
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.util.Constants

/**
 * Created by sega4 on 10/08/2017.
 */

class ProductDetailActivity : AppCompatActivity() {
    internal var rate: Double? = null
    internal var userId: String = ""

    var fragment : Fragment?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        val i = Intent(applicationContext, LoginActivity::class.java)
        if (savedInstanceState == null) {
            var productId: String
            var productuserId: String = ""

            val intent = intent
            val data = intent.data
            if (data == null) {
                // Not loading from deep link
                productId = getIntent().getStringExtra(Constants.product_ID)
                productuserId = getIntent().getStringExtra(Constants.seller_ID)
                loadproductDetailsOf(productId,productuserId )
            } else {
                // Loading from deep link
                val parts = data.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                productId = parts[parts.size - 1]
                println(productId)
                when (productId) {
                // Load product Lists
                    "link.php" -> if (AccountManager.get(this).getAccountsByType(AppManager.ACCOUNT_TYPE).isNotEmpty()) {
                        loadproductsOfType(0)
                    } else {
                        startActivity(i)
                        finish()
                    }
                // Load details of a particular product
                    else -> if (AccountManager.get(this).getAccountsByType(AppManager.ACCOUNT_TYPE).isNotEmpty()) {
                        val abc = productId.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        productId = abc[0].substring(abc[0].lastIndexOf("=") + 1)
                        userId = abc[1].substring(abc[1].lastIndexOf("=") + 1)
                        println(userId + " " + productId)
                        loadproductDetailsOf(productId,productuserId)
                    } else {

                        startActivity(i)
                        finish()
                    }
                }
            }
        }
    }

    private fun loadproductDetailsOf(productId: String, productuserId : String) {
        fragment = ProductDetailFragment()
        val args = Bundle()

        args.putString(Constants.product_ID, productId)
        args.putString(Constants.seller_ID, productuserId)

        (fragment as ProductDetailFragment).arguments = args

        supportFragmentManager.beginTransaction().replace(R.id.product_detail_container, fragment).commit()
    }

    @SuppressLint("CommitPrefEdits")
    private fun loadproductsOfType(viewType: Int) {
        val editor = getSharedPreferences(Constants.TABLE_USER, Context.MODE_PRIVATE).edit()
        editor.putInt(Constants.LAST_SELECTED, viewType)

        editor.commit()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    override fun onResume() {
        super.onResume()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        (fragment as ProductDetailFragment).destroyfragment()
        finish()
    }
    override fun onStart() {
        super.onStart()

    }
    override fun onStop() {
        super.onStop()

    }
}
