package com.fivetrue.hangoutbaby.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.google.GoogleApiUtils;
import com.fivetrue.hangoutbaby.google.PlaceImageData;
import com.fivetrue.hangoutbaby.google.map.StaticMapData;
import com.fivetrue.hangoutbaby.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.hangoutbaby.ui.adapter.place.PlaceImageListAdapter;
import com.fivetrue.hangoutbaby.ui.dialog.LoadingDialog;
import com.fivetrue.hangoutbaby.utils.SimpleViewUtils;
import com.fivetrue.hangoutbaby.vo.PlaceItem;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 16. 9. 22..
 */
public class PlaceEditActivity extends BasePlaceActivity {

    private static final String TAG = "PlaceEditActivity";

    private CollapsingToolbarLayout mCollapsingToolbar = null;
//    private RecyclerView mPlaceImageList = null;
    private CardView mPlaceImageCardView = null;
    private ImageView mPlaceImage = null;
    private ImageView mPlaceMap = null;

    private TextInputLayout mNameInputLayout = null;
    private TextInputEditText mNameInput = null;

    private TextInputLayout mDescriptionInputLayout = null;
    private TextInputEditText mDescriptionInput = null;

//    private PlaceImageListAdapter mPlaceImageListAdapter = null;

    private LoadingDialog mLoadingDialog = null;

    private PlaceItem mPlaceItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        initData();
        initView();
        setPlaceItem(mPlaceItem);
    }

    private void initData() {
        mPlaceItem = getIntent().getParcelableExtra(PlaceItem.class.getName());
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        mPlaceMap = (ImageView) findViewById(R.id.iv_edit_place_map);

        mPlaceImageCardView = (CardView) findViewById(R.id.layout_item_place_image);
        mPlaceImage = (ImageView) findViewById(R.id.iv_edit_place_image);

        mNameInputLayout = (TextInputLayout) findViewById(R.id.input_layout_edit_place_name);
        mNameInput = (TextInputEditText) findViewById(R.id.input_edit_place_name);

        mDescriptionInputLayout = (TextInputLayout) findViewById(R.id.input_layout_edit_place_description);
        mDescriptionInput = (TextInputEditText) findViewById(R.id.input_edit_place_description);

        mLoadingDialog = new LoadingDialog(this);

        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null){
                    mCollapsingToolbar.setTitle(s);
                }
            }
        });
    }

    private void setPlaceItem(PlaceItem item) {
        if (item != null) {
            final int widthSize = getResources().getDisplayMetrics().widthPixels;
            final int heightSize = (int) (widthSize / (4f / 3f));
            StaticMapData.Markers markers = new StaticMapData.Markers(item.getPlaceName(), item.getPlaceLatitude(), item.getPlaceLongitude());
            StaticMapData staticMapData = new StaticMapData(item.getPlaceLatitude(), item.getPlaceLongitude(), widthSize, heightSize, markers);
            GoogleApiUtils.getStaticMapAsync(staticMapData, getString(R.string.app_google_api_key), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()) {
                        SimpleViewUtils.showView(mPlaceMap, View.VISIBLE);
                        mPlaceMap.setImageBitmap(response.getBitmap());
                    } else {
                        SimpleViewUtils.hideView(mPlaceMap, View.INVISIBLE);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PlaceEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            GoogleApiUtils.getPhotosByPlaceId(item.getPlaceId(), getGoogleApiClient(), new GoogleApiUtils.OnLoadPhotoListener() {
                @Override
                public void onLoadImages(final ArrayList<PlaceImageData> placeImageDatas) {
                    if (placeImageDatas != null && placeImageDatas.size() > 0) {
                        mPlaceImage.setImageBitmap(placeImageDatas.get(0).image);
                        mPlaceMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (placeImageDatas.size() > 1) {
                                    Intent intent = new Intent(PlaceEditActivity.this, PlaceImageGalleryActivity.class);
                                    intent.putParcelableArrayListExtra(PlaceImageData.class.getName(), placeImageDatas);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_transform, 0);
                                } else {
                                    Intent intent = new Intent(PlaceEditActivity.this, ImageDetailActivity.class);
                                    intent.putExtra("url", placeImageDatas.get(0).key);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_transform, 0);
                                }
                            }
                        });
                        SimpleViewUtils.showView(mPlaceImageCardView, View.VISIBLE);
                    } else {
                        SimpleViewUtils.showView(mPlaceImageCardView, View.INVISIBLE);
                    }
                }
            });

            mCollapsingToolbar.setTitle(item.getPlaceName());
            mDescriptionInput.setText(item.getPlaceDescription());
            mNameInput.setText(item.getPlaceName());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
        }else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
