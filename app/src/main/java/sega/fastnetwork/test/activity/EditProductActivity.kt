package sega.fastnetwork.test.activity

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.androidnetworking.error.ANError
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_product.*
import org.json.JSONObject
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.imagepicker.TedBottomPicker
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageBean
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageShowPickerBean
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageShowPickerListener
import sega.fastnetwork.test.lib.imagepicker.showpicker.Loader
import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.presenter.EditProductPresenter
import sega.fastnetwork.test.util.CompressImage
import sega.fastnetwork.test.util.Constants
import java.io.File
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class EditProductActivity : AppCompatActivity(),EditProductPresenter.EditProductView {


    val PLACE_PICKER_REQUEST = 3
    val TAG: String = EditProductActivity::class.java.simpleName
    internal var list: List<ImageBean>? = null
    var temp: Int = 0
    var imgBean : ImageBean? = null
    var data: ArrayList<Product>? = null
    var mProduct: Product? =null
    var mRemoteView: RemoteViews? = null
    var mTimeRemain: Double = 0.0
    var mPercent: Int = 0
    var uriList: ArrayList<Uri>? = ArrayList()
    var uriImage: ArrayList<ImageBean>? = ArrayList()
    var imglist: ArrayList<String>? = ArrayList()
    var imglistDel = ""
    var mType = 0
    var mCountImg = 0
    var progress : ProgressDialog? = null
    var statusproduct : String? = null
    private var editProduct: EditProductPresenter? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)
        setSupportActionBar(toolbar_editproduct)
        toolbar_editproduct.inflateMenu(R.menu.uploadproduct_menu)
        toolbar_editproduct.setTitleTextColor(Color.WHITE)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_backarrow_white)
        supportActionBar!!.title = getString(R.string.edit_product)
        editProduct = EditProductPresenter(this)
        addressEdit.setOnClickListener {
            locationPlacesIntent()
        }

        ///=======================Spinner danh muc=========================

///=======================Spinner time=========================
        val timelist = Arrays.asList(*resources.getStringArray(R.array.timeid))
        val categorylist = Arrays.asList(*resources.getStringArray(R.array.category))
        time.setItems(timelist)
        category.setItems(categorylist)
        // ================= get product ===============================================//
        var i:Intent = intent
        mType = i.getIntExtra("type",1)
        if(mType!=2)
        {
            imglist = i.getStringArrayListExtra("imglist")
            for(i in 0..imglist?.size!!)
            {
                if(i!=imglist?.size!!) {
                    mCountImg++;
                    imgBean = ImageBean(Constants.IMAGE_URL+imglist!![i])
                    uriImage?.add(imgBean!!)
                }
            }
        }else{
            price_and_time.visibility = View.GONE
            add_picker_view!!.visibility = View.GONE
            layout_switch.visibility = View.GONE
        }
        mProduct = i.getParcelableExtra("data")
        setDataToView(mProduct)
        list = ArrayList<ImageBean>()
        list = uriImage
        add_picker_view!!.setImageLoaderInterface(Loader())
        add_picker_view!!.setNewData(list!!)
        add_picker_view!!.setMaxNum(4)
        add_picker_view!!.setShowAnim(true)
        if (android.os.Build.VERSION.SDK_INT > 9) {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
//        if(switch_edit.isChecked){
//            edt_switch.text = switch_edit.textOn.toString()
//        } else {
//            edt_switch.text = switch_edit.textOff.toString()
//
//        }
        switch_edit.setOnCheckedChangeListener({ _, _ ->
            run {
                if(switch_edit.isChecked){
                    edt_switch.text = switch_edit.textOn.toString()
//                    statusproduct = "0"
                } else {
                    edt_switch.text = switch_edit.textOff.toString()
//                    statusproduct = "1"
                }
            }
        })
        add_picker_view!!.setPickerListener(object : ImageShowPickerListener {
            override fun addOnClickListener(remainNum: Int) {
                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        val bottomSheetDialogFragment = TedBottomPicker.Builder(this@EditProductActivity)
                                .setOnMultiImageSelectedListener(object : TedBottomPicker.OnMultiImageSelectedListener {
                                    override fun onImagesSelected(uriListselect: ArrayList<Uri>) {
                                        for(i in 0..(uriListselect.size-1))
                                        {
                                            uriList?.add(uriListselect[i])
                                            mCountImg++
                                            add_picker_view!!.addData(ImageBean(getRealFilePath(this@EditProductActivity, uriListselect[i])!!))
                                        }

                                    }
                                })

                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setPeekHeight(1600)
                                .showTitle(false)
                                .setSelectMaxCount((4 - mCountImg))
                                .setCompleteButtonText(getString(R.string.done))
                                .setEmptySelectionText(getString(R.string.noselect))
                                .setSelectedUriList(add_picker_view!!.listUri)
                                .create()

                        bottomSheetDialogFragment.show(supportFragmentManager)


                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                        Toast.makeText(this@EditProductActivity, getString(R.string.per_deni) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                    }


                }

                TedPermission.with(this@EditProductActivity)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage(getString(R.string.per_turnon))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check()
                //
            }

            override fun picOnClickListener(list: List<ImageShowPickerBean>, position: Int, remainNum: Int) {
            }

            override fun delOnClickListener(position: Int, remainNum: Int) {
                if((imglist?.size!!-1) >= position) {
                    if (!imglistDel.equals("")) {
                        imglistDel = imglistDel + " , " + imglist?.get(position)
                        imglist?.remove(imglist?.get(position)!!)
                    } else {
                        imglistDel = imglist?.get(position)!!
                        imglist?.remove(imglist?.get(position)!!)
                    }
                }else {
                    var listup = position - imglist?.size!!
                    uriList?.remove(uriList?.get(listup))
                }
                mCountImg--
            }
        })
        add_picker_view!!.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        editProduct?.cancelRequest()
    }

    override fun isCreateSuccess(success: Boolean,mType: Int) {
        if(success)
        {

            if(uriList?.size!! > 0) {
                uploadImage(File(getRealFilePath(this@EditProductActivity, uriList!![temp])), mProduct?._id.toString())
            }else{
                progress?.dismiss()
                if(mType!=0) {
                    val intent = this.intent
                    this.setResult(0,intent)
               }else{
                    val intent = this.intent
                    this.setResult(1, intent)
                }
                finish()
            }
        }

    }
    private fun setDataToView(mProduct: Product?) {
        productname.setText(mProduct?.productname)

        if(mProduct?.price != null && mProduct.price.toString() != "")
        {
            price.setText(mProduct.price)
        }
        if(mProduct?.time != null && mProduct.time.toString() != "")
        {
            time.selectedIndex = mProduct.time!!.toInt()
           // time.setSelection(mProduct.time!!.toInt())
        }
        number.setText(mProduct?.number)
        category.selectedIndex = mProduct?.category!!.toInt()
        //category.setSelection(mProduct?.category!!.toInt())
        addressText.setText(mProduct.location!!.address)
        description.setText(mProduct.description)
        switch_edit.isChecked = mProduct.status == "0"
        Log.e("statusproduct1",mProduct.status)
    }

    override fun onBackPressed() {
        setResult(999)
        finish()
        super.onBackPressed()
    }

    fun getUriFromUrl(thisUrl: String): Uri {
        val url = URL(thisUrl)
        var builder = Uri.Builder()
                .scheme(url.protocol)
                .authority(url.authority)
                .appendPath(url.path)
        return builder!!.build()
    }

    fun uploadImage(file: File, productid: String) {

        mTimeRemain = 0.0
        mPercent = 0
        mRemoteView = RemoteViews(packageName, R.layout.notification_upload)
        mRemoteView!!.setImageViewResource(R.id.image, R.mipmap.ic_arrow_right_white)
        mRemoteView!!.setTextViewText(R.id.title, getString(R.string.uploading, temp + 1, uriList!!.size))
        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo2)
                .setContent(mRemoteView)
        val notification = mBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

     //   mNotificationManager?.notify(1, notification)


        val task = object : TimerTask() {
            private val mHandler = Handler(Looper.getMainLooper())

            override fun run() {
                mHandler.post({

                    mRemoteView!!.setProgressBar(R.id.progressbarupload, 100, mPercent, false)
                    System.out.println(mTimeRemain)
                    if (mTimeRemain.toInt() / 60 > 0) {

                        mRemoteView!!.setTextViewText(R.id.timeremain, String.format(resources.getString(R.string.remain_time_minute), mTimeRemain.toInt() / 60))
                    } else {
                        mRemoteView!!.setTextViewText(R.id.timeremain, String.format(resources.getString(R.string.remain_time_second), mTimeRemain.toInt()))
                    }



                   // mNotificationManager?.notify(1, notification)
                })
            }
        }

        val timer = Timer()
        timer.scheduleAtFixedRate(task, 0, 500)
        val startTime = System.nanoTime()
        val observable = Rx2AndroidNetworking.upload(Constants.BASE_URL + "upload")
                .addMultipartParameter("productid", productid)
                .addMultipartFile("image", CompressImage.compressImage(file,this))
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(TAG, " bytesSent : " + bytesSent)
                    Log.d(TAG, " bytesReceived : " + bytesReceived)
                    Log.d(TAG, " isFromCache : " + isFromCache)
                }
                .setUploadProgressListener { bytesUploaded, totalBytes ->

                    val elapsedTime = System.nanoTime() - startTime
                    val allTimeForDownloading = elapsedTime * totalBytes / bytesUploaded
                    val remainingTime = allTimeForDownloading - elapsedTime
                    mTimeRemain = remainingTime.toDouble() / 1000000000.0
                    mPercent = (bytesUploaded * 100 / totalBytes).toInt()


                }
                .jsonObjectObservable
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<JSONObject> {
                    override fun onComplete() {
                        Log.d(TAG + "_1", "onComplete Detail : uploadImage completed")
                        Log.d("_1111", temp.toString())
                        if (temp == uriList!!.size) {
                            progress?.dismiss()
                            setResult(1)
                            finish()
                        }
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            if (e.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG + "_1", "onError errorCode : " + e.errorCode)
                                Log.d(TAG + "_1", "onError errorBody : " + e.errorBody)
                                Log.d(TAG + "_1", "onError errorDetail : " + e.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG + "_1", "onError errorDetail : " + e.errorDetail)
                            }
                        } else {
                            Log.d(TAG + "_1", "onError errorMessage : " + e.message)
                        }
                        timer.cancel()
                        timer.purge()
                        //mNotificationManager?.cancel(1)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(response: JSONObject) {
                        mRemoteView!!.setProgressBar(R.id.progressbarupload, 100, 100, false)
                        mRemoteView!!.setTextViewText(R.id.timeremain, "")
                        timer.cancel()
                        timer.purge()

                        Log.d(TAG + "_1", "Image upload Completed" + temp)
                        Log.d(TAG + "_1", "onResponse object : " + response.toString())
                        temp++
                        if (temp + 1 <= uriList!!.size) {
                            uploadImage(File(getRealFilePath(this@EditProductActivity, uriList?.get(temp))), productid)
                        } else {
                            mRemoteView!!.setProgressBar(R.id.progressbarupload, 0, 0, false)
                            mRemoteView!!.setTextViewText(R.id.title, getString(R.string.up_success))
                            mRemoteView!!.setTextViewText(R.id.timeremain, getString(R.string.uploaded_title, temp, uriList!!.size))
                       //     Snackbar.make(findViewById(R.id.root_addproduct), "Uploaded successfully", Snackbar.LENGTH_SHORT).show()
                            FirebaseMessaging.getInstance().subscribeToTopic(productid)
                         //   mNotificationManager?.cancel(1)
                      //      mNotificationManager?.notify(1, notification)
                        }
                    }
                })


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
        if (ids == R.id.menu_editproduct) {
            if(productname.text.equals("") || price.text.equals("") || number.text.equals("") || addressText.text.equals(""))
            {
                Snackbar.make(findViewById(R.id.root_addproduct), getString(R.string.up_fail), Snackbar.LENGTH_SHORT).show()
            }else{
           //     Log.d("AAAAAAAAA",productname.text.toString() + price.text.toString() + time.selectedItem.toString()+ number.text.toString() + category.selectedItem.toString()+ addressEdit.text.toString()+ description.text.toString()+ mProduct?._id.toString()+imglistDel )
                if(imglistDel.equals(""))
                {imglistDel="0"}
                if(switch_edit.isChecked){
                    statusproduct = "0"
                }
                else{
                    statusproduct = "1"
                }
                Log.e("statusproduct","statusproduct: "+ statusproduct)
                progress  =  ProgressDialog.show(this, "", getString(R.string.loading), true)
                progress?.show()
                editProduct!!.ConnectHttp("", productname.text.toString() , price.text.toString() , time.selectedIndex.toString(), number.text.toString() , category.selectedIndex.toString(), addressText.text.toString(), description.text.toString(), statusproduct.toString(), mProduct?._id.toString(),imglistDel)
            }

        }else if (ids == R.id.menu_delproduct){
            imglistDel = ""
            uriList?.clear()
            if(imglist != null && imglist?.size!! >0) {
                for (i in 0..(imglist?.size!! - 1)) {
                    if (!imglistDel.equals("")) {
                        imglistDel = imglistDel + " , " + imglist?.get(i)
                    } else {
                        imglistDel = imglist?.get(i).toString()

                        if(imglist?.size == 1){
                            imglistDel += " , "
                        }
                    }
                }
            }
            if(imglistDel.equals(""))
            {imglistDel="0"}
            progress  =  ProgressDialog.show(this, "", getString(R.string.loading), true)
            progress?.show()
            editProduct!!.ConnectHttpDeleteProduct(mProduct?._id.toString(),imglistDel)
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
                    addressText.setText(add)
                }
            }

        }
    }
}
