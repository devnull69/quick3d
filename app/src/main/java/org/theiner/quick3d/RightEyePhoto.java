package org.theiner.quick3d;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
    private String _filename;
    RightEyePhoto me;
    MediaPlayer _shootMP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_right_eye_photo, null);

        svKamera = (SurfaceView) layout.findViewById(R.id.myrightsurface);
        showFotoView = (ImageView) layout.findViewById(R.id.overlay);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        me=this;

        _filename = getArguments().getString("filename");


        // do we have a camera?
        if (!getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getActivity(), "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            SurfaceHolder sh = svKamera.getHolder();
            sh.addCallback(this);
        }
    }

    public void onClick(View view, final String filename) {
        camera.takePicture(new Camera.ShutterCallback() {

            @Override
            public void onShutter() {
                AudioManager meng = (AudioManager) me.getActivity().getSystemService(Context.AUDIO_SERVICE);
                int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

                if (volume != 0)
                {
                    if (_shootMP == null)
                        _shootMP = MediaPlayer.create(me.getActivity(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                    if (_shootMP != null)
                        _shootMP.start();
                }            }
        }, null, new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                File pictureFileDir = Helper.getDir();

                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

                    Toast.makeText(getActivity(), "Can't create directory to save image.",
                            Toast.LENGTH_LONG).show();
                    return;

                }

                String photoFile = pictureFileDir.getPath() + File.separator + filename + "_right.jpg";

                File pictureFile = new File(photoFile);

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(bytes);
                    fos.close();
                    //Toast.makeText(context, "New Image saved:" + photoFile, Toast.LENGTH_LONG).show();
                    Quick3DMain actMain = (Quick3DMain) getActivity();
                    actMain.callbackAfterPictureSaved();
                } catch (Exception error) {
                    Toast.makeText(getActivity(), "Image could not be saved.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
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

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                int width = size.x;
                int height = size.y;
                float ratio = height / (float)width;

                List<Camera.Size> sizeList = params.getSupportedPictureSizes();
                int chosenSize = Helper.getPictureSizeIndexForHeight(sizeList, 800, ratio);
                params.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);

                Helper.setRotationParameter(getActivity(), cameraId, params);

                camera.setParameters(params);

                camera.setPreviewDisplay(sh);
                camera.startPreview();

                File pictureFileDir = Helper.getDir();

                String fullPath = pictureFileDir.getPath() + File.separator + _filename + "_left.jpg";

                File pictureFile = new File(fullPath);

                if(pictureFile.exists()) {
                    Bitmap myBitmap = Helper.getRotatedBitmap(pictureFile);
                    showFotoView.setImageBitmap(myBitmap);
                    showFotoView.setAlpha(100);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getActivity(), getString(R.string.right_photo_tap), Toast.LENGTH_LONG)
                .show();
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
