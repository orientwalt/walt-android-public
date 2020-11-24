package com.weiyu.baselib.base

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.trello.rxlifecycle2.components.support.RxFragment
import com.weiyu.baselib.util.ModelUtils
import com.weiyu.baselib.widget.CustomCoinNameFilter

abstract class BaseFragment : RxFragment() {
    lateinit var inputMethodManager: InputMethodManager
    private var mContentLayoutResId: Int = 0

    /**
     * 控件是否初始化完成
     */
    private var isViewCreated: Boolean = false

    private var bar: ProgressDialog? = null
    /**
     * 缓存content布局
     */
    protected var contentView: View? = null

    fun setEdittxtForHw(editText: EditText) {
        if (ModelUtils.isEMUI() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //        return super.onCreateView(inflater, container, savedInstanceState);
        if (null != contentView) {
            (contentView!!.parent as ViewGroup).removeViewInLayout(contentView)
        } else {
            mContentLayoutResId = getContentLayoutResId()
            if (0 == mContentLayoutResId) {
                throw IllegalArgumentException(
                        "mContentLayoutResId is 0, "
                                + "you must thought the method getContentLayoutResId() set the mContentLayoutResId's value"
                                + "when you used a fragment which implements the gta.dtp.fragment.BaseFragment."
                )
            }
            contentView = inflater.inflate(mContentLayoutResId, container, false)
            // 注解方式初始化控件
            initializeContentViews()
        }
        isViewCreated = true
        inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    abstract fun viewCreated()
    fun showBar() {
        bar!!.setMessage("加载中...")
        bar!!.show()
    }

    fun showBarCommit() {
        bar!!.setMessage("正在提交...")
        bar!!.show()
    }

    fun hideBar() {
        bar!!.dismiss()
    }

    fun toast(s: String) {
        if (activity is BaseActivity)
            (activity as BaseActivity).toast(s)
    }
    fun showProgressDialog(s: String) {
        if (activity is BaseActivity)
            (activity as BaseActivity).showProgressDialog(s)
    }
    fun dismissProgressDialog() {
        if (activity is BaseActivity)
            (activity as BaseActivity).dismissProgressDialog()
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

    protected fun hideSoftKeyboard() {
        if (activity!!.window.attributes.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity!!.currentFocus != null)
                inputMethodManager.hideSoftInputFromWindow(
                        activity!!.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                )
        }
    }

    fun setEditTextInhibitInputSpace(editText: EditText, length: Int) {
        var filter = object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                if (source == " ")
                    return ""
                return null
            }
        }
        editText.filters = arrayOf<InputFilter>(filter, CustomCoinNameFilter(length))
    }
}