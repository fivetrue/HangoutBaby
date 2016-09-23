package com.fivetrue.hangoutbaby.net.request.place;

import android.content.Context;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.request.BasicRequest;
import com.fivetrue.hangoutbaby.vo.PlaceItem;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class AddPlaceRequest extends BasicRequest<PlaceItem> {

    private static final String PLACE_NAME = "name";
    private static final String PLACE_LATITUDE = "lat";
    private static final String PLACE_LONGITUDE = "lng";
    private static final String PLACE_ADDRESS = "address";
    private static final String PLACE_CITY = "city";
    private static final String PLACE_DESCRIPTION ="description";
    private static final String PLACE_AUTHOR = "author";

    private static final String API = Constants.API_SERVER_HOST + "/api/place/add";

    public AddPlaceRequest(Context context, BaseApiResponse.OnResponseListener<PlaceItem> responseListener) {
        super(context, API, responseListener);
    }

    public void setPlaceImageInfo(PlaceItem item){
        putParam(PLACE_NAME, item.getPlaceName());
        putParam(PLACE_LATITUDE, item.getPlaceLatitude() + "");
        putParam(PLACE_LONGITUDE, item.getPlaceLongitude() + "");
        putParam(PLACE_ADDRESS, item.getPlaceAddress());
        putParam(PLACE_CITY, item.getPlaceCity());
        putParam(PLACE_DESCRIPTION, item.getPlaceDescription());
        putParam(PLACE_AUTHOR, item.getPlaceAuthor());
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<PlaceItem>(){}.getType();
    }

    @Override
    public boolean isCache() {
        return false;
    }
}
