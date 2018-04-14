package com.domobile.demo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.domobile.demo.R;
import com.domobile.demo.retrofit.API;
import com.domobile.demo.retrofit.MyRetrofit;
import com.domobile.demo.utils.CommonUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LauncherIconParseActivity extends AppCompatActivity {

    private EditText mInputUrl;
    private ImageView mImageView;

    private static final String DEFAULT_INPUT_URL = "https://tieba.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_icon_parse);
        init();
    }

    private void init() {
        mInputUrl = findViewById(R.id.et_input_url);
        mImageView = findViewById(R.id.iv_parsed_icon);

        mInputUrl.setText(DEFAULT_INPUT_URL);
    }

    // 点击按钮响应方法
    public void parse(View view) {
        // Step 1. 验证地址
        final String url = mInputUrl.getText().toString().trim();
        if (!URLUtil.isNetworkUrl(url)) {
            Toast.makeText(this, "请输入有效网址", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 2. 获取html
        API api = MyRetrofit.getInstance().getAPI();
        Call<ResponseBody> call = api.getHtml(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String html = response.body().string();
                    // Step 3. 解析图片地址，并计算消耗时间
                    // 解析html耗时：218.3ms
                    String paredUrl = CommonUtils.pareHtml(url, html);
                    if (TextUtils.isEmpty(paredUrl)) {
                        Toast.makeText(LauncherIconParseActivity.this, "网站没有有效图标", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.e("DoMobile", "onResponse: paredUrl = " + paredUrl);

                    // Step 4. 加载解析出的图片地址
                    CommonUtils.loadIcon(LauncherIconParseActivity.this, mImageView, paredUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LauncherIconParseActivity.this, "网址解析失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
