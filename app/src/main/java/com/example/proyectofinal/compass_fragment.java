package com.example.proyectofinal;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class compass_fragment extends Fragment implements SensorEventListener, View.OnClickListener {

    private ImageView imageView, imageView2, imageView3, imageView4;
    private TextView textView, textView2;
    private ImageButton imageButton;
    private List<String> list;
    private ConstraintLayout background;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor, magnetometerSensor;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private int index = 0;
    private int ix = 0;

    boolean isLastAccelerometerArrayCopied = false;
    boolean isLastMagnetometerArrayCopied = false;

    long lastUpdatedTime = 0;
    float currentDegree = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_compass, container,
                false);

        imageView = rootView.findViewById(R.id.brujula1);
        imageView2 = rootView.findViewById(R.id.brujula2);
        imageView3 = rootView.findViewById(R.id.brujula3);
        imageView4 = rootView.findViewById(R.id.brujula4);

        textView = rootView.findViewById(R.id.textView);
        textView2 = rootView.findViewById(R.id.textView2);
        imageButton = rootView.findViewById(R.id.change);

        background = rootView.findViewById(R.id.background);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                index++;

                if (index == 4)
                {
                    index = 0;
                }

            }
        });
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return rootView;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor == accelerometerSensor){
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            isLastAccelerometerArrayCopied = true;
        }
        else if(sensorEvent.sensor == magnetometerSensor) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            isLastMagnetometerArrayCopied = true;
        }

        if(isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis() - lastUpdatedTime > 250){
            SensorManager.getRotationMatrix(rotationMatrix, null,lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);

            RotateAnimation rotateAnimation = new RotateAnimation(currentDegree, -azimuthInDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            switch (index){
                case 0:
                    imageView.startAnimation(rotateAnimation);
                    imageView2.clearAnimation();
                    imageView3.clearAnimation();
                    imageView4.clearAnimation();
                    textView2.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    imageView2.startAnimation(rotateAnimation);
                    imageView.clearAnimation();
                    imageView3.clearAnimation();
                    imageView4.clearAnimation();
                    textView2.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    imageView3.startAnimation(rotateAnimation);
                    imageView2.clearAnimation();
                    imageView.clearAnimation();
                    imageView4.clearAnimation();
                    textView2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    imageView4.startAnimation(rotateAnimation);
                    imageView2.clearAnimation();
                    imageView3.clearAnimation();
                    imageView.clearAnimation();
                    textView2.setVisibility(View.VISIBLE);
                    break;
            }

            currentDegree = -azimuthInDegree;
            lastUpdatedTime = System.currentTimeMillis();

            int x = (int) azimuthInDegree;
            textView.setText(x + "°");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magnetometerSensor);
    }

    @Override
    public void onClick(View view) {

    }
}
