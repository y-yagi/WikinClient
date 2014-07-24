package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;

public class ShowActivty extends Activity {

    private Page mPage;
    private WebView mBodyHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_show);

        Bundle extras = getIntent().getExtras();
        mPage = (Page) extras.getSerializable("page");
        setTitle(mPage.getTitle());

        mBodyHtml = (WebView) findViewById(R.id.bodyHtml);
        mBodyHtml.loadDataWithBaseURL(null, mPage.getExtractedBody(), "text/html", "utf-8", null);

    }
}
