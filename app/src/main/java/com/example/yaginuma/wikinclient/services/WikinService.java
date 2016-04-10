package com.example.yaginuma.wikinclient.services;

import com.example.yaginuma.wikinclient.model.Page;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by yaginuma on 16/04/10.
 */

public interface WikinService {
    @GET("pages.json?recent_pages=true")
    Call<ResponseBody> getPages();

    @GET("pages/search.json")
    Call<ResponseBody> searchPages(@Query("query") String query);

    @PATCH("pages/{id}.json")
    @FormUrlEncoded
    Call<ResponseBody> updatePage(@Path("id") int id, @Field("page[body]") String body);
}
