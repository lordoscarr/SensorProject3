package com.lordoscar.project3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MorseTranslator implements SensorEventListener {

    private CameraManager cameraManager;
    private String cameraId;
    private SensorManager sensorManager;
    private Sensor lightSensor;

    public MorseTranslator(CameraManager cameraManager, String cameraId, SensorManager sensorManager, Sensor lightSensor){
        this.cameraManager = cameraManager;
        this.cameraId = cameraId;
        this.sensorManager = sensorManager;
        this.lightSensor = lightSensor;
    }

    public void send(String message) throws Exception{
        if(message.equals("SOS")){
            dot();
            sleep(500);
            dot();
            sleep(500);
            dot();
            sleep(1500);
            dash();
            sleep(500);
            dash();
            sleep(500);
            dash();
            sleep(1500);
            dot();
            sleep(500);
            dot();
            sleep(500);
            dot();
        }else if(message.equals("OK")){
            dash();
            sleep(500);
            dash();
            sleep(500);
            dash();
            sleep(1500);
            dash();
            sleep(500);
            dot();
            sleep(500);
            dash();
        }


//        for(char c : message.toLowerCase().toCharArray()){
//            switch (c){
//                case ' ':
//                    sleep(3500);
//                    break;
//                case 'a':
//
//                    break;
//                case 'b':
//
//                    break;
//                case 'c':
//
//                    break;
//                case 'd':
//
//                    break;
//                case 'e':
//
//                    break;
//                case 'f':
//
//                    break;
//                case 'g':
//
//                    break;
//                case 'h':
//
//                    break;
//                case 'i':
//
//                    break;
//                case 'j':
//
//                    break;
//                case 'k':
//                    dash();
//                    sleep(500);
//                    dot();
//                    sleep(500);
//                    dash();
//                    break;
//                case 'l':
//
//                    break;
//                case 'm':
//
//                    break;
//                case 'n':
//
//                    break;
//                case 'o':
//                    dash();
//                    sleep(500);
//                    dash();
//                    sleep(500);
//                    dash();
//                    break;
//                case 'p':
//
//                    break;
//                case 'q':
//
//                    break;
//                case 'r':
//
//                    break;
//                case 's':
//                    dot();
//                    sleep(500);
//                    dot();
//                    sleep(500);
//                    dot();
//                    break;
//                case 't':
//
//                    break;
//                case 'u':
//
//                    break;
//                case 'v':
//
//                    break;
//                case 'w':
//
//                    break;
//                case 'x':
//
//                    break;
//                case 'y':
//
//                    break;
//                case 'z':
//
//                    break;
//                case '1':
//
//                    break;
//                case '2':
//
//                    break;
//                case '3':
//
//                    break;
//                case '4':
//
//                    break;
//                case '5':
//
//                    break;
//                case '6':
//
//                    break;
//                case '7':
//
//                    break;
//                case '8':
//
//                    break;
//                case '9':
//
//                    break;
//                case '0':
//
//                    break;
//            }
//            sleep(1500);
//        }
    }

    private void dot() throws Exception{
        Log.d("MORSE", "dot");
        try {
            cameraManager.setTorchMode(cameraId, true);
            sleep(500);
            cameraManager.setTorchMode(cameraId, false);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void dash() throws Exception{
        Log.d("MORSE", "dash");
        cameraManager.setTorchMode(cameraId, true);
        sleep(1500);
        cameraManager.setTorchMode(cameraId, false);
    }

    private static void sleep(long milliseconds) throws Exception{
        TimeUnit.MILLISECONDS.sleep(milliseconds);
    }

    public void registerListener(){
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener(){
        sensorManager.unregisterListener(this);
    }

    long lightOn = 0;
    long darkOn = 0;
    StringBuilder morseString = new StringBuilder();
    int charBegin = 0;
    boolean isLightOn = false;
    boolean islightSwitched = false;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float light = event.values[0];

        Log.d("Light changed", "New value: " + light);

        if(light < 600){
            if(isLightOn){
                islightSwitched = true;
                isLightOn = false;
            }else {
                islightSwitched = false;
            }
        }else {
            if(!isLightOn){
                islightSwitched = true;
                isLightOn = true;
            }else {
                islightSwitched = false;
            }
        }

        if(islightSwitched == false){
            return;
        }

        if(!isLightOn){
            Log.d("Light", "TURNED OFF");
            //Ingen lampa igång
            darkOn = System.currentTimeMillis();
            long time = darkOn - lightOn;

            if(time < 1400){
                //Dot
                morseString.append(".");
            }else if(time >= 1400){
                //Dash
                morseString.append("-");
            }

        }else{
            Log.d("Light", "TURNED ON");
            //Lampa igång
            lightOn = System.currentTimeMillis();
            long time = lightOn - darkOn;

            if(time < 1400){
                //Mellanrum mellan kod i bokstav
            }else if (time >= 1400 && time <= 3400){
                //Ny bokstav
                morseString.append("|");
            }else if(time >= 3400){
                //Mellanslag
                morseString.append("| |");
            }
        }
        Log.d("CURRENT MORSE", morseString.toString());
    }

    public String getTranslation(){

        String[] letters = morseString.toString().split("\\|");

        StringBuilder translated = new StringBuilder();

        for (String letter : letters){
            Log.d("LETTER", letter);
            if(letter.equals("..."))
                translated.append("S");
            if(letter.equals("---"))
                translated.append("O");
            if(letter.equals("-.-"))
                translated.append("K");
        }
        return translated.toString();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("Accuracy changed", "" + accuracy);
    }

    public void clear(){
        charBegin = 0;
        morseString = new StringBuilder();
        isLightOn = false;
        islightSwitched = false;
        lightOn = 0;
        darkOn = 0;
    }
}
