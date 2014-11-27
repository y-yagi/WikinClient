package com.example.yaginuma.wikinclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.yaginuma.wikinclient.api.WikinClient;
import com.example.yaginuma.wikinclient.model.Page;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class EditActivity extends Activity
        implements Response.Listener<JSONObject>, Response.ErrorListener {

    // UI references.
    private TextView mTitleView;
    private EditText mBodyView;
    private View mProgressView;
    private View mEditFormView;
    private Page mPage;
    private WikinClient mWikinClient;

    private static final String TAG = MyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle extras = getIntent().getExtras();
        mPage = (Page) extras.getSerializable("page");

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(mPage.getTitle());

        mBodyView = (EditText) findViewById(R.id.body);
        mBodyView.setText(mPage.getBody());
        mBodyView.setSelection(mBodyView.getText().length());
        Button mEmailSignInButton = (Button) findViewById(R.id.update_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUpdate();
            }
        });

        mEditFormView = findViewById(R.id.edit_form);
        mProgressView = findViewById(R.id.edit_progress);
        mWikinClient = new WikinClient(this);
    }

    public void attemptUpdate() {
        // Reset errors.
        mBodyView.setError(null);

        // Store values at the time of the login attempt.
        String body = mBodyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid body
        if (TextUtils.isEmpty(body)) {
            mTitleView.setError(getString(R.string.error_field_required));
            focusView = mTitleView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            updatePage(body);
        }
    }


    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void updatePage(String body) {
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("body", body);
        String url = mWikinClient.getUpdateUrl() + mPage.getId() + ".json";
        final Context mContext = this;

        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.PUT, url, new JSONObject(jsonParams), this, this
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = mWikinClient.addAuthHeaders(super.getHeaders());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        mQueue.add(myRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        showProgress(false);
        boolean result = true;
        try {
            result = mWikinClient.verificationResponse(response);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Data parse error");
        }
        if (result) {
            Toast.makeText(this, getString(R.string.success_update), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, getString(R.string.error_unknown_exception), Toast.LENGTH_SHORT).show();
        showProgress(false);
    }
}



