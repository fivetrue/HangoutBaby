package com.fivetrue.hangoutbaby.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.R;
import com.fivetrue.hangoutbaby.net.BaseApiResponse;
import com.fivetrue.hangoutbaby.net.ErrorCode;
import com.fivetrue.hangoutbaby.net.NetworkManager;
import com.fivetrue.hangoutbaby.net.request.AddUserRequest;
import com.fivetrue.hangoutbaby.net.request.LoginUserRequest;
import com.fivetrue.hangoutbaby.preferences.ConfigPreferenceManager;
import com.fivetrue.hangoutbaby.vo.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;

/**
 * Created by kwonojin on 16. 9. 19..
 */
public class LoginHelper implements FirebaseAuth.AuthStateListener, FacebookCallback<LoginResult> {

    public enum LoginType{
        Normal, Google, Kakao, Facebook
    }

    public interface OnAccountManagerListener{
        void onUserLoginSuccess(LoginType type, User user);
        void onUserAddSuccess(LoginType type, User user);
        void onUserLoginError(String message);
        void onUserAddError(Exception message);
        void onNetworkError(Exception message);
    }

    public interface OnUserInfoUpdateListener{
        void onUpdateUserInfo(User user);
        void onUpdateFailed(Exception e);
    }

    private static final String TAG = "LoginHelper";


    private static final int REQUEST_GOOGLE_ACCOUNT_LOGIN = 0x44;

    /**
     * Google
     */
    private GoogleApiClient mGoogleApiClient = null;

    /**
     * Facebook
     */
    private static final String[] FACEBOOK_PERMISSION = {"email", "public_profile", "user_friends"};
    private LoginManager mFacebookLoginManager = null;
    private CallbackManager mCallbackManager;

    private ConfigPreferenceManager mConfigPref = null;

    private AddUserRequest mAddUserRequest = null;
    private LoginUserRequest mLoginUserRequest = null;

    private FragmentActivity mActivity = null;

    private OnAccountManagerListener mOnAccountManagerListener = null;

    private LoginType mType = LoginType.Normal;

    private FirebaseAuth mAuth = null;

    public LoginHelper(FragmentActivity activity){
        mActivity = activity;
        mConfigPref = new ConfigPreferenceManager(mActivity);
        mAddUserRequest = new AddUserRequest(mActivity, addUserApiResponse);
        mLoginUserRequest = new LoginUserRequest(mActivity, loginUserApiResponse);

        if(mActivity instanceof OnAccountManagerListener){
            mOnAccountManagerListener = (OnAccountManagerListener) mActivity;
        }

        initFirebase();
        initGoogle();
        initFacebook();

    }
    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    private void initGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREBASE_AUTH_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .enableAutoManage(mActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed() called with: " + "connectionResult = [" + connectionResult + "]");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initFacebook(){
        mFacebookLoginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginManager.registerCallback(mCallbackManager, this);
    }


    public void onStart(){
        mGoogleApiClient.connect();
    }

    public void onStop(){
        mGoogleApiClient.disconnect();
    }

    public boolean isSignIn(){
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getUser(){
        return mAuth.getCurrentUser();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if(requestCode == REQUEST_GOOGLE_ACCOUNT_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleLogin(result);
            }else{
                onUserAddFail(new Exception(mActivity.getString(R.string.failed_login_cancel)));
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleLogin(GoogleSignInResult result){
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, serverAuthCode);
            requestAuthCredential(credential);
        } else {
            onUserAddFail(new Exception(result.getStatus().getStatusMessage()));
        }
    }

    private void requestAuthCredential(AuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                    mAddUserRequest.setUserInfo(user.getUid(), user.getEmail(), user.getPhotoUrl().toString(), mConfigPref.getGcmDeviceId());
                    NetworkManager.getInstance().request(mAddUserRequest);
                }else {
                    onUserAddFail(task.getException());
                }
            }
        });
    }

    private void handlerFacebook(LoginResult loginResult){
        if(loginResult != null){
            AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
            requestAuthCredential(credential);
        }

    }

    public void loginGoogleAccount(){
        Log.d(TAG, "loginGoogleAccount() called with: " + "");
        mType = LoginType.Google;

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, REQUEST_GOOGLE_ACCOUNT_LOGIN);
    }

    public void loginFacebook(){
        mType = LoginType.Facebook;
        mFacebookLoginManager.logOut();
        mFacebookLoginManager.logInWithReadPermissions(mActivity, Arrays.asList(FACEBOOK_PERMISSION));
    }

    public void logout(){
        mConfigPref.saveUser(null);
        mFacebookLoginManager.logOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mAuth.signOut();
    }

    public boolean hasAccount(){
        boolean b = false;
        if(mConfigPref.getUser() != null){
            b = true;
        }
        return b;
    }

    public void saveGcmID(String id){
        mConfigPref.setGcmDeviceId(id);
    }

    public String getGcmID(){
        return mConfigPref.getGcmDeviceId();
    }

    public void updateUser(String name, String imageUrl, final OnUserInfoUpdateListener ll){
        if((!TextUtils.isEmpty(name) || !TextUtils.isEmpty(imageUrl)) && getUser() != null && ll != null){
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            if(!TextUtils.isEmpty(name)){
                builder.setDisplayName(name);
            }
            if(!TextUtils.isEmpty(imageUrl)){
                builder.setPhotoUri(Uri.parse(imageUrl));
            }
            UserProfileChangeRequest profileUpdates = builder.build();
            final FirebaseUser user = getUser();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                LoginUserRequest request = new LoginUserRequest(mActivity, new BaseApiResponse.OnResponseListener<User>() {
                                    @Override
                                    public void onResponse(BaseApiResponse<User> response) {
                                        if(response != null){
                                            if(response.getData() != null){
                                                ll.onUpdateUserInfo(response.getData());
                                            }else{
                                                ll.onUpdateFailed(new Exception(response.getMessage()));
                                            }
                                        }else{
                                            ll.onUpdateFailed(new Exception("Response is null"));
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        ll.onUpdateFailed(error);
                                    }
                                });
                                NetworkManager.getInstance().request(request);
                            }
                        }
                    });
        }

    }

    public void loginUser(String uid, String account, String imageUrl){
        Log.d(TAG, "loginUser() called with: " + "account = [" + account + "], imageUrl = [" + imageUrl + "]");
        mLoginUserRequest.setUserInfo(uid, account, imageUrl, mConfigPref.getGcmDeviceId());
        NetworkManager.getInstance().request(mLoginUserRequest);
    }

    private void onLoginFinish(User user){
        Log.d(TAG, "onLoginFinish() called with: " + "user = [" + user + "]");
        mConfigPref.saveUser(user);
        if(mOnAccountManagerListener != null){
            mOnAccountManagerListener.onUserLoginSuccess(mType, user);
        }
    }

    private void onLoginFail(String message){
        Log.d(TAG, "onLoginFail() called with: " + "message = [" + message + "]");
        if(mOnAccountManagerListener != null){
            mOnAccountManagerListener.onUserLoginError(message);
        }
    }

    private void onUserAddFinish(final User user) {
        Log.d(TAG, "onUserAddFinish() called with: " + "user = [" + user + "]");
        mOnAccountManagerListener.onUserAddSuccess(mType, user);
    }

    private void onUserAddFail(Exception e){
        Log.d(TAG, "onUserAddFail() called with: " + "message = [" + e + "]");
        if(mOnAccountManagerListener != null){
            mOnAccountManagerListener.onUserAddError(e);
        }
    }

    private BaseApiResponse.OnResponseListener<User> addUserApiResponse = new BaseApiResponse.OnResponseListener<User>() {
        @Override
        public void onResponse(BaseApiResponse<User> response) {
            Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
            if(response != null && response.getData() != null){
                switch (mType){
                    case Facebook:
                    case Google:
                    case Kakao:
                        switch (response.getErrorCode()){
                            case ErrorCode.OK:
                            case ErrorCode.EXIST_DATA:
                                onUserAddFinish(response.getData());
                                break;
                            default:
                                onUserAddFail(new Exception(response.getMessage()));
                                break;
                        }
                        break;

                    case Normal:
                        switch (response.getErrorCode()){
                            case ErrorCode.OK:
                                onUserAddFinish(response.getData());
                                break;
                            default:
                                onUserAddFail(new Exception(response.getMessage()));
                                break;
                        }
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(TAG, "onError() called with: " + "error = [" + error + "]");
            if(mOnAccountManagerListener != null){
                mOnAccountManagerListener.onNetworkError(error);
            }

        }
    };

    private BaseApiResponse.OnResponseListener<User> loginUserApiResponse = new BaseApiResponse.OnResponseListener<User>() {
        @Override
        public void onResponse(BaseApiResponse<User> response) {
            Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
            if(response != null){
                switch (response.getErrorCode()){
                    case ErrorCode.OK :
                        onLoginFinish(response.getData());
                        break;

                    default:
                        onLoginFail(response.getMessage());
                        break;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(TAG, "onError() called with: " + "error = [" + error + "]");
            if(mOnAccountManagerListener != null){
                mOnAccountManagerListener.onNetworkError(error);
            }
        }
    };

    @Override
    public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
        if(firebaseAuth != null){
            Log.d(TAG, "onAuthStateChanged() called with: " + "firebaseAuth = [" + firebaseAuth + "]");
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        handlerFacebook(loginResult);
    }

    @Override
    public void onCancel() {
        mOnAccountManagerListener.onUserAddError(new Exception(mActivity.getString(R.string.failed_login_cancel)));
    }

    @Override
    public void onError(FacebookException error) {
        mOnAccountManagerListener.onUserAddError(error);
    }
}
