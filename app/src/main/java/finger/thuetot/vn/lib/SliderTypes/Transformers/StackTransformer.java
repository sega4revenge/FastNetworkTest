package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.util.ViewHelper;


public class StackTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.INSTANCE.setTranslationX(view,position < 0 ? 0f : -view.getWidth() * position);
	}

}
