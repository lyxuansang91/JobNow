package com.androidteam.jobnow.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
}
