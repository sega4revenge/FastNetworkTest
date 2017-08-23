package sega.fastnetwork.test.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.search_category_layout.*
import sega.fastnetwork.test.R

/**
 * Created by VinhNguyen on 8/10/2017.
 */
class Search_FliterCategory_Activity : AppCompatActivity() {

    private val INTENT_DATA_CATEGORY = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
        setContentView(R.layout.search_category_layout)
        toolbars.setTitle(R.string.stt_category)
        toolbars.setTitleTextColor(R.color.black)
        setSupportActionBar(toolbars)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        category_all.setOnClickListener(){
            IntenData(0)
        }
        category_motorbike.setOnClickListener(){
            IntenData(1)
        }
        category_electronic.setOnClickListener(){
            IntenData(2)
        }
        category_education.setOnClickListener(){
            IntenData(3)
        }
        category_toy.setOnClickListener(){
            IntenData(4)
        }
        category_houseware.setOnClickListener(){
            IntenData(5)
        }
        category_fashion.setOnClickListener(){
            IntenData(6)
        }
        category_home.setOnClickListener(){
            IntenData(7)
        }
        category_different.setOnClickListener(){
            IntenData(8)
        }
    }
//    override fun onClick(p0: View?) {
//        when (p0!!.id)
//        {
//            R.id.category_all ->
//                IntenData(0)
//            R.id.category_motorbike ->
//                IntenData(1)
//            R.id.category_electronic ->
//                IntenData(2)
//            R.id.category_education ->
//                IntenData(3)
//            R.id.category_toy ->
//                IntenData(4)
//            R.id.category_houseware ->
//                IntenData(5)
//            R.id.category_fashion ->
//                IntenData(6)
//            R.id.category_home ->
//                IntenData(7)
//            else ->
//                IntenData(8)
//        }
//    }
    fun IntenData(type : Int){
        val intent = this.intent
        intent.putExtra("typeCategory", type)
        this.setResult(INTENT_DATA_CATEGORY, intent)
        finish()
    }
    override fun onBackPressed() {
        val intent = this.intent
        intent.putExtra("typeCategory", 0)
        this.setResult(INTENT_DATA_CATEGORY, intent)
        finish()
        super.onBackPressed()
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val ids = item!!.itemId
        if (ids == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}