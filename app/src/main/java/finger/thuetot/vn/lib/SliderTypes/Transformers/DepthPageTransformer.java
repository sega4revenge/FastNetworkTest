package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.util.ViewHelper;


public class DepthPageTransformer extends BaseTransformer {

	private static final float MIN_SCALE = 0.75f;

	@Override
	protected void onTransform(View view, float position) {
		if (position <= 0f) {
            ViewHelper.INSTANCE.setTranslationX(view,0f);
            ViewHelper.INSTANCE.setScaleX(view,1f);
            ViewHelper.INSTANCE.setScaleY(view,1f);
		} else if (position <= 1f) {
			final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            ViewHelper.INSTANCE.setAlpha(view,1-position);
            ViewHelper.INSTANCE.setPivotY(view,0.5f * view.getHeight());
            ViewHelper.INSTANCE.setTranslationX(view,view.getWidth() * - position);
            ViewHelper.INSTANCE.setScaleX(view,scaleFactor);
            ViewHelper.INSTANCE.setScaleY(view,scaleFactor);
		}
	}

	@Override
	protected boolean isPagingEnabled() {
		return true;
	}

}
