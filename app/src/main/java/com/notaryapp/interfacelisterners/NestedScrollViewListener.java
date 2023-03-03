
package com.notaryapp.interfacelisterners;

import com.notaryapp.components.ScrollViewExt;


public interface NestedScrollViewListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}
