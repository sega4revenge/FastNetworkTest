package sega.fastnetwork.test.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.search_locationlist_layout.*
import sega.fastnetwork.test.R

/**
 * Created by VinhNguyen on 8/9/2017.
 */
class Search_FilterLocationActivity :AppCompatActivity() {
    var type : Int = 0
    private val INTENT_DATA_LOCATION = 1000
    private val INTENT_DATA_AllLOCATION = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
        setContentView(R.layout.search_locationlist_layout)

        toolbars.setTitle(R.string.stt_location2)
        toolbars.setTitleTextColor(R.color.black)

        setSupportActionBar(toolbars)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        bt_all.setOnClickListener {
            val intent = this.intent
            intent.putExtra("typeLocation", 405)
            this.setResult(INTENT_DATA_LOCATION, intent)
            finish()
        }

        bt_bac.setOnClickListener {
            type = 1
            val intent = Intent(this@Search_FilterLocationActivity, Search_FilterLocationSecondActivity::class.java)
            intent.putExtra("type", 1)
            startActivityForResult(intent,9999)

        }

        bt_trung.setOnClickListener {
            type = 2
            val intent = Intent(this@Search_FilterLocationActivity, Search_FilterLocationSecondActivity::class.java)
            intent.putExtra("type", 2)
            startActivityForResult(intent,9999)
        }

        bt_nam.setOnClickListener {
            type = 3
            val intent = Intent(this@Search_FilterLocationActivity, Search_FilterLocationSecondActivity::class.java)
            intent.putExtra("type", 3)
            startActivityForResult(intent,9999)
        }

    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val ids = item!!.itemId
        if (ids == android.R.id.home) {
            val intent = this.intent
            intent.putExtra("typeLocation", 404)
            this.setResult(INTENT_DATA_LOCATION, intent)
            finish()

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        val intent = this.intent
        intent.putExtra("typeLocation", 404)
        this.setResult(INTENT_DATA_LOCATION, intent)
        finish()
        super.onBackPressed()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

       if (resultCode == 9999) {
           var success = data.getIntExtra("selected",1)
           if(success != 0){
               val intent = this.intent
               intent.putExtra("typeLocation", type)
               intent.putExtra("position", data.getIntExtra("position",1))
               this.setResult(INTENT_DATA_LOCATION, intent)
               finish()
           }else{
//               val intent = this.intent
//               intent.putExtra("type", type)
//               this.setResult(Activity.RESULT_OK, intent)
//               finish()
           }
       }
    }

}