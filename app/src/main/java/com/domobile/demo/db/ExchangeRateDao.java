package com.domobile.demo.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Administrator on 2018/4/14.
 */

@Dao
public interface ExchangeRateDao {
    @Query("SELECT * FROM ExchangeRate")
    LiveData<List<ExchangeRate>> getAllExchangeRatesAsync();

    @Query("SELECT * FROM ExchangeRate")
    List<ExchangeRate> getAllExchangeRatesSync();

    @Insert
    void insertExchangeRate(List<ExchangeRate> rates);
//    void insertExchangeRate(ExchangeRate rate);

    @Update(onConflict = REPLACE)
    void updateExchangeRate(List<ExchangeRate> rate);

    @Query("DELETE FROM ExchangeRate")
    void deleteAll();
}
