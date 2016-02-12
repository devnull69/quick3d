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
                if(myApp.getCrossEyedSaved()) {
                    ivSave.setVisibility(View.INVISIBLE);
                } else {
                    ivSave.setVisibility(View.VISIBLE);
                }
            } else {
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

    public void onSave(View view) {
        try {
            ivSave.setVisibility(View.INVISIBLE);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();

            // Picture will be as high as the orignal width. Picture's width will be "scaleFactor" * width of original image
            size.x = (int) (myApp.getImageWidth() * ((float)myApp.getImageWidth() / myApp.getImageHeight()));
            size.y = myApp.getImageWidth();

            Bitmap zielBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);

            int black = Color.argb(255, 0, 0, 0);
            zielBitmap.eraseColor(black);

            Canvas canvas = new Canvas(zielBitmap);

            // Linkes Image soll links von der Mitte plaziert sein
            // Mitte der linken Hälfte = size.x / 4
            // Hälfte von imageSize.height weiter nach links = size.x/4 - imageSize.height/2
            int leftMargin = (int)Math.floor((double)size.x/4 - myApp.getImageHeight()/2);

            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawBitmap(firstBitmap, leftMargin, 0, paint);

            // Rechtes Images genauso weit von der Mitte nach rechts verschieben
            leftMargin = leftMargin + (int)Math.floor((double)size.x/2);
            canvas.drawBitmap(secondBitmap, leftMargin, 0, paint);

            String appendix = "_crosseyed.jpg";

            if (!isCrossEyed) {
                appendix = "_paralleleyed.jpg";
            }

            File pictureFileDir = Helper.getDir();
            pictureFileDir.mkdirs();
            String photoFile = pictureFileDir.getPath() + File.separator + _filename + appendix;

            File pictureFile = new File(photoFile);

            OutputStream fOutputStream = new FileOutputStream(pictureFile);

            zielBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            if (isCrossEyed) {
                myApp.setCrossEyedFilename(photoFile);
                myApp.setCrossEyedSaved(true);
            } else {
                myApp.setParallelEyedFilename(photoFile);
                myApp.setParallelEyedSaved(true);
            }

            Toast.makeText(this, getString(R.string.foto_saved),
                    Toast.LENGTH_LONG).show();
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }

    }

    private void openSharing() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("image/*");

        if(isCrossEyed && !myApp.getCrossEyedSaved() || (!isCrossEyed && !myApp.getParallelEyedSaved()))
            onSave(findViewById(R.id.ivSave));

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
        } else {
            ivClose.setVisibility(View.VISIBLE);
            ivSwitch.setVisibility(View.VISIBLE);
            ivAnaglyph.setVisibility(View.VISIBLE);
            ivWiggle.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            ivSave.setVisibility(View.VISIBLE);
        }
    }

}
