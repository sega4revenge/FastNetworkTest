package finger.thuetot.vn.lib.imagepicker.showpicker

import android.content.Context
import android.net.Uri
import android.support.annotation.AttrRes
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import finger.thuetot.vn.R
import java.util.*


/**
 * Author 姚智胜
 * Version V1.0版本
 * Description: 单纯的图片展示选择控件
 * Date: 2017/4/6
 */

class ImageShowPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private var recyclerView: RecyclerView? = null
    /**
     * 图片加载接口
     */
    private var imageLoaderInterface: ImageLoaderInterface<*>? = null

    private var pickerListener: ImageShowPickerListener? = null


    private var adapter: ImageShowPickerAdapter? = null

    private var list: MutableList<ImageShowPickerBean>? = null
    /**
     * 单个item大小
     */
    private var mPicSize: Int = 0
    /**
     * 添加图片
     */
    private var mAddLabel: Int = 0
    /**
     * 删除图片
     */
    private var mDelLabel: Int = 0
    /**
     * 是否显示删除
     */
    private var isShowDel: Boolean = false
    /**
     * 单行显示数量，默认4
     */
    private var oneLineShowNum: Int = 0
    /**
     * 是否展示动画，默认false
     */
    private var isShowAnim: Boolean = false

    /**
     * 最大数量
     */
    private var maxNum: Int = 0

    /**
     * 设置是否显示动画

     * @param showAnim
     */
    fun setShowAnim(showAnim: Boolean) {
        isShowAnim = showAnim
    }

    /**
     * 设置最大允许图片数量

     * @param maxNum
     */
    fun setMaxNum(maxNum: Int) {
        this.maxNum = maxNum
    }

    init {
        init(getContext(), attrs!!)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        list = ArrayList()
        viewTypedArray(context, attrs)
        recyclerView = RecyclerView(context)
        recyclerView!!.overScrollMode = View.OVER_SCROLL_NEVER
        addView(recyclerView)
    }

    var listUri: ArrayList<Uri>
        get() = adapter!!.listuri
        set(uri) {
            adapter!!.listuri = uri
        }

    private fun viewTypedArray(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageShowPickerView)
        mPicSize = typedArray.getDimensionPixelSize(R.styleable.ImageShowPickerView_pic_size, SizeUtils.sizeUtils.dp2px(getContext(), PIC_SIZE.toFloat()))
        isShowDel = typedArray.getBoolean(R.styleable.ImageShowPickerView_is_show_del, true)
        isShowAnim = typedArray.getBoolean(R.styleable.ImageShowPickerView_is_show_anim, false)
        mAddLabel = typedArray.getResourceId(R.styleable.ImageShowPickerView_add_label, R.mipmap.image_show_piceker_add)
        mDelLabel = typedArray.getResourceId(R.styleable.ImageShowPickerView_del_label, R.drawable.ic_clear)
        oneLineShowNum = typedArray.getInt(R.styleable.ImageShowPickerView_one_line_show_num, ONE_LINE_SHOW_NUM)
        maxNum = typedArray.getInt(R.styleable.ImageShowPickerView_max_num, MAX_NUM)
        typedArray.recycle()
    }

    /**
     * 最后调用方法显示，必须最后调用
     */
    fun show() {
        val layoutManager = MyGridLayoutManager(context, oneLineShowNum)
        layoutManager.isAutoMeasureEnabled = true
        recyclerView!!.layoutManager = layoutManager

        //        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        //        //计算行数
        //        int lineNumber = list.size()%oneLineShowNum==0? list.size()/oneLineShowNum:(list.size()/oneLineShowNum) +1;
        //        //计算高度=行数＊每行的高度 ＋(行数－1)＊10dp的margin ＋ 10dp（为了居中）
        //        //高度的计算需要自己好好理解，否则会产生嵌套recyclerView可以滑动的现象
        ////        layoutParams.height =SizeUtils.getSizeUtils().dp2px(getContext(), lineNumber *mPicSize) ;
        //        layoutParams.height =lineNumber *mPicSize ;
        //
        //        Log.e("lineNumber",""+lineNumber);
        //        Log.e("height",""+layoutParams.height);
        //
        //        recyclerView.setLayoutParams(layoutParams);

        adapter = ImageShowPickerAdapter(maxNum, context, list!!, imageLoaderInterface!!, pickerListener)
        adapter!!.setAddPicRes(mAddLabel)
        adapter!!.setDelPicRes(mDelLabel)
        adapter!!.setIconHeight(mPicSize)
        adapter!!.setShowDel(isShowDel)
        adapter!!.setShowAnim(isShowAnim)
        recyclerView!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    /**
     * 设置选择器监听

     * @param pickerListener 选择器监听事件
     */
    fun setPickerListener(pickerListener: ImageShowPickerListener) {
        this.pickerListener = pickerListener
    }

    /**
     * 图片加载器

     * @param imageLoaderInterface
     */
    fun setImageLoaderInterface(imageLoaderInterface: ImageLoaderInterface<*>) {
        this.imageLoaderInterface = imageLoaderInterface

    }

    /**
     * 添加新数据

     * @param bean
     * *
     * @param <T>
    </T> */
    fun <T : ImageShowPickerBean> addData(bean: T?) {
        if (bean == null) {
            return
        }
        this.list!!.add(bean)
        if (isShowAnim) {
            if (adapter != null) {

                adapter!!.notifyItemChanged(list!!.size - 1)
                adapter!!.notifyItemChanged(list!!.size)
            }
        } else {
            adapter!!.notifyDataSetChanged()
        }

    }

    fun clear() {
        this.list!!.clear()
        adapter!!.clearList()
    }

    /**
     * 添加新数据

     * @param list
     * *
     * @param <T>
    </T> */
    fun <T : ImageShowPickerBean> addData(list: List<T>?) {
        if (list == null) {
            return
        }
        this.list!!.addAll(list)

        if (adapter != null)
            adapter!!.notifyDataSetChanged()


    }

    /**
     * 首次添加数据

     * @param list
     * *
     * @param <T>
    </T> */
    fun <T : ImageShowPickerBean> setNewData(list: List<T>) {
        this.list = ArrayList()
        this.list!!.addAll(list)

        if (isShowAnim) {
            if (adapter != null)
                adapter!!.notifyItemRangeChanged(this.list!!.size - list.size, list.size)

        } else {
            if (adapter != null)
                adapter!!.notifyDataSetChanged()
        }

    }

    companion object {
        /**
         * 默认单个大小
         */
        private val PIC_SIZE = 80
        /**
         * 默认单行显示数量
         */
        private val ONE_LINE_SHOW_NUM = 4
        /**
         * 默认单个大小
         */
        private val MAX_NUM = 9
    }

}
