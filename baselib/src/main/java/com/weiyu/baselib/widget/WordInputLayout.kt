package com.weiyu.baselib.widget

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.weiyu.baselib.R
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.StringUtils

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/12
 */
class WordInputLayout : LinearLayout {
    var editText = arrayOfNulls<EditText>(12)
    val chinese = "[\u4e00-\u9fa5]+"
    val english = "[a-z]+"

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.word_input, this)
        editText[0] = findViewById(R.id.et1)
        editText[1] = findViewById(R.id.et2)
        editText[2] = findViewById(R.id.et3)
        editText[3] = findViewById(R.id.et4)
        editText[4] = findViewById(R.id.et5)
        editText[5] = findViewById(R.id.et6)
        editText[6] = findViewById(R.id.et7)
        editText[7] = findViewById(R.id.et8)
        editText[8] = findViewById(R.id.et9)
        editText[9] = findViewById(R.id.et10)
        editText[10] = findViewById(R.id.et11)
        editText[11] = findViewById(R.id.et12)
        val filter = object : InputFilter {
            override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
            ): CharSequence? {
                if (!source.toString().matches(chinese.toRegex()) && !source.toString().matches(english.toRegex())) {
                    return ""
                }
                return null
            }
        }

        editText.forEach {
            it?.filters = arrayOf<InputFilter>(filter)
            it?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    it.background = resources.getDrawable(R.drawable.edittxext_bg2)
                    it.setSelection(it.text.length)
                } else {
                    it.background = resources.getDrawable(R.drawable.edittxext_bg)
                }
            }
        }
    }

    fun setText(s: List<String>) {
        editText.forEach {
            it?.setTextColor(context.resources.getColor(R.color.txt_333333))
        }
        editText.forEach {
            s.forEach { str ->
                if (str == it?.text.toString()) {
                    it?.setTextColor(context.resources.getColor(R.color.btn_bg_fc5555))
                }
            }
        }
    }

    fun getList1(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        editText.forEach {
            if (!TextUtils.isEmpty(it?.text.toString().trim())) {
                list.add(it?.text.toString().trim())
            }
        }
        if (list.isNotEmpty()) {
            var regexStr = english
            if (StringUtils.isChinese(list[0])) {
                regexStr = chinese
            }
            val filter = object : InputFilter {
                override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                ): CharSequence? {
                    if (!source.toString().matches(regexStr.toRegex())) {
                        return ""
                    }
                    return null
                }
            }
            editText.forEach {
                it?.filters = arrayOf<InputFilter>(filter)
            }
        }else{
            val filter = object : InputFilter {
                override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                ): CharSequence? {
                    if (!source.toString().matches(chinese.toRegex()) && !source.toString().matches(english.toRegex())) {
                        return ""
                    }
                    return null
                }
            }
            editText.forEach {
                it?.filters = arrayOf<InputFilter>(filter)
            }
        }
        return list
    }

    fun getList(): String? {
        val sb = StringBuffer()
        editText.forEach {
            if (TextUtils.isEmpty(it?.text.toString().trim())) {
                it?.requestFocus()
                (context as BaseActivity).toast(context.resources.getString(R.string.empty_word))
                return null
            }
            sb.append(it?.text.toString().trim()).append(" ")
        }
        sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }
}