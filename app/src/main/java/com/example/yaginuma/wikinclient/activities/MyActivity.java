package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.yaginuma.wikinclient.api.WikinClient;
import com.example.yaginuma.wikinclient.fragments.NavigationDrawerFragment;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;
import com.example.yaginuma.wikinclient.services.ProgressDialogBuilder;
import com.example.yaginuma.wikinclient.services.ServiceGenerator;
import com.example.yaginuma.wikinclient.services.WikinService;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

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
    private boolean mLoadCompleted = false;
    private ProgressDialog mProgressDialog;
    private List<Page> mPages;
    private Activity mActivity;

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

        if (this.mWikinClient.baseUrl.length() == 0) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return ;
        }

        mActivity = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

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
        String body = "" ;
        Page page;
        if (mEventCount > 0 ) {
            page = mWikinClient.getPages().get(number - 1);
            body = page.getExtractedBody();
            mCurrentPos = number - 1;
            mTitle = page.getTitle();
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
                if (!mLoadCompleted) return true;
                fetchPageListFromWikin();
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

        if (mWikinClient.baseUrl.length() == 0)  {
            String errMsg = getString(R.string.setting_not_completed);
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialogBuilder.build(this, body);
        mProgressDialog.show();

        WikinService wikinService= ServiceGenerator.createService(WikinService.class, mWikinClient.baseUrl, mWikinClient.userName, mWikinClient.password);
        Call<ResponseBody> call = wikinService.getPages();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    displayPageList(response);
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "responce is not success");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(mActivity, mActivity.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "callback onFailure");
                Log.e(TAG, t.getMessage());
            }
        });
    }

    protected  void displayPageList(Response<ResponseBody> response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            mWikinClient.parseListResponse(jsonObject);
        } catch (Exception e) {
            Toast.makeText(mActivity, mActivity.getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Data parse error");
            e.printStackTrace();
            return;
        }
        mNavigationDrawerFragment.setMenuList(mWikinClient.getMenu());
        Page page = mWikinClient.getPages().get(0);
        mTitle = page.getTitle();
        setTitle(page.getTitle());
        String body = page.getExtractedBody();
        mBodyHtml.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
        mEventCount = mWikinClient.getPageCount();
        mLoadCompleted = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mCurrentPos = 0;
        fetchPageListFromWikin();
    }

    @Override
    public void onClick(View v) {
        Intent editIntent = new Intent(this, EditActivity.class);
        Page page = mWikinClient.getPages().get(this.mCurrentPos);
        editIntent.putExtra("page", page);
        startActivity(editIntent);
    }
}

