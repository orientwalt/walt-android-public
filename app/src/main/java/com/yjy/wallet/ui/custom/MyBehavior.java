package com.yjy.wallet.ui.custom;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * weiweiyu
 * 2020/7/7
 * 575256725@qq.com
 * 13115284785
 */
public class MyBehavior  extends CoordinatorLayout.Behavior<View> {
    private float allDistance = 0; // Head从和RecyclerView同样位置，移到顶部的距离
    private float dependencyDistance = 0; //RecyclerView需要移动的距离
    public MyBehavior() {
    }

    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof SmartRefreshLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (allDistance == 0) {
            allDistance = dependency.getY();//获取child要移动的总距离，是dependency到顶部的距离
            dependencyDistance = allDistance - child.getHeight();//获取滑动控件所要移动的总距离，移动到childView下方
            child.setY(allDistance);//初始化child的位置，和dependency位置一样，不然child会在顶部
        } else {
            float distance = (allDistance - dependency.getY()) / dependencyDistance * allDistance;//child所需要移动的距离！
            child.setY(allDistance - distance <= 0 ? 0 : allDistance - distance);//通过距离，计算出child的坐标，最后为0
        }

        return true;
    }

}
