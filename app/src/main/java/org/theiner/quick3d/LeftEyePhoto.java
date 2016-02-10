package org.theiner.quick3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheineT on 23.09.2014.
 */
public class LeftEyePhoto extends Fragment implements SurfaceHolder.Callback {

    public final static String DEBUG_TAG = "Quick3DLeft";
    private SurfaceView svKamera;
    private Camera camera;
    private int cameraId = 0;
    MediaPlayer _shootMP = null;
    LeftEyePhoto me;
    Q3DApplication myApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = ((Q3DApplication)this.getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_left_eye_photo, null);
        svKamera = (SurfaceView) layout.findViewById(R.id.mysurface);

        myApp.appendTrace("LeftEyePhoto: View erzeugt\n");

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
            // do we have a camera?
            if (!getActivity().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Toast.makeText(getActivity(), "No camera on this device", Toast.LENGTH_LONG)
                        .show();
            } else {
                SurfaceHolder sh = svKamera.getHolder();
                sh.addCallback(this);
            }
            me = this;
            myApp.appendTrace("LeftEyePhoto: SurfaceHolder erzeugt\n");
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this.getActivity());
        }
    }

    public void onClick(View view, final String filename) {
        try {
            myApp.appendTrace("LeftEyePhoto: Photo Start\n");
            camera.takePicture(new Camera.ShutterCallback() {

                @Override
                public void onShutter() {
                    try {
                        AudioManager meng = (AudioManager) me.getActivity().getSystemService(Context.AUDIO_SERVICE);
                        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

                        if (volume != 0) {
                            if (_shootMP == null)
                                _shootMP = MediaPlayer.create(me.getActivity(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                            if (_shootMP != null)
                                _shootMP.start();
                        }
                    } catch(Throwable e) {
                        StackTraceElement se = e.getStackTrace()[0];
                        myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
                        Helper.showTraceDialog(myApp, me.getActivity());
                    }
                }
            }, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    try {
                        myApp.appendTrace("LeftEyePhoto: Photo speichern Start\n");

                        myApp.setLeftEyeBitmap(Helper.getRotatedBitmap(bytes));
                        Quick3DMain actMain = (Quick3DMain) getActivity();

                        myApp.appendTrace("LeftEyePhoto: Photo speichern Ende\n");
                        actMain.callbackAfterPictureSaved();
                    } catch(Throwable e) {
                        StackTraceElement se = e.getStackTrace()[0];
                        myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
                        Helper.showTraceDialog(myApp, me.getActivity());
                    }

                }
            });
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this.getActivity());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder sh) {
        showOnSurface(sh);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    public void showOnSurface(SurfaceHolder sh) {
        try {
            myApp.appendTrace("LeftEyePhoto: Preview auf Surface anzeigen Start\n");
            cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(getActivity(), "No back facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                try {
                    Helper.setCameraDisplayOrientation(getActivity(), cameraId, camera);
                    //camera.setDisplayOrientation(90);

                    Camera.Parameters params = camera.getParameters();

                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        display.getRealSize(size);
                    } else {
                        display.getSize(size);
                    }
                    int width = size.x;
                    int height = size.y;
                    float ratio = height / (float) width;

                    List<Camera.Size> sizeList = params.getSupportedPictureSizes();
                    int chosenSize = Helper.getPictureSizeIndexForHeight(sizeList, 800, ratio);

                    // Fehlerbehandlung, Kompatibilitätsmeldung
                    if(chosenSize==-1) {
                        new AlertDialog.Builder(this.getActivity())
                                .setTitle(R.string.compat_headline)
                                .setMessage(R.string.compat_body)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                        System.exit(0);
                    } else {
                        myApp.setImageWidth(sizeList.get(chosenSize).width);
                        myApp.setImageHeight(sizeList.get(chosenSize).height);

                        params.setPictureSize(myApp.getImageWidth(), myApp.getImageHeight());

                        Helper.setRotationParameter(getActivity(), cameraId, params);

                        camera.setParameters(params);

                        camera.setPreviewDisplay(sh);
                        camera.startPreview();
                    }

                    myApp.appendTrace("LeftEyePhoto: Preview auf Surface anzeigen Ende\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getActivity(), getString(R.string.left_photo_tap), Toast.LENGTH_LONG)
                    .show();
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this.getActivity());
        }
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

}
