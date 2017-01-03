package com.newtech.jobnow.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by manhi on 21/8/2016.
 */
public class Utils {
    public static String getAvatarFBFromNameOrFBId(String name_or_idUser) {
        String address = "http://graph.facebook.com/" + name_or_idUser + "/picture?type=large";
        URL url;
        String newLocation = null;
        try {
            url = new URL(address);
            HttpURLConnection.setFollowRedirects(false); //Do _not_ follow redirects!
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            newLocation = connection.getHeaderField("Location");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newLocation;
    }

    public static String getAvatarFBFromAddress(String address) {
        URL url;
        String newLocation = null;
        try {
            url = new URL(address);
            HttpURLConnection.setFollowRedirects(false); //Do _not_ follow redirects!
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            newLocation = connection.getHeaderField("Location");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newLocation;
    }

    public static String addressAvatarFB(String name_or_idUser) {
        return "http://graph.facebook.com/" + name_or_idUser + "/picture?type=large";
    }

    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static void closeKeyboard(Activity c) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = c.getCurrentFocus();
            if (view != null)
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getLongTime(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = sdf.parse(dateString);

            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = sdf.parse(dateString);


            return formatStringTime(date.getDate(), date.getMonth(), date.getYear());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String formatStringTime(int date, int month, int year) {
        String sdate = "";
        String smonth = "";
        if (date >= 10) {
            sdate = date + "";
        } else {
            sdate = "0" + date;
        }
        if (month >= 10) {
            smonth = month + "";
        } else {
            smonth = "0" + month;
        }
        return year + "-" + smonth + "-" + sdate;
    }

    public static boolean isReadyForPullEnd(RecyclerView recyclerView) {
        View lastView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
        int lastPosition = recyclerView.getChildAdapterPosition(lastView);

        if (lastPosition >= recyclerView.getAdapter().getItemCount() - 1) {
            try {
                return recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getBottom();
            } catch (Exception e) {

            }

        }
        return false;
    }
}
