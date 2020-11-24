package com.yjy.wallet.ui.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * weiweiyu
 * 2020/7/20
 * 575256725@qq.com
 * 13115284785
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = 0;
        outRect.bottom = space;
        outRect.left = 0;
        outRect.right = 0;
    }
}