package sega.fastnetwork.test.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.search_locationlist_second_layout.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.adapter.arrLocationAdapter




/**
 * Created by VinhNguyen on 8/10/2017.
 */
class Search_FilterLocationSecondActivity : AppCompatActivity() {


    var arrLoca : Array<String>? = null
    var menu : Menu? = null
    var type : Int = 0
    var success : Int = 0
    var position :Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
        setContentView(R.layout.search_locationlist_second_layout)
        toolbar.setTitle(R.string.stt_location)
        toolbar.setTitleTextColor(R.color.black)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        var intent = intent
        type = intent.getIntExtra("type",1)
        if(type == 1)
        {
             arrLoca = resources.getStringArray(R.array.mLocation_Bac)
        }else  if(type == 2){
             arrLoca = resources.getStringArray(R.array.mLocation_Trung)
        }else if(type == 3){
            arrLoca = resources.getStringArray(R.array.mLocation_Nam)
        }

        recycler_view!!.layoutManager = LinearLayoutManager(this)
        recycler_view!!.setHasFixedSize(true)
        recycler_view!!.adapter = arrLocationAdapter(arrLoca!!,this)

    }
     fun selectLocation(pos: Int) {
         position = pos
         menu!!.findItem(R.id.action_uploadproduct).setIcon(R.mipmap.ic_select_location)
        success = 1
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        val inflater = menuInflater
        inflater.inflate(R.menu.uploadproduct_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val ids = item!!.itemId
        if (ids == android.R.id.home) {
            val intent = this.intent
            intent.putExtra("selected", 0)
            this.setResult(9999, intent)
            finish()
        }
        if (ids == R.id.action_uploadproduct) {
            if(success != 0)
            {
                val intent = this.intent
                intent.putExtra("selected", 1)
                intent.putExtra("position", position)
                this.setResult(9999, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}