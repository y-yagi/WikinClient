package com.example.yaginuma.wikinclient.api;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by yaginuma on 15/01/12.
 */

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class WikinClientTest {
    private WikinClient mWikinClient;

    @Before
    public void setUp() {
        Application application = Robolectric.application;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(application.getString(R.string.pref_wikin_url), "http://example.com").commit();

        mWikinClient = new WikinClient(application);
    }

    public void setUpDummyData() {
        try {
            String s =
                "{\"pages\":[{\"id\":3,\"title\":\"test\",\"url\":\"http://localhost:3000/test\",\"body\":\"**テストページ3**\",\"extracted_body\":\"\\u003cp\\u003e\\u003cstrong\\u003eテストページ3\\u003c/strong\\u003e\\u003c/p\\u003e\\n\"},{\"id\":2,\"title\":\"2\",\"url\":\"http://localhost:3000/2\",\"body\":\"2\",\"extracted_body\":\"\\u003cp\\u003e2\\u003c/p\\u003e\\n\"},{\"id\":1,\"title\":\"1\",\"url\":\"http://localhost:3000/1\",\"body\":\"1\",\"extracted_body\":\"\\u003cp\\u003e1\\u003c/p\\u003e\\n\"}],\"results_returned\":3}";
            JSONObject dummyData = new JSONObject(s);
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
