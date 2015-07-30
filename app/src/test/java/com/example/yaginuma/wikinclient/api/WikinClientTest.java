package com.example.yaginuma.wikinclient.api;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.yaginuma.wikinclient.BuildConfig;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.TestHelper;
import com.example.yaginuma.wikinclient.activities.MyActivity;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by yaginuma on 15/01/12.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class WikinClientTest {
    private WikinClient mWikinClient;

    @Before
    public void setUp() {
        Application application = RuntimeEnvironment.application;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(application.getString(R.string.pref_wikin_url), "http://example.com").commit();

        mWikinClient = new WikinClient(application);
    }

    public void setUpDummyData() {
        try {
            JSONObject dummyData = new JSONObject(TestHelper.getDummyListResponse());
            mWikinClient.parseListResponse(dummyData);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBaseUrl() {
        assertThat("http://example.com", equalTo(mWikinClient.getBaseUrl()));
    }

    @Test
    public void testGetListUrl() {
        assertThat("http://example.com/pages.json?recent_pages=true", equalTo(mWikinClient.getListUrl()));
    }

    @Test
    public void testGetUpdateUrl() {
        assertThat("http://example.com/pages/", equalTo(mWikinClient.getUpdateUrl()));
    }

    @Test
    public void testSearchUrl() {
        assertThat("http://example.com/pages/search.json?query=%E3%83%86%E3%82%B9%E3%83%88",
                equalTo(mWikinClient.getSearchUrl("テスト")));
    }

    @Test
    public void testGetPageCount() {
        setUpDummyData();
        assertThat(3, equalTo(mWikinClient.getPageCount()));
    }

    @Test
    public void testGetMenu() {
        setUpDummyData();
        String menu[] = mWikinClient.getMenu();
        assertThat(3, equalTo(menu.length));
        assertThat("test", equalTo(menu[0]));
        assertThat("2", equalTo(menu[1]));
        assertThat("1", equalTo(menu[2]));
    }

    @Test
    public void testGetPages() {
        setUpDummyData();
        ArrayList<Page> pagesList = mWikinClient.getPages();
        Page page = pagesList.get(0);
        assertThat(3, equalTo(pagesList.size()));

        assertThat(3, equalTo(page.getId()));
        assertThat("test", equalTo(page.getTitle()));
        assertThat("http://localhost:3000/test", equalTo(page.getUrl()));
        assertThat("**テストページ3**", equalTo(page.getBody()));
        assertThat("<p><strong>テストページ3</strong></p>\n", equalTo(page.getExtractedBody()));
    }
}
