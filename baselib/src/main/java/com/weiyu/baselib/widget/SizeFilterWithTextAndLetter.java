package com.weiyu.baselib.widget;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weiweiyu
 * on 2019/5/27
 */
public class SizeFilterWithTextAndLetter  implements InputFilter {

    private int mMaxLength;
    private int onlyLetterLength;
    private int normalLength;

    private boolean hasChinese;

    Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
    Matcher m;

    public SizeFilterWithTextAndLetter(int onlyLetterLength,int normalLength) {
        this.normalLength = normalLength;
        this.onlyLetterLength = onlyLetterLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (!hasChinese && dest.length() <= normalLength) {
            if (dest.length() >= normalLength) {
                m = p.matcher(dest.toString());
            } else {
                String tmp = source.toString() + dest.toString();
                if (tmp.length() >= normalLength) {
                    tmp = tmp.substring(0,normalLength);
                }
                m = p.matcher(tmp);
            }
            hasChinese = m.find();
            mMaxLength = hasChinese ? normalLength : onlyLetterLength;
        }
        if (mMaxLength == onlyLetterLength) {
            m = p.matcher(source);
            if (m.find()) return "";
        }

        int keep = mMaxLength - (dest.length() - (dend - dstart));
        if (keep <= 0) {
            return "";
        } else if (keep >= end - start) {
            return null;
        } else {
            keep += start;
            if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                --keep;
                if (keep == start) {
                    return "";
                }
            }
            return source.subSequence(start, keep);
        }
    }
}

