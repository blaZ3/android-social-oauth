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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.talview.socialauthwrapper.R;
import com.talview.socialauthwrapper.SocialAuthConstants;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class GithubAuthActivity extends Activity {

    private String GITHUB_CALLBACK_URL;

    private WebView mWebView;
    private OAuthService mOauthService;
    private Token mRequestToken;

    final String startAuthUrl = "https://github.com/login/oauth/authorize?client_id=[CLIENT_ID]&scope=user&redirect_uri=[CALLBACK]";

    String app_id,app_secret_key;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        app_id = intent.getStringExtra(SocialAuthConstants.APP_ID);
        app_secret_key = intent.getStringExtra(SocialAuthConstants.APP_SECRET);
        GITHUB_CALLBACK_URL = intent.getStringExtra(SocialAuthConstants.CALLBACK_URL);

        mWebView = (WebView) findViewById(R.id.web_social_auth);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());

        progressDialog = ProgressDialog.show(GithubAuthActivity.this,getResources().getString(R.string.app_name),"Please wait");
        progressDialog.dismiss();

        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String authUrl = startAuthUrl;
                authUrl = authUrl.replace("[CLIENT_ID]",app_id);
                authUrl = authUrl.replace("[CALLBACK]", GITHUB_CALLBACK_URL);
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
            if ((url != null) && (url.startsWith(GITHUB_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                Log.i("CALLBACK:", url);
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse(url);

                String request_code = uri.getQueryParameter("code");
                mRequestToken = new Token(request_code, app_secret_key);

                final String request_token_url = "https://github.com/login/oauth/access_token";

                Ion.with(GithubAuthActivity.this)
                        .load(request_token_url)
                        .setBodyParameter("client_id", app_id)
                        .setBodyParameter("redirect_uri", GITHUB_CALLBACK_URL)
                        .setBodyParameter("client_secret", app_secret_key)
                        .setBodyParameter("code", request_code)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e == null){
                                    Uri uri = Uri.parse(request_token_url+"?"+result);
                                    final String access_token = uri.getQueryParameter("access_token");
                                    Log.i("ACCESS TOKEN",access_token);

                                    Intent output = new Intent();
                                    output.putExtra(SocialAuthConstants.ACCESS_TOKEN, access_token);
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
