package sega.fastnetwork.test.lib.imagepicker


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.tedbottompicker_content_view.view.*
import kotlinx.android.synthetic.main.tedbottompicker_selected_item.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.imagepicker.adapter.ImageGalleryAdapter
import sega.fastnetwork.test.lib.imagepicker.adapter.PhotoGalleryAdapter
import sega.fastnetwork.test.lib.imagepicker.model.Photo
import sega.fastnetwork.test.lib.imagepicker.model.PhotoDirectory
import sega.fastnetwork.test.lib.imagepicker.util.MediaStoreHelper
import sega.fastnetwork.test.lib.imagepicker.util.RealPathUtil
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TedBottomPicker : BottomSheetDialogFragment() {
    var builder: Builder? = null
    internal var imageGalleryAdapter: ImageGalleryAdapter? = null
    internal var view_title_container: View? = null
    internal var selectedfolder: Int? = null




    internal var contentView: View? = null
    internal var selectedUriList: ArrayList<Uri>? = null
    internal var tempUriList: ArrayList<Uri>? = null
    private var cameraImageUri: Uri? = null
    var listfolder: ArrayList<PhotoDirectory>? = null
    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {


        override fun onStateChanged(bottomSheet: View, newState: Int) {
            Log.d(TAG, "onStateChanged() newState: " + newState)

            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAllowingStateLoss()

            }


        }


        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            Log.d(TAG, "onSlide() slideOffset: " + slideOffset)


        }
    }
    private var galleryAdapter: PhotoGalleryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSavedInstanceState(savedInstanceState)

        //  setRetainInstance(true);
    }

    private fun setupSavedInstanceState(savedInstanceState: Bundle?) {


        if (savedInstanceState == null) {
            cameraImageUri = builder!!.selectedUri
            tempUriList = builder!!.selectedUriList
        } else {
            cameraImageUri = savedInstanceState.getParcelable(EXTRA_CAMERA_IMAGE_URI)
            tempUriList = savedInstanceState.getParcelableArrayList<Uri>(EXTRA_CAMERA_SELECTED_IMAGE_URI)
        }


    }


    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putParcelable(EXTRA_CAMERA_IMAGE_URI, cameraImageUri)
        outState.putParcelableArrayList(EXTRA_CAMERA_SELECTED_IMAGE_URI, selectedUriList)
        super.onSaveInstanceState(outState)

    }

    fun show(fragmentManager: FragmentManager) {

        val ft = fragmentManager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(contentView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)


    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        contentView = View.inflate(context, R.layout.tedbottompicker_content_view, null)
        dialog.setContentView(contentView)
        val layoutParams = (contentView!!.parent as View?)?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            if (builder != null && builder!!.peekHeight > 0) {
                behavior.peekHeight = builder!!.peekHeight
            }

        }




        setRecyclerView()
        setSelectionView()

        selectedUriList = ArrayList()


        if (builder!!.onImageSelectedListener != null && cameraImageUri != null) {
            addUri(cameraImageUri!!)
        } else if (builder!!.onMultiImageSelectedListener != null && tempUriList != null) {
            for (uri in tempUriList!!) {
                addUri(uri)
            }
        }
       
        contentView!!.gallery_rcl.layoutManager = LinearLayoutManager(activity)
        galleryAdapter = PhotoGalleryAdapter(activity)
        contentView!!.gallery_rcl.adapter = galleryAdapter
        galleryAdapter!!.setOnItemClickListener(object : PhotoGalleryAdapter.OnItemClickListener {
            override fun onClick(photos: List<Photo>, position: Int) {

                contentView!!.slidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                contentView!!.folder.text = listfolder!![position].name
                imageGalleryAdapter!!.refresh(photos)
                selectedfolder = position

            }
        })
        contentView!!.btn_camera.setOnClickListener {
            startCameraIntent()
        }

        MediaStoreHelper.getPhotoDirs(activity, object : MediaStoreHelper.PhotosResultCallback {
            override fun onResultCallback(directories: List<PhotoDirectory>) {
                activity.runOnUiThread {
                    galleryAdapter!!.refresh(directories)

                    contentView!!.folder.text = directories[0].name
                    listfolder = directories as ArrayList<PhotoDirectory>

                }
            }
        })

       
        contentView!!.slidingUpPanelLayout!!.anchorPoint = 0.5f
        contentView!!.slidingUpPanelLayout!!.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {}

            override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {}
        })
        contentView!!.slidingUpPanelLayout!!.setFadeOnClickListener({ contentView!!.slidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED })

        setDoneButton()
        checkMultiMode()

    }


    private fun setSelectionView() {

        if (builder!!.emptySelectionText != null) {
            contentView!!.selected_photos_empty!!.text = builder!!.emptySelectionText
        }


    }

    private fun setDoneButton() {


        contentView!!.btn_done!!.setOnClickListener { onMultiSelectComplete() }
    }

    private fun onMultiSelectComplete() {

        if (selectedUriList!!.size < builder!!.selectMinCount) {
            val message: String
            if (builder!!.selectMinCountErrorText != null) {
                message = builder!!.selectMinCountErrorText!!
            } else {
                message = String.format(resources.getString(R.string.select_min_count), builder!!.selectMinCount)
            }

            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            return
        }


        builder!!.onMultiImageSelectedListener!!.onImagesSelected(selectedUriList!!)
        dismissAllowingStateLoss()
    }

    private fun checkMultiMode() {
        if (!isMultiSelect) {
            contentView!!.btn_done?.visibility = View.GONE
            contentView!!.selected_photos_container_frame?.visibility = View.GONE
        }

    }



    private fun setRecyclerView() {

        val gridLayoutManager = GridLayoutManager(activity, 3)
        contentView!!.rc_gallery!!.layoutManager = gridLayoutManager
        contentView!!.rc_gallery!!.addItemDecoration(GridSpacingItemDecoration(gridLayoutManager.spanCount, builder!!.spacing, false))
        updateAdapter()
    }

    private fun updateAdapter() {

        imageGalleryAdapter = ImageGalleryAdapter(
                activity, builder!!)
        contentView!!.rc_gallery!!.adapter = imageGalleryAdapter
        imageGalleryAdapter!!.setOnItemClickListener(object : ImageGalleryAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {

                val pickerTile = imageGalleryAdapter!!.getItem(position)

                when (pickerTile.tileType) {
                    ImageGalleryAdapter.PickerTile.IMAGE -> complete(pickerTile.imageUri!!)

                    else -> errorMessage()
                }

            }
        })
    }

    private fun complete(uri: Uri) {
        Log.d(TAG, "selected uri: " + uri.toString())
        //uri = Uri.parse(uri.toString());
        if (isMultiSelect) {


            if (selectedUriList!!.contains(uri)) {
                removeImage(uri)
            } else {
                addUri(uri)
            }


        } else {
            builder!!.onImageSelectedListener!!.onImageSelected(uri)
            dismissAllowingStateLoss()
        }

    }

    private fun addUri(uri: Uri): Boolean {


        if (selectedUriList!!.size == builder!!.selectMaxCount) {
            val message: String
            if (builder!!.selectMaxCountErrorText != null) {
                message = builder!!.selectMaxCountErrorText!!
            } else {
                message = String.format(resources.getString(R.string.select_max_count), builder!!.selectMaxCount)
            }

            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            return false
        }


        selectedUriList!!.add(uri)

        val rootView = LayoutInflater.from(activity).inflate(R.layout.tedbottompicker_selected_item, null)


        rootView.tag = uri

        contentView!!.selected_photos_container?.addView(rootView, selectedUriList!!.size - 1)



// Set only target params:


        val px = resources.getDimension(R.dimen.tedbottompicker_selected_image_height).toInt()
        rootView.selected_photo.layoutParams = FrameLayout.LayoutParams(px, px)

        if (builder!!.imageProvider == null) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.ic_gallery)
                    .error(R.drawable.img_error)
                    .priority(Priority.HIGH)

            Glide.with(activity)
                    .load(uri)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(rootView.selected_photo as ImageView)
        } else {
            builder!!.imageProvider!!.onProvideImage(rootView.selected_photo as ImageView, uri)
        }


        if (builder!!.deSelectIconDrawable != null) {

            rootView.iv_close.setImageDrawable(builder!!.deSelectIconDrawable)
        }

        rootView.iv_close.setOnClickListener { removeImage(uri) }


        updateSelectedView()
        imageGalleryAdapter!!.setSelectedUriList(selectedUriList!!, uri)
        return true

    }

    private fun removeImage(uri: Uri) {

        selectedUriList!!.remove(uri)


        for (i in 0..contentView!!.selected_photos_container!!.childCount - 1) {
            val childView = contentView!!.selected_photos_container!!.getChildAt(i)


            if (childView.tag == uri) {
                contentView!!.selected_photos_container!!.removeViewAt(i)
                break
            }
        }

        updateSelectedView()
        imageGalleryAdapter!!.setSelectedUriList(selectedUriList!!, uri)
    }

    private fun updateSelectedView() {

        if (selectedUriList == null || selectedUriList!!.size == 0) {
            contentView!!.selected_photos_empty!!.visibility = View.VISIBLE
            contentView!!.selected_photos_container!!.visibility = View.GONE
            System.out.println(" so6 luong")
        } else {
            contentView!!.selected_photos_empty!!.visibility = View.GONE
            contentView!!.selected_photos_container!!.visibility = View.VISIBLE
            System.out.println(" so luong")
        }

    }

    private fun startCameraIntent() {
        val cameraInent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraInent.resolveActivity(activity.packageManager) == null) {
            errorMessage("This Application do not have Camera Application")
            return
        }

        val imageFile = imageFile
        val photoURI = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", imageFile)

        val resolvedIntentActivities = context.packageManager.queryIntentActivities(cameraInent, PackageManager.MATCH_DEFAULT_ONLY)
        resolvedIntentActivities
                .map { it.activityInfo.packageName }
                .forEach { context.grantUriPermission(it, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION) }

        cameraInent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(cameraInent, REQ_CODE_CAMERA)

    }

    private // Create an image file name
            /* prefix *//* suffix *//* directory */// Save a file: path for use with ACTION_VIEW intents
    val imageFile: File?
        get() {
            var imageFile: File? = null
            try {
                val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "JPEG_" + timeStamp + "_"
                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

                if (!storageDir.exists())
                    storageDir.mkdirs()

                imageFile = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                )
                cameraImageUri = Uri.fromFile(imageFile)
            } catch (e: IOException) {
                e.printStackTrace()
                errorMessage("Could not create imageFile for camera")
            }


            return imageFile
        }

    private fun errorMessage(message: String? = null) {
        val errorMessage = message ?: "Something wrong."

        if (builder!!.onErrorListener == null) {
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            builder!!.onErrorListener!!.onError(errorMessage)
        }
    }


    private val isMultiSelect: Boolean
        get() = builder!!.onMultiImageSelectedListener != null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {


            when (requestCode) {
                REQ_CODE_GALLERY -> onActivityResultGallery(data!!)
                REQ_CODE_CAMERA -> onActivityResultCamera(cameraImageUri!!)

                else -> errorMessage()
            }


        }

    }

    private fun onActivityResultCamera(cameraImageUri: Uri) {

        MediaScannerConnection.scanFile(context, arrayOf(cameraImageUri.path), arrayOf("image/jpeg"), object : MediaScannerConnection.MediaScannerConnectionClient {
            override fun onMediaScannerConnected() {

            }

            override fun onScanCompleted(s: String, uri: Uri) {


                activity.runOnUiThread {
                    updateAdapter()
                    complete(cameraImageUri)
                }
            }
        })
    }


    private fun onActivityResultGallery(data: Intent) {
        val temp = data.data

        if (temp == null) {
            errorMessage()
        }

        val realPath = RealPathUtil.getRealPath(activity, temp)

        var selectedImageUri: Uri?
        try {
            selectedImageUri = Uri.fromFile(File(realPath))
        } catch (ex: Exception) {
            selectedImageUri = Uri.parse(realPath)
        }

        if (selectedImageUri != null) {
            complete(selectedImageUri)
        }

    }


    interface OnMultiImageSelectedListener {
        fun onImagesSelected(uriList: ArrayList<Uri>)
    }

    interface OnImageSelectedListener {
        fun onImageSelected(uri: Uri)
    }

    interface OnErrorListener {
        fun onError(message: String)
    }

    interface ImageProvider {
        fun onProvideImage(imageView: ImageView, imageUri: Uri)
    }

    class Builder(var context: Context) {
        var previewMaxCount = 25


        var deSelectIconDrawable: Drawable? = null
        var selectedForegroundDrawable: Drawable? = null

        var spacing = 1
        var onImageSelectedListener: OnImageSelectedListener? = null
        var onMultiImageSelectedListener: OnMultiImageSelectedListener? = null
        var onErrorListener: OnErrorListener? = null
        var imageProvider: ImageProvider? = null
        var showCamera = true
        var showGallery = true
        var peekHeight = -1

        var title: String = ""
        var showTitle = true
        var titleBackgroundResId: Int = 0

        var selectMaxCount = Integer.MAX_VALUE
        var selectMinCount = 0


        var completeButtonText: String? = null
        var emptySelectionText: String? = null
        var selectMaxCountErrorText: String? = null
        var selectMinCountErrorText: String? = null


        internal var selectedUriList: ArrayList<Uri> = ArrayList()
        internal var selectedUri: Uri? = null

        init {


            setSpacingResId(R.dimen.tedbottompicker_grid_layout_margin)
        }


        fun setSpacingResId(@DimenRes dimenResId: Int): Builder {
            this.spacing = context.resources.getDimensionPixelSize(dimenResId)
            return this
        }

        fun setDeSelectIcon(@DrawableRes deSelectIconResId: Int): Builder {
            setDeSelectIcon(ContextCompat.getDrawable(context, deSelectIconResId))
            return this
        }

        fun setDeSelectIcon(deSelectIconDrawable: Drawable): Builder {
            this.deSelectIconDrawable = deSelectIconDrawable
            return this
        }

        fun setSelectedForeground(@DrawableRes selectedForegroundResId: Int): Builder {
            setSelectedForeground(ContextCompat.getDrawable(context, selectedForegroundResId))
            return this
        }

        fun setSelectedForeground(selectedForegroundDrawable: Drawable): Builder {
            this.selectedForegroundDrawable = selectedForegroundDrawable
            return this
        }

        fun setPreviewMaxCount(previewMaxCount: Int): Builder {
            this.previewMaxCount = previewMaxCount
            return this
        }

        fun setSelectMaxCount(selectMaxCount: Int): Builder {
            this.selectMaxCount = selectMaxCount
            return this
        }

        fun setSelectMinCount(selectMinCount: Int): Builder {
            this.selectMinCount = selectMinCount
            return this
        }

        fun setOnImageSelectedListener(onImageSelectedListener: OnImageSelectedListener): Builder {
            this.onImageSelectedListener = onImageSelectedListener
            return this
        }

        fun setOnMultiImageSelectedListener(onMultiImageSelectedListener: OnMultiImageSelectedListener): Builder {
            this.onMultiImageSelectedListener = onMultiImageSelectedListener
            return this
        }


        fun setOnErrorListener(onErrorListener: OnErrorListener): Builder {
            this.onErrorListener = onErrorListener
            return this
        }

        fun showCameraTile(showCamera: Boolean): Builder {
            this.showCamera = showCamera
            return this
        }

        fun showGalleryTile(showGallery: Boolean): Builder {
            this.showGallery = showGallery
            return this
        }

        fun setSpacing(spacing: Int): Builder {
            this.spacing = spacing
            return this
        }

        fun setPeekHeight(peekHeight: Int): Builder {
            this.peekHeight = peekHeight
            return this
        }

        fun setPeekHeightResId(@DimenRes dimenResId: Int): Builder {
            this.peekHeight = context.resources.getDimensionPixelSize(dimenResId)
            return this
        }


        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setTitle(@StringRes stringResId: Int): Builder {
            this.title = context.resources.getString(stringResId)
            return this
        }

        fun showTitle(showTitle: Boolean): Builder {
            this.showTitle = showTitle
            return this
        }

        fun setCompleteButtonText(completeButtonText: String): Builder {
            this.completeButtonText = completeButtonText
            return this
        }

        fun setCompleteButtonText(@StringRes completeButtonResId: Int): Builder {
            this.completeButtonText = context.resources.getString(completeButtonResId)
            return this
        }

        fun setEmptySelectionText(emptySelectionText: String): Builder {
            this.emptySelectionText = emptySelectionText
            return this
        }

        fun setEmptySelectionText(@StringRes emptySelectionResId: Int): Builder {
            this.emptySelectionText = context.resources.getString(emptySelectionResId)
            return this
        }


        fun setSelectMaxCountErrorText(selectMaxCountErrorText: String): Builder {
            this.selectMaxCountErrorText = selectMaxCountErrorText
            return this
        }

        fun setSelectMaxCountErrorText(@StringRes selectMaxCountErrorResId: Int): Builder {
            this.selectMaxCountErrorText = context.resources.getString(selectMaxCountErrorResId)
            return this
        }

        fun setSelectMinCountErrorText(selectMinCountErrorText: String): Builder {
            this.selectMinCountErrorText = selectMinCountErrorText
            return this
        }

        fun setSelectMinCountErrorText(@StringRes selectMinCountErrorResId: Int): Builder {
            this.selectMinCountErrorText = context.resources.getString(selectMinCountErrorResId)
            return this
        }


        fun setTitleBackgroundResId(@ColorRes colorResId: Int): Builder {
            this.titleBackgroundResId = colorResId
            return this
        }

        fun setImageProvider(imageProvider: ImageProvider): Builder {
            this.imageProvider = imageProvider
            return this
        }


        fun setSelectedUriList(selectedUriList: ArrayList<Uri>): Builder {
            this.selectedUriList = selectedUriList
            return this
        }

        fun setSelectedUri(selectedUri: Uri): Builder {
            this.selectedUri = selectedUri
            return this
        }

        fun create(): TedBottomPicker {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw RuntimeException("Missing required WRITE_EXTERNAL_STORAGE permission. Did you remember to request it first?")
            }

            if (onImageSelectedListener == null && onMultiImageSelectedListener == null) {
                throw RuntimeException("You have to use setOnImageSelectedListener() or setOnMultiImageSelectedListener() for receive selected Uri")
            }

            val customBottomSheetDialogFragment = TedBottomPicker()

            customBottomSheetDialogFragment.builder = this
            return customBottomSheetDialogFragment
        }


    }

    companion object {

        val TAG = "TedBottomPicker"
        internal val REQ_CODE_CAMERA = 1
        internal val REQ_CODE_GALLERY = 2
        internal val EXTRA_CAMERA_IMAGE_URI = "camera_image_uri"
        internal val EXTRA_CAMERA_SELECTED_IMAGE_URI = "camera_selected_image_uri"
    }


}
