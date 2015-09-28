package com.angcyo.ondemand.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.angcyo.ondemand.R;

import java.util.List;

/**
 * Created by angcyo on 15-09-28-028.
 */
public class RsenRadioGroup extends RadioGroup {
    public RsenRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RsenRadioGroup(Context context) {
        super(context);
    }

    public int getCheckedRadioButtonIndex() {
        return indexOfChild(findViewById(getCheckedRadioButtonId()));
    }

    public void addView(List<String> titles) {
        for (String title : titles) {
            RadioButton rb = new RadioButton(getContext(), null, R.attr.radioButton);
            rb.setText(title);
            this.addView(rb, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (titles.size() > 0) {
            this.check(getChildAt(0).getId());//默认选择第一个
        }
    }

    public void addView(List<String> titles, int checkIndex) {
        addView(titles);
        if (checkIndex >= 0 && titles.size() > checkIndex) {
            this.check(getChildAt(checkIndex).getId());//默认选择第一个
        }
    }
}
