package com.fivetrue.hangoutbaby.net.request.place;

import android.content.Context;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.request.BasicRequest;
import com.fivetrue.hangoutbaby.vo.PlaceItem;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class GetPlaceRequest extends BasicRequest<List<PlaceItem>> {

    private static final String API = Constants.API_SERVER_HOST + "/api/place/get";

    public GetPlaceRequest(Context context, BaseApiResponse.OnResponseListener<List<PlaceItem>> responseListener) {
        super(context, API, responseListener);
    }


    @Override
    protected Type getClassType() {
        return new TypeToken<List<PlaceItem>>(){}.getType();
    }

    @Override
    public boolean isCache() {
        return true;
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return 1000 * 60 * 10;
    }
}
