package com.weiyu.baselib.base

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.trello.rxlifecycle2.components.support.RxFragment


abstract class LazyFragment : RxFragment() {
    private var mContentLayoutResId: Int = 0
    var isVisibles: Boolean = false
    /**
     * 控件是否初始化完成
     */
    private var isViewCreated: Boolean = false

    /**
     * 缓存content布局
     */
    private var contentView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //        return super.onCreateView(inflater, container, savedInstanceState);
        if (null == contentView) {
            mContentLayoutResId = getContentLayoutResId()
            if (0 == mContentLayoutResId) {
                throw IllegalArgumentException(
                        "mContentLayoutResId is 0, "
                                + "you must thought the method getContentLayoutResId() set the mContentLayoutResId's value"
                                + "when you used a fragment which implements the gta.dtp.fragment.BaseFragment."
                )
            }
            contentView = inflater.inflate(mContentLayoutResId, container, false)
            isViewCreated = true
            // 注解方式初始化控件
            initializeContentViews()
        }

        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated()
    }

    /**
     * 获取子类fragment布局资源id（作用等同于activity的setContentView（int resId）中指定的resId）
     */
    abstract fun getContentLayoutResId(): Int

    /**
     * 初始化具体子类布局资源里的views
     */
    abstract fun initializeContentViews()

    fun showBar() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showProgressDialog("")
        }
    }


    fun hideBar() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissProgressDialog()
        }
    }

    fun startActivity(tClass: Class<*>) {
        startActivity(Intent(activity, tClass))
    }

    //获取屏幕的宽度
    fun getScreenWidth(context: Context): Int {
        val manager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.width
    }

    //获取屏幕的高度
    fun getScreenHeight(context: Context): Int {
        val manager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.height
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            isVisibles = true
            onVisible()
        } else {
            isVisibles = false
            onInvisible()
        }
    }


    abstract fun viewCreated()
    private var isLoaded = false
    private fun onVisible() {
        if (isLoaded) {
            refreshLoad()
        }
        if (!isLoaded && isViewCreated && userVisibleHint) {
            isLoaded = true
            lazyLoad()
        }
    }

    protected open fun refreshLoad() {}
    protected abstract fun lazyLoad()

    private fun onInvisible() {}
}