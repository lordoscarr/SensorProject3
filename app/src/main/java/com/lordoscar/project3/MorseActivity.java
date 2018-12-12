package com.lordoscar.project3;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            morseTranslator = new MorseTranslator(cameraManager, cameraId);
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
}
