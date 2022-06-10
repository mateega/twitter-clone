package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Comment;

import okhttp3.Headers;

public class CommentActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    android.widget.ImageView ivProfileImageComment;
    TextView tvScreenName;

    EditText etComposeComment;
    Button bTweetComment;
    String replyUser;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setActionBarIcon();

        client = TwitterApp.getRestClient(this);

        etComposeComment = findViewById(R.id.etComposeComment);
        bTweetComment = findViewById(R.id.bTweetComment);

        ivProfileImageComment = (ImageView) findViewById(R.id.ivProfileImageComment);
        Glide.with(this).load(getIntent().getStringExtra("profileImage")).into(ivProfileImageComment);
        replyUser = "@" + getIntent().getStringExtra("handle");

        tvScreenName = findViewById(R.id.tvReplyHandle);
        tvScreenName.setText(replyUser);

        etComposeComment.setText(replyUser);

        // set click listener on button
        bTweetComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etComposeComment.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(CommentActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                }

                client.publishReply(tweetContent, getIntent().getStringExtra("replyStatusId"), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);

                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish(); // closes the activity and passes data to parent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });

            }
        });



    }








    public void setActionBarIcon() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.twitter_icon_2);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }
}