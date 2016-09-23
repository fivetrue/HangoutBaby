package com.fivetrue.hangoutbaby.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.image.ImageLoadManager;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.NetworkManager;
import com.fivetrue.hangoutbaby.net.request.place.GetPlaceRequest;
import com.fivetrue.hangoutbaby.vo.PlaceItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.places.Place;

import java.util.List;

public class MainActivity extends BasePlaceActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";


    private NetworkImageView mNavImage = null;
    private TextView mNavName = null;
    private TextView mNavAccount = null;
    private DrawerLayout mDrawerLayout = null;

    private ViewGroup mLayoutBanner = null;
    private RecyclerView mRecyclerRecentlyPlace = null;

    private FloatingActionButton mRegisterdPlaceButton = null;

    private GetPlaceRequest mGetPlaceRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initNavigation();
        initView();

        NetworkManager.getInstance().request(mGetPlaceRequest);
    }

    private void initData(){
        mGetPlaceRequest = new GetPlaceRequest(this, onResponseListener);
    }

    private void initNavigation(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mNavImage = (NetworkImageView) headerView.findViewById(R.id.iv_nav_header);
        mNavAccount = (TextView) headerView.findViewById(R.id.tv_nav_header_name_account);
        mNavName = (TextView) headerView.findViewById(R.id.tv_nav_header_name);

        mNavImage.setImageUrl(getLoginHelper().getUser().getPhotoUrl().toString(), ImageLoadManager.getImageLoader());
        mNavAccount.setText(getLoginHelper().getUser().getEmail());
        mNavName.setText(getLoginHelper().getUser().getDisplayName());
    }

    private void initView(){
        mRecyclerRecentlyPlace = (RecyclerView) findViewById(R.id.rv_main_recently_registered_place);
        mRegisterdPlaceButton = (FloatingActionButton) findViewById(R.id.fab_register_place);

        mRegisterdPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlaceData();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (closeDrawer()) {
        } else {
            super.onBackPressed();
        }
    }

    protected void openDrawer(){
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){

        }
    }

    protected boolean closeDrawer(){
        boolean b = false;
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            b = true;
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return b;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            getLoginHelper().logout();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    protected int getFragmentAnchorLayoutID() {
        return R.id.layout_main_content;
    }

    @Override
    protected void onPickedPlaceItem(PlaceItem placeItem, Place place) {
        super.onPickedPlaceItem(placeItem, place);
        Intent intent = new Intent(this, PlaceEditActivity.class);
        intent.putExtra(PlaceItem.class.getName(), placeItem);
        startActivity(intent);
    }

    private void setPlaceList(List<PlaceItem> placeList){

    }

    private BaseApiResponse.OnResponseListener<List<PlaceItem>> onResponseListener = new BaseApiResponse.OnResponseListener<List<PlaceItem>>() {
        @Override
        public void onResponse(BaseApiResponse<List<PlaceItem>> response) {
            if(response != null && response.getData() != null){
                setPlaceList(response.getData());
            }
        }

        @Override
        public void onError(VolleyError error) {

        }
    };

}
