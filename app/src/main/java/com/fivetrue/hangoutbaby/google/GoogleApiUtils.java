package com.fivetrue.hangoutbaby.google;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

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
import java.util.Arrays;
import java.util.List;

/**
 * Created by kwonojin on 16. 9. 13..
 */
public class GoogleApiUtils {

    private static final String TAG = "GoogleApiUtils";

    public interface OnLoadPhotoMetadataBufferListener{
        void onLoadPhotoMetadataBuffer(PlacePhotoMetadataBuffer buffer);

    }

    public interface OnLoadPhotoListener{
        void onLoadImages(ArrayList<PlaceImageData> placeImageDatas);
    }


    public static void getStaticMapAsync(StaticMapData data, String apiKey, ImageLoader.ImageListener ll){
        ImageLoadManager.getInstance().loadImageUrl(getStaticMapUrl(data, apiKey), ll);
    }

    public static String getStaticMapUrl(StaticMapData data, String apiKey){
        return data.toMapImageUrl(apiKey);
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

    public static void getPhotosByPlaceId(String placeId , final GoogleApiClient apiClient, final OnLoadPhotoListener ll){
        if(placeId != null && apiClient != null){
            new AsyncTask<String, Void, ArrayList<PlaceImageData>>(){

                @Override
                protected ArrayList<PlaceImageData> doInBackground(String... params) {
                    ArrayList<PlaceImageData> placeImageDatas = new ArrayList<PlaceImageData>();
                    String place = params[0];
                    if(place != null){
                        PlacePhotoMetadataBuffer photoMetadataBuffer = null;
                        PlacePhotoMetadataResult result = Places.GeoDataApi
                                .getPlacePhotos(apiClient, place).await();
                        if (result != null && result.getStatus().isSuccess()) {
                            photoMetadataBuffer = result.getPhotoMetadata();
                            for(PlacePhotoMetadata data : photoMetadataBuffer) {
                                Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(data.getAttributions().toString());
                                if (bm != null && !bm.isRecycled()) {
                                    PlaceImageData imageData = new PlaceImageData(place, data, bm);
                                    placeImageDatas.add(imageData);
                                } else {
                                    bm = data.getPhoto(apiClient).await().getBitmap();
                                    if (bm != null && !bm.isRecycled()) {
                                        ImageLoadManager.getInstance().putBitmapToCache(data.getAttributions().toString(), bm);
                                        PlaceImageData imageData = new PlaceImageData(place, data, bm);
                                        placeImageDatas.add(imageData);
                                    }
                                }
                            }

                        }
                    }
                    return placeImageDatas;
                }

                @Override
                protected void onPostExecute(ArrayList<PlaceImageData> placeImageDatas) {
                    super.onPostExecute(placeImageDatas);
                    if(placeImageDatas != null){
                        ll.onLoadImages(placeImageDatas);
                    }

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, placeId);
        }
    }

//    public static void getPhotosFromPhotoMetadata(PlacePhotoMetadataBuffer buffer, final GoogleApiClient apiClient, final OnLoadPhotoListener ll){
//        ArrayList<PlacePhotoMetadata> datas = new ArrayList<>();
//        for (PlacePhotoMetadata data : buffer) {
//            datas.add(data);
//        }
//        getPhotosFromPhotoMetadata(datas, apiClient, ll);
//
//    }

//    public static void getPhotosFromPhotoMetadata(final GoogleApiClient apiClient, final OnLoadPhotoListener ll, PlacePhotoMetadata... metadata){
//        getPhotosFromPhotoMetadata(Arrays.asList(metadata), apiClient, ll);
//    }
//
//    public static void getPhotosFromPhotoMetadata(List<PlacePhotoMetadata> metadata, final GoogleApiClient apiClient, final OnLoadPhotoListener ll){
//        if(metadata != null && apiClient != null){
//            new AsyncTask<List<PlacePhotoMetadata>, Void, List<PlaceImageData>>(){
//
//                @Override
//                protected List<PlaceImageData> doInBackground(List<PlacePhotoMetadata>... params) {
//                    List<PlaceImageData> placeImageDatas = new ArrayList<PlaceImageData>();
//                    List<PlacePhotoMetadata> metadata = params[0];
//                    for(PlacePhotoMetadata data : metadata){
//                        Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(data.getAttributions().toString());
//                        if(bm != null && !bm.isRecycled()){
//                            PlaceImageData imageData = new PlaceImageData(data, bm);
//                            placeImageDatas.add(imageData);
//                        }else{
//                            bm = data.getPhoto(apiClient).await().getBitmap();
//                            if(bm != null && !bm.isRecycled()){
//                                ImageLoadManager.getInstance().putBitmapToCache(data.getAttributions().toString(), bm);
//                                PlaceImageData imageData = new PlaceImageData(data, bm);
//                                placeImageDatas.add(imageData);
//                            }
//                        }
//                    }
//                    return placeImageDatas;
//                }
//
//                @Override
//                protected void onPostExecute(List<PlaceImageData> bitmaps) {
//                    super.onPostExecute(bitmaps);
//                    if(bitmaps != null){
//                        ll.onLoadImages(bitmaps);
//                    }
//
//                }
//            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, metadata);
//        }
//    }


//    public static void getPhotosFromPhotoMetadata(PlacePhotoMetadata metadata, final GoogleApiClient apiClient, final OnLoadPhotoListener ll){
//        if(metadata != null && apiClient != null){
//            Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(metadata.getAttributions().toString());
//            if(bm != null && !bm.isRecycled()){
//                ll.onLoadImages(bm);
//            }else{
//                new AsyncTask<PlacePhotoMetadata, Void, Bitmap>(){
//
//                    @Override
//                    protected Bitmap doInBackground(PlacePhotoMetadata... params) {
//                        Bitmap bitmap = null;
//                        PlacePhotoMetadata metadata = params[0];
//                        if (metadata != null) {
//                            bitmap = metadata.getPhoto(apiClient).await().getBitmap();
//                            if(bitmap != null && !bitmap.isRecycled()){
//                                ImageLoadManager.getInstance().putBitmapToCache(metadata.getAttributions().toString(), bitmap);
//                            }
//                        }
//                        return bitmap;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Bitmap bitmap) {
//                        super.onPostExecute(bitmap);
//                        if(bitmap != null){
//                            ll.onLoadImages(bitmap);
//                        }
//
//                    }
//                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, metadata);
//            }
//        }
//    }
}
