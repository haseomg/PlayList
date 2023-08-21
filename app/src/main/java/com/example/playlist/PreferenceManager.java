package com.example.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceManager {
    String TAG = "[PreferenceManager]";
    String prefName = "song";
    String prefStr = " ";


    SharedPreferences getPreference(Context context) {
        Log.i(TAG, "getPreference");
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public void setString(Context context, String key, String value) {
        Log.i(TAG, "setString");
        SharedPreferences shared = getPreference(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void settingShared(Context context, String key, String value) {
        // 현재 재생 중인 곡 저장 용도
        Log.i(TAG, "settingShared");
        SharedPreferences shared = getPreference(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        // editor.apply() == 쉐어드에 저장을 해주는데 반환 값이 없다.
        editor.apply();
    }

    public String getString(Context context, String key) {
        Log.i(TAG, "getString");
        SharedPreferences shared = getPreference(context);
        String value = shared.getString(key, prefStr);
        return value;
    }

    public void removeKey(Context context, String key) {
        Log.i(TAG, "removeKey");
        SharedPreferences shared = getPreference(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.remove(key);
        // editor.commit() == 쉐어드에 저장을 성공하면 boolean 타입인 true 값을 반환한다.
        editor.commit();
    }

    public void clear(Context context) {
        Log.i(TAG, "clear");
        SharedPreferences shared = getPreference(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.commit();
    }

}

