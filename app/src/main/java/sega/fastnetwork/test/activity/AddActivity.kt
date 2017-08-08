package sega.fastnetwork.test.activity

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
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
import android.widget.RemoteViews
import android.widget.Toast
import com.androidnetworking.error.ANError
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
import sega.fastnetwork.test.manager.AppAccountManager
import sega.fastnetwork.test.presenter.AddPresenter
import sega.fastnetwork.test.util.Constants
import sega.fastnetwork.test.view.AddView
import java.io.File
import java.util.*

class AddActivity : AppCompatActivity(), AddView {


    var mNotificationManager: NotificationManager? = null
    val TAG = AddActivity::class.java.simpleName
    var temp: Int = 0
    internal var list: List<ImageBean>? = null
    var uriList: ArrayList<Uri>? = ArrayList()
    var mRemoteView: RemoteViews? = null
    var mTimeRemain: Double = 0.0
    var mPercent: Int = 0
    var mAddPresenter: AddPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        list = ArrayList<ImageBean>()
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
                        System.out.println("cho roi")
                        val bottomSheetDialogFragment = TedBottomPicker.Builder(this@AddActivity)
                                .setOnMultiImageSelectedListener(object : TedBottomPicker.OnMultiImageSelectedListener {
                                    override fun onImagesSelected(uriList: ArrayList<Uri>) {
                                        showUriList(uriList)
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
                        Toast.makeText(this@AddActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                    }


                }

                TedPermission(this@AddActivity)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
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




        setMultiShowButton()


    }


    private fun setMultiShowButton() {
        println(uriList!!.size)
        button12345!!.setOnClickListener {
            if (uriList!!.size == 0) {
                Toast.makeText(this, "Please choose image", Toast.LENGTH_LONG).show()
            }
            else if (productname!!.text.toString().equals("") || price!!.text.toString().equals("") || description!!.text.toString().equals("")) {
                Toast.makeText(this, "Please input", Toast.LENGTH_LONG).show()
            }
            else {
                temp = 0
                mAddPresenter!!.createProduct(AppAccountManager.getAppAccountUserId(this),productname.text.toString(),price.text.toString(),description.text.toString())

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

    override fun isCreateSuccess(success: Boolean, productid: String) {
        if (success)
            uploadImage(File(getRealFilePath(this@AddActivity, uriList!![temp])), productid)
    }

    fun uploadImage(file: File, productid: String) {

        mTimeRemain = 0.0
        mPercent = 0
        mRemoteView = RemoteViews(packageName, R.layout.notification_upload)
        mRemoteView!!.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        mRemoteView!!.setTextViewText(R.id.title, getString(R.string.uploading, temp + 1, uriList!!.size))
        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo2)
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
                .addMultipartFile("image", file)
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
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG + "_1", "onError errorCode : " + anError.errorCode)
                                Log.d(TAG + "_1", "onError errorBody : " + anError.errorBody)
                                Log.d(TAG + "_1", "onError errorDetail : " + anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG + "_1", "onError errorDetail : " + anError.errorDetail)
                            }
                        } else {
                            Log.d(TAG + "_1", "onError errorMessage : " + e.message)
                        }
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
                            mRemoteView!!.setProgressBar(R.id.progressbarupload, 0, 0, false)
                            mRemoteView!!.setTextViewText(R.id.title, "Uploaded successfully")
                            mRemoteView!!.setTextViewText(R.id.timeremain, getString(R.string.uploaded_title, temp, uriList!!.size))
                            Snackbar.make(findViewById(R.id.root_addproduct), "Uploaded successfully", Snackbar.LENGTH_SHORT).show()
                            mNotificationManager?.cancel(1)
                            mNotificationManager?.notify(1, notification)
                        }
                    }
                })


    }

}
