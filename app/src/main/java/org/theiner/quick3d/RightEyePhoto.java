package org.theiner.quick3d;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class RightEyePhoto extends Fragment implements SurfaceHolder.Callback{

    public final static String DEBUG_TAG = "Quick3DRight";
    private SurfaceView svKamera;
    private ImageView showFotoView;
    private Camera camera;
    private int cameraId = 0;
    RightEyePhoto me;
    MediaPlayer _shootMP;
    Q3DApplication myApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = ((Q3DApplication)this.getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_right_eye_photo, null);

        svKamera = (SurfaceView) layout.findViewById(R.id.myrightsurface);
        showFotoView = (ImageView) layout.findViewById(R.id.overlay);

        myApp.appendTrace("RightEyePhoto: View erzeugt\n");

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);

            me = this;

            // do we have a camera?
            if (!getActivity().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Toast.makeText(getActivity(), "No camera on this device", Toast.LENGTH_LONG)
                        .show();
            } else {
                SurfaceHolder sh = svKamera.getHolder();
                sh.addCallback(this);
            }
            myApp.appendTrace("RightEyePhoto: SurfaceHolder erzeugt\n");
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this.getActivity());
        }
    }

    public void onClick(View view, final String filename) {
        try {
            myApp.appendTrace("RightEyePhoto: Photo Start\n");
            camera.takePicture(new Camera.ShutterCallback() {

                @Override
                public void onShutter() {
                    AudioManager meng = (AudioManager) me.getActivity().getSystemService(Context.AUDIO_SERVICE);
                    int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

                    if (volume != 0) {
                        if (_shootMP == null)
                            _shootMP = MediaPlayer.create(me.getActivity(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                        if (_shootMP != null)
                            _shootMP.start();
                    }
                }
            }, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    myApp.appendTrace("RightEyePhoto: Photo speichern Start\n");
                    myApp.setRightEyeBitmap(Helper.getRotatedBitmap(bytes));
                    Quick3DMain actMain = (Quick3DMain) getActivity();

                    myApp.appendTrace("RightEyePhoto: Photo speichern Ende, Starte Thread für Anaglyphberechnung\n");

                    new Thread(new Runnable() {
                        public void run() {
                            Bitmap halftoneBitmap;

                            Bitmap zielBitmap = myApp.getRightEyeBitmap();
                            zielBitmap = zielBitmap.copy(zielBitmap.getConfig(), true);
                            halftoneBitmap = zielBitmap.copy(zielBitmap.getConfig(), true);

                            Bitmap rotBitmap = myApp.getLeftEyeBitmap();
                            int imgWidth = zielBitmap.getWidth();
                            int imgHeight = zielBitmap.getHeight();

                            int[] zielpixels = new int[imgHeight * imgWidth];
                            int[] redpixels = new int[imgHeight * imgWidth];
                            int[] halftonepixels = new int[imgHeight * imgWidth];
                            zielBitmap.getPixels(zielpixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
                            rotBitmap.getPixels(redpixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

                            for (int i = 0; i < imgHeight * imgWidth; i++) {
                                try {
                                    zielpixels[i] = Color.argb(Color.alpha(zielpixels[i]), Color.red(redpixels[i]), Color.green(zielpixels[i]), Color.blue(zielpixels[i]));

                                    // halftone
                                    int redpart = (int) (Color.red(redpixels[i]) * 0.299 + Color.green(redpixels[i]) * 0.587 + Color.blue(redpixels[i]) * 0.114);
                                    halftonepixels[i] = Color.argb(Color.alpha(zielpixels[i]), redpart, Color.green(zielpixels[i]), Color.blue(zielpixels[i]));
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }

                            zielBitmap.setPixels(zielpixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
                            halftoneBitmap.setPixels(halftonepixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
                            myApp.setAnaglyphBitmap(zielBitmap);
                            myApp.setHalftoneBitmap(halftoneBitmap);
                        }
                    }).start();

                    actMain.callbackAfterPictureSaved();
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
            myApp.appendTrace("RightEyePhoto: Preview auf Surface anzeigen Start\n");
            cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(getActivity(), "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                try {
                    Helper.setCameraDisplayOrientation(getActivity(), cameraId, camera);
                    //camera.setDisplayOrientation(90);

                    Camera.Parameters params = camera.getParameters();

                    params.setPictureSize(myApp.getImageWidth(), myApp.getImageHeight());

                    Helper.setRotationParameter(getActivity(), cameraId, params);

                    camera.setParameters(params);

                    camera.setPreviewDisplay(sh);
                    camera.startPreview();

                    myApp.appendTrace("RightEyePhoto: Preview auf Surface anzeigen Ende\n");

                    showFotoView.setImageBitmap(myApp.getLeftEyeBitmap());
                    showFotoView.setAlpha(100);
                    myApp.appendTrace("RightEyePhoto: Overlay linkes Photo fertig\n");
                } catch (IOException e) {
                    StackTraceElement se = e.getStackTrace()[0];
                    myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
                    Helper.showTraceDialog(myApp, this.getActivity());
                }
            }
            Toast.makeText(getActivity(), getString(R.string.right_photo_tap), Toast.LENGTH_LONG)
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
