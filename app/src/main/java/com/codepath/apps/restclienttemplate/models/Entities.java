package com.codepath.apps.restclienttemplate.models;

import android.util.Log;
import android.widget.ImageView;


import com.codepath.apps.restclienttemplate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Entities {

    public String mediaUrl;

    // empty constructer needed for the Parceler library
    public Entities(){}

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        if (jsonObject.has("media")) {
            String url = jsonObject.getJSONArray("media").getJSONObject(0).getString("media_url_https");
            entities.mediaUrl = url;

        }
        return entities;
    }
}
