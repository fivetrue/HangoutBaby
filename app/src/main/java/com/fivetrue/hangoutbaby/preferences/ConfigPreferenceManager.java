package com.fivetrue.hangoutbaby.preferences;

import android.content.Context;
import android.net.Uri;

import com.fivetrue.hangoutbaby.vo.AppConfig;
import com.fivetrue.hangoutbaby.vo.User;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;

/**
 * Created by ojin.kwon on 2016-02-02.
 */
public class ConfigPreferenceManager {

    public static final String PREF_NAME = "config";

    private static final String USER_INFO = "user";
    private static final String APP_CONFIG = "app_config";
    private static final String GCM_DEVICE = "gcm_dvice_id";
    private static final String FIRST_OPEN = "first_open";
    private static final String LAST_LOCATION_BOUNDS = "last_location_bounds";

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

    public void setGcmDeviceId(String id){
        if(id != null){
            mHelper.putData(GCM_DEVICE, id);
        }
    }

    public String getGcmDeviceId(){
        return mHelper.getData(GCM_DEVICE, null);
    }

    public void setAppConfig(AppConfig config){
        if(config == null){
            mHelper.putData(APP_CONFIG, null);
        }else{
            mHelper.putData(APP_CONFIG, mGson.toJson(config));
        }
    }

    public AppConfig getAppConfig(){
        AppConfig config = null;
        if(mHelper.getData(APP_CONFIG, null) != null){
            config = mGson.fromJson(mHelper.getData(APP_CONFIG, null), AppConfig.class);
        }
        return config;
    }

    public void setLastPlaceLatLngBounds(LatLngBounds bounds){
        if(bounds == null){
            mHelper.putData(LAST_LOCATION_BOUNDS, null);
        }else{
            mHelper.putData(LAST_LOCATION_BOUNDS, mGson.toJson(bounds));
        }
    }

    public LatLngBounds getLastPlaceLatLngBounds(){
        LatLngBounds latLngBounds = null;
        if(mHelper.getData(LAST_LOCATION_BOUNDS, null) != null){
            latLngBounds = mGson.fromJson(mHelper.getData(LAST_LOCATION_BOUNDS, null), LatLngBounds.class);
        }
        return latLngBounds;
    }

}
