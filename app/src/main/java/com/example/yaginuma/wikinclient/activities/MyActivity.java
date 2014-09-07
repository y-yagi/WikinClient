package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yaginuma.wikinclient.api.WikinClient;
import com.example.yaginuma.wikinclient.fragments.NavigationDrawerFragment;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;
import com.example.yaginuma.wikinclient.providers.WikinClientSuggestionProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class MyActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Response.Listener<JSONObject>, Response.ErrorListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private WebView mBodyHtml;
    private int mEventCount = 0;
    private int mCurrentPos = 0;
    private WikinClient mWikinClient;

    private static final String TAG = MyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mBodyHtml = (WebView) findViewById(R.id.bodyHtml);

        this.mWikinClient = new WikinClient(this);

        if (this.mWikinClient.getBaseUrl().length() == 0) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return ;
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        String body = "";
        if (mEventCount > 0 ) {
            body = mWikinClient.getPages().get(number - 2).getExtractedBody();
            mCurrentPos = number - 2;
        }
        mBodyHtml.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.my, menu);
            restoreActionBar();

            // 検索処理設定
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
            searchView.setSearchableInfo(searchableInfo);
            final Context mContext = this;

            final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent listIntent = new Intent(mContext, ListActivity.class);
                    listIntent.putExtra("query", query);
                    startActivity(listIntent);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingIntent);
                return true;
            case R.id.action_reload:
                fetchPageListFromWikin();
                return true;
            case R.id.action_edit:
                Intent editIntent = new Intent(this, EditActivity.class);
                Page page = mWikinClient.getPages().get(this.mCurrentPos);
                editIntent.putExtra("page", page);
                startActivity(editIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MyActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private void fetchPageListFromWikin() {
        String body = "Now Loading...";
        mBodyHtml.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);

        if (mWikinClient.getBaseUrl().length() == 0)  {
            String errMsg = getString(R.string.setting_not_completed);
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET, mWikinClient.getListUrl(),
                null, this, this
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mWikinClient.addAuthHeaders(super.getHeaders());
            };
        };
        RetryPolicy policy = new DefaultRetryPolicy(WikinClient.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        mQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mCurrentPos = 0;
        fetchPageListFromWikin();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            mWikinClient.parseListResponse(response);
            mNavigationDrawerFragment.setMenuList(mWikinClient.getMenu());
            String body = "";
            if (mWikinClient.getPageCount() > 0 ) {
                Page page = mWikinClient.getPages().get(0);
                body = page.getExtractedBody();
                mBodyHtml.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
                mEventCount = mWikinClient.getPageCount();
            }
        } catch (JSONException e) {
            Toast.makeText(this, this.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Data parse error");
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, this.getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Data load error");
        error.printStackTrace();
    }
}

