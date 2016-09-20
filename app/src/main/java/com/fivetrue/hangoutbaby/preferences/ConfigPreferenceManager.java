package com.fivetrue.hangoutbaby.preferences;

import android.content.Context;
import android.net.Uri;

import com.fivetrue.hangoutbaby.vo.User;
import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;

/**
 * Created by ojin.kwon on 2016-02-02.
 */
public class ConfigPreferenceManager {

    private static final String PREF_NAME = "config";

    private static final String USER_INFO = "user";

    private static final String GCM_DEVICE = "gcm_dvice_id";

    private static final String FIRST_OPEN = "first_open";
    private static final String PUSH_SETTING = "push_setting";

    private SharedPreferenceHelper mHelper = null;

    private Gson mGson = null;
    private Context mContext = null;

    public ConfigPreferenceManager(Context context){
        mContext = context;
        mHelper = new SharedPreferenceHelper(context, PREF_NAME);
        mGson = new Gson();
    }

    public void setFirstOpen(boolean b){
        mHelper.putData(FIRST_OPEN, b);
    }

    public boolean isFirstOpen(){
        return mHelper.getData(FIRST_OPEN, true);
    }

    public void saveUser(User user){
        if(user == null){
            mHelper.putData(USER_INFO, null);
        }else{
            mHelper.putData(USER_INFO, mGson.toJson(user));
        }
    }

    public User getUser(){
        User user = null;
        if(mHelper.getData(USER_INFO, null) != null){
            user = mGson.fromJson(mHelper.getData(USER_INFO, null), User.class);
        }
        return user;
    }

    public void setPushSetting(boolean b){
        mHelper.putData(PUSH_SETTING, b);
    }

    public boolean isSettingPush(){
        return mHelper.getData(PUSH_SETTING, true);
    }

    public void setGcmDeviceId(String id){
        if(id != null){
            mHelper.putData(GCM_DEVICE, id);
        }
    }

    public String getGcmDeviceId(){
        return mHelper.getData(GCM_DEVICE, null);
    }

}
