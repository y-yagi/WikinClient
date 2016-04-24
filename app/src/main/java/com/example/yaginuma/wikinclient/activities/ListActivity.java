package com.example.yaginuma.wikinclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.adapters.PageListAdapter;
import com.example.yaginuma.wikinclient.api.WikinClient;
import com.example.yaginuma.wikinclient.model.Page;
import com.example.yaginuma.wikinclient.services.ServiceGenerator;
import com.example.yaginuma.wikinclient.services.WikinService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends Activity {
    private ListView mListView;
    private GridView mGridView;
    private TextView mHeaderView;
    private Activity mActivity;

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
        mActivity = this;
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
                searchQuery = query;
                showProgress(true);
                searchFromWikin();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
        WikinService wikinService= ServiceGenerator.createService(WikinService.class, mWikinClient.baseUrl, mWikinClient.userName, mWikinClient.password);
        Call<ResponseBody> call = wikinService.searchPages(searchQuery);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    displaySearchResult(response);
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "responce is not success");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showProgress(false);
                Toast.makeText(mActivity, mActivity.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "callback onFailure");
            }
        });
    }

    public void displaySearchResult(Response<ResponseBody> response) {
        PageListAdapter pageListAdapter = new PageListAdapter(this);
        pageListAdapter.clear();

        final Context context = this;
        showProgress(false);
        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            mWikinClient.parseListResponse(responseBody);
        } catch (Exception e) {
            Toast.makeText(this, this.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Data parse error");
            e.printStackTrace();
        }

        mHeaderView.setText("「" + searchQuery + "」の検索結果");

        if (mWikinClient.getPageCount() > 0) {
            for (Page page : mWikinClient.getPages()) {
                pageListAdapter.add(page);
            }
        }

        if (mListView != null) {
            mListView.setAdapter(pageListAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Page page = (Page) mListView.getItemAtPosition(position);
                    Intent showIntent = new Intent(context, ShowActivty.class);
                    showIntent.putExtra("page", page);
                    startActivity(showIntent);
                }
            });
        } else if (mGridView != null) {
            mGridView.setAdapter(pageListAdapter);
        }
    }
}
