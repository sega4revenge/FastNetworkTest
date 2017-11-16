package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.util.ViewHelper;


public class ZoomOutTransformer extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        final float scale = 1f + Math.abs(position);
        ViewHelper.INSTANCE.setScaleX(view,scale);
        ViewHelper.INSTANCE.setScaleY(view,scale);
        ViewHelper.INSTANCE.setPivotX(view,view.getWidth() * 0.5f);
        ViewHelper.INSTANCE.setPivotY(view,view.getWidth() * 0.5f);
        ViewHelper.INSTANCE.setAlpha(view,position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
        if(position < -0.9){
            //-0.9 to prevent a small bug
            ViewHelper.INSTANCE.setTranslationX(view,view.getWidth() * position);
        }
    }

}