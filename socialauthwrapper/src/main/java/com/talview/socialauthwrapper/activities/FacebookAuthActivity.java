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
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class FacebookAuthActivity extends Activity {

    private String FACEBOOK_CALLBACK_URL;

    private WebView mWebView;
    private OAuthService mOauthService;
    private Token mRequestToken;

    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        String app_id = intent.getStringExtra(SocialAuthConstants.APP_ID);
        String app_secret_key = intent.getStringExtra(SocialAuthConstants.APP_SECRET);
        FACEBOOK_CALLBACK_URL = intent.getStringExtra(SocialAuthConstants.CALLBACK_URL);

        mWebView = (WebView) findViewById(R.id.web_social_auth);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());

        mOauthService = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(app_id)
                .apiSecret(app_secret_key)
                .callback(FACEBOOK_CALLBACK_URL)
                .scope("email")
                .build();

        startAuthorize();
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                mRequestToken = mOauthService.getRequestToken();
                String authUrl = mOauthService.getAuthorizationUrl(null);
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
            if ((url != null) && (url.startsWith(FACEBOOK_CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                Log.i("CALLBACK:", url);
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse(url);
                final Verifier verifier = new Verifier(uri.getQueryParameter("code"));
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        return mOauthService.getAccessToken(mRequestToken, verifier);
                    }

                    @Override
                    protected void onPostExecute(final Token accessToken) {
                        Intent output = new Intent();
                        output.putExtra(SocialAuthConstants.ACCESS_TOKEN, accessToken.getToken().toString());
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
