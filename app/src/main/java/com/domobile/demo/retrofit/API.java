package com.domobile.demo.retrofit;

import com.domobile.demo.bean.ExchangeRateBean;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface API {
    @Headers("User-Agent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like" +
            "Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko)" +
            "Version/10.3 Mobile/14E277 Safari/603.1.30'")
    @GET
    Call<ResponseBody> getHtml(@Url String url);

    @GET
    Flowable<ExchangeRateBean> getExchangeRate(@Url String url);
}
