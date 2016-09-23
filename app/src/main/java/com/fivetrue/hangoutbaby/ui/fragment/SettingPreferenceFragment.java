package com.fivetrue.hangoutbaby.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.fivetrue.hangoutbaby.R;

/**
 * Created by kwonojin on 16. 9. 21..
 */
public class SettingPreferenceFragment extends PreferenceFragmentCompat implements BaseFragmentImp, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "SettingPreferenceFragment";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.config_prefrences, s);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public String getFragmentTitle() {
        return null;
    }

    @Override
    public String getFragmentBackStackName() {
        return TAG;
    }

    @Override
    public int getChildFragmentAnchorId() {
        return getId();
    }

    @Override
    public int getFragmentNameResource() {
        return 0;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
