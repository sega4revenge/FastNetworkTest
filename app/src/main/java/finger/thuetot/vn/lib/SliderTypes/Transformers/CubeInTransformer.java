package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.util.ViewHelper;


public class CubeInTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		// Rotate the fragment on the left or right edge
        ViewHelper.INSTANCE.setPivotX(view,position > 0 ? 0 : view.getWidth());
        ViewHelper.INSTANCE.setPivotY(view,0);
        ViewHelper.INSTANCE.setRotation(view,-90f * position);
	}

	@Override
	public boolean isPagingEnabled() {
		return true;
	}

}
