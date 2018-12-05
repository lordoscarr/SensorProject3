package com.lordoscar.project3;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String[] presets = new String[] { "Very low", "Low", "Normal", "Bright", "Very bright"};
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private ContentResolver contentResolver;
    private Window window;
    private boolean isChecked = false;
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

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }else {
            Toast.makeText(this, "No proximity sensor found.", Toast.LENGTH_SHORT).show();
        }

        contentResolver = getContentResolver();
        window = getWindow();
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
        Toast.makeText(this, "Unregistered listener.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Registered listener.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float light = event.values[0];

        Log.d("Sensor changed", "New value: " + light);


        if(light>0 && light<100){
            changeScreenBrightness(1/light);
        }
    }

    private void changeScreenBrightness(float brightness){
        if(isChecked){
            if(!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(intent);
            }else{
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 20);
            }
        }else {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = brightness;
            window.setAttributes(layoutParams);
        }
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
