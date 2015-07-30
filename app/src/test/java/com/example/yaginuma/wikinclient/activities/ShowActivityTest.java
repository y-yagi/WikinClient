package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.example.yaginuma.wikinclient.BuildConfig;
import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.TestHelper;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowWebView;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


/**
 * Created by yaginuma on 15/01/12.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShowActivityTest {
    private ShowActivty showActivity;
    private ShadowWebView shadowWebView;

    @Before
    public void setUp() {
        Page page = new Page(1, "test title", "test body", "<p>test body</p>", "");
        Intent intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ShowActivty.class);
        intent.putExtra("page", page);

        showActivity = Robolectric.buildActivity(ShowActivty.class).withIntent(intent).create().get();
    }

    @Test
    public void testDisplayTitle() throws Exception {
        assertThat("test title", equalTo(showActivity.getTitle()));
    }

    @Test
    public void testDisplayWebView() throws Exception {
        WebView webView = (WebView)showActivity.findViewById(R.id.bodyHtml);
        shadowWebView = Shadows.shadowOf(webView);

        ShadowWebView.LoadDataWithBaseURL loadData = shadowWebView.getLastLoadDataWithBaseURL();
        assertThat("<p>test body</p>", equalTo(loadData.data));

    }
}
