package com.weiyu.baselib.widget;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;

/**
 * Created by weiweiyu
 * on 2019/5/21
 */
public class LineFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        SpannableString ss = new SpannableString(source);
        Object[] spans = ss.getSpans(0, ss.length(), Object.class);
        if (spans != null) {
            for (Object span : spans) {
                if (span instanceof UnderlineSpan) {
                    return "";
                }
            }
        }
        return null;

    }
}
