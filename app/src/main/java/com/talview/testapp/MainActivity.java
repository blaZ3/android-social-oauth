package com.talview.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.talview.socialauthwrapper.SocialAuthConstants;
import com.talview.socialauthwrapper.activities.FacebookAuthActivity;
import com.talview.socialauthwrapper.activities.TwitterAuthActivity;

public class MainActivity extends AppCompatActivity {

    public static final String FB_APP_ID = "1029103087121755";
    public static final String FB_APP_SECRET = "eeafc02123f6470997812fa4aa85a222";

    public static final String TWITTER_APP_ID = "XFruaEEc8MJgdGqSM21oBZ0tb";
    public static final String TWITTER_APP_SECRET = "fomg3T0P2pj0mfL75k9Ms3NrZiFaSU6Pa5kgJMmmWSBiIKngZN";


    private static final String FB_CALLBACK_URL = "https://www.facebook.com/connect/login_success.html";
    private static final String TWITTER_CALLBACK_URL = "https://engage.talview.com/ouath/twitter/callback";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

//        Intent linkedinAuthIntent = new Intent(MainActivity.this, LinkedInAuthActivity.class);
//        linkedinAuthIntent.putExtra(SocialAuthConstants.APP_ID, "75h5ezfyrphus0");
//        linkedinAuthIntent.putExtra(SocialAuthConstants.APP_SECRET, "4Sm6CX1x2Mikbk6U");
//        linkedinAuthIntent.putExtra(SocialAuthConstants.CALLBACK_URL, "https://engage.talview.com/ouath/linkedin/callback");
//        startActivityForResult(linkedinAuthIntent, SocialAuthConstants.DO_AUTH_LINKEDIN);

//        Intent googleAuthIntent = new Intent(MainActivity.this, GoogleAuthActivity.class);
//        googleAuthIntent.putExtra(SocialAuthConstants.APP_ID, "365746886263-htjoplj7gh69a9ojmel7tku5kdjjq8d6.apps.googleusercontent.com");
//        googleAuthIntent.putExtra(SocialAuthConstants.APP_SECRET, "XPK5xI3_V_aToyEywM-Cukt6");
//        googleAuthIntent.putExtra(SocialAuthConstants.CALLBACK_URL, "https://engage.talview.com/ouath/google/callback");
//        startActivityForResult(googleAuthIntent, SocialAuthConstants.DO_AUTH_GOOGLE);

//        Intent githubAuthIntent = new Intent(MainActivity.this, GithubAuthActivity.class);
//        githubAuthIntent.putExtra(SocialAuthConstants.APP_ID, "e3af420803f70817eaaa");
//        githubAuthIntent.putExtra(SocialAuthConstants.APP_SECRET, "1180773fafdd99a3b93d489b14d694a396c0287c");
//        githubAuthIntent.putExtra(SocialAuthConstants.CALLBACK_URL, "https://engage.talview.com/oauth/github/callback");
//        startActivityForResult(githubAuthIntent, SocialAuthConstants.DO_AUTH_GITHUB);

//        Intent stackoverflowbAuthIntent = new Intent(MainActivity.this, StackoverflowAuthActivity.class);
//        stackoverflowbAuthIntent.putExtra(SocialAuthConstants.APP_ID, "5679");
//        stackoverflowbAuthIntent.putExtra(SocialAuthConstants.APP_SECRET, "K59S)2rl65w2j0wGzq1Ijg((");
//        stackoverflowbAuthIntent.putExtra(SocialAuthConstants.APP_KEY, "xiVGEVS6cBZruleHlS*5nw((");
//        stackoverflowbAuthIntent.putExtra(SocialAuthConstants.CALLBACK_URL, "https://engage.talview.com/oauth/stackoverflow/callback");
//        startActivityForResult(stackoverflowbAuthIntent, SocialAuthConstants.DO_AUTH_STACKOVERFLOW);

    }

    public void doFbLogin(View v){
        Intent fbAuthIntent = new Intent(MainActivity.this, FacebookAuthActivity.class);
        fbAuthIntent.putExtra(SocialAuthConstants.APP_ID, "429353233928451");
        fbAuthIntent.putExtra(SocialAuthConstants.APP_SECRET, "36febae030f7c7bd454b099d7fb53444");
        fbAuthIntent.putExtra(SocialAuthConstants.CALLBACK_URL, "https://engage.talview.com/ouath/facebook/callback");
        startActivityForResult(fbAuthIntent, SocialAuthConstants.DO_AUTH_FB);
    }

    public void doTwitterLogin(View v){
        Intent twitterAuthIntent = new Intent(MainActivity.this, TwitterAuthActivity.class);
        twitterAuthIntent.putExtra(SocialAuthConstants.APP_ID, "XFruaEEc8MJgdGqSM21oBZ0tb");
        twitterAuthIntent.putExtra(SocialAuthConstants.APP_SECRET, "fomg3T0P2pj0mfL75k9Ms3NrZiFaSU6Pa5kgJMmmWSBiIKngZN");
        twitterAuthIntent.putExtra(SocialAuthConstants.CALLBACK_URL, "https://engage.talview.com/ouath/twitter/callback");
        startActivityForResult(twitterAuthIntent, SocialAuthConstants.DO_AUTH_TWITTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            String response = data.getStringExtra(SocialAuthConstants.ACCESS_TOKEN);
            Log.i("RESPONSE", response);
            switch (requestCode){
                case SocialAuthConstants.DO_AUTH_TWITTER:
                    textView.setText(response);
                    break;
                case SocialAuthConstants.DO_AUTH_LINKEDIN:
                    textView.setText(response);
                    break;
                case SocialAuthConstants.DO_AUTH_GOOGLE:
                    textView.setText(response);
                    break;
                case SocialAuthConstants.DO_AUTH_FB:
                    textView.setText(response);
                    break;
                case SocialAuthConstants.DO_AUTH_GITHUB:
                    textView.setText(response);
                case SocialAuthConstants.DO_AUTH_STACKOVERFLOW:
                    textView.setText(response);
                    break;
                default:
                    break;
            }
        }
    }
}
