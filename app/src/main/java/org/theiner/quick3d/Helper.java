package org.theiner.quick3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.Surface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by TheineT on 22.09.2014.
 */
public class Helper {

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static void setRotationParameter(Activity activity, int cameraId, Camera.Parameters param) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        rotation = (rotation + 45) / 90 * 90;

        int toRotate = (info.orientation + rotation) % 360;

        param.setRotation(toRotate);
    }

    public static int getPictureSizeIndexForHeight(List<Camera.Size> sizeList, int height, float ratio) {
        int chosenHeight = -1;
        int previousHeight = 0;
        for(int i=0; i<sizeList.size(); i++) {
            float thisRatio = sizeList.get(i).width/(float)sizeList.get(i).height;
            if(thisRatio == ratio)
                if(sizeList.get(i).height < height) {
                    chosenHeight = previousHeight;
                    break;
                } else {
                    previousHeight = i;
                }
        }
        return chosenHeight;
    }

    public static File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "Quick3D");
    }

    public static Bitmap getRotatedBitmap(byte[] bildDaten) {
        Bitmap result = BitmapFactory.decodeByteArray(bildDaten, 0, bildDaten.length);
        if(result.getWidth() > result.getHeight()) {
                Matrix m = new Matrix();
                m.postRotate(90);
                result = Bitmap.createBitmap(result,
                        0, 0, result.getWidth(), result.getHeight(),
                        m, true);
        }
        return result;
    }

    public static void showTraceDialog(Q3DApplication myApp, Activity fromActivity) {
        new AlertDialog.Builder(fromActivity)
                .setTitle("Throwable + Trace")
                .setMessage(myApp.getTrace())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static Bitmap fisheye(Bitmap srcimage) {
    /*
     *    Fish eye effect
     *    tejopa, 2012-04-29
     *    http://popscan.blogspot.com
     *    http://www.eemeli.de
     */

        // get image pixels
        double w = srcimage.getWidth();
        double h = srcimage.getHeight();
        int[] srcpixels = new int[(int)(w*h)];
        srcimage.getPixels(srcpixels, 0, (int) w, 0, 0, (int) w, (int) h);

        Bitmap resultimage = srcimage.copy(srcimage.getConfig(), true);

        // create the result data
        int[] dstpixels = new int[(int)(w*h)];
        // for each row
        for (int y=0;y<h;y++) {
            // normalize y coordinate to -1 ... 1
            double ny = ((2*y)/h)-1;
            // pre calculate ny*ny
            double ny2 = ny*ny;
            // for each column
            for (int x=0;x<w;x++) {
                // preset to black
                dstpixels[(int)(y*w+x)] = 0;

                // normalize x coordinate to -1 ... 1
                double nx = ((2*x)/w)-1;
                // pre calculate nx*nx
                double nx2 = nx*nx;
                // calculate distance from center (0,0)
                // this will include circle or ellipse shape portion
                // of the image, depending on image dimensions
                // you can experiment with images with different dimensions
                double r = Math.sqrt(nx2+ny2);
                // discard pixels outside from circle!
                if (0.0<=r&&r<=1.0) {
                    double nr = Math.sqrt(1.0-r*r);
                    // new distance is between 0 ... 1
                    nr = (r + (1.0-nr)) / 2.0;
                    // discard radius greater than 1.0
                    if (nr<=1.0) {
                        // calculate the angle for polar coordinates
                        double theta = Math.atan2(ny,nx);
                        // calculate new x position with new distance in same angle
                        double nxn = nr*Math.cos(theta);
                        // calculate new y position with new distance in same angle
                        double nyn = nr*Math.sin(theta);
                        // map from -1 ... 1 to image coordinates
                        int x2 = (int)(((nxn+1)*w)/2.0);
                        // map from -1 ... 1 to image coordinates
                        int y2 = (int)(((nyn+1)*h)/2.0);
                        // find (x2,y2) position from source pixels

                        int srcpos = (int)(y2*w+x2);
                        // make sure that position stays within arrays
                        if (srcpos>=0 & srcpos < w*h) {
                            // get new pixel (x2,y2) and put it to target array at (x,y)
                            dstpixels[(int)(y*w+x)] = srcpixels[srcpos];
                        }
                    }
                }
            }

        }

        resultimage.setPixels(dstpixels, 0, (int) w, 0, 0, (int) w, (int) h);
        //return result pixels
        return resultimage;
    }

    public static Bitmap putOnBiggerBitmap(Bitmap original) {
        Point size = new Point();
        size.x = original.getHeight() / 2;
        size.y = original.getWidth();

        double aspect = (double)(original.getHeight()) / original.getWidth();

        // Oben und unten 15% Platz lassen
        int ymargin = (int) (size.y * 0.15);
        int rectHeight = size.y - (ymargin*2);
        int rectWidth = (int) (rectHeight / aspect);
        int xmargin = (size.x - rectWidth) / 2;

        Bitmap zielBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);

        int black = Color.argb(255, 0, 0, 0);
        zielBitmap.eraseColor(black);

        Canvas canvas = new Canvas(zielBitmap);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawBitmap(original, null, new Rect(xmargin, ymargin, rectWidth + xmargin, rectHeight + ymargin), paint);

        return zielBitmap;
    }

    public static void showMessageOnClose(final Activity act) {
        new AlertDialog.Builder(act)
                .setTitle(R.string.close_sure)
                .setMessage(R.string.close_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })

                        .show();
    }
}
