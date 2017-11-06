package sega.fastnetwork.test.activity

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
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
import kotlinx.android.synthetic.main.activity_add.*
import org.json.JSONObject
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.imagepicker.TedBottomPicker
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageBean
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageShowPickerBean
import sega.fastnetwork.test.lib.imagepicker.showpicker.ImageShowPickerListener
import sega.fastnetwork.test.lib.imagepicker.showpicker.Loader
import sega.fastnetwork.test.manager.AppManager
import sega.fastnetwork.test.presenter.AddPresenter
import sega.fastnetwork.test.util.CompressImage
import sega.fastnetwork.test.util.Constants
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddActivity : AppCompatActivity(), AddPresenter.AddView {

    private val PLACE_PICKER_REQUEST = 3
    var mNotificationManager: NotificationManager? = null
    val TAG: String = AddActivity::class.java.simpleName
    var temp: Int = 0
    internal var list: List<ImageBean>? = null
    var uriList: ArrayList<Uri>? = ArrayList()
    var mRemoteView: RemoteViews? = null
    var mTimeRemain: Double = 0.0
    var mPercent: Int = 0
    var lat1: String = ""
    var lot1: String = ""
    var mDoubleCreate = false
    private var mAddPresenter: AddPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar_addproduct)
        toolbar_addproduct.inflateMenu(R.menu.uploadproduct_menu)
        toolbar_addproduct.setTitleTextColor(Color.WHITE)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_backarrow_white)
        supportActionBar!!.title = getString(R.string.ac_add)
///=======================Cho thue/ Can thue=========================
        toggle.setOnCheckedChangeListener { _, checkedId ->
            run {
                if (checkedId == R.id.borrow) {
                    add_picker_view.visibility = View.VISIBLE
                    price_and_time.visibility = View.VISIBLE
                } else {
                    add_picker_view.visibility = View.GONE
                    price_and_time.visibility = View.GONE
                }
            }
        }
///=======================Lay dia chi=========================
        addressEdit.setOnClickListener {
            map_loading.visibility = View.VISIBLE
            addressEdit.visibility = View.GONE
            locationPlacesIntent()
//            addressEdit.isEnabled = false
        }
///=======================Spinner danh muc=========================



///=======================Spinner time=========================
        val timelist = Arrays.asList(*resources.getStringArray(R.array.timeid))
        val categorylist = Arrays.asList(*resources.getStringArray(R.array.category))
        time.setItems(timelist)
        category.setItems(categorylist)


        list = ArrayList()
        mAddPresenter = AddPresenter(this)
        Log.e("list", "======" + list!!.size)
        add_picker_view!!.setImageLoaderInterface(Loader())
        add_picker_view!!.setNewData(list!!)
        add_picker_view!!.setMaxNum(4)
        add_picker_view!!.setShowAnim(true)
        if (android.os.Build.VERSION.SDK_INT > 9) {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        add_picker_view!!.setPickerListener(object : ImageShowPickerListener {
            override fun addOnClickListener(remainNum: Int) {
                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        val bottomSheetDialogFragment = TedBottomPicker.Builder(this@AddActivity)
                                .setOnMultiImageSelectedListener(object : TedBottomPicker.OnMultiImageSelectedListener {
                                    override fun onImagesSelected(uriList: ArrayList<Uri>) =
                                            showUriList(uriList)
                                })

                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setPeekHeight(1600)
                                .showTitle(false)
                                .setSelectMaxCount(4)
                                .setCompleteButtonText(getString(R.string.ac_add))
                                .setEmptySelectionText(getString(R.string.noselect))
                                .setSelectedUriList(add_picker_view!!.listUri)
                                .create()

                        bottomSheetDialogFragment.show(supportFragmentManager)


                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) =
                            Toast.makeText(this@AddActivity, getString(R.string.per_deni) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()


                }

                TedPermission.with(this@AddActivity)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage(getString(R.string.per_turnon))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check()
                //
            }

            override fun picOnClickListener(list: List<ImageShowPickerBean>, position: Int, remainNum: Int) {

            }

            override fun delOnClickListener(position: Int, remainNum: Int) {

            }
        })
        add_picker_view!!.show()

//        jtb.setOnCheckedChangeListener(this@AddActivity)

//        setMultiShowButton()


    }

    override fun onDestroy() {
        super.onDestroy()
        mAddPresenter!!.cancelRequest() }

    private fun locationPlacesIntent() {
        try {
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this@AddActivity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        map_loading.visibility = View.GONE
        addressEdit.visibility = View.VISIBLE
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this@AddActivity, data)
                if (place != null) {
                    val latLng = place.latLng
                    lat1 = latLng.latitude.toString()
                    lot1 = latLng.longitude.toString()
                    val add = place.address as String
                    //
                    addressText.setText(add)
                }
            }

        }
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

    private fun showUriList(uriList: ArrayList<Uri>) {
        // Remove all views before
        // adding the new ones.

        add_picker_view!!.clear()
        temp = 0
        this.uriList = uriList


        if (uriList.size == 1) {
            add_picker_view!!.listUri = uriList
            add_picker_view!!.addData(ImageBean(getRealFilePath(this@AddActivity, uriList[0])!!))

        } else {
            val list = uriList.map { ImageBean(getRealFilePath(this@AddActivity, it)!!) }

            add_picker_view!!.addData(list)
            add_picker_view!!.listUri = uriList


        }


    }

    override fun isCreateSuccess(success: Boolean, productid: String, type: String) {
        Log.e("PROID", productid)
        if (success) {
            if (type == "1") {
                uploadImage(File(getRealFilePath(this@AddActivity, uriList!![temp])), productid)
            }else{
                progressBar_addproduct.visibility = View.GONE
                setResult(Activity.RESULT_OK,Intent())
                finish()
                mDoubleCreate = true
            }
        }
        else {
            progressBar_addproduct.visibility = View.GONE
            Snackbar.make(findViewById(R.id.root_addproduct),getString(R.string.server_unreachable), Snackbar.LENGTH_SHORT).show()
        }
    }

    fun uploadImage(file: File, productid: String) {

        mTimeRemain = 0.0
        mPercent = 0
        mRemoteView = RemoteViews(packageName, R.layout.notification_upload)
        mRemoteView!!.setImageViewResource(R.id.image, R.drawable.logo_fn)
        mRemoteView!!.setTextViewText(R.id.title, getString(R.string.uploading, temp + 1, uriList!!.size))
        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_fn)
                .setContent(mRemoteView)
        val notification = mBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        mNotificationManager?.notify(1, notification)


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



                    mNotificationManager?.notify(1, notification)
                })
            }
        }

        val timer = Timer()
        timer.scheduleAtFixedRate(task, 0, 500)
        val startTime = System.nanoTime()
        val observable = Rx2AndroidNetworking.upload(Constants.BASE_URL + "upload")
                .addMultipartParameter("productid", productid)
                .addMultipartFile("image", CompressImage.compressImage(file, this))
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
                        progressBar_addproduct.visibility = View.GONE
                        Snackbar.make(findViewById(R.id.root_addproduct),getString(R.string.server_unreachable), Snackbar.LENGTH_SHORT).show()
                        timer.cancel()
                        timer.purge()
                        mNotificationManager?.cancel(1)
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
                            uploadImage(File(getRealFilePath(this@AddActivity, uriList?.get(temp))), productid)
                        } else {
                            mDoubleCreate = true
                            mRemoteView!!.setProgressBar(R.id.progressbarupload, 0, 0, false)
                            mRemoteView!!.setTextViewText(R.id.title, getString(R.string.up_success))
                            mRemoteView!!.setTextViewText(R.id.timeremain, getString(R.string.uploaded_title, temp, uriList!!.size))
                            FirebaseMessaging.getInstance().subscribeToTopic(productid)
                            mNotificationManager?.cancel(1)
                            mNotificationManager?.notify(1, notification)
                            progressBar_addproduct.visibility = View.GONE
                            setResult(Activity.RESULT_OK,Intent())
                            finish()
                        }
                    }
                })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.uploadproduct_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_uploadproduct) {
            if (toggle.checkedRadioButtonId == borrow.id) {
//                Toast.makeText(this, time.selectedIndex.toString() + " " + category.selectedIndex.toString(), Toast.LENGTH_LONG).show()
                if (uriList!!.size == 0) {
                    Toast.makeText(this, getString(R.string.choose_image), Toast.LENGTH_LONG).show()
                } else if (productname!!.text.toString() == "" || price!!.text.toString() == "" || number!!.text.toString() == "" || addressText!!.text.toString() == "" || description!!.text.toString() == "") {
                    Toast.makeText(this, getString(R.string.st_errpass), Toast.LENGTH_LONG).show()
                } else {
                    if(!mDoubleCreate) {
                        progressBar_addproduct.visibility = View.VISIBLE
                        temp = 0
                        mAddPresenter!!.createProduct(AppManager.getAppAccountUserId(this), productname.text.toString(), price.text.toString(), time.selectedIndex.toString(), number.text.toString(), category.selectedIndex.toString(), addressText.text.toString(), description.text.toString(), lat1, lot1, Constants.BORROW)
                    }else{
                     Snackbar.make(findViewById(R.id.root_addproduct), getString(R.string.up_success), Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else if (toggle.checkedRadioButtonId == needborrow.id) {

                if (productname!!.text.toString() == "" || number!!.text.toString() == "" || category.selectedIndex.toString() == "" || addressText!!.text.toString() == "" || description!!.text.toString() == "") {
                    Toast.makeText(this, getString(R.string.input), Toast.LENGTH_LONG).show()
                } else {
                    if(!mDoubleCreate) {
                        progressBar_addproduct.visibility = View.VISIBLE
                        temp = 0
                        mAddPresenter!!.createProduct(AppManager.getAppAccountUserId(this), productname.text.toString(), "", "", number.text.toString(), category.selectedIndex.toString(), addressText.text.toString(), description.text.toString(), lat1, lot1, Constants.NEEDBORROW)
                    }else{
                        Snackbar.make(findViewById(R.id.root_addproduct),getString(R.string.up_success), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            System.out.println("upload")
//            startActivity(Intent(applicationContext, AddActivity::class.java))
            return true
        } else if (id == android.R.id.home) {
//            Log.e("AaaA","AAA")
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
