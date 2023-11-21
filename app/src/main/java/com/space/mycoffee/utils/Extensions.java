package com.space.mycoffee.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.space.mycoffee.model.Address;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.jvm.JvmStatic;

public class Extensions {
    public static void toast(Context context, @StringRes int strRes) {
        Toast.makeText(context, strRes, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void copyToClipboard(@NonNull Context context, String text) {
        String label = "MyCoffee";
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clipData);
    }

    @NonNull
    public static List<Integer> arrayToList(int[] array) {
        List<Integer> list = new ArrayList<>();
        if (array == null) return list;
        for (int value : array) {
            list.add(value);
        }
        return list;
    }

    public static void changeStatusBarColor(@NonNull Context context, int color) {
        Window window = ((Activity) context).getWindow();
        window.setStatusBarColor(context.getColor(color));
        window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static boolean isEmail(String str) {
        if (str == null) return false;
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        return str.matches(emailPattern) && str.length() > 4;
    }

    public static boolean isNumberCode(@NonNull String str) {
        String regex = ".*\\D.*";

        return !str.matches(regex);
    }

    @NonNull
    public static String getStringFromAddress(@NonNull Address address) {
        return String.format("%s, %s, %s, %s", address.getAddress(),
                address.getPrecinct().getName_with_type(),
                address.getDistricts().getName_with_type(),
                address.getProvinces_cities().getName_with_type());
    }

    public static boolean isName(String str) {
        if (str == null) return false;
        String regex = "^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ]" +
                "[a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ]" +
                "[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ]" +
                "[a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$";

        if (str.isEmpty())
            return false;
        return str.matches(regex) && str.contains(" ");
    }

    public static boolean isString(String str){
        String regex = ".*[!@#$%&*()'+,\\-./:;<=>?\\[\\]^_`{|}].*";

        if (str.isEmpty())
            return false;
        return !str.matches(regex) && str.contains(" ");
    }

    public static boolean isPhoneNumber(String str){
        String regex = "0\\d{9}";
        if (str == null)
            return false;

        return str.matches(regex);
    }

    public static void defaultStatusBarColor(@NonNull Context context) {
        Window window = ((Activity) context).getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @JvmStatic
    public static int dpToPx(float dp, @NonNull Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static long numDays(String date){
        if (date == null) return 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        Date datePosted = null;
        try {
            datePosted = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long days = dateNow.getTime() - (datePosted != null ? datePosted.getTime() : 0);
        return days/(3600*1000*24);
    }

    @JvmStatic
    public static String convertNumberToStringComma(int num) {
        String src = String.valueOf(num);
        int lenght = src.length() - 3;
        while (lenght > 0){
            src = src.substring(0, lenght) + "." + src.substring(lenght);
            lenght -= 3;
        }
        return src;
    }

    public static boolean checkDateTimeSell(String dateTime) {
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (dateTime == null)
            return true;
        Date date = new Date();
        Date dateCheck = null;

        try {
            dateCheck = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (dateCheck.getTime() - date.getTime()) < 0;
    }
}
