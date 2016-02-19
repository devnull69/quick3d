package org.theiner.quick3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.theiner.quick3d.asynch.ImageData;
import org.theiner.quick3d.asynch.ImageSaver;
import org.theiner.quick3d.asynch.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class ShowFotos extends Activity {

    private String _filename;
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private ImageView ivLeft;
    private ImageView ivRight;

    private ImageView ivClose;
    private ImageView ivSwitch;
    private ImageView ivAnaglyph;
    private ImageView ivWiggle;
    private ImageView ivShare;
    private ImageView ivSave;
    private TextView tvCurrent;

    Q3DApplication myApp;
    private Boolean isCrossEyed = true;

    private boolean showTools = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myApp = ((Q3DApplication)this.getApplicationContext());
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_fotos);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = getWindow().getDecorView();

                int uiOptions = decorView.getSystemUiVisibility();
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }

            myApp.appendTrace("ShowFotos: Super Konstruktor aufgerufen, Content View gesetzt.\n");

            Intent intent = getIntent();
            _filename = intent.getStringExtra(Quick3DMain.FILENAME_MESSAGE);

            myApp.appendTrace("ShowFotos: Intent erhalten. Filename: " + _filename + "\n");

            ivLeft = (ImageView) findViewById(R.id.ivLeft);
            ivRight = (ImageView) findViewById(R.id.ivRight);
            ivClose = (ImageView) findViewById(R.id.ivClose);
            ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
            ivAnaglyph = (ImageView) findViewById(R.id.ivAnaglyph);
            ivWiggle = (ImageView) findViewById(R.id.ivWiggle);
            ivShare = (ImageView) findViewById(R.id.ivShare);
            ivSave = (ImageView) findViewById(R.id.ivSave);
            tvCurrent = (TextView) findViewById(R.id.tvCurrent);

            myApp.appendTrace("ShowFotos: Image Views gefunden.\n");

            firstBitmap = myApp.getRightEyeBitmap();
            ivLeft.setImageBitmap(firstBitmap);

            myApp.appendTrace("ShowFotos: Rechtes Foto eingebunden.\n");

            secondBitmap = myApp.getLeftEyeBitmap();
            ivRight.setImageBitmap(secondBitmap);

            myApp.appendTrace("ShowFotos: Linkes Foto eingebunden.\n");

            ivClose.setImageResource(R.drawable.icon_close);
            ivSwitch.setImageResource(R.drawable.icon_switch);
            ivAnaglyph.setImageResource(R.drawable.icon_anaglyph);
            ivWiggle.setImageResource(R.drawable.icon_wiggle);
            ivShare.setImageResource(R.drawable.icon_share);
            ivSave.setImageResource(R.drawable.icon_save);

            tvCurrent.setText(R.string.crosseyed);

            if(myApp.getCrossEyedSaved()) {
                // Save Button verstecken
                ivSave.setVisibility(View.INVISIBLE);
            }

            myApp.appendTrace("ShowFotos: Image Resourcen gesetzt.\n");

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;
    }

    public void onClose(View view) {
        System.exit(0);
    }

    public void onSwitch(View view) {
        try {
            Bitmap help = firstBitmap;
            firstBitmap = secondBitmap;
            secondBitmap = help;
            ivLeft.setImageBitmap(firstBitmap);
            ivRight.setImageBitmap(secondBitmap);

            isCrossEyed = !isCrossEyed;

            if(isCrossEyed) {
                tvCurrent.setText(R.string.crosseyed);
                if(myApp.getCrossEyedSaved()) {
                    ivSave.setVisibility(View.INVISIBLE);
                } else {
                    ivSave.setVisibility(View.VISIBLE);
                }
            } else {
                tvCurrent.setText(R.string.paralleleyed);
                if(myApp.getParallelEyedSaved()) {
                    ivSave.setVisibility(View.INVISIBLE);
                } else {
                    ivSave.setVisibility(View.VISIBLE);
                }
            }

            myApp.appendTrace("ShowFotos: Seiten gewechselt\n");
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    public void onAnaglyph(View view) {
        try {
            Intent intent = new Intent(this, ShowAnaglyph.class);
            intent.putExtra(Quick3DMain.FILENAME_MESSAGE, _filename);
            startActivity(intent);
            myApp.appendTrace("ShowFotos: Auf Anaglyph gewechselt. Finish\n");
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
            myApp.appendTrace("ShowFotos: Auf Wiggle gewechselt. Finish\n");
            finish();
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    private void doSave(final boolean toBeShared) {
        ivSave.setVisibility(View.INVISIBLE);
        final ShowFotos that = this;
        ImageSaver.SaveCompleteListener scl = new ImageSaver.SaveCompleteListener() {
            @Override
            public void onSaveComplete(String result) {
                Toast.makeText(that, getString(R.string.foto_saved),
                        Toast.LENGTH_LONG).show();

                if(toBeShared) {
                    doShare();
                }
            }
        };

        ImageData imageData = new ImageData();
        if(isCrossEyed)
            imageData.setImageType(ImageType.CROSSEYED);
        else
            imageData.setImageType(ImageType.PARALLELEYED);

        imageData.setFileName(_filename);
        imageData.setFirstBitmap(firstBitmap);
        imageData.setSecondBitmap(secondBitmap);

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

        File file;
        if(isCrossEyed) {
            file = new File(myApp.getCrossEyedFilename());
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        } else {
            file = new File(myApp.getParallelEyedFilename());
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        startActivity(shareIntent);
    }

    private void openSharing() {
        if(isCrossEyed && !myApp.getCrossEyedSaved() || (!isCrossEyed && !myApp.getParallelEyedSaved())) {
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
            ivAnaglyph.setVisibility(View.INVISIBLE);
            ivWiggle.setVisibility(View.INVISIBLE);
            ivShare.setVisibility(View.INVISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
            tvCurrent.setVisibility(View.INVISIBLE);
        } else {
            ivClose.setVisibility(View.VISIBLE);
            ivSwitch.setVisibility(View.VISIBLE);
            ivAnaglyph.setVisibility(View.VISIBLE);
            ivWiggle.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            if((isCrossEyed && !myApp.getCrossEyedSaved()) || (!isCrossEyed && !myApp.getParallelEyedSaved()))
                ivSave.setVisibility(View.VISIBLE);
            tvCurrent.setVisibility(View.VISIBLE);
        }
    }

}
