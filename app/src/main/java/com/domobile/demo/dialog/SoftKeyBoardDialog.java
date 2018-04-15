package com.domobile.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;

import com.domobile.demo.R;
import com.domobile.demo.adapter.ExchangeRateAdapter;
import com.domobile.demo.db.ExchangeRate;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018/4/15.
 */

public class SoftKeyBoardDialog extends Dialog implements View.OnClickListener {

    private int mPosition;
    private ExchangeRate mExchangeRate;
    private ExchangeRateAdapter mAdapter;
    private ExchangeRateAdapter.ViewHolder mHolder;
    private boolean reset;
    private int mWindowScrollHeight;

    private DecimalFormat mFormat;
    private SparseIntArray mHashMap = new SparseIntArray(16);
    private final Object OBJ_PRESSED = new Object();

    public SoftKeyBoardDialog(@NonNull Context context) {
        this(context, 0);
    }

    public SoftKeyBoardDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.myDialog);
        setCancelable(true);
        setCustomView();
    }

    private void setCustomView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.exchange_rate_dialog, null);
        rootView.findViewById(R.id.bt_0).setOnClickListener(this);
        rootView.findViewById(R.id.bt_1).setOnClickListener(this);
        rootView.findViewById(R.id.bt_2).setOnClickListener(this);
        rootView.findViewById(R.id.bt_3).setOnClickListener(this);
        rootView.findViewById(R.id.bt_4).setOnClickListener(this);
        rootView.findViewById(R.id.bt_5).setOnClickListener(this);
        rootView.findViewById(R.id.bt_6).setOnClickListener(this);
        rootView.findViewById(R.id.bt_7).setOnClickListener(this);
        rootView.findViewById(R.id.bt_8).setOnClickListener(this);
        rootView.findViewById(R.id.bt_9).setOnClickListener(this);
        rootView.findViewById(R.id.bt_dot).setOnClickListener(this);
        rootView.findViewById(R.id.bt_hide).setOnClickListener(this);

        mHashMap.put(R.id.bt_0, 0);
        mHashMap.put(R.id.bt_1, 1);
        mHashMap.put(R.id.bt_2, 2);
        mHashMap.put(R.id.bt_3, 3);
        mHashMap.put(R.id.bt_4, 4);
        mHashMap.put(R.id.bt_5, 5);
        mHashMap.put(R.id.bt_6, 6);
        mHashMap.put(R.id.bt_7, 7);
        mHashMap.put(R.id.bt_8, 8);
        mHashMap.put(R.id.bt_9, 9);
        mHashMap.put(R.id.bt_dot, -1);
        mHashMap.put(R.id.bt_hide, -1);

        mFormat = new DecimalFormat(",###.##");
        mFormat.setRoundingMode(RoundingMode.DOWN);

        setContentView(rootView);
    }

    private String currentValue;

    public void setAssociateData(int position, ExchangeRateAdapter adapter) {
        mPosition = position;
        mAdapter = adapter;
        mExchangeRate = mAdapter.getExchangeRateAtPosition(position);
        mExchangeRate.pressed = true;
        reset = true;

        currentValue = String.valueOf(mExchangeRate.currentValue);

        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 隐藏
        if (id == R.id.bt_hide) {
            dismiss();
            return;
        }

        // 点
        if (id == R.id.bt_dot) {
            if (reset || currentValue.contains(".")) {

            } else {
                currentValue += ".";
            }

        } else if (currentValue.equals("0") || reset) {
            currentValue = String.valueOf(mHashMap.get(id));
            reset = false;

        } else {
            currentValue += String.valueOf(mHashMap.get(id));
        }

        if (currentValue.length() > 10) {
            currentValue = currentValue.substring(0, 10);
        }
        double result = Double.valueOf(currentValue);
        mExchangeRate.currentValue = result;
        mAdapter.updateBaseExchangeRate(mExchangeRate);
    }

    @Override
    public void dismiss() {
        reset = false;
        mExchangeRate.pressed = false;
        mAdapter.notifyItemChanged(mPosition);
        super.dismiss();
    }
}
