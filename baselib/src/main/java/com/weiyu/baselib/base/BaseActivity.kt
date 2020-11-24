package com.weiyu.baselib.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.support.annotation.RequiresApi
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jaeger.library.StatusBarUtil
import com.trello.rxlifecycle2.components.support.RxFragmentActivity
import com.weiyu.baselib.R
import com.weiyu.baselib.util.LanguageUtil
import com.weiyu.baselib.util.ModelUtils
import com.weiyu.baselib.widget.NameLengthFilter


/**
 * Created by shaco on 16/3/15.
 */
abstract class BaseActivity : RxFragmentActivity(), ActivityResponsable {
    var mContentLayoutResId: Int = 0
    //Activity辅助类
    var mActivityHelper: ActivityHelper? = null
    lateinit var inputMethodManager: InputMethodManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.getInstance().addActivity(this)
        mActivityHelper = ActivityHelper(this)
        //        initWindow();
        mContentLayoutResId = getContentLayoutResId()
        if (0 == mContentLayoutResId) {
            throw IllegalArgumentException(
                    "mContentLayoutResId is 0, "
                            + "you must thought the method getContentLayoutResId() set the mContentLayoutResId's value"
                            + "when you used a fragment which implements the gta.dtp.fragment.BaseFragment."
            )
        }
        setContentView(mContentLayoutResId)
        if (savedInstanceState != null) {
            savedInstanceState(savedInstanceState)
        }
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        initializeContentViews()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setStatusBar()
    }

    protected open fun setStatusBar() {
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
    }


    protected open fun savedInstanceState(savedInstanceState: Bundle) {

    }

    //设置布局
    protected abstract fun getContentLayoutResId(): Int

    protected abstract fun initializeContentViews()

    /**
     * 返回
     *
     * @param view
     */
    fun back(view: View) {
        finish()
    }
    fun setEdittxtForHwNum(editText: EditText) {
        if (ModelUtils.isEMUI() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
    fun setEdittxtForHw(editText: EditText) {
        if (ModelUtils.isEMUI() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    /**
     * 弹对话框
     *
     * @param title            标题
     * @param msg              消息
     * @param positive         确定
     * @param positiveListener 确定回调
     * @param negative         否定
     * @param negativeListener 否定回调
     */
    override fun alert(
            title: String, msg: String, positive: String,
            positiveListener: DialogInterface.OnClickListener, negative: String,
            negativeListener: DialogInterface.OnClickListener
    ) {
        mActivityHelper!!.alert(title, msg, positive, positiveListener, negative, negativeListener)
    }

    /**
     * 弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param positive                 确定
     * @param positiveListener         确定回调
     * @param negative                 否定
     * @param negativeListener         否定回调
     * @param isCanceledOnTouchOutside 外部点是否可以取消对话框
     */
    override fun alert(
            title: String, msg: String, positive: String,
            positiveListener: DialogInterface.OnClickListener, negative: String,
            negativeListener: DialogInterface.OnClickListener,
            isCanceledOnTouchOutside: Boolean?
    ) {
        mActivityHelper!!.alert(
                title, msg, positive, positiveListener, negative, negativeListener,
                isCanceledOnTouchOutside
        )
    }

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    override fun toast(msg: String) {
        mActivityHelper!!.toast(msg, Toast.LENGTH_SHORT)
    }

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    override fun toast(msg: String, period: Int) {
        mActivityHelper!!.toast(msg, period)
    }

    override fun toastPosition(msg: String, period: Int) {
        mActivityHelper!!.toastPosition(msg, period)
    }

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    override fun showProgressDialog(msg: String) {
        mActivityHelper!!.showProgressDialog(msg)
    }

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    override fun showProgressDialog(
            msg: String, cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener
    ) {
        mActivityHelper!!.showProgressDialog(msg, cancelable, cancelListener)
    }

    override fun dismissProgressDialog() {
        mActivityHelper!!.dismissProgressDialog()
    }

    fun startActivity(c: Class<*>) {
        startActivity(Intent(this@BaseActivity, c))
    }

    protected fun hideSoftKeyboard() {
        if (window.attributes.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (currentFocus != null)
                inputMethodManager.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                )
        }
    }

     fun showSoftKeyboard(view: View) {
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun setEditTextInhibitInputSpace(editText: EditText, length: Int) {
        var filter = object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                if (source == " ")
                    return ""
                return null
            }
        }
        editText.filters = arrayOf<InputFilter>(filter, NameLengthFilter(length))
    }

    fun getWindowWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return windowManager.defaultDisplay.width
    }

    fun getWindowHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return windowManager.defaultDisplay.height
    }

    fun TextView.checkBlank(message: String): String? {
        val text = this.text.toString()
        if (text.isBlank()) {
            toast(message, 1)
            return null
        }
        return text
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
    }

    override fun attachBaseContext(newBase: Context) {
        val context = languageWork(newBase)
        super.attachBaseContext(context)
    }

    private fun languageWork(context: Context): Context {
        // 8.0及以上使用createConfigurationContext设置configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateResources(context)
        } else {
            context
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun updateResources(context: Context): Context {
        val resources = context.resources
        val locale = LanguageUtil.getLocale(context) ?: return context
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.locales = LocaleList(locale)
        return context.createConfigurationContext(configuration)
    }
}