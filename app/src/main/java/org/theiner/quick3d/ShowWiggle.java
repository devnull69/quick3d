package org.theiner.quick3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class ShowWiggle extends Activity {

    private String _filename;
    private Bitmap rightBitmap;
    private Bitmap leftBitmap;
    ShowWiggle me;
    Timer myTimer;
    Q3DApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myApp = ((Q3DApplication)this.getApplicationContext());
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_wiggle);

            Intent intent = getIntent();
            _filename = intent.getStringExtra(Quick3DMain.FILENAME_MESSAGE);

            File pictureFileDir = Helper.getDir();

            String fullPath = pictureFileDir.getPath() + File.separator + _filename + "_right.jpg";

            File pictureFile = new File(fullPath);

            if (pictureFile.exists()) {
                rightBitmap = Helper.getRotatedBitmap(pictureFile);
            }

            fullPath = pictureFileDir.getPath() + File.separator + _filename + "_left.jpg";

            pictureFile = new File(fullPath);

            if (pictureFile.exists()) {
                leftBitmap = Helper.getRotatedBitmap(pictureFile);
            }

            ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
            ivClose.setImageResource(R.drawable.icon_close);

            ImageView ivTwoImages = (ImageView) findViewById(R.id.ivTwoImages);
            ivTwoImages.setImageResource(R.drawable.icon_twoimages);

            ImageView ivAnaglyph = (ImageView) findViewById(R.id.ivAnaglyph);
            ivAnaglyph.setImageResource(R.drawable.icon_anaglyph);

            me = this;

            myTimer = new Timer();
            TimerTask myTimerTask = new TimerTask() {

                private boolean isLeft = false;

                @Override
                public void run() {
                    me.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                ImageView ivWiggle = (ImageView) findViewById(R.id.ivWiggle);
                                if (isLeft) {
                                    ivWiggle.setImageBitmap(rightBitmap);
                                    isLeft = false;
                                } else {
                                    ivWiggle.setImageBitmap(leftBitmap);
                                    isLeft = true;
                                }
                            } catch(Exception e) {
                                StackTraceElement se = e.getStackTrace()[0];
                                myApp.prependTrace(e.getMessage() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
                                Helper.showTraceDialog(myApp, me);
                            }
                        }
                    });
                }
            };

            myTimer.scheduleAtFixedRate(myTimerTask, 0, 150);
        } catch (Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.getMessage() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void onClose(View view) {
        myTimer.cancel();
        myTimer.purge();
        System.exit(0);
    }

    public void onTwoImages(View view) {
        try {
            myTimer.cancel();
            myTimer.purge();
            Intent intent = new Intent(this, ShowFotos.class);
            intent.putExtra(Quick3DMain.FILENAME_MESSAGE, _filename);
            startActivity(intent);
            finish();
        } catch(Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.getMessage() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    public void onAnaglyph(View view) {
        try {
            myTimer.cancel();
            myTimer.purge();
            Intent intent = new Intent(this, ShowAnaglyph.class);
            intent.putExtra(Quick3DMain.FILENAME_MESSAGE, _filename);
            startActivity(intent);
            finish();
        } catch(Exception e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.getMessage() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }
}
