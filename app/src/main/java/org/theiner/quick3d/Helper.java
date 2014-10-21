package org.theiner.quick3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
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
        if(chosenHeight == -1)
            chosenHeight = previousHeight;
        return chosenHeight;
    }

    public static File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "Quick3D");
    }

    public static Bitmap getRotatedBitmap(File ff) {
        Bitmap result;
        int toRotate = neededRotation(ff);
        result = BitmapFactory.decodeFile(ff.getAbsolutePath());
        if(toRotate != 0) {
            try {
                Matrix m = new Matrix();
                m.postRotate(toRotate);
                result = Bitmap.createBitmap(result,
                        0, 0, result.getWidth(), result.getHeight(),
                        m, true);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static int neededRotation(File ff)
    {
        try
        {

            ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
            { return 270; }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
            { return 180; }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            { return 90; }
            return 0;

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static void showTraceDialog(Q3DApplication myApp, Activity fromActivity) {
        new AlertDialog.Builder(fromActivity)
                .setTitle("Exception + Trace")
                .setMessage(myApp.getTrace())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }
}
