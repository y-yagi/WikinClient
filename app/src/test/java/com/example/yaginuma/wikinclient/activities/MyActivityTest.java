package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.yaginuma.wikinclient.BuildConfig;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.TestHelper;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


/**
 * Created by yaginuma on 15/01/12.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MyActivityTest {
    private MyActivity myActivity;
    private ShadowWebView shadowWebView;

    @Before
    public void setUp() {
        setUpPreferences();
        myActivity = Robolectric.buildActivity(MyActivity.class).create().resume().get();
    }

    public void setUpPreferences() {
        Context settingActivity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(settingActivity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(settingActivity.getString(R.string.pref_wikin_url), "http://example.com").commit();
    }

    public Response<ResponseBody> dummyResponse() {
        Response<ResponseBody> response = null;
        try {
            MediaType type = MediaType.parse("Content-Type: application/json; charset=utf-8");
            ResponseBody responseBody = ResponseBody.create(type, TestHelper.getDummyListResponse());
            response = Response.success(responseBody);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Test
    public void testDisplayTitle() throws Exception {
        myActivity.displayPageList(dummyResponse());
        assertThat("test", equalTo(myActivity.getTitle()));
    }

    @Test
    public void testDisplayWebView() throws Exception {
        myActivity.displayPageList(dummyResponse());

        WebView webView = (WebView)myActivity.findViewById(R.id.bodyHtml);
        shadowWebView = Shadows.shadowOf(webView);

        ShadowWebView.LoadDataWithBaseURL loadData = shadowWebView.getLastLoadDataWithBaseURL();
        assertThat("<p><strong>テストページ3</strong></p>", equalTo(loadData.data.trim()));
    }
}
