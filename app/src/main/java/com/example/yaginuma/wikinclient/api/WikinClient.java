package com.example.yaginuma.wikinclient.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yaginuma on 14/07/08.
 */
public class WikinClient {
    private Context mContext;
    private String mEncodedAuth;

    private int mPageCount;
    private String[] mMenu;
    private ArrayList<Page> mPages;

    private static final String TAG = WikinClient.class.getSimpleName();
    public String baseUrl;
    public String userName;
    public String password;

    public WikinClient(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        this.baseUrl = sharedPreferences.getString(mContext.getString(R.string.pref_wikin_url), "");
        userName = sharedPreferences.getString(mContext.getString(R.string.pref_wikin_auth_user_name), "");
        password = sharedPreferences.getString(mContext.getString(R.string.pref_wikin_auth_password), "");
        this.mEncodedAuth = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.DEFAULT);
        this.mPages = new ArrayList<Page>();
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


