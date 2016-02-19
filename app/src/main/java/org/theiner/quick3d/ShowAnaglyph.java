package org.theiner.quick3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.theiner.quick3d.asynch.ImageData;
import org.theiner.quick3d.asynch.ImageSaver;
import org.theiner.quick3d.asynch.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class ShowAnaglyph extends Activity {

    private String _filename;
    private Bitmap zielBitmap;
    Q3DApplication myApp;

    ImageView ivClose;
    ImageView ivSwitch;
    ImageView ivTwoImages;
    ImageView ivWiggle;
    ImageView ivShare;
    ImageView ivSave;
    ImageView ivAnaglyph;

    private boolean isHalftone = false;

    private boolean showTools = true;

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

            // wait if Anagylph thread is not yet ready
            while(myApp.getAnaglyphBitmap() == null);

            zielBitmap = myApp.getAnaglyphBitmap();
            isHalftone = false;

            ivAnaglyph = (ImageView) findViewById(R.id.ivAnaglyph);
            ivAnaglyph.setImageBitmap(zielBitmap);

            ivClose = (ImageView) findViewById(R.id.ivClose);
            ivClose.setImageResource(R.drawable.icon_close);

            ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
            ivSwitch.setImageResource(R.drawable.icon_halftone);

            ivTwoImages = (ImageView) findViewById(R.id.ivTwoImages);
            ivTwoImages.setImageResource(R.drawable.icon_twoimages);

            ivWiggle = (ImageView) findViewById(R.id.ivWiggle);
            ivWiggle.setImageResource(R.drawable.icon_wiggle);

            ivShare = (ImageView) findViewById(R.id.ivShare);
            ivShare.setImageResource(R.drawable.icon_share);

            ivSave = (ImageView) findViewById(R.id.ivSave);
            ivSave.setImageResource(R.drawable.icon_save);
            if(myApp.getAnaglyphSaved())
                ivSave.setVisibility(View.INVISIBLE);

        } catch(Throwable e) {
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
        } catch(Throwable e) {
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
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    private void doSave(final boolean toBeShared) {
        ivSave.setVisibility(View.INVISIBLE);
        final ShowAnaglyph that = this;
        ImageSaver.SaveCompleteListener scl = new ImageSaver.SaveCompleteListener() {
            @Override
            public void onSaveComplete(String result) {
                Toast.makeText(that, getString(R.string.anaglyph_saved),
                        Toast.LENGTH_LONG).show();

                if(toBeShared) {
                    doShare();
                }
            }
        };

        ImageData imageData = new ImageData();

        imageData.setFileName(_filename);

        if(isHalftone) {
            imageData.setImageType(ImageType.HALFTONE);
        } else {
            imageData.setImageType(ImageType.ANAGLYPH);
        }

        imageData.setFirstBitmap(zielBitmap);
        ImageSaver imageSaver = new ImageSaver(myApp, scl);
        imageSaver.execute(imageData);
    }

    public void onSave(View view) {
        try {
            doSave(false);

        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    private void doShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("image/*");

        String dateiname = myApp.getAnaglyphFilename();
        if(isHalftone)
            dateiname = myApp.getHalftoneFilename();
        File file = new File(dateiname);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        startActivity(shareIntent);
    }

    private void openSharing() {
        if((!isHalftone && !myApp.getAnaglyphSaved()) || (isHalftone && !myApp.getHalftoneSaved())) {
            doSave(true);
        } else {
            doShare();
        }
    }

    public void onShare(View view) {
        openSharing();
    }

    public void onShowHide(View view) {
        showTools = !showTools;
        if(!showTools) {
            ivClose.setVisibility(View.INVISIBLE);
            ivSwitch.setVisibility(View.INVISIBLE);
            ivTwoImages.setVisibility(View.INVISIBLE);
            ivWiggle.setVisibility(View.INVISIBLE);
            ivShare.setVisibility(View.INVISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
        } else {
            ivClose.setVisibility(View.VISIBLE);
            ivSwitch.setVisibility(View.VISIBLE);
            ivTwoImages.setVisibility(View.VISIBLE);
            ivWiggle.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            if(!myApp.getAnaglyphSaved())
                ivSave.setVisibility(View.VISIBLE);
        }
    }

    public void onSwitch(View view) {
        try {

            isHalftone = !isHalftone;

            if(isHalftone) {
                // wait if Halftone thread is not yet ready
                while(myApp.getHalftoneBitmap() == null);
                zielBitmap = myApp.getHalftoneBitmap();
                ivAnaglyph.setImageBitmap(zielBitmap);
                if(myApp.getHalftoneSaved()) {
                    ivSave.setVisibility(View.INVISIBLE);
                } else {
                    ivSave.setVisibility(View.VISIBLE);
                }
            } else {
                zielBitmap = myApp.getAnaglyphBitmap();
                ivAnaglyph.setImageBitmap(zielBitmap);
                if(myApp.getAnaglyphSaved()) {
                    ivSave.setVisibility(View.INVISIBLE);
                } else {
                    ivSave.setVisibility(View.VISIBLE);
                }
            }

            myApp.appendTrace("ShowAnaglyph: Halftone/Anaglyph gewechselt\n");
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }
}
