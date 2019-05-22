package com.example.checking_room;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Waitmsg> waitmsgList;
    List<Ghmsg> passedList;
    SmoothScrollLayout<Ghmsg> passedRv;
    MyAdapter<Ghmsg> ghAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_layout);
        initSmoothScrollLayout();
        initPassedRv();
    }

    private void initPassedRv() {
        passedRv = findViewById(R.id.passed_scroll_layout);
        passedList=new ArrayList<>();
        passedList.add(new Ghmsg("诊室1","小朋友1","1"));
        passedList.add(new Ghmsg("诊室1","小朋友2","2"));
        passedList.add(new Ghmsg("诊室1","小朋友3","3"));
        passedList.add(new Ghmsg("诊室1","小朋友4","4"));
        //ghAdapter = new MyAdapter();
        passedRv.setData(passedList);
        //ghAdapter.setList(passedList);
    }

    private void initSmoothScrollLayout() {
        SmoothScrollLayout<Waitmsg> layout = findViewById(R.id.waiting_scroll_layout);
        waitmsgList = new ArrayList<>();
        //dumb test
        waitmsgList.add(new Waitmsg("检查室1","小朋友1","5"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友2","6"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友3","7"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友4","8"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友5","9"));
        layout.setData(waitmsgList);
    }
}
