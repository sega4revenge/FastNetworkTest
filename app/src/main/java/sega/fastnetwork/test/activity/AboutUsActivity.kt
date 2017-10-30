package sega.fastnetwork.test.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.toolbar.view.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import sega.fastnetwork.test.R


class AboutUsActivity : AppCompatActivity() {


    // nguyen nhu viet1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_backarrow_white)
        supportActionBar!!.setBackgroundDrawable(getDrawable(R.color.toolbar))
        supportActionBar!!.elevation = 0F
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val v = LayoutInflater.from(this).inflate(R.layout.toolbar,null)
        v.toolbar_title.text = "About"
        supportActionBar!!.customView = v
        val aboutPage = AboutPage(this)


                .isRTL(false)
                .setImage(R.drawable.logo_fn)
                .addItem(Element().setTitle("Version 1.2"))
                //                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("hhl190295@gmail.com")
                .addFacebook("apptimviec")
                .addPlayStore("com.finger.jobfind")
                .addWebsite("http://www.finger.co.kr/home/index.jsp")
                .setDescription(resources.getString(R.string.btn_forgot))
                //                .addFacebook("the.medy")
                //                .addTwitter("medyo80")
                //                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                //                .addInstagram("medyo80")
                //                .addGitHub("medyo")
                //  .addItem(getCopyRightsElement())
                .create()

        setContentView(aboutPage)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val ids = item?.itemId
        if (ids == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    //    internal val copyRightsElement: Element
//        get() {
//            val copyRightsElement = Element()
//            val copyrights = String.format(getString(R.string.btn_join), Calendar.getInstance().get(Calendar.YEAR))
//            copyRightsElement.setTitle(copyrights)
//            copyRightsElement.setIconDrawable(R.drawable.ic_amounts)
//            copyRightsElement.setIcon(R.drawable.ic_amounts)
//            copyRightsElement.setColor(ContextCompat.getColor(this, mehdi.sakout.aboutpage.R.color.about_item_icon_color))
//            copyRightsElement.gravity = Gravity.CENTER
//            copyRightsElement.onClickListener = View.OnClickListener {
//                Toast.makeText(applicationContext, copyrights, Toast.LENGTH_SHORT).show() }
//            return copyRightsElement
//        }

}