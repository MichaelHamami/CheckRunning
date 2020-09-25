package com.example.checkrunning;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MyToaster {

    private static MyToaster instance;
    private static Context context;

    public static MyToaster getInstance() {
        return instance;
    }

    private MyToaster(Context context) {
        this.context = context;
    }

    public static MyToaster initHelper(Context context) {
        if (instance == null)
            instance = new MyToaster(context);
        return instance;
    }

    public void showToast(final String message) {
        // If we put it into handler - we can call in from asynctask outside of main uithread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
