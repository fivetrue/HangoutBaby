package com.fivetrue.hangoutbaby.ui.adapter.place;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.google.PlaceImageData;
import com.fivetrue.hangoutbaby.image.ImageLoadManager;
import com.fivetrue.hangoutbaby.ui.adapter.BaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

/**
 * Created by kwonojin on 16. 9. 22..
 */
public class PlaceImageListAdapter extends BaseRecyclerAdapter<PlaceImageData, PlaceImageListAdapter.PlaceImageHolder>{

    private static final String TAG = "PlaceImageListAdapter";

    private GoogleApiClient mGoogleApiClient = null;

    public PlaceImageListAdapter(List<PlaceImageData> data, GoogleApiClient client) {
        super(data, R.layout.item_place_image);
        mGoogleApiClient = client;
    }

    @Override
    protected PlaceImageHolder makeHolder(View view) {
        PlaceImageHolder holder =  new PlaceImageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PlaceImageHolder holder, int position) {
        final PlaceImageData data = getItem(position);
        if(data != null){
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(data);
                }
            });
            Bitmap bitmap = ImageLoadManager.getInstance().getBitmapFromCache(data.key);
            if(bitmap != null && !bitmap.isRecycled()){
                holder.image.setImageBitmap(bitmap);
            }
//            GoogleApiUtils.getPhotosFromPhotoMetadata(data, mGoogleApiClient, new GoogleApiUtils.OnLoadPhotoListener() {
//                @Override
//                public void onLoadImages(Bitmap bitmap) {
//                    if (bitmap != null && !bitmap.isRecycled()) {
//                        holder.image.setImageBitmap(bitmap);
//                    }
//                }
//            });
        }
    }

    protected static class PlaceImageHolder extends RecyclerView.ViewHolder{

        private View layout = null;
        private ImageView image = null;

        public PlaceImageHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_place_image);
            image = (ImageView) itemView.findViewById(R.id.iv_item_place_image);

        }
    }
}
