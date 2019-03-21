package com.example.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.entities.User;

public class Entity {

    public static String getCurrentUser(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Constants.FILE_DATA_NAME, Context.MODE_PRIVATE);
        return pre.getString("user_name", "");
    }

    public static void saveCurrentUser(Context context, User user) {
        SharedPreferences pre = context.getSharedPreferences(Constants.FILE_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("user_name", user.getUser_name());
        edit.commit();
    }

    public static int deleteCurrentUser(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Constants.FILE_DATA_NAME, Context.MODE_PRIVATE);
        try {
            SharedPreferences.Editor edit = pre.edit();
            edit.remove("user_name");
            edit.commit();
            return 1;
        } catch (Exception e) {

        }
        return -1;
    }
}
