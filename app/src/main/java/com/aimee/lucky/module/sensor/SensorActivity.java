package com.aimee.lucky.module.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aimee.lucky.R;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SensorActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "SensorActivity";
    public static final int UPDATE_INTERVAL_TIME = 50;

    @Bind(R.id.tv_shake_result)
    TextView tvShakeResult;
    @Bind(R.id.ll_add_content)
    LinearLayout llAddContent;
    @Bind(R.id.tv_add_willingness)
    TextView tvAddWillingness;
    @Bind(R.id.ll_total_willingness)
    LinearLayout llTotalWillingness;
    @Bind(R.id.tv_title_back)
    ImageView tvTitleBack;
    @Bind(R.id.tv_title_name)
    TextView tvTitleName;

    private Sensor mSensor;
    private Vibrator mVibrator;
    private SensorManager mSensorManager;

    private long lastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null)
            mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_title_back:
                finish();
                break;
            case R.id.tv_add_willingness:  // 增加意愿
                addWillingnessLayout();
                break;
            case R.id.tv_del_willingness:
                delWillingnessLayout((View) view.getTag());
                break;
        }
    }

    private void initView() {
        tvTitleName.setText(R.string.str_shake);
        tvTitleBack.setOnClickListener(this);
        tvAddWillingness.setOnClickListener(this);
        addWillingnessLayout();
    }

    private void addWillingnessLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View singleWillingnessView = layoutInflater.inflate(R.layout.layout_single_willingness, null);
        TextView tvDelWillingness = singleWillingnessView.findViewById(R.id.tv_del_willingness);
        tvDelWillingness.setTag(singleWillingnessView);
        tvDelWillingness.setOnClickListener(this);
        llTotalWillingness.addView(singleWillingnessView);
    }

    private void delWillingnessLayout(View singleWillingnessView) {
        llTotalWillingness.removeView(singleWillingnessView);
    }

    private void initData() {
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            // 加速度传感器
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mSensor != null) {
                // mSensor ： 需要注册的sensor
                // SensorManager.SENSOR_DELAY_GAME : 反应速率
                mSensorManager.registerListener(mSensorEventListener,
                        mSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    /**
     * 重力感应监听
     */
    SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastTime) < UPDATE_INTERVAL_TIME) {
                return;
            }
            lastTime = curTime;
            // 传感器信息改变时执行该方法
            float[] values = sensorEvent.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            // 一般在这三个方向的重力加速度为40就达到了摇晃手机的状态
            int medumValue = 15;
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                // 启动震动，并持续指定的时间
                mVibrator.vibrate(200);
                String result = getResources().getString(R.string.str_shake_result);
                int randomNum = getRandomNum();
                if (llTotalWillingness != null) {
                    LinearLayout linearLayout = (LinearLayout) llTotalWillingness.getChildAt(randomNum);
                    EditText etWillingness = linearLayout.findViewById(R.id.et_willingness);
                    if (etWillingness != null) {
                        String willingnessStr = etWillingness.getText().toString();
                        if (!TextUtils.isEmpty(willingnessStr)) {
                            tvShakeResult.setText("选中意愿：" + etWillingness.getText().toString());
                        }
                    } else {
                        tvShakeResult.setText("请填写意愿");
                    }
                } else {
                    tvShakeResult.setText("请填写意愿");
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    /**
     * 获取[0,10)范围内的随机数
     */
    private int getRandomNum() {
        int max = 0;
        if (llTotalWillingness != null) {
            max = llTotalWillingness.getChildCount();
        }
        Random random = new Random();
        return random.nextInt(max);
    }
}
