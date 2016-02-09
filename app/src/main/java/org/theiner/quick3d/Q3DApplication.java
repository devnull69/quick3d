package org.theiner.quick3d;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by TheineT on 21.10.2014.
 */
public class Q3DApplication extends Application {
    private String trace;

    private int imageWidth;
    private int imageHeight;

    private Boolean anaglyphSaved = false;
    private Boolean crossEyedSaved = false;
    private Boolean parallelEyedSaved = false;

    private Bitmap leftEyeBitmap = null;
    private Bitmap rightEyeBitmap = null;
    private Bitmap anaglyphBitmap = null;

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String s) {
        trace = s;
    }

    public void appendTrace(String s) {
        trace += s;
    }

    public void prependTrace(String s) { trace = s + trace;}

    public Boolean getAnaglyphSaved() {
        return anaglyphSaved;
    }

    public void setAnaglyphSaved(Boolean b) {
        anaglyphSaved = b;
    }


    public Bitmap getLeftEyeBitmap() {
        return leftEyeBitmap;
    }

    public void setLeftEyeBitmap(Bitmap leftEyeBitmap) {
        this.leftEyeBitmap = leftEyeBitmap;
    }

    public Bitmap getRightEyeBitmap() {
        return rightEyeBitmap;
    }

    public void setRightEyeBitmap(Bitmap rightEyeBitmap) {
        this.rightEyeBitmap = rightEyeBitmap;
    }

    public Bitmap getAnaglyphBitmap() {
        return anaglyphBitmap;
    }

    public void setAnaglyphBitmap(Bitmap anaglyphBitmap) {
        this.anaglyphBitmap = anaglyphBitmap;
    }

    public Boolean getCrossEyedSaved() {
        return crossEyedSaved;
    }

    public void setCrossEyedSaved(Boolean crossEyedSaved) {
        this.crossEyedSaved = crossEyedSaved;
    }

    public Boolean getParallelEyedSaved() {
        return parallelEyedSaved;
    }

    public void setParallelEyedSaved(Boolean parallelEyedSaved) {
        this.parallelEyedSaved = parallelEyedSaved;
    }
}
