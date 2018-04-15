package com.domobile.demo.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.domobile.demo.R;
import com.domobile.demo.adapter.ExchangeRateAdapter;
import com.domobile.demo.db.ExchangeRate;

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExchangeRateHelperActivity extends AppCompatActivity {

    private static final String DEFAULT_EXCHANGE_RATE_URL = "https://api.fixer.io/latest";

    private List<ExchangeRate> mData;
    private ExchangeRateResultViewModel mViewModel;

    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecycleView;
    private ExchangeRateAdapter mAdapter;

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
                Log.e("DoMobile", "observe database: " + (exchangeRates == null ? null : exchangeRates.size()));
                if (exchangeRates != null && exchangeRates.size() > 0) {
                    mViewModel.sortRatesByPosition(exchangeRates);
                    mData = exchangeRates;
                    mAdapter.setExchangeRates(mData);
                } else {
                    mViewModel.updateExchangeRate();
                }
            }
        });
    }

    private void initView() {
        setTitle("");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRecycleView = findViewById(R.id.rv_exchange_rate_list);
        mAdapter = new ExchangeRateAdapter(this, mData);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);

        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecycleView);
    }

    private ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
            int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mData, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mData, i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            //返回true表示执行拖动
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundResource(R.color.exchange_rate_bg_pressed);
            } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                Flowable.just(mData).subscribeOn(Schedulers.newThread())
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<List<ExchangeRate>>() {
                            @Override
                            public void accept(List<ExchangeRate> exchangeRates) throws Exception {
                                mViewModel.updateRate(exchangeRates);
                            }
                        });
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundResource(R.color.exchange_rate_bg_normal);
        }
    };

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
