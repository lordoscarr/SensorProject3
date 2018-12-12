package com.lordoscar.project3;

import android.hardware.camera2.CameraManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MorseTranslator {

    private CameraManager cameraManager;
    private String cameraId;

    public MorseTranslator(CameraManager cameraManager, String cameraId){
        this.cameraManager = cameraManager;
        this.cameraId = cameraId;

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

    private void dot(){
        Log.d("MORSE", "dot");
        try {
            cameraManager.setTorchMode(cameraId, true);
            sleep(500);
            cameraManager.setTorchMode(cameraId, false);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void dash(){
        Log.d("MORSE", "dash");
        try {
            cameraManager.setTorchMode(cameraId, true);
            Timer timer = new Timer();
            cameraManager.setTorchMode(cameraId, false);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static void sleep(long milliseconds) throws Exception{
        TimeUnit.MILLISECONDS.sleep(milliseconds);
    }
}
