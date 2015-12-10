package com.talview.socialauthandroid.WebVIewClients;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.talview.socialauthandroid.helpers.MiscHelpers;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vivek  on 19/10/15.
 */
public class TwitterWebViewClient extends WebViewClient {

    ProgressDialog progressDialog;
    private String TWITTER_CALLBACK_URL;
    String app_id,app_secret_key,oauth_secret_key;
    Activity activity;
    WebViewClientCallback webViewClientCallback;

    public TwitterWebViewClient(@NonNull Activity _activity, @NonNull String _callbackURL,
                                @NonNull String _appId, @NonNull String _appSecret,
                                @NonNull String _oauth_secret_key,
                                @NonNull WebViewClientCallback _callback,
                                @NonNull ProgressDialog _progressDialog){
        super();

        activity                = _activity;
        TWITTER_CALLBACK_URL    = _callbackURL;
        webViewClientCallback   = _callback;
        progressDialog          = _progressDialog;
        app_secret_key          = _appSecret;
        oauth_secret_key        = _oauth_secret_key;
        app_id                  = _appId;
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        progressDialog.show();
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if ((url != null) && (url.startsWith(TWITTER_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
            Log.i("CALLBACK:", url);
            view.stopLoading();
            view.setVisibility(View.INVISIBLE);
            Uri uri = Uri.parse(url);

            String oauth_token = uri.getQueryParameter("oauth_token");
            String oauth_verifier = uri.getQueryParameter("oauth_verifier");


            final String request_token_url = "https://api.twitter.com/oauth/access_token";

            String oauth_nonce = MiscHelpers.randomString(10);
            String oauth_timestamp = ""+MiscHelpers.getTimestamp();

            Map<String, String> map = new TreeMap<String, String>();
            map.put("oauth_signature_method", "HMAC-SHA1");
            map.put("oauth_version", "1.0");
            map.put("oauth_consumer_key", app_id);
            map.put("oauth_nonce", oauth_nonce);
            map.put("oauth_timestamp", oauth_timestamp);
            map.put("oauth_token", oauth_token); //not applicable for request_token

            try{
                String oauth_signature = MiscHelpers.getOAuthSignature(map, app_secret_key,
                        oauth_secret_key, "https://api.twitter.com/oauth/request_token");

                String Authorization = "OAuth oauth_consumer_key=\""+app_id+"\"," +
                        "oauth_token=\""+oauth_token+"\"," +
                        "oauth_signature_method=\"HMAC-SHA1\"," +
                        "oauth_timestamp=\""+oauth_timestamp+"\"," +
                        "oauth_nonce=\""+oauth_nonce+"\"," +
                        "oauth_version=\"1.0\"," +
                        "oauth_signature=\""+oauth_signature+"\"";

                Ion.with(activity)
                        .load("POST", request_token_url)
                        .setHeader("Authorization", Authorization)
                        .setBodyParameter("oauth_verifier", oauth_verifier)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e == null) {
                                    Uri uri = Uri.parse(request_token_url + "?" + result);
                                    final String access_token = uri.getQueryParameter("oauth_token");
                                    final String access_token_secret = uri.getQueryParameter("oauth_token_secret");
                                    webViewClientCallback.execute(access_token+":"+access_token_secret);
                                }
                            }
                        });

            }catch (Exception ex){

            }


        } else {
            progressDialog.dismiss();
            super.onPageFinished(view, url);
        }
    }

}
