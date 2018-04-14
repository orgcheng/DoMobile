package com.domobile.demo.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.domobile.demo.R;
import com.domobile.demo.db.ExchangeRate;
import com.domobile.demo.utils.CommonUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ExchangeRateHelperActivity extends AppCompatActivity {

    private static final String DEFAULT_EXCHANGE_RATE_URL = "https://api.fixer.io/latest";

    private List<ExchangeRate> mData;
    private ExchangeRateResultViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate_helper);
        initView();
        initData();
    }

    private void initData() {
        mViewModel = ViewModelProviders.of(this).get(ExchangeRateResultViewModel.class);
        mViewModel.getExchangeRates().observe(this, new Observer<List<ExchangeRate>>() {
            @Override
            public void onChanged(@Nullable List<ExchangeRate> exchangeRates) {
                if (exchangeRates != null && exchangeRates.size() > 0) {
                    Log.e("DoMobile", "data from database");
                    mViewModel.sortRatesByPosition(exchangeRates);
                    mData = exchangeRates;
                    // TOOD 更新Adapter
                }

                if (CommonUtils.needUpdateExchangeRate(getApplicationContext())) {
                    mViewModel.updateExchangeRate();
                }
            }
        });
    }

    private void initView() {
        setTitle("");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DecimalFormat df = new DecimalFormat(",###.##");
        df.setRoundingMode(RoundingMode.DOWN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, Menu.FIRST, 0, R.string.exchange_rate_update);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            mViewModel.updateExchangeRate();
        }
        return super.onOptionsItemSelected(item);
    }
}
