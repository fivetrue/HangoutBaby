package com.fivetrue.hangoutbaby.net;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by HanyLuv on 2016-03-14.
 */
public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    /**
     * Application Context
     */
    private Context mContext = null;
    private RequestQueue mQueue = null;
    private NetworkCache mNetworkCache = null;

    private static NetworkManager sInstance = null;


    public static NetworkManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("NetworkManager has to called init in Application");
        }
        return sInstance;
    }

    public static void init(Context context){
        sInstance = new NetworkManager(context);
    }

    private NetworkManager(Context context) {
        mContext = context;
        mQueue = Volley.newRequestQueue(context);
        mNetworkCache = new NetworkCache(mContext.getCacheDir());
    }

    // P요청
//    public void request(RequestType type, Map keyValues, NetworkListener listener) {
//        mQueue = Volley.newRequestQueue(context);
//        JsonObjectRequest request = createRequest(type, keyValues, listener);
//        mQueue.add(request);
//    }

    public void request(BaseApiRequest baseApiRequest){
        if(baseApiRequest != null){
            Request request = null;
            switch (baseApiRequest.getMethod()) { //요청 타입에따라 api주소를 (apiAddr) 설정한다.
                case Request.Method.GET:
                    request = requestGet(baseApiRequest);
                    break;
                case Request.Method.POST:
                    request = requestPost(baseApiRequest);
                    break;
            }

            if(request != null){
                mQueue.add(request);
            }
        }
    }

    private Request requestPost(final BaseApiRequest baseApiRequest){
        Request request = null;
        if(baseApiRequest != null){
            Cache.Entry entry = mNetworkCache.get(baseApiRequest.getUrl());
            Log.d(TAG, "requestPost " + baseApiRequest.toString());

            if(baseApiRequest.isCache() && entry != null && !entry.isExpired()){
                Log.d(TAG, "api cached : " + entry);
                try {
                    BaseApiResponse apiResponse = baseApiRequest.getResponse();
                    if(apiResponse != null){
                        apiResponse.setResponse(new JSONObject(new String(entry.data)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    VolleyError error = new VolleyError(e);
                    BaseApiResponse apiResponse = baseApiRequest.getResponse();
                    if(apiResponse != null){
                        apiResponse.setError(error);
                    }
                }
            }else{
                request = new StringRequest(baseApiRequest.getMethod(), baseApiRequest.getUrl(),
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                /**
                                 * Request result를 캐싱한다
                                 */
                                Cache.Entry entry = new Cache.Entry();
                                entry.data = response.toString().getBytes();
                                entry.lastModified = System.currentTimeMillis();
                                entry.ttl = System.currentTimeMillis() + baseApiRequest.getCacheTimeMilliseconds();
                                mNetworkCache.put(baseApiRequest.getUrl(), entry);
                                BaseApiResponse apiResponse = baseApiRequest.getResponse();

                                if(apiResponse != null){
                                    try {
                                        apiResponse.setResponse(new JSONObject(response));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                Log.d(TAG, "error => " + error.toString());
                                BaseApiResponse apiResponse = baseApiRequest.getResponse();
                                if(apiResponse != null){
                                    apiResponse.setError(error);
                                }
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return baseApiRequest.getHeaders();
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return baseApiRequest.getParams();
                    }
                };
            }
        }
        return request;
    }

    private Request requestGet(final BaseApiRequest baseApiRequest){
        Request request = null;
        if(baseApiRequest != null){
            Log.d(TAG, "requestPost " + baseApiRequest.toString());
            request = new StringRequest(baseApiRequest.getMethod(), baseApiRequest.getUrl(),
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            BaseApiResponse apiResponse = baseApiRequest.getResponse();
                            if(apiResponse != null){
                                try {
                                    apiResponse.setResponse(new JSONObject(response));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d(TAG,"error => "+error.toString());
                            BaseApiResponse apiResponse = baseApiRequest.getResponse();
                            if(apiResponse != null){
                                apiResponse.setError(error);
                            }
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return baseApiRequest.getHeaders();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return baseApiRequest.getParams();
                }
            };
        }
        return request;
    }

    public RequestQueue getRequestQueue(){
        return mQueue;
    }
}
