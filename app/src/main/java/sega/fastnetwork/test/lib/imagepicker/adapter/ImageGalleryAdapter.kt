package sega.fastnetwork.test.lib.imagepicker.adapter

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.annotation.IntDef
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.tedbottompicker_grid_item.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.imagepicker.TedBottomPicker
import sega.fastnetwork.test.lib.imagepicker.model.Photo
import java.io.File
import java.util.*



/**
 * Created by TedPark on 2016. 8. 30..
 */
class ImageGalleryAdapter(internal var context: Context, internal var builder: TedBottomPicker.Builder) : RecyclerView.Adapter<ImageGalleryAdapter.GalleryViewHolder>() {


    internal var pickerTiles: ArrayList<PickerTile>
    internal var onItemClickListener: OnItemClickListener? = null
    internal var selectedUriList: ArrayList<Uri>


    init {

        pickerTiles = ArrayList<PickerTile>()
        selectedUriList = ArrayList<Uri>()


        var imageCursor: Cursor? = null
        try {
            val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION)
            val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"


            imageCursor = context.applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy)
            //imageCursor = sContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);


            if (imageCursor != null) {


                while (imageCursor.moveToNext()) {
                    val imageLocation = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val imageFile = File(imageLocation)
                    pickerTiles.add(PickerTile(Uri.fromFile(imageFile)))


                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (imageCursor != null && !imageCursor.isClosed) {
                imageCursor.close()
            }
        }

    }

    fun refresh(photos: List<Photo>) {
        pickerTiles.clear()
        for (photo in photos) {
            pickerTiles.add(PickerTile(Uri.fromFile(File(photo.path))))
        }

        notifyDataSetChanged()
    }

    fun setSelectedUriList(selectedUriList: ArrayList<Uri>, uri: Uri) {
        this.selectedUriList = selectedUriList

        var position = -1


        var pickerTile: PickerTile
        for (i in pickerTiles.indices) {
            pickerTile = pickerTiles[i]
            if (pickerTile.isImageTile && pickerTile.imageUri == uri) {
                position = i
                break
            }

        }


        if (position >= 0) {
            notifyItemChanged(position)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = View.inflate(context, R.layout.tedbottompicker_grid_item, null)
        val holder = GalleryViewHolder(view)


        return holder
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

        val pickerTile = getItem(position)


        val isSelected : Boolean?


            val uri = pickerTile.imageUri
            if (builder.imageProvider == null) {
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .error(R.drawable.img_error)
                        .centerCrop()
                        .placeholder(R.drawable.ic_gallery)
                        .priority(Priority.HIGH)
                Glide.with(context)
                        .load(uri)
                        .apply(options)
                        .into(holder.itemView.iv_thumbnail)
             /*   Glide.with(context)
                        .load(uri)
                        .thumbnail(0.1f)
                        .dontAnimate()
                        .centerCrop()
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.img_error)
                        .into(holder.iv_thumbnail)*/
            } else {
                builder.imageProvider!!.onProvideImage(holder.itemView.iv_thumbnail, uri!!)
            }


            isSelected = selectedUriList.contains(uri)





        if (holder.itemView.root is FrameLayout) {

            val foregroundDrawable: Drawable

            if (builder.selectedForegroundDrawable != null) {

                foregroundDrawable = builder.selectedForegroundDrawable!!
            } else {

                foregroundDrawable = ContextCompat.getDrawable(context, R.drawable.gallery_photo_selected)
            }

            (holder.itemView.root as FrameLayout).foreground = if (isSelected) foregroundDrawable else null
        }


        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener { onItemClickListener!!.onItemClick(holder.itemView, position) }
        }
    }

    fun getItem(position: Int): PickerTile {
        return pickerTiles[position]
    }

    override fun getItemCount(): Int {
        return pickerTiles.size
    }

    fun setOnItemClickListener(
            onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }


    open class PickerTile protected constructor(val imageUri: Uri?, @param:TileType
    val tileType: Int) {

        internal constructor(imageUri: Uri) : this(imageUri, IMAGE)

        override fun toString(): String {
            if (isImageTile) {
                return "ImageTile: " + imageUri
            }else {
                return "Invalid item"
            }
        }

        val isImageTile: Boolean
            get() = tileType == IMAGE




        @IntDef(IMAGE.toLong())
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class TileType


        companion object {

          const  val IMAGE = 1

        }
    }

    inner class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
