package sega.fastnetwork.test.lib.imagepicker.adapter


import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_photo_gallery.view.*
import sega.fastnetwork.test.R
import sega.fastnetwork.test.lib.imagepicker.model.Photo
import sega.fastnetwork.test.lib.imagepicker.model.PhotoDirectory
import java.util.*


/**
 * Describe : 相册列表展示
 * Created by Rain on 17-4-28.
 */
class PhotoGalleryAdapter(val context: Context) : RecyclerView.Adapter<PhotoGalleryAdapter.PhotoGalleryViewHolder>() {
    private var selected: Int = 0
    private val directories = ArrayList<PhotoDirectory>()
    val imageSize: Int

    init {
        val metrics = DisplayMetrics()
        val display = (context as Activity).windowManager.defaultDisplay
        display.getMetrics(metrics)
        this.imageSize = metrics.widthPixels / 6

    }

    fun refresh(directories: List<PhotoDirectory>) {
        this.directories.clear()
        this.directories.addAll(directories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_gallery, null)
        return PhotoGalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {
        if (getItem(position).coverPath == null) {
            return
        }
        if (selected == position) {
            holder.itemView.photo_gallery_select.setImageResource(R.drawable.select_icon)
        } else {
            holder.itemView.photo_gallery_select.setImageBitmap(null)
        }
        holder.itemView.name.text = getItem(position).name
        holder.itemView.num.text = context.getString(R.string.gallery_num, getItem(position).photoPaths.size.toString())
        holder.itemView.imageView.layoutParams.height = imageSize
        holder.itemView.imageView.layoutParams.width = imageSize

        Glide.with(context).load(getItem(position).coverPath).into(holder.itemView.imageView)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (holder.itemView.id == R.id.photo_gallery_rl) {
                if (onItemClickListener != null) {
                    changeSelect(position)
                    onItemClickListener!!.onClick(getItem(position).getPhotos(),position)
                }
            }
        }


    }
    override fun getItemCount(): Int {
        return directories.size
    }

    fun getItem(position: Int): PhotoDirectory {
        return this.directories[position]
    }

    private fun changeSelect(position: Int) {
        this.selected = position
        notifyDataSetChanged()
    }

    class PhotoGalleryViewHolder(view: View) : RecyclerView.ViewHolder(view)


    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(photos: List<Photo>,position: Int)
    }

}
