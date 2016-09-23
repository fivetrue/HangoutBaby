package com.fivetrue.hangoutbaby.net.request.place;

import android.content.Context;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.request.BasicRequest;
import com.fivetrue.hangoutbaby.vo.PlaceComment;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class GetPlaceCommentRequest extends BasicRequest<List<PlaceComment>> {

    private static final String PLACE_ID = "placeId";

    private static final String API = Constants.API_SERVER_HOST + "/api/place/comment/get";

    public GetPlaceCommentRequest(Context context, BaseApiResponse.OnResponseListener<List<PlaceComment>> responseListener) {
        super(context, API, responseListener);
    }

    public void setPlaceId(String placeId){
        putParam(PLACE_ID, placeId);
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<List<PlaceComment>>(){}.getType();
    }

    @Override
    public boolean isCache() {
        return false;
    }
}
