package com.antstudios.as.bite.gen.change.brightnesscontroller.services;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.antstudios.as.bite.gen.change.brightnesscontroller.R;

public class FloatingWidgetService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    ImageView Closebtn,Appicon;

    private SeekBar brightnessBtn;
    private long startTime = 0L;
    private boolean isRunning = false;
    int seconds=0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                     if(seconds>2){
                        if (isRunning) {
                            isRunning = false;
                            stopSelf();
                        }
                    }
                     else if (isRunning) {
                        long elapsedTime = System.currentTimeMillis() - startTime;

                        seconds = (int) (elapsedTime % 60000) / 1000;

                        //  String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // Create a WindowManager object.
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Create the floating view.
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);

        // Add a touch listener to the floating view.
        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                seconds=0;
                /*// Update the floating view's position.
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) v.getLayoutParams();
                params.x = (int) event.getRawX() - v.getWidth() / 2;
                params.y = (int) event.getRawY() - v.getHeight() / 2;
                mWindowManager.updateViewLayout(v, params);*/

                return true;
            }
        });

        brightnessBtn=mFloatingView.findViewById(R.id.float_seek);
        Appicon=mFloatingView.findViewById(R.id.app_icon_btn);
        Closebtn=mFloatingView.findViewById(R.id.close_btn);
        Closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    isRunning = false;
                    stopSelf();
                }
            }
        });

        try {
            brightnessBtn.setProgress(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        brightnessBtn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                mainActivity.changeBrightness(i);
                seconds=0;
                Appicon.setRotation(i);
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // Add the floating view to the window manager.
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.CENTER_VERTICAL| Gravity.TOP;
        mWindowManager.addView(mFloatingView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the floating view from the window manager.
        mWindowManager.removeView(mFloatingView);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

