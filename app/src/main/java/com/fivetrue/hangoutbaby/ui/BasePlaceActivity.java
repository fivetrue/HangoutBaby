package com.fivetrue.hangoutbaby.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.google.map.StaticMapData;
import com.fivetrue.hangoutbaby.helper.UserInfoHelper;
import com.fivetrue.hangoutbaby.preferences.ConfigPreferenceManager;
import com.fivetrue.hangoutbaby.vo.PlaceItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.UserInfo;

public class BasePlaceActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, IPlaceUI{

    private static final LatLngBounds DEFAULT_LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(37.5615534,126.9751701)
            , new LatLng(37.5615534,126.9751701));

    private static final String TAG = "BasePlaceActivity";

    private static final int REQUEST_PLACE_PICKER = 0x00;

    private UserInfoHelper mUserInfoHelper = null;

    private GoogleApiClient mGoogleApiClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mUserInfoHelper = new UserInfoHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mUserInfoHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        mUserInfoHelper.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected() called with: " + "bundle = [" + bundle + "]");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called with: " + "i = [" + i + "]");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called with: " + "connectionResult = [" + connectionResult + "]");
    }

    @Override
    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    @Override
    public String getAuthor() {
        UserInfo userinfo = mUserInfoHelper.getUser();
        return userinfo.getEmail().substring(0, userinfo.getEmail().indexOf("@"));
    }

    protected UserInfoHelper getLoginHelper(){
        return mUserInfoHelper;
    }

    protected void pickPlaceData(){
        showLoadingDialog();
        try {
            LatLngBounds bounds = mUserInfoHelper.getConfigPref().getLastPlaceLatLngBounds();
            if(bounds == null){
                bounds = DEFAULT_LAT_LNG_BOUNDS;
            }
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(bounds);
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
            dismissLoadingDialog();
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
            dismissLoadingDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PLACE_PICKER) {
            dismissLoadingDialog();
            if(resultCode == Activity.RESULT_OK){
                Place place = PlacePicker.getPlace(this, data);
                mUserInfoHelper.getConfigPref().setLastPlaceLatLngBounds(PlacePicker.getLatLngBounds(data));
                final int widthSize = getResources().getDisplayMetrics().widthPixels;
                final int heightSize = (int)(widthSize / (4f/3f));
                StaticMapData.Markers markers=  new StaticMapData.Markers(place.getName().toString(), place.getLatLng());
                StaticMapData staticMapData = new StaticMapData(place.getLatLng(), widthSize, heightSize, markers);
                PlaceItem placeItem = new PlaceItem(place);
                placeItem.setPlaceImageUrl(staticMapData.toMapImageUrl(getString(R.string.app_google_api_key)));
                placeItem.setPlaceAuthor(getAuthor());
                onPickedPlaceItem(placeItem, place);
            }
        }
    }

    protected void onPickedPlaceItem(PlaceItem placeItem, Place place){

    }
}
