package com.talview.socialAuthtestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.talview.socialauthandroid.SocialAuthManager;

public class TestActivity extends AppCompatActivity {

    SocialAuthManager socialAuthManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        String github_callback_url = "callback_url";
        String github_app_id = "app_id";
        String github_app_secret = "app_secret";

        //TODO replace callback_url, app_id and app_secret with your respective values

        socialAuthManager = new SocialAuthManager(TestActivity.this,
                SocialAuthManager.GITHUB,
                github_callback_url,
                github_app_id,
                github_app_secret);

    }


    public void doTest(View v){
        socialAuthManager.doAuthorise(new SocialAuthManager.SocialAuthInterface() {
            @Override
            public void authCallback(boolean success, int provider, String access_token, String access_token_secret) {
                Toast.makeText(getApplicationContext(), "Koi " + access_token+":"+access_token_secret, Toast.LENGTH_LONG).show();
            }
        });
    }

}
