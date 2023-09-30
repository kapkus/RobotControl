package com.pl.edu.prz.robotui.control.panel;

import static java.lang.Math.abs;

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

public class VerticalThumbstickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private float actualX, actualY;
    private VerticalThumbstickListener VerticalThumbstickCallBack;
    private boolean flag = true;

    public VerticalThumbstickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof VerticalThumbstickListener)
            VerticalThumbstickCallBack = (VerticalThumbstickListener) context;
    }

    public VerticalThumbstickView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof VerticalThumbstickListener)
            VerticalThumbstickCallBack = (VerticalThumbstickListener) context;
    }

    public VerticalThumbstickView(Context context, AttributeSet attributes) {
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof VerticalThumbstickListener)
            VerticalThumbstickCallBack = (VerticalThumbstickListener) context;
    }

    private void setupDimensions() {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 8;
        hatRadius = Math.min(getWidth(), getHeight()) / 6;

    }

    private void drawThumbstick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            colors.setAntiAlias(true);
            myCanvas.drawColor(ResourcesCompat.getColor(getResources(), R.color.thumbstick_background, null));
            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, 2 * baseRadius, baseRadius, colors);
            myCanvas.drawCircle(centerX, getHeight() - (2 * baseRadius), baseRadius, colors);
            myCanvas.drawRect((getWidth() / 2) - baseRadius, (getHeight() / 2) - 2 * baseRadius, (getWidth() / 2) + baseRadius, (getHeight() / 2) + 2 * baseRadius, colors);
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

                float distance = abs(centerY - e.getY());
                actualX = centerX;

                if (distance < 2 * baseRadius) {
                    actualY = e.getY();
                } else {
                    float ratio = 2 * baseRadius / distance;
                    actualY = centerY + (e.getY() - centerY) * ratio;
                }
                flag = true;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (flag) {
                            VerticalThumbstickCallBack.onVerticalThumbstickMoved(((actualY - centerY) / (2 * baseRadius)) / 10, v.getId());
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
                distance = abs(centerY - e.getY());
                actualX = centerX;

                if (distance < 2 * baseRadius) {
                    actualY = e.getY();
                } else {
                    float ratio = 2 * baseRadius / distance;
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

    public void setListener(VerticalThumbstickView.VerticalThumbstickListener listener) {
        VerticalThumbstickCallBack = listener;
    }

    public interface VerticalThumbstickListener {
        void onVerticalThumbstickMoved(float zPos, int id);
    }
}
