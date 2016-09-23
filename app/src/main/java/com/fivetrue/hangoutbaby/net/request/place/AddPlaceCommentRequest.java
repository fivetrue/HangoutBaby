package com.fivetrue.hangoutbaby.net.request.place;

import android.content.Context;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.request.BasicRequest;
import com.fivetrue.hangoutbaby.vo.PlaceComment;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class AddPlaceCommentRequest extends BasicRequest<PlaceComment> {

    public static final String PLACE_ID = "placeId";
    public static final String PLACE_IMAGE_URL = "imageUrl";
    public static final String PLACE_COMMENT = "comment";
    public static final String PLACE_FEE = "fee";
    public static final String PLACE_UPLOADER = "author";

    private static final String API = Constants.API_SERVER_HOST + "/api/place/comment/add";

    public AddPlaceCommentRequest(Context context, BaseApiResponse.OnResponseListener<PlaceComment> responseListener) {
        super(context, API, responseListener);
    }

    public void setPlaceImageInfo(PlaceComment comment, String uploader){
        putParam(PLACE_ID, comment.getPlaceId());
        putParam(PLACE_IMAGE_URL, comment.getImageUrl());
        putParam(PLACE_COMMENT, comment.getComment());
        putParam(PLACE_FEE, comment.getFeeBand() + "");
        putParam(PLACE_UPLOADER, uploader);
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<PlaceComment>(){}.getType();
    }

    @Override
    public boolean isCache() {
        return false;
    }
}
