package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.util.ViewHelper;


public class ZoomInTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float scale = position < 0 ? position + 1f : Math.abs(1f - position);
		ViewHelper.INSTANCE.setScaleX(view,scale);
        ViewHelper.INSTANCE.setScaleY(view,scale);
        ViewHelper.INSTANCE.setPivotX(view,view.getWidth() * 0.5f);
        ViewHelper.INSTANCE.setPivotY(view,view.getHeight() * 0.5f);
        ViewHelper.INSTANCE.setAlpha(view,position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
	}

}
