package com.fivetrue.hangoutbaby.google;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.hangoutbaby.google.map.StaticMapData;
import com.fivetrue.hangoutbaby.image.ImageLoadManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 9. 13..
 */
public class GoogleApiUtils {

    private static final String TAG = "GoogleApiUtils";

    private static final int DEFAULT_STATIC_MAP_ZOOM = 18;
    private static final int DEFAULT_STATIC_MAP_WIDTH = 200;
    private static final int DEFAULT_STATIC_MAP_HEIGHT = 200;
    private static final String DEFAULT_STATIC_MAP_MARKER_COLOR = "red";

    public interface OnLoadPhotoMetadataBufferListener{
        void onLoadPhotoMetadataBuffer(PlacePhotoMetadataBuffer buffer);

    }

    public interface OnLoadPhotoListener{
        void onLoadImages(Bitmap bitmap);
    }


    public static void getStaticMapAsync(Place place, String apiKey, ImageLoader.ImageListener ll){
        StaticMapData.Markers markers=  new StaticMapData.Markers(DEFAULT_STATIC_MAP_MARKER_COLOR, place.getName().toString(), place.getLatLng());
        StaticMapData staticMapData = new StaticMapData(place.getLatLng(), DEFAULT_STATIC_MAP_ZOOM
                , DEFAULT_STATIC_MAP_WIDTH, DEFAULT_STATIC_MAP_HEIGHT, markers);
        getStaticMapAsync(staticMapData, apiKey, ll);
    }

    public static void getStaticMapAsync(double lat, double lng, int zoom, int width, int height, String apiKey, ImageLoader.ImageListener ll){
        getStaticMapAsync(new StaticMapData(lat, lng, zoom, width, height, null), apiKey, ll);
    }

    public static void getStaticMapAsync(StaticMapData data, String apiKey, ImageLoader.ImageListener ll){
        ImageLoadManager.getInstance().loadImageUrl(data.toMapImageUrl(apiKey), ll);
    }

    public static void getPhotoMetaDatabBuffer(String placeId , final GoogleApiClient apiClient, final OnLoadPhotoMetadataBufferListener ll){
        if(placeId != null && apiClient != null){
            new AsyncTask<String, Void, PlacePhotoMetadataBuffer>(){

                @Override
                protected PlacePhotoMetadataBuffer doInBackground(String... params) {
                    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                    String place = params[0];
                    PlacePhotoMetadataBuffer photoMetadataBuffer = null;
                    PlacePhotoMetadataResult result = Places.GeoDataApi
                            .getPlacePhotos(apiClient, place).await();
                    if (result != null && result.getStatus().isSuccess()) {
                        photoMetadataBuffer = result.getPhotoMetadata();
                    }
                    return photoMetadataBuffer;
                }

                @Override
                protected void onPostExecute(PlacePhotoMetadataBuffer buffer) {
                    super.onPostExecute(buffer);
                    if(buffer != null){
                        ll.onLoadPhotoMetadataBuffer(buffer);
                    }

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, placeId);
        }
    }

    public static void getPhotosFromPhotoMetadata(PlacePhotoMetadata metadata, final GoogleApiClient apiClient, final OnLoadPhotoListener ll){
        if(metadata != null && apiClient != null){
            Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(metadata.getAttributions().toString());
            if(bm != null && !bm.isRecycled()){
                ll.onLoadImages(bm);
            }else{
                new AsyncTask<PlacePhotoMetadata, Void, Bitmap>(){

                    @Override
                    protected Bitmap doInBackground(PlacePhotoMetadata... params) {
                        Bitmap bitmap = null;
                        PlacePhotoMetadata metadata = params[0];
                        if (metadata != null) {
                            bitmap = metadata.getPhoto(apiClient).await().getBitmap();
                            if(bitmap != null && !bitmap.isRecycled()){
                                ImageLoadManager.getInstance().putBitmapToCache(metadata.getAttributions().toString(), bitmap);
                            }
                        }
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if(bitmap != null){
                            ll.onLoadImages(bitmap);
                        }

                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, metadata);
            }
        }
    }
}
