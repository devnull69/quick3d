package org.theiner.quick3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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


public class ShowCardboard extends Activity {

    private String _filename;
    private Bitmap cardboardBitmap;
    private ImageView ivImage;

    private ImageView ivClose;
    private ImageView ivTwoImages;
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
            setContentView(R.layout.activity_show_cardboard);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = getWindow().getDecorView();

                int uiOptions = decorView.getSystemUiVisibility();
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }

            myApp.appendTrace("ShowCardboard: Super Konstruktor aufgerufen, Content View gesetzt.\n");

            Intent intent = getIntent();
            _filename = intent.getStringExtra(Quick3DMain.FILENAME_MESSAGE);

            myApp.appendTrace("ShowCardboard: Intent erhalten. Filename: " + _filename + "\n");

            // wait if Cardboard thread is not yet ready
            while(myApp.getCardboardBitmap() == null);

            ivImage = (ImageView) findViewById(R.id.ivImage);
            ivClose = (ImageView) findViewById(R.id.ivClose);
            ivTwoImages = (ImageView) findViewById(R.id.ivTwoImages);
            ivAnaglyph = (ImageView) findViewById(R.id.ivAnaglyph);
            ivWiggle = (ImageView) findViewById(R.id.ivWiggle);
            ivShare = (ImageView) findViewById(R.id.ivShare);
            ivSave = (ImageView) findViewById(R.id.ivSave);

            myApp.appendTrace("ShowCardboard: Image Views gefunden.\n");

            cardboardBitmap = myApp.getCardboardBitmap();
            ivImage.setImageBitmap(cardboardBitmap);

            myApp.appendTrace("ShowCardboard: Bild eingebunden.\n");


            ivClose.setImageResource(R.drawable.icon_close);
            ivTwoImages.setImageResource(R.drawable.icon_twoimages);
            ivAnaglyph.setImageResource(R.drawable.icon_anaglyph);
            ivWiggle.setImageResource(R.drawable.icon_wiggle);
            ivShare.setImageResource(R.drawable.icon_share);
            ivSave.setImageResource(R.drawable.icon_save);

            if(myApp.getCardboardSaved()) {
                // Save Button verstecken
                ivSave.setVisibility(View.INVISIBLE);
            }

            myApp.appendTrace("ShowCardboard: Image Resourcen gesetzt.\n");

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
        Helper.showMessageOnClose(this);
    }

    public void onTwoImages(View view) {
        try {
            Intent intent = new Intent(this, ShowFotos.class);
            intent.putExtra(Quick3DMain.FILENAME_MESSAGE, _filename);
            startActivity(intent);
            myApp.appendTrace("ShowCardboard: Auf zwei Images gewechselt. Finish\n");
            finish();
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
        final ShowCardboard that = this;
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
        imageData.setImageType(ImageType.CARDBOARD);

        imageData.setFileName(_filename);
        imageData.setFirstBitmap(cardboardBitmap);

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
        file = new File(myApp.getCardboardFilename());
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(shareIntent);
    }

    private void openSharing() {
        if(!myApp.getCardboardSaved()) {
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
            ivTwoImages.setVisibility(View.INVISIBLE);
            ivAnaglyph.setVisibility(View.INVISIBLE);
            ivWiggle.setVisibility(View.INVISIBLE);
            ivShare.setVisibility(View.INVISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
        } else {
            ivClose.setVisibility(View.VISIBLE);
            ivTwoImages.setVisibility(View.VISIBLE);
            ivAnaglyph.setVisibility(View.VISIBLE);
            ivWiggle.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            if(!myApp.getCardboardSaved())
                ivSave.setVisibility(View.VISIBLE);
        }
    }

}
