package com.lordoscar.project3;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Policy;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String[] presets = new String[] { "Very low", "Low", "Normal", "Bright", "Very bright"};
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private ContentResolver contentResolver;
    private Window window;
    private boolean isChecked = false;
    private boolean isFlashOn = false;
    private Button morseButton;

    TextView warningText;
    String selectedPreset = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{android. Manifest.permission.WRITE_SETTINGS}, 0);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.CAMERA}, 1888);

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }else {
            Toast.makeText(this, "No light sensor found.", Toast.LENGTH_SHORT).show();
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }else {
            Toast.makeText(this, "No light sensor found.", Toast.LENGTH_SHORT).show();
        }

        contentResolver = getContentResolver();
        window = getWindow();

        morseButton = findViewById(R.id.morseButton);
        morseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MorseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeComponents(){
        Switch screenSwitch = findViewById(R.id.screenSwitch);
        warningText = findViewById(R.id.warningText);
        Spinner presetSpinner = findViewById(R.id.presetSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, presets);
        presetSpinner.setAdapter(adapter);
        presetSpinner.setOnItemSelectedListener(new SpinnerListener());
        presetSpinner.setSelection(2);
        screenSwitch.setOnCheckedChangeListener(new SwitchListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        turnOffFlash();
        Toast.makeText(this, "Unregistered listeners.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Registered listeners.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            float light = event.values[0];

            Log.d("Light changed", "New value: " + light);

            if (light>=100)
                light = 100;
            if (light<=0)
                light = 0;

            float brightness = light / 100;

            changeScreenBrightness(brightness);
        }else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            float distance = event.values[0];

            Log.d("Proximity changed", "New value: " + distance);

            if (distance < event.sensor.getMaximumRange()){
                //Turn on flash
                turnOnFlash();
            }else{
                //Turn off flash?
                turnOffFlash();
            }
        }
    }

    private void turnOnFlash(){
        if(isFlashOn)
            return;

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            isFlashOn = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void turnOffFlash(){
        if (!isFlashOn)
            return;

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            isFlashOn = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void changeScreenBrightness(float brightness){
        if(isChecked){
            if(!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(intent);
            }else{
                int systemBrightness = calcSystemBrightness(brightness);
                //Log.d("System brightness", systemBrightness + "");
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, systemBrightness);
            }
        }
        float screenBrightness = calcScreenBrightness(brightness);
        //Log.d("Screen brightness", screenBrightness + "");
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightness;
        window.setAttributes(layoutParams);
    }

    private int calcSystemBrightness(float brightness){
        switch (selectedPreset){
            case "Very low":
                return (int) (255 * 0.05 + 25.5 * brightness);
            case "Low":
                return (int) (255 * 0.3 + 25.5 * brightness);
            case "Normal":
                return (int) (255 * 0.5 + 25.5 * brightness);
            case "Bright":
                return (int) (255 * 0.7 + 25.5 * brightness);
            case "Very bright":
                return (int) (255 * 0.9 + 25.5 * brightness);
        }
        return 100;
    }

    private float calcScreenBrightness(float brightness){
        switch (selectedPreset){
            case "Very low":
                return 0.05F + 0.1F * brightness;
            case "Low":
                return 0.3F + 0.1F * brightness;
            case "Normal":
                return 0.5F + 0.1F * brightness;
            case "Bright":
                return 0.7F + 0.1F * brightness;
            case "Very bright":
                return 0.9F + 0.1F * brightness;
        }
        return 100;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("Accuracy changed", sensor.getName() + " " + accuracy);
    }

    class SwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            MainActivity.this.isChecked = isChecked;
            warningText.setVisibility(View.GONE);
            if(isChecked){
                warningText.setVisibility(View.VISIBLE);
            }
        }
    }

    class SpinnerListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedPreset = presets[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            selectedPreset = presets[2];
            ((Spinner) parent).setSelection(2);
        }
    }
}
