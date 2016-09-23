package com.fivetrue.hangoutbaby.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.preference.PreferenceManager;

import com.fivetrue.hangoutbaby.R;

/**
 * Created by ojin.kwon on 2016-02-02.
 */
public class SettingPreferenceHelper {

    public SharedPreferences mSharedPreference;

    private Context mContext = null;

    private static SettingPreferenceHelper sInstance = null;

    public static SettingPreferenceHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new SettingPreferenceHelper(context);
        }
        return sInstance;
    }

    private SettingPreferenceHelper(Context context) {
        mContext = context;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getData(String key, boolean defaultVal) {

        return mSharedPreference.getBoolean(key, defaultVal);
    }

    public String getData(String key, String defaultVal) {

        return mSharedPreference.getString(key, defaultVal);
    }

    public int getData(String key, int defaultVal) {

        return mSharedPreference.getInt(key, defaultVal);
    }

    public long getData(String key, long defaultVal) {

        return mSharedPreference.getLong(key, defaultVal);
    }

    public void putData(String key, boolean val) {

        Editor edit = mSharedPreference.edit();
        edit.putBoolean(key, val);
        edit.commit();
    }

    public void putData(String key, int val) {

        Editor edit = mSharedPreference.edit();
        edit.putInt(key, val);
        edit.commit();
    }

    public void putData(String key, long val) {

        Editor edit = mSharedPreference.edit();
        edit.putLong(key, val);
        edit.commit();
    }

    public void putData(String key, String val) {
        Editor edit = mSharedPreference.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public void setPushSetting(boolean b) {
        putData(mContext.getString(R.string.pref_key_push), b);
    }

    public boolean isPushSetting(){
        return getData(mContext.getString(R.string.pref_key_push), true);
    }

}
