package com.jj.comics.common.callback;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;

public class OnScrollListenerWithButton extends RecyclerView.OnScrollListener {
    private WeakReference<View> mButton;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    public OnScrollListenerWithButton(View button) {
        mButton = new WeakReference<View>(button);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mButton == null || mButton.get() == null)
            return;
        if (!recyclerView.canScrollVertically(-1)) {//滑动到顶部
            animateOut(mButton.get());
        } else
            animateIn(mButton.get());
    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final View button) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).translationY(button.getHeight() + getMarginBottom(button)).setInterpolator(INTERPOLATOR).withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            OnScrollListenerWithButton.this.mIsAnimatingOut = true;
                        }

                        public void onAnimationCancel(View view) {
                            OnScrollListenerWithButton.this.mIsAnimatingOut = false;
                        }

                        public void onAnimationEnd(View view) {
                            OnScrollListenerWithButton.this.mIsAnimatingOut = false;
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        } else {
            button.setVisibility(View.GONE);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(View button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).translationY(0)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    private int getMarginBottom(View v) {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }
}
