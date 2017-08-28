package sega.fastnetwork.test.lib.SliderTypes.Transformers;

import android.view.View;

import sega.fastnetwork.test.util.ViewHelper;


public class RotateDownTransformer extends BaseTransformer {

	private static final float ROT_MOD = -15f;

	@Override
	protected void onTransform(View view, float position) {
		final float width = view.getWidth();
		final float height = view.getHeight();
		final float rotation = ROT_MOD * position * -1.25f;

		ViewHelper.INSTANCE.setPivotX(view,width * 0.5f);
        ViewHelper.INSTANCE.setPivotY(view,height);
        ViewHelper.INSTANCE.setRotation(view,rotation);
	}
	
	@Override
	protected boolean isPagingEnabled() {
		return true;
	}

}
