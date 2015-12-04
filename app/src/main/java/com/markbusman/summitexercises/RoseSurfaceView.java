package com.markbusman.summitexercises;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by markbusman on 07/10/2015.
 */
public class RoseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    double degreeToDraw = 0.0;

    public RoseSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public RoseSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
    }

    public RoseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int radius = width / 2;

        if (width > height) {
            radius = height / 2;
        }

        float k3 = (float) 0.00555 * radius;
        float k10 = (float) 0.0185 * radius;
        float k70 = (float) 0.12963 * radius;
        float k80 = (float) 0.14815 * radius;

        // paint the 1st black outlne
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius, p);

        // paint the orange circle
        p = new Paint();
        p.setColor(Color.parseColor("#FFA000"));
        p.setAntiAlias(true);
        p.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.WHITE, Color.parseColor("#FFA000"), Shader.TileMode.MIRROR));
        canvas.drawCircle(width / 2, height / 2, radius - k10, p);

        // paint the arc
        p = new Paint();
        p.setColor(Color.parseColor("#FF5B00"));
        p.setAntiAlias(true);
        p.setAlpha(87);

        RectF rect = new RectF(width / 2 - radius + k10, height/ 2 - radius + k10, width/ 2 + radius - k10, height/ 2 + radius - k10);
        canvas.drawArc(rect, -90, (float) degreeToDraw, true, p);

        // paint the spokes at 30º intervals
        p = new Paint();
        p.setColor(Color.BLACK);
        //p.setAntiAlias(true);
        p.setStrokeWidth(3f);
        canvas.drawLine(width / 2 - radius + k3, height / 2, width / 2 + radius - k3 / 2, height / 2, p);
        canvas.drawLine(width / 2, height / 2 - radius + k3, width / 2, height / 2 + radius - k3, p);

        // now draw a 30 degree line
        double y2 = Math.sin(Math.toRadians(330)) * radius;
        double x2 = Math.cos(Math.toRadians(330)) * radius;
        double y1 = Math.sin(Math.toRadians(300)) * radius;
        double x1 = Math.cos(Math.toRadians(300)) * radius;
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x2, height/ 2 + (float) y2, p);
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x1, height/ 2 + (float) y1, p);

        y2 = Math.sin(Math.toRadians(240)) * radius;
        x2 = Math.cos(Math.toRadians(240)) * radius;
        y1 = Math.sin(Math.toRadians(210)) * radius;
        x1 = Math.cos(Math.toRadians(210)) * radius;
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x2, height/ 2 + (float) y2, p);
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x1, height/ 2 + (float) y1, p);

        y2 = Math.sin(Math.toRadians(150)) * radius;
        x2 = Math.cos(Math.toRadians(150)) * radius;
        y1 = Math.sin(Math.toRadians(120)) * radius;
        x1 = Math.cos(Math.toRadians(120)) * radius;
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x2, height/ 2 + (float) y2, p);
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x1, height/ 2 + (float) y1, p);

        y2 = Math.sin(Math.toRadians(60)) * radius;
        x2 = Math.cos(Math.toRadians(60)) * radius;
        y1 = Math.sin(Math.toRadians(30)) * radius;
        x1 = Math.cos(Math.toRadians(30)) * radius;
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x2, height/ 2 + (float) y2, p);
        canvas.drawLine(width/ 2, height/ 2, width/ 2 + (float) x1, height/ 2 + (float) y1, p);

        // Paint the second black outline
        p = new Paint();
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius - k70, p);

        // paint the white inner part
        p = new Paint();
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius - k80, p);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            synchronized (holder) {
                onDraw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


    }
}
