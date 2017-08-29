package sega.fastnetwork.test.activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.gun0912.tedpermission.PermissionListener
import kotlinx.android.synthetic.main.activity_edit_product.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.imagepicker.TedBottomPicker
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageBean
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageShowPickerBean
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageShowPickerListener
import sega.fastnetwork.test.lib.imagepicker.showpicker.Loader
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.util.Constants
import java.net.URL


class EditProductActivity : AppCompatActivity() {
    val PLACE_PICKER_REQUEST = 3
    internal var list: List<ImageBean>? = null
    var temp: Int = 0
    var imgBean : ImageBean? = null
    var data: ArrayList<Product>? = null
    var mProduct: Product? =null
    var uriList: ArrayList<Uri>? = ArrayList()
    var uriImage: ArrayList<ImageBean>? = ArrayList()
    var imglist: ArrayList<String>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)
        setSupportActionBar(toolbar_editproduct)
        toolbar_editproduct.inflateMenu(R.menu.uploadproduct_menu)
        toolbar_editproduct.setTitleTextColor(Color.BLACK)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.back_arrow)
        supportActionBar!!.title = "Edit product"
        addressEdit.setOnClickListener {
            locationPlacesIntent()
        }
        ///=======================Spinner danh muc=========================
        val adaptercategory = ArrayAdapter.createFromResource(this@EditProductActivity, R.array.category, android.R.layout.simple_spinner_item)
        adaptercategory.setDropDownViewResource(android.R.layout.simple_list_item_checked)
        category.adapter = adaptercategory
///=======================Spinner time=========================
        val adaptertime = ArrayAdapter.createFromResource(this@EditProductActivity, R.array.timeid, android.R.layout.simple_spinner_item)
        adaptertime.setDropDownViewResource(android.R.layout.simple_list_item_checked)
        time.adapter = adaptertime
        // ================= get product ===============================================//
        var i:Intent = intent
        mProduct = i.getParcelableExtra("data")
        imglist = i.getStringArrayListExtra("imglist")

        setDataToView(mProduct)

        list = ArrayList<ImageBean>()

        for(i in 0..imglist?.size!!)
        {
            if(i!=imglist?.size!!) {
                Log.d("sssss", Constants.IMAGE_URL+imglist!![i])
                imgBean = ImageBean(Constants.IMAGE_URL+imglist!![i])
                uriImage?.add(imgBean!!)
            }
        }

        Log.d("sssss", uriList?.size.toString())
        list = uriImage

        add_picker_view!!.setImageLoaderInterface(Loader())
        add_picker_view!!.setNewData(list!!)
        add_picker_view!!.setMaxNum(4)
        add_picker_view!!.setShowAnim(true)
        if (android.os.Build.VERSION.SDK_INT > 9) {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        add_picker_view!!.setPickerListener(object : ImageShowPickerListener {
            override fun addOnClickListener(remainNum: Int) {
                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        val bottomSheetDialogFragment = TedBottomPicker.Builder(this@EditProductActivity)
                                .setOnMultiImageSelectedListener(object : TedBottomPicker.OnMultiImageSelectedListener {
                                    override fun onImagesSelected(uriList: ArrayList<Uri>) {
                                        for(i in 0..(uriList.size-1))
                                        {
                                            add_picker_view!!.addData(ImageBean(getRealFilePath(this@EditProductActivity, uriList[i])!!))
                                        }
                                     //   showUriList(uriList)
                                    }
                                })

                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setPeekHeight(1600)
                                .showTitle(false)
                                .setSelectMaxCount(4)
                                .setCompleteButtonText("Done")
                                .setEmptySelectionText("No Select")
                                .setSelectedUriList(add_picker_view!!.listUri)
                                .create()

                        bottomSheetDialogFragment.show(supportFragmentManager)


                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                        Toast.makeText(this@EditProductActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                    }


                }

             /*   TedPermission(this@EditProductActivity)
                  //      .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check()
                // */
            }

            override fun picOnClickListener(list: List<ImageShowPickerBean>, position: Int, remainNum: Int) {

            }

            override fun delOnClickListener(position: Int, remainNum: Int) {

            }
        })
        add_picker_view!!.show()

    }

    private fun setDataToView(mProduct: Product?) {
        productname.setText(mProduct?.productname)
        price.setText(mProduct?.price)
        time.setSelection(mProduct?.time!!.toInt())
        number.setText(mProduct?.number)
        category.setSelection(mProduct?.category!!.toInt())
        addressEdit.setText(mProduct?.address)
        description.setText(mProduct?.description)

    }

    fun getUriFromUrl(thisUrl: String): Uri {
        val url = URL(thisUrl)
        var builder = Uri.Builder()
                .scheme(url.protocol)
                .authority(url.authority)
                .appendPath(url.path)
        return builder!!.build()
    }
    private fun showUriList(uriList: ArrayList<Uri>) {
        add_picker_view!!.clear()
        temp = 0
        this.uriList = uriList


        if (uriList.size == 1) {
            add_picker_view!!.listUri = uriList
            add_picker_view!!.addData(ImageBean(getRealFilePath(this@EditProductActivity, uriList[0])!!))

        } else {
            val list = uriList.map { ImageBean(getRealFilePath(this@EditProductActivity, it)!!) }

            add_picker_view!!.addData(list)
            add_picker_view!!.listUri = uriList


        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editproduct, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val ids = item!!.itemId
        if (ids == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getRealFilePath(context: Context, uri: Uri?): String? {
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
    private fun locationPlacesIntent() {
        try {
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this@EditProductActivity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this@EditProductActivity, data)
                if (place != null) {
                    val latLng = place.latLng
                    var lot1 = latLng.latitude.toString()
                    var lat1 = latLng.longitude.toString()
                    val add = place.address as String
                    addressEdit.setText(add)
                }
            }

        }
    }
}
