package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;
import android.webkit.WebView;

import com.example.yaginuma.wikinclient.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


/**
 * Created by yaginuma on 15/01/12.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {

    @Test
    public void displayRecentUpdatePage() throws Exception {
        Activity activity = Robolectric.buildActivity(MyActivity.class).create().get();
    }

}
