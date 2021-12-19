package com.example.citydangersalert;

import android.os.Handler;
import android.os.Looper;

public class RepetingThread extends Thread{
    public Handler handler;
    @Override
    public void run() {
        Looper.prepare();
        handler=new Handler();
        Looper.loop();
    }
}
