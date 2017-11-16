package finger.thuetot.vn.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import finger.thuetot.vn.R
import finger.thuetot.vn.customview.ExtendedViewPager
import finger.thuetot.vn.customview.ImageLoader
import finger.thuetot.vn.customview.TouchImageView
import finger.thuetot.vn.util.Constants
import java.util.*

/**a
 * Created by Sega on 09/08/2016.
 */
class Fullscreen : Activity(), View.OnTouchListener {
    // These matrices will be used to move and zoom image
    internal var matrix = Matrix()
    internal var savedMatrix = Matrix()
    internal var start = PointF()
    internal var oldDist: Float = 0.toFloat()
    internal var listimage: ArrayList<String>? = ArrayList()
    internal var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_fullscreen)
        verifyStoragePermissions(this)


    }

    fun backscreen(view: View) {
        finish()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val view = v as ImageView
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                Log.d(TAG, "mode=DRAG")
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                Log.d(TAG, "oldDist=" + oldDist)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                    Log.d(TAG, "mode=ZOOM")
                }
            }
            MotionEvent.ACTION_MOVE ->
                /*  *//*  if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                }*//*
                else*/
                if (mode == ZOOM) {
                    val newDist = spacing(event)
                    Log.d(TAG, "newDist=" + newDist)
                    if (newDist > 10f) {
                        matrix.set(savedMatrix)
                        val scale = newDist / oldDist
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }
                }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                Log.d(TAG, "mode=NONE")
            }
        }
        // Perform the transformation
        view.imageMatrix = matrix
        return true // indicate event was handled
    }

    private fun spacing(event: MotionEvent): Float {

        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {

        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    internal inner class TouchImageAdapter : PagerAdapter() {
        override fun getCount(): Int {

            return listimage!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {

            val img = TouchImageView(container.context)
            val mLoader = ImageLoader(container.context)
            mLoader.DisplayImageFull(Constants.IMAGE_URL + listimage!![position], img)
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            return img
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {

            return view === `object`
        }
    }


    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        } else {
            init()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                init()
            }
        }
    }

    fun init() {
        val mViewPager = findViewById<View>(R.id.view_pager) as ExtendedViewPager
        val extras = intent.extras
        pos = extras!!.getInt("pos")
        println(pos.toString() + "ok")
        listimage = extras.getStringArrayList("data")
        assert(listimage != null)
        println(listimage!![pos])
        mViewPager.adapter = TouchImageAdapter()
        mViewPager.currentItem = pos
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
    }

    companion object {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        private val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // We can be in one of these 3 states
        val NONE = 0
        val DRAG = 1
        val ZOOM = 2
        private val TAG = "Touch"
        var mid = PointF()
        var mode = NONE
    }

}