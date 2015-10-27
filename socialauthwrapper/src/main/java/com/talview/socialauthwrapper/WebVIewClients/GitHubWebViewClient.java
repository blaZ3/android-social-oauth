package com.talview.socialauthwrapper.WebVIewClients;

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

/**
 * Created by root on 19/10/15.
 */
public class GitHubWebViewClient extends WebViewClient {

    ProgressDialog progressDialog;
    private String GITHUB_CALLBACK_URL;
    String app_id,app_secret_key;
    Activity activity;
    WebViewClientCallback webViewClientCallback;

    public GitHubWebViewClient(@NonNull Activity _activity, @NonNull String _callbackURL,
                               @NonNull String _appId, @NonNull String _appSecret,
                               @NonNull WebViewClientCallback _callback,
                               @NonNull ProgressDialog _progressDialog){
        super();

        activity                = _activity;
        GITHUB_CALLBACK_URL = _callbackURL;
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
        if ((url != null) && (url.startsWith(GITHUB_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
            Log.i("CALLBACK:", url);
            view.stopLoading();
            view.setVisibility(View.INVISIBLE);
            Uri uri = Uri.parse(url);

            String request_code = uri.getQueryParameter("code");

            final String request_token_url = "https://github.com/login/oauth/access_token";

            Ion.with(activity)
                    .load(request_token_url)
                    .setBodyParameter("client_id", app_id)
                    .setBodyParameter("redirect_uri", GITHUB_CALLBACK_URL)
                    .setBodyParameter("client_secret", app_secret_key)
                    .setBodyParameter("code", request_code)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                Uri uri = Uri.parse(request_token_url + "?" + result);
                                final String access_token = uri.getQueryParameter("access_token");
                                webViewClientCallback.execute(access_token);
                            }
                        }
                    });

        } else {
            progressDialog.dismiss();
            super.onPageFinished(view, url);
        }
    }

}
