package com.example.yaginuma.wikinclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.api.WikinClient;
import com.example.yaginuma.wikinclient.model.Page;
import com.example.yaginuma.wikinclient.services.ServiceGenerator;
import com.example.yaginuma.wikinclient.services.WikinService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class EditActivity extends Activity {
    // UI references.
    private TextView mTitleView;
    private EditText mBodyView;
    private View mProgressView;
    private View mEditFormView;
    private Page mPage;
    private WikinClient mWikinClient;
    private Activity mActivity;

    private static final String TAG = MyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle extras = getIntent().getExtras();
        mPage = (Page) extras.getSerializable("page");
        setTitle(mPage.getTitle());

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
        mActivity = this;
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
        WikinService wikinService= ServiceGenerator.createService(WikinService.class, mWikinClient.getBaseUrl(), mWikinClient.userName, mWikinClient.password);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "page[body]=" + body);
        Call<ResponseBody> call = wikinService.updatePage(mPage.getId(), body);
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

    private void displaySearchResult(Response<ResponseBody> response) {
        showProgress(false);
        boolean result = true;
        try {
            JSONObject responseBody = new JSONObject(response.body().string());
            result = mWikinClient.verificationResponse(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Data parse error");
        }
        if (result) {
            Toast.makeText(this, getString(R.string.success_update), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_SHORT).show();
        }
    }
}



