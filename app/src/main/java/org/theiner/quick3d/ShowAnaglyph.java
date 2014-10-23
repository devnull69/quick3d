package org.theiner.quick3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class ShowAnaglyph extends Activity {

    private String _filename;
    private Bitmap zielBitmap;
    private Bitmap rotBitmap;
    Q3DApplication myApp;
    ImageView ivSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myApp = ((Q3DApplication)this.getApplicationContext());
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_anaglyph);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = getWindow().getDecorView();

                int uiOptions = decorView.getSystemUiVisibility();
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }

            Intent intent = getIntent();
            _filename = intent.getStringExtra(Quick3DMain.FILENAME_MESSAGE);

            File pictureFileDir = Helper.getDir();

            String fullPath = pictureFileDir.getPath() + File.separator + _filename + "_right.jpg";

            File pictureFile = new File(fullPath);

            if (pictureFile.exists()) {
                zielBitmap = Helper.getRotatedBitmap(pictureFile);
                zielBitmap = zielBitmap.copy(zielBitmap.getConfig(), true);
            }

            fullPath = pictureFileDir.getPath() + File.separator + _filename + "_left.jpg";

            pictureFile = new File(fullPath);

            if (pictureFile.exists()) {
                rotBitmap = Helper.getRotatedBitmap(pictureFile);
            }

            int imgWidth = zielBitmap.getWidth();
            int imgHeight = zielBitmap.getHeight();

            int[] zielpixels = new int[imgHeight * imgWidth];
            int[] redpixels = new int[imgHeight * imgWidth];
            zielBitmap.getPixels(zielpixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
            rotBitmap.getPixels(redpixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

            for (int i = 0; i < imgHeight * imgWidth; i++) {
                try {
                    zielpixels[i] = Color.argb(Color.alpha(zielpixels[i]), Color.red(redpixels[i]), Color.green(zielpixels[i]), Color.blue(zielpixels[i]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            zielBitmap.setPixels(zielpixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

            ImageView ivAnaglyph = (ImageView) findViewById(R.id.ivAnaglyph);
            ivAnaglyph.setImageBitmap(zielBitmap);

            ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
            ivClose.setImageResource(R.drawable.icon_close);

            ImageView ivTwoImages = (ImageView) findViewById(R.id.ivTwoImages);
            ivTwoImages.setImageResource(R.drawable.icon_twoimages);

            ImageView ivWiggle = (ImageView) findViewById(R.id.ivWiggle);
            ivWiggle.setImageResource(R.drawable.icon_wiggle);

            if(!myApp.getAnaglyphSaved()) {
                ivSave = (ImageView) findViewById(R.id.ivSave);
                ivSave.setImageResource(R.drawable.icon_save);
            }

        } catch(Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void onClose(View view) {
        System.exit(0);
    }

    public void onTwoImages(View view) {
        try {
            Intent intent = new Intent(this, ShowFotos.class);
            intent.putExtra(Quick3DMain.FILENAME_MESSAGE, _filename);
            startActivity(intent);
            finish();
        } catch(Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    public void onWiggle(View view) {
        try {
            Intent intent = new Intent(this, ShowWiggle.class);
            intent.putExtra(Quick3DMain.FILENAME_MESSAGE, _filename);
            startActivity(intent);
            finish();
        } catch(Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    public void onSave(View view) {
        try {
            File pictureFileDir = Helper.getDir();
            String photoFile = pictureFileDir.getPath() + File.separator + _filename + "_anaglyph.jpg";

            File pictureFile = new File(photoFile);

            OutputStream fOutputStream = new FileOutputStream(pictureFile);

            zielBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            ivSave.setVisibility(View.INVISIBLE);

            myApp.setAnaglyphSaved(true);

            Toast.makeText(this, getString(R.string.anaglyph_saved),
                    Toast.LENGTH_LONG).show();

        } catch(Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }
}
