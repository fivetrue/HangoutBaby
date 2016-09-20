package com.fivetrue.hangoutbaby.net.request;

import android.content.Context;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.vo.AppConfig;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class AppConfigRequest extends BasicRequest<AppConfig> {

    private static final String API = Constants.API_SERVER_HOST + "/api/config/app";

    public AppConfigRequest(Context context, BaseApiResponse.OnResponseListener<AppConfig> responseListener) {
        super(context, API, responseListener);
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<AppConfig>(){}.getType();
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return 1000 * 60 * 60 * 6;
    }

    @Override
    public boolean isCache() {
        return true;
    }
}
