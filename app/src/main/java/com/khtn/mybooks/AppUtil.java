package com.khtn.mybooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.firebase.auth.PhoneAuthProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtil {
    public static PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    public static boolean isNetworkAvailable(Context context){
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    // checking the phone
    public static boolean isPhoneNumber(String str){
        if (str == null)
            return false;
        if (str.charAt(0) != '0')
            return false;
        if (str.length() != 10)
            return false;
        for (char c : str.toCharArray())
            if (c < '0' || c > '9')
                return false;
        return true;
    }

    // checking the email
    public static boolean isEmail(String str){
        String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        return str.matches(emailPattern) && str.length() > 4;
    }

    // convert number to string
    // ex: 10000 -> 10.000
    public static String convertNumber(int num){
        String src = String.valueOf(num);
        int lenght = src.length() - 3;
        while (lenght > 0){
            src = src.substring(0, lenght) + "." + src.substring(lenght);
            lenght -= 3;
        }
        return src;
    }

    // number of days from "date" to present
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

    public static String getStringResourceByName(String aString, Context context) {
        String packageName = context.getPackageName();
        int resId = context.getResources()
                .getIdentifier(aString, "string", packageName);
        if (resId == 0)
            return aString;
        else
            return context.getString(resId);
    }
}
