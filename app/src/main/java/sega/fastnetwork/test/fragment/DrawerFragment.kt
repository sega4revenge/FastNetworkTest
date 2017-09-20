package sega.fastnetwork.test.fragment


import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_drawer_main.*
import kotlinx.android.synthetic.main.header.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.activity.AddActivity
import sega.fastnetwork.test.activity.ChangePasswordActivity
import sega.fastnetwork.test.activity.SearchActivity
import sega.fastnetwork.test.lib.imagepicker.TedBottomPicker
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageBean
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.model.Response
import sega.fastnetwork.test.model.User
import sega.fastnetwork.test.presenter.DrawerPresenter
import sega.fastnetwork.test.util.Constants
import java.io.File
import java.util.*


/**
 * Created by sega4 on 08/08/2017.
 */

class DrawerFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener,DrawerPresenter.DrawerView {
    override fun changeAvatarSuccess(t: Response) {
        AppManager.saveAccountUser(context, t.user!!, 0)
                        Log.e("pic",Constants.IMAGE_URL+ t.user!!.photoprofile)
                        Glide.with(context)
                                .load(Constants.IMAGE_URL+ t.user!!.photoprofile)
                                .thumbnail(0.1f)
                                .apply(options)
                                .into(navigation_view.getHeaderView(0).avatar_header)
    }

    override fun setErrorMessage(errorMessage: String) {
        Toast.makeText(activity,errorMessage,Toast.LENGTH_LONG).show()
    }

    override fun getUserDetail(response: Response) {
//        Log.e("getUserDetail",user.name + " " + user.email)
        navigation_view.getHeaderView(0).username_header.text = response.user!!.name
        txtname.text  = response.user!!.name
    }


    private val mDrawerHandler = Handler()
    private var mSelectedId: Int = 0
    private var morecategory = false
    private var isTablet: Boolean? = null
    var temp: Int = 0
    var uriList: Uri? = null
    internal var list: List<ImageBean>? = null

    var fragment: Fragment? = null
    var user :User?=null

    var photoprofile : String? = null
    private var mDrawerPresenter: DrawerPresenter? = null
    val options = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .placeholder(R.drawable.logo)
            .error(R.drawable.img_error)
            .priority(Priority.HIGH)!!
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
     
        // Setup toolbar
        isTablet = resources.getBoolean(R.bool.is_tablet)
        mDrawerPresenter = DrawerPresenter(this)

        if (view != null) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        user = AppManager.getUserDatafromAccount(context, AppManager.getAppAccount(context)!!)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        navigation_view!!.setNavigationItemSelectedListener(this)

        val mDrawerToggle = object : ActionBarDrawerToggle(activity,
                drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                super.onDrawerSlide(drawerView, 0f)
            }

            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                super.onDrawerSlide(drawerView, 0f)
            }
        }
        moreCategory.setImageResource(R.mipmap.ic_launcher)
        drawer_layout!!.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        navigation_view!!.menu.findItem(R.id.nav_1).isChecked = true
        navigate(R.id.nav_1)
        addproduct.setOnClickListener {
            startActivity(Intent(this@DrawerFragment.activity, AddActivity::class.java))
        }


        navigation_view.getHeaderView(0).username_header.text = user!!.name
        navigation_view.getHeaderView(0).email_header.text  = user!!.email
        linMotobike.setOnClickListener(){
            ChangeCategory(0,HomeFragment())
        }
        linelectronic.setOnClickListener(){
            ChangeCategory(1,HomeFragment())
        }
        linfashion.setOnClickListener(){
            ChangeCategory(2,HomeFragment())
        }
        linhome.setOnClickListener(){
            ChangeCategory(3,HomeFragment())
        }
        linmombaby.setOnClickListener(){
            ChangeCategory(4,HomeFragment())
        }
        lingdnt.setOnClickListener(){
            ChangeCategory(5,HomeFragment())
        }
        linshort.setOnClickListener(){
            ChangeCategory(6,HomeFragment())
        }
        linvpnn.setOnClickListener(){
            ChangeCategory(7,HomeFragment())
        }
        lindeff.setOnClickListener(){
            ChangeCategory(999,HomeFragment())
        }
        moreCategory.setOnClickListener(){
            if(mSelectedId == R.id.nav_1)
            {
                if(!morecategory)
                {
                    linmore.visibility = View.VISIBLE
                    divide.visibility = View.VISIBLE
                    moreCategory.setImageResource(R.mipmap.ic_hidecategory)
                    morecategory = true

                }else{

                    linmore.setVisibility(View.GONE)
                    moreCategory.setImageResource(R.mipmap.ic_launcher)
                    divide.visibility = View.GONE
                    morecategory = false
                }
            }else if(mSelectedId == R.id.nav_3){
                if(!morecategory)
                {
                    linmoreInfor.visibility = View.VISIBLE
                    divide.visibility = View.VISIBLE
                    moreCategory.setImageResource(R.mipmap.ic_hidecategory)
                    morecategory = true

                }else{

                    linmoreInfor.setVisibility(View.GONE)
                    moreCategory.setImageResource(R.mipmap.ic_launcher)
                    divide.visibility = View.GONE
                    morecategory = false
                }
            }

        }
        txtname.text = user!!.name
        txtemail.text = user!!.email
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
        Glide.with(activity)
                .load(photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(imgAvatar)
        changePass.setOnClickListener {
            val intentchangepw = Intent(activity,ChangePasswordActivity::class.java)
            startActivity(intentchangepw)
        }
        changeInfor.setOnClickListener {
            val aleftdialog = AlertDialog.Builder(activity)
            aleftdialog.setMessage("Enter new name:")
            val input = EditText(activity)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
            input.layoutParams = lp
            aleftdialog.setView(input)
            aleftdialog.setIcon(R.drawable.com_facebook_button_icon)
            aleftdialog.setPositiveButton("OK", { _, _ ->
//                Toast.makeText(activity,"Info Matched: "+input.text, Toast.LENGTH_SHORT).show()
                mDrawerPresenter!!.eidtInfoUser(user!!._id.toString(),input.text.toString())
            })
            aleftdialog.setNegativeButton("NO", { _, _ ->
                Toast.makeText(activity,"NO", Toast.LENGTH_SHORT).show()
            })
            aleftdialog.show()
        }
        list = ArrayList()
        Log.e("list", "======" + list!!.size)

        if (android.os.Build.VERSION.SDK_INT > 9) {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        edit_avatar.setOnClickListener {
            Toast.makeText(activity,"OK OK OK PICK",Toast.LENGTH_LONG).show()
            val permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {

                    val bottomSheetDialogFragment = TedBottomPicker.Builder(activity)
                            .setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
                                override fun onImageSelected(uri: Uri) {
                                    showUriList(uri)
                                }

//                                    override fun onImagesSelected(uriList: ArrayList<Uri>) =
//                                            showUriList(uriList)
                            })
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setSelectMaxCount(1)
                            .setCompleteButtonText("Done")
                            .setEmptySelectionText("No Select")
                            .create()

                    bottomSheetDialogFragment.show(fragmentManager)


                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>) =
                        Toast.makeText(activity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()


            }

            TedPermission.with(activity)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check()
            //
        }


    }
    private fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }
    private fun showUriList(uriList:Uri) {
        // Remove all views before
        // adding the new ones.


        temp = 0
        this.uriList = uriList


        imgAvatar.setImageURI(uriList)
        mDrawerPresenter!!.changeAvatar(File(getRealFilePath(activity, uriList)), user!!._id!!,user!!.photoprofile!!,context)
//        uploadImage(File(getRealFilePath(activity, uriList)), user!!._id!!,user!!.photoprofile!!)

//            edit_avatar!!.addData(ImageBean(getRealFilePath(activity, uriList)!!))


    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_drawer_main, container, false)

    fun isClosedDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()

    }
    private fun ChangeCategory(mCategory: Int, mFragment: Fragment){
        Log.d("Runnnnnnn",mCategory.toString())
         if (fragment != null) {
             if(mSelectedId == R.id.nav_1){
             val bundle = Bundle()
                 bundle.putInt("Category",mCategory)
             val transaction = activity.supportFragmentManager.beginTransaction()
             try {
                 var frag = mFragment
                 frag.arguments =  bundle
                 transaction.replace(R.id.content_frame, frag).commit()
             } catch (ignored: IllegalStateException) {
             }

         }
     }
    }


    private fun hideMoreAction(){
        linmore.visibility = View.GONE
        linmoreInfor.visibility = View.GONE
    }
    private fun navigate(itemId: Int) {
        /*  val elevation = findViewById(R.id.elevation)*/

        when (itemId) {
            R.id.nav_1 -> {
                mSelectedId = itemId
                toolbar_title.setText(R.string.nav_home)
                fragment = HomeFragment()

                hideMoreAction()
                // change status show/hide action profile => category
                if(linInfor.visibility != View.GONE){
                    linInfor.visibility = View.GONE
                }
                if(linCate.visibility != View.VISIBLE){
                    linCate.visibility = View.VISIBLE
                }
                morecategory = false
            }
            R.id.nav_2 -> {
                mSelectedId = itemId
                toolbar_title.setText(R.string.nav_category)
                fragment = CategoryFragment()

                hideMoreAction()

                if(categorylist.visibility != View.GONE){
                    categorylist.visibility = View.GONE
                }
                morecategory = false
            }
            R.id.nav_3 -> {
                mSelectedId = itemId
                toolbar_title.setText(R.string.nav_category)
                fragment = DetailProfileFragment()

                hideMoreAction()

                // change status show/hide action category => profile
                if(linCate.visibility != View.GONE){
                    linCate.visibility = View.GONE
                }
                if(linInfor.visibility != View.VISIBLE){
                    linInfor.visibility = View.VISIBLE
                }
                morecategory = false

            }
            R.id.nav_4 -> {
//                mSelectedId = itemId
//                toolbar_title.setText("ChangePassword")
//                val intent = Intent((activity as AppCompatActivity), ChangePasswordActivity::class.java)
//                startActivity(intent)
                                AppManager.removeAccount(activity)
//
                return
            }
            R.id.nav_5 -> {
                val intent = Intent((activity as AppCompatActivity), SearchActivity::class.java)
                startActivity(intent)
            }

//            R.id.nav_6 -> {
//                /*   startActivity(new Intent(this, AboutActivity.class));*/
//                navigation_view!!.menu.findItem(mSelectedId).isChecked = true
//                return
//            }
//            R.id.nav_8 -> {
//                /*   startActivity(new Intent(this, AboutActivity.class));*/
//                AppManager.removeAccount(activity)
//
//                return
//            }
        }

        /*  val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(4f))*/

        if (fragment != null) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            try {
                val bundle = Bundle()
                bundle.putInt("Category",999)
                println("chuan bi")
                fragment?.arguments =  bundle
                transaction.replace(R.id.content_frame, fragment).commit()

                //elevation shadow
                /*  if (elevation != null) {
                      params.topMargin = if (navFragment is HomeFragment) dp(48f) else 0

                      val a = object : Animation() {
                          override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                              elevation.layoutParams = params
                          }
                      }
                      a.duration = 150
                      elevation.startAnimation(a)
                  }*/
            } catch (ignored: IllegalStateException) {
            }

        }
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        mSelectedId = menuItem.itemId
        mDrawerHandler.removeCallbacksAndMessages(null)
        mDrawerHandler.postDelayed({ navigate(mSelectedId) }, 250)
        drawer_layout.closeDrawers()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
      
    }


    companion object {

        private val SELECTED_ITEM_ID = "SELECTED_ITEM_ID"
    }




}


