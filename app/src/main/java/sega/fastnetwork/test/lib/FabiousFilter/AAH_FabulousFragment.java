package sega.fastnetwork.test.lib.FabiousFilter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import sega.fastnetwork.test.R;
import sega.fastnetwork.test.lib.FabiousFilter.viewpagerbottomsheet.BottomSheetUtils;
import sega.fastnetwork.test.lib.FabiousFilter.viewpagerbottomsheet.ViewPagerBottomSheetBehavior;
import sega.fastnetwork.test.lib.FabiousFilter.viewpagerbottomsheet.ViewPagerBottomSheetDialog;
import sega.fastnetwork.test.lib.FabiousFilter.viewpagerbottomsheet.ViewPagerBottomSheetDialogFragment;


/**
 * Created by krupenghetiya on 05/10/16.
 */

public class AAH_FabulousFragment extends ViewPagerBottomSheetDialogFragment {


    private DisplayMetrics metrics;


    private FrameLayout bottomSheet;
    private ViewPagerBottomSheetBehavior mBottomSheetBehavior;
    private int fab_outside_y_offest = 0;
    private boolean is_fab_outside_peekheight;

    //user params
    private int peek_height = 400;



    private View view_main;
    private View viewgroup_static;


    private View contentView;
    private Callbacks callbacks;

    private ViewPager viewPager;


    private ViewPagerBottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new ViewPagerBottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(View bottomSheet, int newState) {
            switch (newState) {
                case ViewPagerBottomSheetBehavior.STATE_HIDDEN:
                    if (callbacks != null) {
                        callbacks.onResult("swiped_down");
                    }
                    dismiss();
                    break;
                case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
                    ViewGroup.LayoutParams params = view_main.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view_main.setLayoutParams(params);
                    break;
                case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
                    ViewGroup.LayoutParams params1 = view_main.getLayoutParams();
                    params1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view_main.setLayoutParams(params1);
                    break;
            }

        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {
            if (viewgroup_static != null) {
                int range = (int) (metrics.heightPixels - (metrics.density * peek_height));
                viewgroup_static.animate().translationY(-range + (range * slideOffset)).setDuration(0).start();
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metrics = this.getResources().getDisplayMetrics();

    }



    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);

        if (viewPager != null) {
            BottomSheetUtils.setupViewPager(viewPager);
        }

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(contentView);




        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetBehavior = ViewPagerBottomSheetBehavior.from(((View) contentView.getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            if (((metrics.heightPixels - (metrics.density * peek_height)) + (metrics.density) - (metrics.density)) <= 0) {
                is_fab_outside_peekheight = true;
                mBottomSheetBehavior.setPeekHeight(metrics.heightPixels);
                fab_outside_y_offest = (int) (metrics.heightPixels- (metrics.density * peek_height));
            } else {
                mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
            }
            contentView.requestLayout();

        }

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ViewPagerBottomSheetDialog d = (ViewPagerBottomSheetDialog) dialog;
                bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                if (viewgroup_static != null) {
                    int range = (int) (metrics.heightPixels - (metrics.density * peek_height));
                    viewgroup_static.animate().translationY(-range).setDuration(0).start();
                }

                fabAnim();


            }
        });


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof ViewPagerBottomSheetBehavior) {
            ((ViewPagerBottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }






        }






    private void fabAnim() {

        //Do something after 100ms
        mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
        ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        if (is_fab_outside_peekheight) {
            bottomSheet.requestLayout();
        }



    }

    public void closeFilter(final Object o) {

        if (ViewPagerBottomSheetBehavior.from(bottomSheet).getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        }

        if (is_fab_outside_peekheight) {
            mBottomSheetBehavior.setPeekHeight(metrics.heightPixels );
            ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
            bottomSheet.requestLayout();
//                            fabulous_fab.setY(fab_outside_y_offest - fab_pos_y + getStatusBarHeight(getContext()));
        } else {
            mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
        }
        if (callbacks != null) {
            callbacks.onResult(o);
        }
        dismiss();
    }
    @Override
    public void onStop() {

        super.onStop();
    }


    public interface Callbacks {
        void onResult(Object result);
    }


    public void setPeekHeight(int peek_height) {
        this.peek_height = peek_height;
    }

    public void setViewMain(View view_main) {
        this.view_main = view_main;
    }

    public void setViewgroupStatic(View viewgroup_static) {
        this.viewgroup_static = viewgroup_static;
    }

    public void setMainContentView(View contentView) {
        this.contentView = contentView;
    }


    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }





    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
