package com.tbossgroup.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangs on 2018-03-17.
 */

public class VwUtils {
    public static final int ROUNDED_CORNERS = 8;

    public static int dp2px(Context ctx, float dpValue) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int px = (int) (dpValue * (metrics.densityDpi / 160f));
        return px;
    }

    public static void viewAnimator(View view) {
        viewAnimator(view, 600);
    }

    /**
     * 小到大动画
     */
    public static void viewAnimator(View view, int duriation) {
        ObjectAnimator ScaleAnim1 = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.0f);
        ScaleAnim1.setDuration(duriation);
        ScaleAnim1.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator ScaleAnim2 = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f);
        ScaleAnim2.setDuration(duriation);
        ScaleAnim2.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 0f);
        fadeAnim.setDuration(duriation);
        ObjectAnimator appearAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        appearAnim.setDuration(duriation);
        appearAnim.setInterpolator(new DecelerateInterpolator());
        AnimatorSet as = new AnimatorSet();
        as.play(appearAnim).with(ScaleAnim1);
        as.play(appearAnim).with(ScaleAnim2);
        as.play(fadeAnim).before(appearAnim);
        as.start();
    }

    /**
     * 文本单位
     */
    public static void txtUnit(Activity act, TextView tv, String num, int colorsize1, int colorsize2,
                               String unit) {
        String txt = num + unit;
        int length = txt.length();
        SpannableString spannableString = new SpannableString(txt);
        spannableString.setSpan(new TextAppearanceSpan(act, colorsize1), 0, length - unit.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new TextAppearanceSpan(act, colorsize2), length - unit.length(), length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public static int setVL(Activity act, View view, int w, int h) {
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        //int height = metrics.heightPixels;
        ViewGroup.LayoutParams vl = view.getLayoutParams();
        if (w > 0) {
            vl.width = (w * width) / 375;
        }
        if (h > 0) {
            vl.height = (h * width) / 375;
        }
        return vl.width;
    }

    public static int setVLH(Activity act, View view, int w, int h) {
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        //int height = metrics.heightPixels;
        ViewGroup.LayoutParams vl = view.getLayoutParams();
        if (w > 0) {
            vl.width = (w * width) / 375;
        }
        if (h > 0) {
            vl.height = (h * width) / 375;
        }
        return vl.height;
    }

    public static void setVLWH(Activity act, View view, int w, int h) {
        ViewGroup.LayoutParams vl = view.getLayoutParams();
        vl.width = w;
        vl.height = h;
    }

    public static int getSW(Activity act) {
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        return width;
    }

    public static int getSH(Activity act) {
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        return height;
    }

    public static int getSW(Activity act, int w) {
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        return (w * width) / 375;
    }

    public static int measureLayout(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int width = view.getMeasuredWidth();
        return width;
    }

    public static String addZero(int number) {
        if (number > 9) {
            return String.valueOf(number);
        }
        return "0" + number;
    }

    public static String getWeek(int week) {
        if (week == 2) {
            return "周一";
        } else if (week == 3) {
            return "周二";
        } else if (week == 4) {
            return "周三";
        } else if (week == 5) {
            return "周四";
        } else if (week == 6) {
            return "周五";
        } else if (week == 7) {
            return "周六";
        } else {
            return "周日";
        }
    }

    public static String getViewTimeText(String time) {
        long l = 0;
        try {
            l = Long.parseLong(time);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l * 1000);
        StringBuilder builder = new StringBuilder();
        builder.append(addZero(calendar.get(Calendar.MONTH) + 1));
        builder.append("-");
        builder.append(addZero(calendar.get(Calendar.DAY_OF_MONTH)));
        builder.append(" ");
        builder.append(addZero(calendar.get(Calendar.HOUR_OF_DAY)));
        builder.append(":");
        builder.append(addZero(calendar.get(Calendar.MINUTE)));
        return builder.toString();
    }

    public static void setSoftInput(Activity act, boolean isShow) {
        InputMethodManager imm =
                (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        if (isShow) {
            if (act.getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(act.getCurrentFocus(), 0);
            }
        } else {
            if (act.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void hideKeyboard(View v) {
        if (v == null) {
            return;
        }
        InputMethodManager imm =
                (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    public static String getTime(String p) {
        DateFormat df = new SimpleDateFormat(p, Locale.SIMPLIFIED_CHINESE);
        String time = df.format(new Date());
        return time;
    }

    public static String getTime(String p, long l) {
        DateFormat df = new SimpleDateFormat(p, Locale.SIMPLIFIED_CHINESE);
        String time = df.format(new Date(l));
        return time;
    }

//    public static View getNoDataView(Context context,String textDes){
//        View view;
//        view = View.inflate(context, R.layout.view_no_data,null);
//        TextView info = view.findViewById(R.id.tv_noData);
//        info.setText(textDes);
//        return view;
//    }
}

