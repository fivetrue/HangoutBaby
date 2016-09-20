package com.fivetrue.hangoutbaby.service;

import android.os.Bundle;
import android.util.Log;

import com.fivetrue.hangoutbaby.preferences.ConfigPreferenceManager;
import com.fivetrue.hangoutbaby.service.notification.NotificationData;
import com.fivetrue.hangoutbaby.service.notification.NotificationService;
import com.fivetrue.hangoutbaby.ui.SplashActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

/**
 * Created by kwonojin on 16. 3. 28..
 */
public class GCMService extends GcmListenerService {

    private static final int DEFAULT_NOTIFICATION_ID = 0x88;
    private static final String TAG = "GCMService";
    private static final String DATA_KEY = "data";

    private ConfigPreferenceManager mConfigPref = null;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(mConfigPref == null){
            mConfigPref = new ConfigPreferenceManager(this);
        }

        String message = data.getString(DATA_KEY);
        if(message != null && mConfigPref.isSettingPush()){
            NotificationData noti = new Gson().fromJson(message, NotificationData.class);
            if(noti.getId() <= 0){
                noti.setId(DEFAULT_NOTIFICATION_ID);
            }
            noti.setTargetClass(SplashActivity.class.getName());
            NotificationService.createNotifcation(this, noti);
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
    }


}
