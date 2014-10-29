package org.theiner.quick3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class ShowFotos extends Activity {

    private String _filename;
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private ImageView ivLeft;
    private ImageView ivRight;
    Q3DApplication myApp;

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
            ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
            ImageView ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
            ImageView ivAnaglyph = (ImageView) findViewById(R.id.ivAnaglyph);
            ImageView ivWiggle = (ImageView) findViewById(R.id.ivWiggle);

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
}
