package com.fivetrue.hangoutbaby.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.google.GoogleStorageManager;
import com.fivetrue.hangoutbaby.helper.ImageCroppingHelper;
import com.fivetrue.hangoutbaby.helper.UserInfoHelper;
import com.fivetrue.hangoutbaby.ui.dialog.LoadingDialog;
import com.fivetrue.hangoutbaby.vo.User;

/**
 * Created by kwonojin on 16. 9. 18..
 */
public class LoginActivity extends BaseActivity implements UserInfoHelper.OnAccountManagerListener{

    private static final String TAG = "LoginActivity";

    private Button mLoginGoogle = null;
    private Button mLoginFacebook = null;

    private UserInfoHelper mUserInfoHelper = null;
    private ImageCroppingHelper mImageCroppingHelper = null;

    private LoadingDialog mLoadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserInfoHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserInfoHelper.onStop();
    }

    private void initData(){
        mUserInfoHelper = new UserInfoHelper(this);
        mImageCroppingHelper = new ImageCroppingHelper(this);
    }

    private void initView(){
        mLoadingDialog = new LoadingDialog(this);
        mLoginGoogle = (Button) findViewById(R.id.btn_login_google);
        mLoginFacebook  = (Button) findViewById(R.id.btn_login_facebook);

        mLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog.show();
                mUserInfoHelper.loginGoogleAccount();
            }
        });

        mLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog.show();
                mUserInfoHelper.loginFacebook();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUserInfoHelper.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onUserLoginSuccess(UserInfoHelper.LoginType type, User user) {
        if(user != null){
            if(user.getState() == 0){
                setResult(RESULT_OK);
            }else{
                setResult(RESULT_CANCELED);
            }
        }
        finish();
    }

    @Override
    public void onUserAddSuccess(UserInfoHelper.LoginType type, final User user) {
        if(user != null){
            if(user.getState() == 0){
                if(TextUtils.isEmpty(user.getUserImageUrl()) || user.getUserImageUrl().equalsIgnoreCase("null")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.question_setting_profile_image)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mImageCroppingHelper.startChoosingAndCrop(150, 150, 1, 1, new ImageCroppingHelper.OnCroppingListener() {
                                        @Override
                                        public void onCropImage(Uri originalUri, Uri croppedUri, Bitmap image) {
                                            mLoadingDialog.show();
                                            GoogleStorageManager.getInstnace(LoginActivity.this).uploadProfileImage(user.getUserId(), image, new GoogleStorageManager.OnUploadResultListener() {
                                                @Override
                                                public void onUploadSuccess(Uri url) {
                                                    Log.d(TAG, "onUploadSuccess() called with: " + "url = [" + url + "]");
                                                    mUserInfoHelper.updateUser(user.getUserId(), url.toString(), new UserInfoHelper.OnUserInfoUpdateListener() {
                                                        @Override
                                                        public void onUpdateUserInfo(User user) {
                                                            mLoadingDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onUpdateFailed(Exception e) {
                                                            mLoadingDialog.dismiss();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onUploadFailed(Exception e) {
                                                    Log.d(TAG, "onUploadFailed() called with: " + "e = [" + e + "]");
                                                    mLoadingDialog.dismiss();
                                                    Toast.makeText(LoginActivity.this, R.string.failed_upload_profile_image, Toast.LENGTH_SHORT).show();
                                                    mUserInfoHelper.loginUser(user.getUserUid(), user.getUserId(), user.getUserImageUrl());
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed(Uri originalUri, Uri croppedUri, Exception e) {
                                            Log.d(TAG, "onFailed() called with: " + "originalUri = [" + originalUri + "], croppedUri = [" + croppedUri + "], e = [" + e + "]");
                                            Toast.makeText(LoginActivity.this, R.string.failed_crop_image, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mUserInfoHelper.loginUser(user.getUserUid(), user.getUserId(), user.getUserImageUrl());
                        }
                    }).setCancelable(false)
                            .show();
                }else{
                    mUserInfoHelper.loginUser(user.getUserUid(), user.getUserId(), user.getUserImageUrl());
                }
            }else{
                Toast.makeText(LoginActivity.this, R.string.can_not_use_account, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onUserLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mLoadingDialog.dismiss();
    }

    @Override
    public void onUserAddError(Exception message) {
        Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        mLoadingDialog.dismiss();
    }

    @Override
    public void onNetworkError(Exception message) {
        Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        mLoadingDialog.dismiss();
    }

}
