package com.cnlive.barragedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.cnlive.barragedemo.danmu.DanmuControl;
import com.cnlive.barragedemo.model.Danmu;
import com.cnlive.danmudemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import master.flame.danmaku.controller.IDanmakuView;

/**
 * @Author Mr.hou
 * @time 2017/3/16
 * @Desc
 */

public class DanmuActivity extends AppCompatActivity implements View.OnClickListener{
    private IDanmakuView mDanmakuView;
    private DanmuControl mDanmuControl;
    private Switch hideOrShow;
    private Button btnAddDanmu;
    private EditText input;
    private Danmu danmu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmu);
        mDanmuControl = new DanmuControl(this);
        danmu=new Danmu();
        initview();
    }

    private void initview() {
        mDanmakuView = (IDanmakuView) findViewById(R.id.danmakuView);
        hideOrShow = (Switch) findViewById(R.id.hide_show);
        btnAddDanmu = (Button) findViewById(R.id.send);
        input = (EditText) findViewById(R.id.input);
        findViewById(R.id.gray).setOnClickListener(this);
        findViewById(R.id.green).setOnClickListener(this);
        findViewById(R.id.yellow).setOnClickListener(this);
        findViewById(R.id.orange).setOnClickListener(this);
        findViewById(R.id.small_font).setOnClickListener(this);
        findViewById(R.id.mid_font).setOnClickListener(this);
        findViewById(R.id.big_font).setOnClickListener(this);
        findViewById(R.id.t_pos).setOnClickListener(this);
        findViewById(R.id.m_pos).setOnClickListener(this);
        findViewById(R.id.b_pos).setOnClickListener(this);
        btnAddDanmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
//                input.setText("");
            }
        });
        hideOrShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDanmuControl.show();
                } else {
                    mDanmuControl.hide();
                }
            }
        });
        mDanmuControl.setDanmakuView(mDanmakuView);

    }

    private void setData() {
        List<Danmu> danmus = new ArrayList<>();
        Danmu danmu = new Danmu(0, 1, "Like", R.mipmap.ic_default_header, input.getText().toString(),color,textsize,postiton);
        danmu.setContent(input.getText().toString());
        danmus.add(danmu);
        Collections.shuffle(danmus);
        mDanmuControl.addDanmuList(danmus);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mDanmuControl.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmuControl.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmuControl.destroy();
    }

    private int color=0;
    private int textsize=0;
    private int postiton=0;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gray:
                danmu.setTextColor(1);
                color=1;
                break;
            case R.id.green:
                danmu.setTextColor(2);
                color=2;
                break;
            case R.id.yellow:
                danmu.setTextColor(3);
                color=3;
                break;
            case R.id.orange:
                danmu.setTextColor(4);
                color=4;
                break;
            case R.id.small_font:
                danmu.setTextSize(1);
                textsize=1;
                break;
            case R.id.mid_font:
                danmu.setTextSize(2);
                textsize=2;
                break;
            case R.id.big_font:
                danmu.setTextSize(3);
                textsize=3;
                break;
            case R.id.t_pos:
                danmu.setPosition(1);
                 postiton=1;
                break;
            case R.id.m_pos:
                danmu.setPosition(2);
                postiton=2;
                break;
            case R.id.b_pos:
                danmu.setPosition(3);
                postiton=3;
                break;
        }
    }
}
