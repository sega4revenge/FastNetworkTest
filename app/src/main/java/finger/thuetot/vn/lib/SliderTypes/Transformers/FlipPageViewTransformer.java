package finger.thuetot.vn.lib.SliderTypes.Transformers;

import android.view.View;

import finger.thuetot.vn.lib.SliderTypes.Tricks.ViewPagerEx;
import finger.thuetot.vn.util.ViewHelper;


public class FlipPageViewTransformer extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        float percentage = 1 - Math.abs(position);
        view.setCameraDistance(12000);
        setVisibility(view, position);
        setTranslation(view);
        setSize(view, position, percentage);
        setRotation(view, position, percentage);
    }

    private void setVisibility(View page, float position) {
        if (position < 0.5 && position > -0.5) {
            page.setVisibility(View.VISIBLE);
        } else {
            page.setVisibility(View.INVISIBLE);
        }
    }

    private void setTranslation(View view) {
        ViewPagerEx viewPager = (ViewPagerEx) view.getParent();
        int scroll = viewPager.getScrollX() - view.getLeft();
        ViewHelper.INSTANCE.setTranslationX(view,scroll);
    }

    private void setSize(View view, float position, float percentage) {
        ViewHelper.INSTANCE.setScaleX(view,(position != 0 && position != 1) ? percentage : 1);
        ViewHelper.INSTANCE.setScaleY(view,(position != 0 && position != 1) ? percentage : 1);
    }

    private void setRotation(View view, float position, float percentage) {
        if (position > 0) {
            ViewHelper.INSTANCE.setRotationY(view,-180 * (percentage + 1));
        } else {
            ViewHelper.INSTANCE.setRotationY(view,180 * (percentage + 1));
        }
    }
}