package com.lordoscar.project3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MorseActivity extends AppCompatActivity {

    private TextView resultText;
    private Button clearButton;
    private Button sosButton;
    private Button okButton;
    private MorseTranslator morseTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse);
        resultText = findViewById(R.id.resultText);
        clearButton = findViewById(R.id.clearButton);
        sosButton = findViewById(R.id.sosButton);
        okButton = findViewById(R.id.okButton);

        ButtonListener bl = new ButtonListener();
        clearButton.setOnClickListener(bl);
        sosButton.setOnClickListener(bl);
        okButton.setOnClickListener(bl);

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor;
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }else {
            Toast.makeText(this, "No light sensor found.", Toast.LENGTH_SHORT).show();
        }

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            morseTranslator = new MorseTranslator(cameraManager, cameraId, sensorManager, lightSensor );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.equals(clearButton)){
                resultText.setText("");
            }else if (v.equals(sosButton)){
                try{
                    morseTranslator.send("SOS");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(v.equals(okButton)){
                try{
                    morseTranslator.send("OK");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Toast.makeText(this, "Unregistered listeners.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Registered listeners.", Toast.LENGTH_SHORT).show();
    }
}
