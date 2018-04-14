package com.domobile.demo.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.domobile.demo.bean.ExchangeRateBean;
import com.domobile.demo.db.AppDatabase;
import com.domobile.demo.db.ExchangeRate;
import com.domobile.demo.db.ExchangeRateDao;
import com.domobile.demo.retrofit.API;
import com.domobile.demo.retrofit.MyRetrofit;
import com.domobile.demo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/14.
 */

public class ExchangeRateResultViewModel extends AndroidViewModel {
    private LiveData<List<ExchangeRate>> mExchangeRates;

    private AppDatabase mDb;
    private static final String DEFAULT_EXCHANGE_RATE_URL = "https://api.fixer.io/latest";

    public ExchangeRateResultViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getInstance(application);
        mExchangeRates = mDb.exchangeRateModel().getAllExchangeRatesAsync();
    }

    public LiveData<List<ExchangeRate>> getExchangeRates() {
        return mExchangeRates;
    }

    public void inster(List<ExchangeRate> rates) {
        Log.e("DoMobile", "insert rates");
        ExchangeRateDao exchangeRateDao = mDb.exchangeRateModel();
        exchangeRateDao.insertExchangeRate(rates);
    }

    public void updateRate(List<ExchangeRate> oldRates, List<ExchangeRate> newRates) {
        for (int i = 0; i < newRates.size(); i++) {
            ExchangeRate newRate = newRates.get(i);
            newRate.position = Integer.MAX_VALUE;
            for (int j = 0; j < oldRates.size(); j++) {
                ExchangeRate oldRate = oldRates.get(j);
                if (newRate.country.equals(oldRate.country)) {
                    newRate.position = oldRate.position;
                }
            }
        }

        sortRatesByPosition(newRates);

        for (int i = 0; i < newRates.size(); i++) {
            ExchangeRate newRate = newRates.get(i);
            newRate.position = i;
        }

        ExchangeRateDao exchangeRateDao = mDb.exchangeRateModel();
        exchangeRateDao.deleteAll();
        inster(newRates);
    }

    public void sortRatesByPosition(List<ExchangeRate> rates) {
        Collections.sort(rates, new Comparator<ExchangeRate>() {
            @Override
            public int compare(ExchangeRate o1, ExchangeRate o2) {
                return o1.position - o2.position;
            }
        });
    }

    public void updateExchangeRate() {
        Log.e("DoMobile", "updateExchangeRate");
        API api = MyRetrofit.getInstance().getAPI();
        Flowable<ExchangeRateBean> flowable = api.getExchangeRate(DEFAULT_EXCHANGE_RATE_URL);
        flowable.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<ExchangeRateBean>() {
                    @Override
                    public void accept(ExchangeRateBean exchangeRateBean) throws Exception {
                        if (exchangeRateBean == null) {
                            Toast.makeText(getApplication(), "汇率更新失败", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        CommonUtils.setExchangeRateUpdateDate(ExchangeRateResultViewModel.this.getApplication(), exchangeRateBean.getDate());

                        HashMap<String, String> rateBeans = exchangeRateBean.getRates();
                        Set<Map.Entry<String, String>> entries = rateBeans.entrySet();
                        rateBeans.put(exchangeRateBean.getBase(), "1");

                        List<ExchangeRate> rates = new ArrayList<>();
                        int count = 0;
                        for (Map.Entry<String, String> entry : entries) {
                            ExchangeRate exchangeRate = new ExchangeRate();
                            exchangeRate.country = entry.getKey();
                            exchangeRate.rate = Double.parseDouble(entry.getValue());
                            exchangeRate.position = count++;
                            rates.add(exchangeRate);
                        }
                        List<ExchangeRate> oldRates = mExchangeRates.getValue();
                        if (oldRates == null || oldRates.size() == 0) {
                            inster(rates);
                        } else {
                            Log.e("DoMobile", "更新数据库");
                            updateRate(oldRates, rates);
                        }
                    }
                });
    }
}
