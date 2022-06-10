package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.w3c.dom.Comment;

import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHoldler> {

    TwitterClient client;
    Context context;
    List<Tweet> tweets;
    ClickListener listener;
    Boolean heartFilled;

    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    // pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets, ClickListener listener) {
        this.context = context;
        this.tweets = tweets;
        this.listener = listener;
    }

    // for reach row, inflate the layout
    @NonNull
    @Override
    public ViewHoldler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHoldler(view, listener);
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHoldler holder, int position) {
        // get the data at position
        Tweet tweet = tweets.get(position);

        // bind the tweet with viewholder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // add a list of items
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }


    // define a viewholder
    public class ViewHoldler extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView ivProfileImage;
        TextView tvNickname;
        TextView tvScreenName;
        TextView tvCreatedAt;
        TextView tvBody;
        CardView ivMediaHolder;
        ImageView ivMedia;

        ImageView ivComment;
        ImageView ivRetweet;
        ImageView ivHeart;

        WeakReference<ClickListener> listenerRef;

        // one row in the recycler view (a tweet!)
        public ViewHoldler(@NonNull View itemView, ClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);


            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvNickname = itemView.findViewById(R.id.tvNickname);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivMediaHolder = itemView.findViewById(R.id.ivMediaHolder);
            ivMedia = itemView.findViewById(R.id.ivMedia);

            ivComment = itemView.findViewById(R.id.ivComment);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivHeart = itemView.findViewById(R.id.ivHeart);

            ivComment.setOnClickListener(this);
            ivRetweet.setOnClickListener(this);
            ivHeart.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);

            tvBody.setText(tweet.body);
            tvNickname.setText(tweet.user.name);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvCreatedAt.setText("· " + tweet.createdAt);

            if (tweet.mediaUrl != null) {
                ivMediaHolder.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.mediaUrl).into(ivMedia);
            }

            Glide.with(context).load(R.drawable.comment).into(ivComment);
            Glide.with(context).load(R.drawable.retweet).into(ivRetweet);
            Glide.with(context).load(R.drawable.heart).into(ivHeart);
            heartFilled = tweet.liked;

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == ivComment.getId()) {
                //Toast.makeText(view.getContext(), "reply", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("handle", tweets.get(getAdapterPosition()).user.screenName);
                intent.putExtra("profileImage", tweets.get(getAdapterPosition()).user.profileImageUrl);
                intent.putExtra("replyStatusId", tweets.get(getAdapterPosition()).id);

                context.startActivity(intent);
            } else if (view.getId() == ivHeart.getId()) {
                tongleHeart(tweets.get(getAdapterPosition()).id);
            } else {
                Toast.makeText(view.getContext(), "row pressed", Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        private void tongleHeart(String tweetId) {
            if (heartFilled) {
                Glide.with(context).load(R.drawable.heart).into(ivHeart);
                heartFilled = false;
//                client.likeTweet(tweetId, false, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Headers headers, JSON json) {
//                        // success
//                    }
//                    @Override
//                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                    }
//                });

            } else {
                Glide.with(context).load(R.drawable.heart_filled).into(ivHeart);
                heartFilled = true;
//                client.likeTweet(tweetId, false, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Headers headers, JSON json) {
//                        // success
//                    }
//                    @Override
//                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                    }
//                });
            }

        }

        @Override
        public boolean onLongClick(View view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Hello Dialog").setMessage("LONG CLICK DIALOG WINDOW")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.create().show();
            listenerRef.get().onLongClicked((getAdapterPosition()));

            return true;
        }
    }
}