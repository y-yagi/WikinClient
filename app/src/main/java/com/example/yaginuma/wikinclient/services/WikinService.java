package com.example.yaginuma.wikinclient.services;

import com.example.yaginuma.wikinclient.model.Page;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by yaginuma on 16/04/10.
 */

public interface WikinService {
    @GET("pages.json?recent_pages=true")
    Call<ResponseBody> getPages();
}
