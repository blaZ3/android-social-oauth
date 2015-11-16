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

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.talview.socialauthandroid.helpers.MiscHelpers;

/**
 * Created by root on 19/10/15.
 */
public class LinkedInWebViewClient extends WebViewClient {

    ProgressDialog progressDialog;
    private String LINKEDIN_CALLBACK_URL;
    String app_id,app_secret_key;
    Activity activity;
    WebViewClientCallback webViewClientCallback;

    public LinkedInWebViewClient(@NonNull Activity _activity, @NonNull String _callbackURL,
                                 @NonNull String _appId,@NonNull String _appSecret,
                                 @NonNull WebViewClientCallback _callback,
                                 @NonNull ProgressDialog _progressDialog){
        super();

        activity                = _activity;
        LINKEDIN_CALLBACK_URL   = _callbackURL;
        webViewClientCallback   = _callback;
        progressDialog          = _progressDialog;
        app_secret_key          = _appSecret;
        app_id                  = _appId;
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        progressDialog.show();
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if ((url != null) && (url.startsWith(LINKEDIN_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
            Log.i("CALLBACK:", url);
            view.stopLoading();
            view.setVisibility(View.INVISIBLE);
            Uri uri = Uri.parse(url);

            String request_code = uri.getQueryParameter("code");
            Log.i("ACCESS CODE", request_code);

            final String request_token_url = "https://www.linkedin.com/uas/oauth2/accessToken?" +
                    "grant_type=authorization_code&" +
                    "code="+request_code+"&" +
                    "redirect_uri="+LINKEDIN_CALLBACK_URL+"&" +
                    "client_id="+app_id+"&" +
                    "client_secret="+app_secret_key;

            Ion.with(activity)
                    .load(request_token_url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null){
                                JsonObject response = MiscHelpers.parseAsJsonObject(result);
                                webViewClientCallback.execute(response.get("access_token").getAsString()+":"+"");
                            }
                        }
                    });

        } else {
            progressDialog.dismiss();
            super.onPageFinished(view, url);
        }
    }

}
