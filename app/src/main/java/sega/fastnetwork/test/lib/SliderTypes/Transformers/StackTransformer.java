package sega.fastnetwork.test.lib.SliderTypes.Transformers;

import android.view.View;

import sega.fastnetwork.test.util.ViewHelper;


public class StackTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.INSTANCE.setTranslationX(view,position < 0 ? 0f : -view.getWidth() * position);
	}

}
