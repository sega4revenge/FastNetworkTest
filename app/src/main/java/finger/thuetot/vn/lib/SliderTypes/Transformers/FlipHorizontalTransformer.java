package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.util.ViewHelper;


public class FlipHorizontalTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float rotation = 180f * position;
        ViewHelper.INSTANCE.setAlpha(view,rotation > 90f || rotation < -90f ? 0 : 1);
        ViewHelper.INSTANCE.setPivotY(view,view.getHeight()*0.5f);
		ViewHelper.INSTANCE.setPivotX(view,view.getWidth() * 0.5f);
		ViewHelper.INSTANCE.setRotationY(view,rotation);
	}

}
