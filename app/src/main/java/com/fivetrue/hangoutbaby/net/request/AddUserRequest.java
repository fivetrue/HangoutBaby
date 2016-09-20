package com.fivetrue.hangoutbaby.net.request;

import android.content.Context;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.vo.User;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class AddUserRequest extends BasicRequest<User> {

    public static final String USER_UID = "uid";
    public static final String USER_ID = "userId";
    public static final String USER_IMAGE_URL = "imageUrl";
    public static final String USER_GCM_ID = "gmcId";

    private static final String API = Constants.API_SERVER_HOST + "/api/user/add";

    public AddUserRequest(Context context, BaseApiResponse.OnResponseListener<User> responseListener) {
        super(context, API, responseListener);
    }

    public void setUserInfo(String uid, String account, String imageUrl, String gcmId){
        putParam(USER_UID, uid);
        putParam(USER_ID, account);
        if(gcmId != null){
            putParam(USER_GCM_ID, gcmId);
        }
        if(imageUrl != null){
            putParam(USER_IMAGE_URL, imageUrl);
        }
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<User>(){}.getType();
    }

    @Override
    public boolean isCache() {
        return false;
    }
}
