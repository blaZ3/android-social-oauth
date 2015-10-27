package com.talview.socialauthwrapper.activities;

import android.app.Activity;
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

import com.talview.socialauthwrapper.R;
import com.talview.socialauthwrapper.SocialAuthConstants;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class TwitterAuthActivity extends Activity {

    private String TWITTER_CALLBACK_URL;

    private WebView mWebView;
    private OAuthService mOauthService;
    private Token mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        String app_id = intent.getStringExtra(SocialAuthConstants.APP_ID);
        String app_secret_key = intent.getStringExtra(SocialAuthConstants.APP_SECRET);
        TWITTER_CALLBACK_URL = intent.getStringExtra(SocialAuthConstants.CALLBACK_URL);

        mWebView = (WebView) findViewById(R.id.web_social_auth);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());

        mOauthService = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(app_id)
                .apiSecret(app_secret_key)
                .callback(TWITTER_CALLBACK_URL)
                .build();

        startAuthorize();
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                mRequestToken = mOauthService.getRequestToken();
                String authUrl = mOauthService.getAuthorizationUrl(mRequestToken);
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
            if ((url != null) && (url.startsWith(TWITTER_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                Log.i("CALLBACK:", url);
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse(url);
                final Verifier verifier = new Verifier(uri.getQueryParameter("oauth_verifier"));
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        return mOauthService.getAccessToken(mRequestToken, verifier);
                    }

                    @Override
                    protected void onPostExecute(final Token accessToken) {
                        Intent output = new Intent();
                        output.putExtra(SocialAuthConstants.ACCESS_TOKEN, accessToken.getToken().toString()+":"+accessToken.getSecret());
                        setResult(RESULT_OK, output);
                        finish();
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };
}

