package com.talview.socialauthwrapper.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.talview.socialauthwrapper.R;
import com.talview.socialauthwrapper.SocialAuthConstants;
import com.talview.socialauthwrapper.helpers.MiscHelpers;

public class LinkedInAuthActivity extends Activity {

    private String LINKEDIN_CALLBACK_URL;

    private WebView mWebView;

    final String startAuthUrl = "https://www.linkedin.com/uas/oauth2/authorization?" +
            "response_type=code&" +
            "client_id=[CLIENT_ID]&" +
            "redirect_uri=[CALLBACK]&" +
            "state=TALVIEW&" +
            "scope=r_emailaddress";

    String app_id,app_secret_key;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        app_id = intent.getStringExtra(SocialAuthConstants.APP_ID);
        app_secret_key = intent.getStringExtra(SocialAuthConstants.APP_SECRET);
        LINKEDIN_CALLBACK_URL = intent.getStringExtra(SocialAuthConstants.CALLBACK_URL);

        mWebView = (WebView) findViewById(R.id.web_social_auth);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());

        progressDialog = ProgressDialog.show(LinkedInAuthActivity.this,getResources().getString(R.string.app_name),"Please wait");
        progressDialog.dismiss();

        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String authUrl = startAuthUrl;
                authUrl = authUrl.replace("[CLIENT_ID]",app_id);
                authUrl = authUrl.replace("[CALLBACK]", LINKEDIN_CALLBACK_URL);
                return authUrl;
            }

            @Override
            protected void onPostExecute(String url) {
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressDialog.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if ((url != null) && (url.startsWith(LINKEDIN_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                Log.i("CALLBACK:", url);
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse(url);

                String request_code = uri.getQueryParameter("code");
                Log.i("ACCESS CODE", request_code);

                final String request_token_url = "https://www.linkedin.com/uas/oauth2/accessToken?" +
                        "grant_type=authorization_code&" +
                        "code="+request_code+"&" +
                        "redirect_uri="+LINKEDIN_CALLBACK_URL+"&" +
                        "client_id="+app_id+"&" +
                        "client_secret="+app_secret_key;

                Ion.with(LinkedInAuthActivity.this)
                        .load(request_token_url)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e == null){
                                    JsonObject response = MiscHelpers.parseAsJsonObject(result);
                                    Intent output = new Intent();
                                    output.putExtra(SocialAuthConstants.ACCESS_TOKEN, response.get("access_token").getAsString());
                                    setResult(RESULT_OK, output);
                                    finish();

                                }
                            }
                        });

            } else {
                progressDialog.dismiss();
                super.onPageFinished(view, url);
            }
        }

    };



}
