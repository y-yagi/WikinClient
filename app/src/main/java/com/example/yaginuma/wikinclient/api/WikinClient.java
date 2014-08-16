package com.example.yaginuma.wikinclient.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaginuma on 14/07/08.
 */
public class WikinClient {
    private Context mContext;
    private String mBaseUrl;
    private String mEncodedAuth;

    private int mPageCount;
    private String[] mMenu;
    private ArrayList<Page> mPages;

    private static final String LIST_PATH = "/pages.json?recent_pages=true";
    private static final String UPDATE_PATH = "/pages/";
    private static final String SEARTH_PATH = "/pages/search.json?query=";
    private static final String TAG = WikinClient.class.getSimpleName();

    public WikinClient(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        this.mBaseUrl = sharedPreferences.getString(mContext.getString(R.string.pref_wikin_url), "");
        String userName = sharedPreferences.getString(mContext.getString(R.string.pref_wikin_auth_user_name), "");
        String password = sharedPreferences.getString(mContext.getString(R.string.pref_wikin_auth_password), "");
        this.mEncodedAuth = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.DEFAULT);
        this.mPages = new ArrayList<Page>();
    }

    public String getBaseUrl() { return this.mBaseUrl; }

    public String getListUrl() {
        return this.mBaseUrl + LIST_PATH;
    }

    public String getUpdateUrl() {
        return this.mBaseUrl + UPDATE_PATH;
    }

    public String getSearchUrl(String query) {
        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this.mBaseUrl + SEARTH_PATH + encodedQuery;
    }

    public void parseListResponse(JSONObject response) throws JSONException {
        JSONArray pages = response.getJSONArray("pages");
        this.mPageCount = response.getInt("results_returned");
        this.mMenu = new String[mPageCount];
        this.mPages.clear();

        for (int i = 0; i < mPageCount; i++) {
            Page page = new Page(
                    pages.getJSONObject(i).getInt("id"),
                    pages.getJSONObject(i).getString("title"),
                    pages.getJSONObject(i).getString("body"),
                    pages.getJSONObject(i).getString("extracted_body"),
                    pages.getJSONObject(i).getString("url")
            );
            mPages.add(page);
            mMenu[i] = page.getTitle();
        }
    }


    public String[] getMenu() {
        return this.mMenu;
    }

    public ArrayList<Page> getPages() {
        return this.mPages;
    }

    public int getPageCount() {
        return this.mPageCount;
    }

    public Map<String, String> addAuthHeaders(Map<String, String> oldHeaders) {
        Map<String, String> newHeaders = new HashMap<String, String>();
        newHeaders.putAll(oldHeaders);
        newHeaders.put("Authorization", "Basic " + mEncodedAuth);
        return newHeaders;
    }

    public boolean verificationResponse(JSONObject response) throws JSONException {
        String result = response.getString("status");
        if (result.equals("ok")) {
            return true;
        } else {
            return false;
        }
    }
}


