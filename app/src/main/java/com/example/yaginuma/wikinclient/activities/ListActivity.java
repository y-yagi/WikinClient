package com.example.yaginuma.wikinclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.adapters.PageListAdapter;
import com.example.yaginuma.wikinclient.api.WikinClient;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ListActivity extends Activity
    implements  Response.Listener<JSONObject>, Response.ErrorListener {

    private ListView mListView;
    private GridView mGridView;
    private TextView mHeaderView;

    private View mProgressView;
    private WikinClient mWikinClient;
    private int mEventCount = 0;
    private String searchQuery;

    private static final String TAG = ListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.list_view);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mHeaderView = (TextView) findViewById(R.id.list_header);
        mProgressView = findViewById(R.id.list_progress);
        this.mWikinClient = new WikinClient(this);

        Bundle extras = getIntent().getExtras();
        searchQuery = extras.getString("query");

        showProgress(true);
        searchFromWikin();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mListView.setVisibility(show ? View.GONE : View.VISIBLE);
            mListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void searchFromWikin() {
        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(this);
        mQueue.add(new JsonObjectRequest(Request.Method.GET, mWikinClient.getSearchUrl(searchQuery),
                null, this, this
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mWikinClient.addAuthHeaders(super.getHeaders());
            };
        });
    }


    @Override
    public void onResponse(JSONObject response) {
        PageListAdapter pageListAdapter = new PageListAdapter(this);
        showProgress(false);
        try {
            mWikinClient.parseListResponse(response);
            mHeaderView.setText("「" + searchQuery + "」の検索結果");

            if (mWikinClient.getPageCount() > 0 ) {
                for (Page page : mWikinClient.getPages()) {
                    pageListAdapter.add(page);
                }

                if (mListView!= null) {
                    mListView.setAdapter(pageListAdapter);
                } else if (mGridView != null) {
                    mGridView.setAdapter(pageListAdapter);
                 }
            }
        } catch (JSONException e) {
            Toast.makeText(this, this.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Data parse error");
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showProgress(false);
        Toast.makeText(this, this.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Data load error");
        error.printStackTrace();
    }
}
