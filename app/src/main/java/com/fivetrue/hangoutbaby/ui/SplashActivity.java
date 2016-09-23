package com.fivetrue.hangoutbaby.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.helper.UserInfoHelper;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.NetworkManager;
import com.fivetrue.hangoutbaby.net.request.AppConfigRequest;
import com.fivetrue.hangoutbaby.utils.AppUtils;
import com.fivetrue.hangoutbaby.vo.AppConfig;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


/**
 * Created by kwonojin on 16. 8. 29..
 */
public class SplashActivity extends BaseActivity{

    private static final String TAG = "SplashActivity";

    private static final int REQUEST_DEFAULT_PERMISSIONS = 0x33;
    private static final int REQUEST_LOGIN = 0x44;

    private static final String[] PERMISSIONS = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static final String[] ALLOWED_SCHEME = {
            "cardcapture",
    };

    private UserInfoHelper mLoginManager = null;
    private AppConfigRequest mAppConfigRequest = null;

    private TextView mTitleText = null;
    private Button mLoginButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoginManager.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoginManager.onStop();
    }

    private void initData(){
        mAppConfigRequest = new AppConfigRequest(this, appConfigOnResponseListener);
        mLoginManager = new UserInfoHelper(this);
    }

    private void initView(){
        mLoginButton = (Button) findViewById(R.id.btn_splash_login);
        mTitleText = (TextView) findViewById(R.id.tv_splash_title);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLogin();
            }
        });
        mLoginButton.setText(R.string.check_app_version_info);
        NetworkManager.getInstance().request(mAppConfigRequest);
    }

    private void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    new Pair<View, String>(mTitleText, getString(R.string.app_name)),
                    new Pair<View, String>(mLoginButton, getString(R.string.login)));
            startActivityForResult(intent, REQUEST_LOGIN, options.toBundle());
        }else{
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkPermission() called with: " + " trying check permission");
            // Should we show an explanation?
            if (hasPermissions()) {
                Log.i(TAG, "checkPermission: has Permission OK");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                checkIntent(getIntent());

            } else {
                // No explanation needed, we can request the permission.
                Log.i(TAG, "checkPermission: has no Permission = " + PERMISSIONS.toString());
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS,
                        REQUEST_DEFAULT_PERMISSIONS);

                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Log.i(TAG, "checkPermission() called with: " + " check permission OK");
            checkIntent(getIntent());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermissions(){
        boolean readPermission = PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean writePermission = PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission && writePermission;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");
        switch (requestCode) {
            case REQUEST_DEFAULT_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if(hasPermissions()){
                    checkIntent(getIntent());
                }else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.permission_denied_alert_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.i(TAG, "checkPermission: has no Permission = " + PERMISSIONS.toString());
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    PERMISSIONS,
                                    REQUEST_DEFAULT_PERMISSIONS);
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setCancelable(false)
                            .show();
                }
                return;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        checkIntent(intent);
    }

    private void checkIntent(Intent intent){
        if(intent != null && intent.getAction() != null){
            String action = intent.getAction();
            Log.d(TAG, "checkIntent() called with: " + "action = [" + action + "]");

            if(action.equals(Intent.ACTION_SEND)){
                Intent i = new Intent(intent);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }else{
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }else{
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }

    private void startApplication(){
        final AppConfig appConfig = mLoginManager.getAppConfig();
        if(appConfig != null){
            if(AppUtils.getApplicationVersionCode(this) < appConfig.getAppVersionCode() && appConfig.getForceUpdate() > 0){
                new android.app.AlertDialog.Builder(SplashActivity.this)
                        .setTitle(R.string.notice)
                        .setMessage(R.string.config_force_update)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.goAppStore(SplashActivity.this, appConfig.getAppMarketUrl());
                                finish();
                            }
                        }).create().show();
                return;
            }
        }

        if(mLoginManager.getUser() != null){
            checkIntent(getIntent());
        }
    }

    private void onAppConfig(final AppConfig appConfig){
        Log.d(TAG, "onAppConfig() called with: " + "appConfig = [" + appConfig + "]");
        if(appConfig != null){
            mLoginManager.setAppConfig(appConfig);
            String regId = mLoginManager.getGcmID();
            if(regId == null){
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String regId = null;
                        if(params != null && params.length > 0){
                            String senderId = params[0];
                            GoogleCloudMessaging gcm =  GoogleCloudMessaging.getInstance(getApplicationContext());
                            try {
                                regId = gcm.register(senderId);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return regId;
                    }

                    @Override
                    protected void onPostExecute(String value) {
                        super.onPostExecute(value);
                        if(value != null){
                            mLoginManager.saveGcmID(value);
                        }else{
                            Log.e(TAG, "registerDevice Gcm register error");
                        }

                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appConfig.getSenderId());
            }
        }else{
            //TODO : AppCOnfig error
            Log.e(TAG, "registerDevice AppConfig is null");
        }
    }

    private BaseApiResponse.OnResponseListener<AppConfig> appConfigOnResponseListener = new BaseApiResponse.OnResponseListener<AppConfig>() {

        private int mRetryCount = 0;
        @Override
        public void onResponse(BaseApiResponse<AppConfig> response) {
            if(response != null && response.getData() != null){
                mLoginButton.setEnabled(true);
                mLoginButton.setText(R.string.login);
                onAppConfig(response.getData());
                startApplication();
            }else{
                if(mRetryCount < 3){
                    mRetryCount ++;
                    NetworkManager.getInstance().request(mAppConfigRequest);
                }else{
                    mLoginButton.setEnabled(true);
                    mLoginButton.setText(R.string.login);
                    startApplication();
                }
            }

        }

        @Override
        public void onError(VolleyError error) {
            if(mRetryCount < 3){
                mRetryCount ++;
                NetworkManager.getInstance().request(mAppConfigRequest);
            }else{
                mLoginButton.setEnabled(true);
                mLoginButton.setText(R.string.login);
                startApplication();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_LOGIN : {
                if(resultCode == RESULT_OK){
                    startApplication();
                }
            }
        }
    }

}
