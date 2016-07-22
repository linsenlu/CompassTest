package com.example.administrator.compasstest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_compass,iv_arrow;

    private SensorManager manager;
    private Sensor sensorAcc;
    private Sensor sensorMag;

    private float lastDdgree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_compass = (ImageView) findViewById(R.id.iv_compass);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        //获取传感器的管理器
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //加速度传感器
        sensorAcc = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //磁力传感器
        sensorMag = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //注册监听
        manager.registerListener(listener, sensorAcc,SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(listener, sensorMag,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager!=null){
            manager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener = new SensorEventListener() {
        float [] accVaules = new float [3];
        float [] magVaules = new float [3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            //判断event的类型是加速度传感器还是地磁传感器
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                accVaules = event.values.clone();
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magVaules = event.values.clone();
                Log.e("print","地磁:"+event.values[0]);
            }

            float R[] = new float[9];
            float values[] = new float[3];
            //获取旋转矩阵的R数组
            SensorManager.getRotationMatrix(R,null,accVaules,magVaules);
            //计算手机的旋转数据,values[0]代表手机围绕Z轴旋转的弧度
            SensorManager.getOrientation(R,values);
            //将弧度转为角度，取反，用于选择compass背景图
            float rotateDegree = (float) -(Math.toDegrees(values[0]));

            if (Math.abs(rotateDegree-lastDdgree)>1){
                RotateAnimation rotateAnimation  = new RotateAnimation(lastDdgree,rotateDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setFillAfter(true);
                iv_compass.startAnimation(rotateAnimation);
                lastDdgree = rotateDegree;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
