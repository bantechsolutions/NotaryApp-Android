package com.notaryapp.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

import com.notaryapp.interfacelisterners.NestedScrollViewListener;

public class ScrollViewExt extends NestedScrollView {
    private NestedScrollViewListener scrollViewListener = null;
    public ScrollViewExt(Context context) {
        super(context);
    }

    public ScrollViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(NestedScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}