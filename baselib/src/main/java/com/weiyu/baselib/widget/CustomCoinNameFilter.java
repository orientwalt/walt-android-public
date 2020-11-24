package com.weiyu.baselib.widget;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by weiweiyu
 * on 2019/5/21
 */
public class CustomCoinNameFilter implements InputFilter {

    private int maxLength;//最大长度，ASCII码算一个，其它算两个

    public CustomCoinNameFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (TextUtils.isEmpty(source)) {
            return null;
        }
        int inputCount = 0;
        int destCount = 0;
        inputCount = getCurLength(source);
        if (dest.length() != 0)
            destCount = getCurLength(dest);
        if (destCount >= maxLength)
            return "";
        else {
            int count = inputCount + destCount;
            if (dest.length() == 0) {
                if (count <= maxLength)
                    return null;
                else
                    return sub(source, maxLength);
            }
            if (count > maxLength) {
                //int min = count - maxLength;
                int maxSubLength = maxLength - destCount;
                return sub(source, maxSubLength);
            }
        }
        return null;
    }

    private CharSequence sub(CharSequence sq, int subLength) {
        int needLength = 0;
        int length = 0;
        for (int i = 0; i < sq.length(); i++) {
            if (sq.charAt(i) < 128)
                length += 1;
            else
                length += 2;
            ++needLength;
            if (subLength <= length) {
                return sq.subSequence(0, needLength);
            }
        }
        return sq;
    }

    private int getCurLength(CharSequence s) {
        int length = 0;
        if (s == null)
            return length;
        else {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) < 128)
                    length += 1;
                else
                    length += 2;
            }
        }
        return length;
    }
}
