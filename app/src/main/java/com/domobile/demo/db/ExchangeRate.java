package com.domobile.demo.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2018/4/14.
 */

@Entity
public class ExchangeRate {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long id;

    public String country;

    public double rate;

    public int position;

    @Ignore
    public double currentValue;

    @Ignore
    public boolean pressed;
}
