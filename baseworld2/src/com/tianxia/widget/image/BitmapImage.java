package com.tianxia.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

public class BitmapImage implements SmartImage {
    private Bitmap bitmap;

    public BitmapImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(Context context) {
        return bitmap;
    }
}