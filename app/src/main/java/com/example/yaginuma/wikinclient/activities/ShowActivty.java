package com.example.yaginuma.wikinclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;
import com.melnykov.fab.FloatingActionButton;

public class ShowActivty extends Activity implements View.OnClickListener {

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit:
                Intent editIntent = new Intent(this, EditActivity.class);
                editIntent.putExtra("page", mPage);
                startActivity(editIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra("page", mPage);
        startActivity(editIntent);
    }
}
