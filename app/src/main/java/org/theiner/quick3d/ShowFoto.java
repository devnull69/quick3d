package org.theiner.quick3d;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class ShowFoto extends Fragment {

    private String _filename;

    private ImageView showFotoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_show_foto, null);

        showFotoView = (ImageView) layout.findViewById(R.id.showFoto);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _filename = getArguments().getString("filename");

        File pictureFileDir = getDir();

        String fullPath = pictureFileDir.getPath() + File.separator + _filename + "_left.jpg";

        File pictureFile = new File(fullPath);

        if(pictureFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

            showFotoView.setImageBitmap(myBitmap);
        }

    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "Quick3D");
    }
}
