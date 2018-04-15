package com.domobile.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.domobile.demo.R;
import com.domobile.demo.db.ExchangeRate;
import com.domobile.demo.dialog.SoftKeyBoardDialog;
import com.domobile.demo.utils.CommonUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2018/4/15.
 */

public class ExchangeRateAdapter extends RecyclerView.Adapter<ExchangeRateAdapter.ViewHolder> {
    private List<ExchangeRate> mData;
    private DecimalFormat mFormat;
    private Context mContext;
    private SoftKeyBoardDialog mDialog;

    private ExchangeRate mBaseExchangeRate;

    public ExchangeRateAdapter(Context context, List<ExchangeRate> data) {
        mContext = context;
        this.mData = data;

        mFormat = new DecimalFormat(",###.####");
        mFormat.setRoundingMode(RoundingMode.UP);

        initKeyBoardDialog();
        updateBaseExchangeRate();
    }

    private void initKeyBoardDialog(){
        mDialog = new SoftKeyBoardDialog(mContext);
        Window window = mDialog.getWindow();
        int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = widthPixels;
        attributes.height = CommonUtils.dipToPx(mContext, 100);
        window.setAttributes(attributes);
        window.setGravity(Gravity.BOTTOM);
    }

    public void setExchangeRates(List<ExchangeRate> data) {
        mData = data;
        updateBaseExchangeRate();
        notifyDataSetChanged();
    }

    private void updateBaseExchangeRate() {
        if(mData == null){
            return;
        }
        for (ExchangeRate item : mData) {
            if (item.country.equals("USD")) {
                mBaseExchangeRate = item;
                mBaseExchangeRate.currentValue = 1;
                return;
            }
        }
    }

    public void updateBaseExchangeRate(ExchangeRate exchangeRate) {
        mBaseExchangeRate = exchangeRate;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.exchange_rate_recycle_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public ExchangeRate getExchangeRateAtPosition(int position) {
        return mData.get(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ExchangeRate exchangeRate = mData.get(position);
        holder.country.setText(exchangeRate.country);

        if(exchangeRate.country.equals(mBaseExchangeRate.country)){
            holder.exchangeValue.setText(mFormat.format( mBaseExchangeRate.currentValue));
        }else{
            exchangeRate.currentValue = mBaseExchangeRate.currentValue / mBaseExchangeRate.rate * exchangeRate.rate;
            holder.exchangeValue.setText(mFormat.format(exchangeRate.currentValue));
        }

        if (exchangeRate.pressed) {
            holder.exchangeValue.setTextColor(mContext.getResources().getColor(R.color.exchange_rate_pressed));
        } else {
            holder.exchangeValue.setTextColor(mContext.getResources().getColor(R.color.exchange_rate_normal));
        }

        holder.exchangeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setAssociateData(position, ExchangeRateAdapter.this);
                if (!mDialog.isShowing()) {
                    mDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView country;
        public TextView exchangeValue;

        public ViewHolder(View itemView) {
            super(itemView);
            country = itemView.findViewById(R.id.tv_country);
            exchangeValue = itemView.findViewById(R.id.tv_exchange_value);
        }
    }
}
