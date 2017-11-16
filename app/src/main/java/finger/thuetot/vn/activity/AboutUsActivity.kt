package finger.thuetot.vn.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import finger.thuetot.vn.R
import kotlinx.android.synthetic.main.toolbar.view.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutUsActivity : AppCompatActivity() {


    // nguyen nhu viet1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backarrow_white)
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.toolbar))
        supportActionBar?.elevation = 0F
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val v = LayoutInflater.from(this).inflate(R.layout.toolbar,null)
        v.toolbar_title.text = getString(R.string.nav_about)
        supportActionBar?.customView = v
        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo_about)
                .addItem(Element().setTitle("Version 1.0"))
                //                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("hhl190295@gmail.com")
                .addFacebook("thuetot.vn")
                .addPlayStore("finger.thuetot.vn")
                .addWebsite("http://thuetot.vn/")
                .setDescription(resources.getString(R.string.txt_slogan))
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
        val intent = Intent(this@AboutUsActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        finish()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val ids = item?.itemId
        if (ids == android.R.id.home) {
            val intent = Intent(this@AboutUsActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            finish()
         //   super.onBackPressed()
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