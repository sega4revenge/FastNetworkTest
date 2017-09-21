package sega.fastnetwork.test.lib.MaterialChips.views


import android.content.Context
import android.util.AttributeSet
import android.view.View

class ChipsInputEditText : android.support.v7.widget.AppCompatEditText {

    var filterableListView: FilterableListView? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    var isFilterableListVisible: Boolean = false
        get() = filterableListView != null && filterableListView!!.visibility == View.VISIBLE
}
