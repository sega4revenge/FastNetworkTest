package finger.thuetot.vn.lib.imagepicker.showpicker

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Author 姚智胜
 * Version V1.0版本
 * Description: 处理recyclerview在adapter内调用notifyItemChanged崩溃的解决方法
 * Date: 2017/4/15
 */

class MyGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        //override this method and implement code as below
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun collectAdjacentPrefetchPositions(dx: Int, dy: Int, state: RecyclerView.State?, layoutPrefetchRegistry: RecyclerView.LayoutManager.LayoutPrefetchRegistry?) {
        try {
            super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry)
        } catch (e: IllegalArgumentException) {
            Log.e("TAG", "catch exception")
        }

    }
}
