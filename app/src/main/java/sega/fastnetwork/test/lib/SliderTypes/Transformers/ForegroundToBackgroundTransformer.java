package sega.fastnetwork.test.lib.SliderTypes.Transformers;

import android.view.View;

import sega.fastnetwork.test.util.ViewHelper;


public class ForegroundToBackgroundTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float height = view.getHeight();
		final float width = view.getWidth();
		final float scale = min(position > 0 ? 1f : Math.abs(1f + position), 0.5f);

		ViewHelper.INSTANCE.setScaleX(view,scale);
        ViewHelper.INSTANCE.setScaleY(view,scale);
        ViewHelper.INSTANCE.setPivotX(view,width * 0.5f);
        ViewHelper.INSTANCE.setPivotY(view,height * 0.5f);
        ViewHelper.INSTANCE.setTranslationX(view,position > 0 ? width * position : -width * position * 0.25f);
	}

	private static float min(float val, float min) {
		return val < min ? min : val;
	}

}
