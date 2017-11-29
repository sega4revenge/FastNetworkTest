package finger.thuetot.vn.lib.imagepicker.showpicker

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import finger.thuetot.vn.R
import java.util.*


/**
 * Author 姚智胜
 * Version V1.0版本
 * Description:
 * Date: 2017/4/10
 */

 class ImageShowPickerAdapter(private val mMaxNum: Int, private val context: Context, private val list: MutableList<ImageShowPickerBean>, var imageLoaderInterface: ImageLoaderInterface<*>, private val pickerListener: ImageShowPickerListener?) : RecyclerView.Adapter<ImageShowPickerAdapter.ViewHolder>(), ImageShowPickerPicListener {
    var listuri = ArrayList<Uri>()

    private var iconHeight: Int = 0

    private var delPicRes: Int = 0

    private var addPicRes: Int = 0

    private var isShowAnim: Boolean = false

    private var isShowDel: Boolean = false

    fun setShowDel(showDel: Boolean) {
        isShowDel = showDel
    }

    fun setShowAnim(showAnim: Boolean) {
        isShowAnim = showAnim
    }

    fun setIconHeight(iconHeight: Int) {
        this.iconHeight = iconHeight
    }

    fun setDelPicRes(delPicRes: Int) {
        this.delPicRes = delPicRes
    }

    fun setAddPicRes(addPicRes: Int) {
        this.addPicRes = addPicRes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val frameLayout = FrameLayout(context)
        parent.addView(frameLayout)
        frameLayout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        val vh = ViewHolder(frameLayout, imageLoaderInterface, this)

        frameLayout.addView(vh.iv_pic)
        frameLayout.addView(vh.iv_del)
        return vh
    }

    fun clearList() {
        this.list.clear()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list.size == 0 || list.size == position) {

            imageLoaderInterface.displayImage(context, addPicRes, holder.iv_pic)
            holder.iv_del.visibility = View.GONE
        } else {
            if ("" == list[position].imageShowPickerUrl) {

                imageLoaderInterface.displayImage(context, list[position].imageShowPickerDelRes, holder.iv_pic)
            } else {
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .centerCrop()
                        .placeholder(R.drawable.ic_gallery)
                        .priority(Priority.HIGH)
                Glide.with(context)
                        .load(list[position].imageShowPickerUrl)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(holder.iv_pic)
                /*  imageLoaderInterface.displayImage(context, list.get(position).getImageShowPickerUrl(), holder.iv_pic);*/
            }
            if (isShowDel) {
                holder.iv_del.visibility = View.VISIBLE
                holder.iv_del.setImageResource(delPicRes)
            } else {
                holder.iv_del.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {

        return if (list.size < mMaxNum) list.size + 1 else list.size

    }

    override fun onDelClickListener(position: Int) {
        Log.e("ImageShow","onDelClickListener")

        try {
         //   listuri.removeAt(position)
            list.removeAt(position)
        } catch (e: ArrayIndexOutOfBoundsException) {
            println(e)
        }

        if (isShowAnim) {
            notifyItemRemoved(position)
            if (list.size - 1 == 0) {

                notifyItemChanged(0)
            }
        } else {

            notifyDataSetChanged()
        }

        pickerListener!!.delOnClickListener(position, mMaxNum - list.size)
    }

    override fun onPicClickListener(position: Int) {
        Log.e("ImageShow","onPicClickListener")

        if (position == list.size) {
            pickerListener?.addOnClickListener(mMaxNum - position - 1)
        } else {
            pickerListener?.picOnClickListener(list, position, if (mMaxNum > list.size)
                mMaxNum - list.size - 1
            else
                0)
        }
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    inner class ViewHolder(view: View, imageLoaderInterface: ImageLoaderInterface<*>, private val picOnClickListener: ImageShowPickerPicListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var iv_pic: ImageView = imageLoaderInterface.createImageView(view.context)
        var iv_del: ImageView


        init {
            val pic_params = FrameLayout.LayoutParams(iconHeight,
                    iconHeight)
            pic_params.setMargins(15, 15, 15, 15)
            iv_pic.layoutParams = pic_params
            iv_del = ImageView(view.context)
            val layoutParams = FrameLayout.LayoutParams(90, 90)
            layoutParams.gravity = Gravity.TOP or Gravity.END
            iv_del.setPadding(5, 5, 5, 5)
            iv_del.layoutParams = layoutParams
            iv_pic.id = R.id.iv_image_show_picker_pic
            iv_del.id = R.id.iv_image_show_picker_del
            iv_pic.setOnClickListener(this)
            iv_del.setOnClickListener(this)
            iv_del.visibility = View.VISIBLE
        }

        override fun onClick(v: View) {
            Log.e("ImageShow","onClick")
            val i = v.id
            if (i == R.id.iv_image_show_picker_pic) {
                picOnClickListener.onPicClickListener(layoutPosition)
            } else if (i == R.id.iv_image_show_picker_del) {
                iv_del.visibility = View.GONE
                picOnClickListener.onDelClickListener(layoutPosition)
            }
        }
    }
}
