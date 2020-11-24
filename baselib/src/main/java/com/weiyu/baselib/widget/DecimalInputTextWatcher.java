package com.weiyu.baselib.widget;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Pattern;

public class DecimalInputTextWatcher implements TextWatcher {
    private final EditText mDecimalInputEt;
    private Pattern mPattern;

    /**
     * 不限制整数位数和小数位数
     */
    public DecimalInputTextWatcher(EditText decimalInputEt) {
        mDecimalInputEt = decimalInputEt;
    }

    /**
     * 限制整数位数或着限制小数位数
     *
     * @param type   限制类型
     * @param number 限制位数
     */
    public DecimalInputTextWatcher(EditText decimalInputEt, Type type, int number) {
        mDecimalInputEt = decimalInputEt;
        if (type == Type.decimal) {
            mPattern = Pattern.compile("^[0-9]+(\\.[0-9]{0," + number + "})?$");
        } else if (type == Type.integer) {
            mPattern = Pattern.compile("^[0-9]{0," + number + "}+(\\.[0-9]{0,})?$");
        }
    }

    /**
     * 既限制整数位数又限制小数位数
     *
     * @param integers 整数位数
     * @param decimals 小数位数
     */

    public DecimalInputTextWatcher(EditText decimalInputEt, int integers, int decimals) {
        mDecimalInputEt = decimalInputEt;
        mPattern = Pattern.compile("^[0-9]{0," + integers + "}+(\\.[0-9]{0," + decimals + "})?$");
    }

    public DecimalInputTextWatcher(EditText decimalInputEt, int integers, int decimals, Back back) {
        this.back = back;
        mDecimalInputEt = decimalInputEt;
        mPattern = Pattern.compile("^[0-9]{0," + integers + "}+(\\.[0-9]{0," + decimals + "})?$");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable editable = mDecimalInputEt.getText();
        String text = s.toString();
        if (TextUtils.isEmpty(text)) {
            if (this.back != null) {
                back.text();
            }
            return;
        }
        if ((s.length() > 1) && (s.charAt(0) == '0') && s.charAt(1) != '.') {   //删除首位无效的“0”
            editable.delete(0, 1);
            return;
        }
        if (text.equals(".")) {                                    //首位是“.”自动补“0”
            editable.insert(0, "0");
            return;
        }
        if (mPattern != null && !mPattern.matcher(text).matches() && editable.length() > 0) {
            editable.delete(editable.length() - 1, editable.length());
            return;
        }
        if (this.back != null) {
            back.text();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public enum Type {
        integer, decimal
    }

    private Back back;

    public void setBack(Back back) {
        this.back = back;
    }

    public interface Back {
        void text();
    }
}
