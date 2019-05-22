package com.example.checking_room;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名：SmoothScrollLayout
 * 作者：Yun.Lei
 * 功能：
 * 创建日期：2018-02-24 10:24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class SmoothScrollLayout<T> extends FrameLayout {

    private ScrollHandler mHandler;
    private MyAdapter<T> mAdapter;
    private RecyclerView recyclerView;

    public SmoothScrollLayout(@NonNull Context context) {
        this(context, null);
    }

    public SmoothScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_smooth_scroll, this);
        mHandler = new ScrollHandler(this);
        mAdapter = new MyAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.waiting_rv);
        recyclerView.setAdapter(mAdapter);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.SmoothScrollLayout);
        int spanCount = array.getInt(R.styleable.SmoothScrollLayout_customSpanCount, 1);
        if(spanCount==1){
            recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(context,spanCount));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;       //拦截事件
    }

    public void setData(List<T> data) {
        mAdapter.setList(data);
        if (data != null && data.size() > 0) {
            mHandler.sendEmptyMessageDelayed(0, 100);
        }
    }

    public void smoothScroll() {
        recyclerView.smoothScrollBy(0, 5);
        mHandler.sendEmptyMessageDelayed(0, 100);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 弱引用防止内存泄露
     */
    private static class ScrollHandler extends Handler {
        private WeakReference<SmoothScrollLayout> view;

        public ScrollHandler(SmoothScrollLayout mView) {
            view = new WeakReference<>(mView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (view.get() != null) {
                view.get().smoothScroll();
            }
        }
    }


}
