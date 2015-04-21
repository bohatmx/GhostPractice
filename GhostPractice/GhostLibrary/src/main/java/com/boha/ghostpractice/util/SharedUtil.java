package com.boha.ghostpractice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boha.ghostpractice.data.WebServiceResponse;

/**
 * Created by aubreyM on 2014/07/25.
 */
public class SharedUtil {

    public static final String LAST_SEARCH = "lastSearch";
    public static void saveSeachString(String search, Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(LAST_SEARCH, search);
        ed.commit();
    }
    public static String getSearchString(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String s = sp.getString(LAST_SEARCH, null);
        return s;
    }
    public static String getUserInfo(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(sp.getString("userName", "")).append("\n");
        sb.append("UserID: ").append(sp.getInt("userID",0)).append("\n");
        sb.append("Practice: ").append(sp.getString("companyName",""));
        return sb.toString();

    }
    public static String getPracticeName(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.getString("companyName","");
    }
    public static void saveUserPreferences(WebServiceResponse response, Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("userID", response.getUser().getUserID());
        ed.putString("userName", response.getUser().getUserName());
        ed.putString("deviceID", response.getDeviceID());
        if (response.getUser().getCellphone() != null) {
            ed.putString("cellphone", response.getUser().getCellphone());
        }
        if (response.getUser().getEmail() != null) {
            ed.putString("email", response.getUser().getEmail());
        }
        if (response.getUser().getCompany() != null) {
            ed.putInt("companyID", response.getUser().getCompany().getCompanyID());
            ed.putString("companyName", response.getUser().getCompany().getCompanyName());
        }
        ed.commit();
        Log.i("SharedUtil", "#### GP User preferences have been stored. userName: " +
                response.getUser().getUserName());
    }

    public static void saveAppPreferences(WebServiceResponse response, Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("appID", response.getApp().getAppID());
        ed.putInt("platformID", response.getPlatform().getPlatformID());
        ed.commit();
        Log.i("SharedUtil", "#### GP App preferences have been stored. appID: " + response.getApp().getAppID());
    }
}
