package com.domobile.demo.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by orgcheng on 18-1-27.
 */

public class MyRetrofit {
    private static final String TAG = "MyRetrofit";

    private String mHostUrl = "http://www.wanandroid.com/";
    private Retrofit mRetrofit;
    private API mApi;
    static class Holder {
        public static MyRetrofit sInstance = new MyRetrofit();
    }

    public static MyRetrofit getInstance() {
        return Holder.sInstance;
    }

    private MyRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mHostUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();

        mApi = mRetrofit.create(API.class);
    }

    public API getAPI(){
        return mApi;
    }

    public void getHtml(String url) {
        Call<ResponseBody> call = mApi.getHtml(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
