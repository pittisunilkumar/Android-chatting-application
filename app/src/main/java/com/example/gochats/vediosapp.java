package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.gochats.Adapter.TikAdapter;
import com.example.gochats.modes.tikmodals;


import java.util.ArrayList;
import java.util.List;

public class vediosapp extends AppCompatActivity {

    private List<tikmodals>mediaObjectList = new ArrayList<>();
    private TikAdapter demoAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vediosapp);
        recyclerView = (RecyclerView) findViewById(R.id.recy1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ////////////////////////////////////////////////
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        /////////////////////////////////////////////////////////
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));
        mediaObjectList.add(new tikmodals("","","","","","","","",""));


        demoAdapter = new TikAdapter(mediaObjectList,getApplicationContext());
        recyclerView.setAdapter(demoAdapter);
        demoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(vediosapp.this,MainActivity.class));
        finish();
    }
}