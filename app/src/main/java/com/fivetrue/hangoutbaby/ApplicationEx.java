package com.fivetrue.hangoutbaby;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fivetrue.hangoutbaby.image.ImageLoadManager;
import com.fivetrue.hangoutbaby.net.NetworkManager;

/**
 * Created by kwonojin on 16. 9. 13..
 */
public class ApplicationEx extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        NetworkManager.init(this);
        ImageLoadManager.init(NetworkManager.getInstance().getRequestQueue());
    }
}
