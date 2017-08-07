package sega.fastnetwork.test.customview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import sega.fastnetwork.test.R;

/**
 * Created by sega4 on 07/08/2017.
 */

public class ItemPaddingDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    public ItemPaddingDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public ItemPaddingDecoration(@NonNull Context context) {
        this(context.getResources().getDimensionPixelSize(R.dimen.recycler_item_padding));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
}
