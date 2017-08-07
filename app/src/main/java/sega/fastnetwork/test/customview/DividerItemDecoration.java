package sega.fastnetwork.test.customview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sega4 on 07/08/2017.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration{
    private Paint paint;
    private int dividerHeight;

    public DividerItemDecoration(int color, int dHeight){
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(dHeight);

        dividerHeight = dHeight;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(0, 0, dividerHeight, dividerHeight);

    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);




    }

}