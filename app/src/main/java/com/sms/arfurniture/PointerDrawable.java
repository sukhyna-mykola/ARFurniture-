package com.sms.arfurniture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PointerDrawable extends Drawable {


    private final Paint paint = new Paint();
    private boolean enabled, visible;
    private Bitmap bitmap;

    public PointerDrawable(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps);
        bitmap = Bitmap.createScaledBitmap(bitmap, 25, 25, false);
        paint.setColor(Color.GRAY);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float cx = canvas.getWidth() / 2;
        float cy = canvas.getHeight() / 2;
        if (visible) {
            if (enabled) {
                canvas.drawBitmap(bitmap, cx - bitmap.getWidth() / 2, cy - bitmap.getHeight() / 2, paint);
            } else {
                canvas.drawText("X", cx, cy, paint);
            }
        }
    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
