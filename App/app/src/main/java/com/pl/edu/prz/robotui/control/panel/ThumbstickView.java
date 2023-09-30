package com.pl.edu.prz.robotui.control.panel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.pl.edu.prz.robotui.R;

public class ThumbstickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    public Coords coords = new Coords();
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private ThumbstickListener ThumbstickCallBack;
    private float actualX, actualY;
    private boolean flag = true;

    public ThumbstickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof ThumbstickListener)
            ThumbstickCallBack = (ThumbstickListener) context;
    }

    public ThumbstickView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof ThumbstickListener)
            ThumbstickCallBack = (ThumbstickListener) context;
    }

    public ThumbstickView(Context context, AttributeSet attributes) {
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof ThumbstickListener)
            ThumbstickCallBack = (ThumbstickListener) context;
    }

    private void setupDimensions() {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 6;
    }

    private void drawThumbstick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            colors.setAntiAlias(true);
            myCanvas.drawColor(ResourcesCompat.getColor(getResources(), R.color.thumbstick_background, null));
            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setARGB(255, 23, 21, 21);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawThumbstick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float displacement = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    actualX = e.getX();
                    actualY = e.getY();
                } else {
                    float ratio = baseRadius / displacement;
                    actualX = centerX + (e.getX() - centerX) * ratio;
                    actualY = centerY + (e.getY() - centerY) * ratio;
                }
                flag = true;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (flag) {
                            ThumbstickCallBack.onThumbstickMoved(((actualX - centerX) / baseRadius) / 10, ((actualY - centerY) / baseRadius) / 10, v.getId());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
                drawThumbstick(actualX, actualY);
                break;

            case MotionEvent.ACTION_MOVE:
                displacement = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    actualX = e.getX();
                    actualY = e.getY();
                } else {
                    float ratio = baseRadius / displacement;
                    actualX = centerX + (e.getX() - centerX) * ratio;
                    actualY = centerY + (e.getY() - centerY) * ratio;
                }
                drawThumbstick(actualX, actualY);
                break;

            case MotionEvent.ACTION_UP:
                flag = false;
                drawThumbstick(centerX, centerY);
                break;
        }
        return true;
    }

    public void setListener(ThumbstickListener listener) {
        ThumbstickCallBack = listener;
    }

    public interface ThumbstickListener {
        void onThumbstickMoved(float xPercent, float yPercent, int id);
    }
}
