package com.talview.socialauthandroid;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.talview.socialauthandroid.WebVIewClients.FacebookWebViewClient;
import com.talview.socialauthandroid.WebVIewClients.GitHubWebViewClient;
import com.talview.socialauthandroid.WebVIewClients.LinkedInWebViewClient;
import com.talview.socialauthandroid.WebVIewClients.StackOverFlowWebViewClient;
import com.talview.socialauthandroid.WebVIewClients.TwitterWebViewClient;
import com.talview.socialauthandroid.WebVIewClients.WebViewClientCallback;
import com.talview.socialauthandroid.helpers.MiscHelpers;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vivek  on 19/10/15.
 */
public class SocialAuthManager {

    public static final int FACEBOOK      = 1;
    public static final int GOOGLE        = 2;
    public static final int LINKEDIN      = 3;
    public static final int STACKOVERFLOW = 4;
    public static final int TWITITER      = 5;
    public static final int GITHUB        = 6;

    String authUrl;

    final String linkedinStartAuthUrl = "https://www.linkedin.com/uas/oauth2/authorization?" +
            "response_type=code&" +
            "client_id=[CLIENT_ID]&" +
            "redirect_uri=[CALLBACK]&" +
            "state=TALVIEW&" +
            "scope=r_emailaddress";

    final String githubStartAuthUrl = "https://github.com/login/oauth/authorize?" +
            "client_id=[CLIENT_ID]&" +
            "scope=user&" +
            "redirect_uri=[CALLBACK]";

    final String stackOverFlowStartAuthUrl = "https://stackexchange.com/oauth?" +
            "client_id=[CLIENT_ID]&" +
            "scope=private_info&" +
            "redirect_uri=[CALLBACK]";

    final String twitterStartAuthUrl = "https://api.twitter.com/oauth/authorize?" +
            "oauth_token=[OAUTH_TOKEN]";

    final String facebookStartAuthUrl = "https://www.facebook.com/dialog/oauth?" +
            "client_id=[CLIENT_ID]&" +
            "redirect_uri=[CALLBACK]";

    Activity activity;
    Context context;
    int type;

    Dialog dialog;
    SocialAuthInterface socialAuthInterface;
    WebViewClient mwebViewClient;

    String callbackUrl,app_id,app_secret;
    ProgressDialog progressDialog;

    public SocialAuthManager(@NonNull Activity _activity, @NonNull int _type,
                             @NonNull String _callbackUrl, @NonNull String _appID,
                             @NonNull String _appSecret){
        activity    = _activity;
        context     = activity.getApplicationContext();
        type        = _type;
        callbackUrl = _callbackUrl;
        app_id      = _appID;
        app_secret  = _appSecret;
    }

    WebViewClientCallback webViewClientCallback = new WebViewClientCallback() {
        @Override
        public void execute(String result) {
            progressDialog.dismiss();
            dialog.dismiss();
            String access_token = result.split(":")[0];
            try{
                String access_token_secret = result.split(":")[1];
                socialAuthInterface.authCallback(true, type, access_token, access_token_secret);
            }catch (Exception ex){
                socialAuthInterface.authCallback(true, type, access_token, "");
            }
        }
    };

    public void doAuthorise(@NonNull SocialAuthInterface authInterface){
        socialAuthInterface = authInterface;

        progressDialog = ProgressDialog.show(activity, "Loading", "Please wait");
        progressDialog.dismiss();

        switch (type){
            case LINKEDIN:
                mwebViewClient = new LinkedInWebViewClient(activity, callbackUrl, app_id,
                        app_secret, webViewClientCallback,progressDialog);

                authUrl = linkedinStartAuthUrl;
                authUrl = authUrl.replace("[CLIENT_ID]",app_id);
                authUrl = authUrl.replace("[CALLBACK]", callbackUrl);
                showDialog(authUrl);
                break;
            case GITHUB:
                mwebViewClient = new GitHubWebViewClient(activity, callbackUrl, app_id,
                        app_secret, webViewClientCallback,progressDialog);

                authUrl = githubStartAuthUrl;
                authUrl = authUrl.replace("[CLIENT_ID]",app_id);
                authUrl = authUrl.replace("[CALLBACK]", callbackUrl);
                showDialog(authUrl);
                break;
            case STACKOVERFLOW:
                mwebViewClient = new StackOverFlowWebViewClient(activity, callbackUrl, app_id,
                        app_secret, webViewClientCallback,progressDialog);

                authUrl = stackOverFlowStartAuthUrl;
                authUrl = authUrl.replace("[CLIENT_ID]",app_id);
                authUrl = authUrl.replace("[CALLBACK]", callbackUrl);
                showDialog(authUrl);
                break;
            case FACEBOOK:
                mwebViewClient = new FacebookWebViewClient(activity, callbackUrl, app_id,
                        app_secret, webViewClientCallback,progressDialog);

                authUrl = facebookStartAuthUrl;
                authUrl = authUrl.replace("[CLIENT_ID]",app_id);
                authUrl = authUrl.replace("[CALLBACK]", callbackUrl);
                showDialog(authUrl);
                break;
            case TWITITER:
                String oauth_nonce = MiscHelpers.randomString(10);
                String oauth_timestamp = ""+MiscHelpers.getTimestamp();

                Log.d("TWITTER oauth_nonce",oauth_nonce);
                Log.d("TWITTER oauth_timestamp",oauth_timestamp);

                Map<String, String> map = new TreeMap<String, String>();
                map.put("oauth_signature_method", "HMAC-SHA1");
                map.put("oauth_version", "1.0");
                map.put("oauth_consumer_key", app_id);
                map.put("oauth_nonce", oauth_nonce);
                map.put("oauth_timestamp", oauth_timestamp);
//                map.put("oauth_token", ""); //not applicable for request_token

                try{
                    String oauth_signature = MiscHelpers.getOAuthSignature(map,
                            app_secret,
                            "",
                            "https://api.twitter.com/oauth/request_token");

                    Log.d("TWITTER oauth_signature",oauth_signature);

                    String Authorization = "OAuth oauth_consumer_key=\""+app_id+"\"," +
                            "oauth_signature_method=\"HMAC-SHA1\"," +
                            "oauth_timestamp=\""+oauth_timestamp+"\"," +
                            "oauth_nonce=\""+oauth_nonce+"\"," +
                            "oauth_version=\"1.0\"," +
                            "oauth_signature=\""+oauth_signature+"\"";

                    Log.d("TWITTER Authorization",Authorization);

                    progressDialog.show();
                    Ion.with(context)
                            .load("POST", "https://api.twitter.com/oauth/request_token")
                            .setHeader("Authorization", Authorization)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    progressDialog.dismiss();
                                    if (e == null){
                                        /*
                                        oauth_token=ikswvwAAAAAAhznUAAABUIWqd-M&oauth_token_secret=dS0Vny8yCdb7jdQKIUZ4HBL7i5RGGmbp&
                                        oauth_callback_confirmed=true
                                         */
                                        String oauth_token = (result.split("&")[0]).split("=")[1];
                                        String oauth_token_secret = (result.split("&")[1]).split("=")[1];

                                        Toast.makeText(context, oauth_token + "$$$" + oauth_token_secret, Toast.LENGTH_LONG).show();

                                        mwebViewClient = new TwitterWebViewClient(activity,callbackUrl,
                                                app_id,app_secret, oauth_token_secret,
                                                webViewClientCallback, progressDialog);

                                        authUrl = twitterStartAuthUrl;
                                        authUrl = authUrl.replace("[OAUTH_TOKEN]", oauth_token);
                                        showDialog(authUrl);
                                    }else{
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            });


                }catch (Exception ex){
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            default:
                //TODO remove this default
                mwebViewClient = new LinkedInWebViewClient(activity, callbackUrl, app_id,
                        app_secret, webViewClientCallback,progressDialog);
                break;
        }
    }

    private void showDialog(@NonNull String startUrl){
        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);

        WebView webView = (WebView) dialog.findViewById(R.id.theWebView);

        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(mwebViewClient);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(startUrl);

        ImageView imgClose = (ImageView)dialog.findViewById(R.id.imgDialogClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface SocialAuthInterface{
        public void authCallback(boolean success, int provider, String access_token, String access_token_secret);
    }

}

