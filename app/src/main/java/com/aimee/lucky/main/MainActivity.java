package com.aimee.lucky.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aimee.lucky.R;
import com.aimee.lucky.module.sensor.SensorActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.btn_shake)
    Button btnShake;
    @Bind(R.id.tv_title_back)
    ImageView tvTitleBack;
    @Bind(R.id.tv_title_name)
    TextView tvTitleName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shake:
                openSensorActivity();
                break;
        }
    }

    private void initView() {
        tvTitleName.setText(R.string.str_main_title);
        tvTitleBack.setVisibility(View.GONE);
        btnShake.setOnClickListener(this);
    }

    private void openSensorActivity() {
        Intent intent = new Intent(MainActivity.this, SensorActivity.class);
        startActivity(intent);
    }
}
