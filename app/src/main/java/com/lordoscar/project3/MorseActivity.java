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
    private Button readButton;
    private MorseTranslator morseTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse);
        resultText = findViewById(R.id.resultText);
        clearButton = findViewById(R.id.clearButton);
        sosButton = findViewById(R.id.sosButton);
        okButton = findViewById(R.id.okButton);
        readButton = findViewById(R.id.readButton);

        ButtonListener bl = new ButtonListener();
        clearButton.setOnClickListener(bl);
        sosButton.setOnClickListener(bl);
        okButton.setOnClickListener(bl);
        readButton.setOnClickListener(bl);

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = null;
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

    boolean reading = false;

    class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.equals(clearButton)){
                morseTranslator.clear();
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
            }else if(v.equals(readButton)){
                if(reading){
                    readButton.setText("READ MORSE");
                    morseTranslator.unregisterListener();
                    resultText.setText(morseTranslator.getTranslation());
                    reading = false;
                }else {
                    readButton.setText("FINISH READING");
                    morseTranslator.clear();
                    resultText.setText("");
                    morseTranslator.registerListener();
                    reading = true;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        morseTranslator.unregisterListener();
        readButton.setText("READ MORSE");
        reading = false;
        Toast.makeText(this, "Unregistered listeners.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Registered listeners.", Toast.LENGTH_SHORT).show();
    }
}
