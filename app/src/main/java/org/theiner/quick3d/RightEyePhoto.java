package org.theiner.quick3d;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class RightEyePhoto extends Fragment implements SurfaceHolder.Callback{

    public final static String DEBUG_TAG = "Quick3DRight";
    private SurfaceView svKamera;
    private Camera camera;
    private int cameraId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_right_eye_photo, null);

        svKamera = (SurfaceView) layout.findViewById(R.id.myrightsurface);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
    }

    public void onClick(View view, String filename) {
        camera.takePicture(null, null,
                new PhotoHandler(getActivity().getApplicationContext(), filename + "_right.jpg"));
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

                List<Camera.Size> sizeList = params.getSupportedPictureSizes();
                int chosenSize = Helper.getPictureSizeIndexForHeight(sizeList, 800);
                params.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);

                Helper.setRotationParameter(getActivity(), cameraId, params);

                camera.setParameters(params);

                camera.setPreviewDisplay(sh);
                camera.startPreview();
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
                Log.d(DEBUG_TAG, "Camera found");
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
