package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.ClickListener {

    public static final String TAG = "TimelineActivity";
    public final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(TimelineActivity.this, tweets, this);
        // recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        // lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh the list
                // make sure to call swipeConatiner.setRefreshing(false)
                // one the network request has completed successfully
                fetchTimelineAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateHomeTimeline();
        setActionBarIcon();
        //hideProgressBar();

    }

    private void fetchTimelineAsync(int page) {
        showProgressBar();
        // send the network request to fetch the updated data
        // 'client' here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // clear out old items before appending in the new ones
                adapter.clear();
                // ...the data has come back, add new items to your adapter..
                try {
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
        hideProgressBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            // compose icon has been selected
            // Toast.makeText(this, "Compose!",  Toast.LENGTH_SHORT).show();
            // navigate to the compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
//        if (item.getItemId() == R.id.ivComment) {
//            Intent intent = new Intent(this, CommentActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
//            return true;
//        }



        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get date from intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // update the recycler view with the tweet
            // modify data source of tweets
            tweets.add(0, tweet);
            // update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure " + response, throwable);
            }
        });
    }

    public void onLogoutButton(View view) {
        finish();

        // forget who's logged in
        TwitterApp.getRestClient(this).clearAccessToken();

        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void setActionBarIcon() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.twitter_icon_2);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @Override
    public void onPositionClicked(int position) {

    }

    @Override
    public void onLongClicked(int position) {

    }
}