package com.talview.socialAuthtestapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.talview.socialauthandroid.SocialAuthManager;

public class TestActivity extends AppCompatActivity {

    Dialog dialog;
    SocialAuthManager socialAuthManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        socialAuthManager = new SocialAuthManager(TestActivity.this,
                SocialAuthManager.LINKEDIN,
                "https://engage.talview.com/ouath/linkedin/callback",
                "75h5ezfyrphus0",
                "4Sm6CX1x2Mikbk6U");

//        socialAuthManager = new SocialAuthManager(TestActivity.this,
//                SocialAuthManager.GITHUB,
//                "https://engage.talview.com/oauth/github/callback",
//                "e3af420803f70817eaaa",
//                "1180773fafdd99a3b93d489b14d694a396c0287c");

//        socialAuthManager = new SocialAuthManager(TestActivity.this,
//                SocialAuthManager.STACKOVERFLOW,
//                "https://engage.talview.com/oauth/stackoverflow/callback",
//                "5679",
//                "K59S)2rl65w2j0wGzq1Ijg((");

//        socialAuthManager = new SocialAuthManager(TestActivity.this,
//                SocialAuthManager.FACEBOOK,
//                "https://engage.talview.com/ouath/facebook/callback",
//                "429353233928451",
//                "36febae030f7c7bd454b099d7fb53444");

//        socialAuthManager = new SocialAuthManager(TestActivity.this,
//                SocialAuthManager.TWITITER,
//                "https://engage.talview.com/ouath/twitter/callback",
//                "XFruaEEc8MJgdGqSM21oBZ0tb",
//                "fomg3T0P2pj0mfL75k9Ms3NrZiFaSU6Pa5kgJMmmWSBiIKngZN");
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
