package com.fivetrue.hangoutbaby.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.helper.LoginHelper;
import com.fivetrue.hangoutbaby.image.ImageLoadManager;
import com.fivetrue.hangoutbaby.preferences.ConfigPreferenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity";

    private static final int REQUEST_PLACE_PICKER = 0x00;

    private NetworkImageView mNavImage = null;
    private TextView mNavName = null;
    private TextView mNavAccount = null;
    private DrawerLayout mDrawerLayout = null;

    private LoginHelper mLoginHelper = null;

    private GoogleApiClient mGoogleApiClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLoginHelper = new LoginHelper(this);
    }

    private void initView(){
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

        mNavImage.setImageUrl(mLoginHelper.getUser().getPhotoUrl().toString(), ImageLoadManager.getImageLoader());
        mNavAccount.setText(mLoginHelper.getUser().getEmail());
        mNavName.setText(mLoginHelper.getUser().getDisplayName());
    }

    protected void pickPlaceData(){
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(MainActivity.this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
            mLoginHelper.logout();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            Status status = PlacePicker.getStatus(this, data);
            LatLngBounds latLngBounds = PlacePicker.getLatLngBounds(data);
            onPickedPlaceData(place, status, latLngBounds.getCenter());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onPickedPlaceData(Place place, Status status, LatLng latLng){

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
}
