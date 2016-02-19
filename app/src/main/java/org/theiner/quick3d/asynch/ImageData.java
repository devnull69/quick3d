package org.theiner.quick3d.asynch;

import android.graphics.Bitmap;

/**
 * Created by TTheiner on 16.02.2016.
 */
public class ImageData {
    private ImageType imageType;
    private Bitmap firstBitmap = null;
    private Bitmap secondBitmap = null;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public Bitmap getFirstBitmap() {
        return firstBitmap;
    }

    public void setFirstBitmap(Bitmap firstBitmap) {
        this.firstBitmap = firstBitmap;
    }

    public Bitmap getSecondBitmap() {
        return secondBitmap;
    }

    public void setSecondBitmap(Bitmap secondBitmap) {
        this.secondBitmap = secondBitmap;
    }
}
