package com.fivetrue.hangoutbaby.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.google.PlaceImageData;
import com.fivetrue.hangoutbaby.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.hangoutbaby.ui.adapter.place.PlaceImageListAdapter;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class PlaceImageGalleryActivity extends BasePlaceActivity{

    private static final String TAG = "ImageDetailActivity";

    private RecyclerView mRecyclerView = null;

    private PlaceImageListAdapter mPlaceImageListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_image_gallery);
        initView();
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_place_image_gallery);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<PlaceImageData> imageDatas = getIntent().getParcelableArrayListExtra(PlaceImageData.class.getName());
        setImageData(imageDatas);
    }

    private void setImageData(ArrayList<PlaceImageData> placeImageDatas){
        if(placeImageDatas != null && placeImageDatas.size() > 0){
            if(mPlaceImageListAdapter == null){
                mPlaceImageListAdapter = new PlaceImageListAdapter(placeImageDatas, getGoogleApiClient());
                mPlaceImageListAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<PlaceImageData>() {
                    @Override
                    public void onClick(PlaceImageData data) {
                        Intent intent = new Intent(PlaceImageGalleryActivity.this, ImageDetailActivity.class);
                        intent.putExtra("url", data.key);
                        startActivity(intent);
                    }
                });
                mRecyclerView.setAdapter(mPlaceImageListAdapter);
            }else{
                mPlaceImageListAdapter.setData(placeImageDatas);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.exit_transform);
    }
}
