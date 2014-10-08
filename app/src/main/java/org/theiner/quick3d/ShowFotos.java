package org.theiner.quick3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_fotos);

        Intent intent = getIntent();
        _filename = intent.getStringExtra(Quick3DMain.FILENAME_MESSAGE);

        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
        ImageView ivSwitch = (ImageView) findViewById(R.id.ivSwitch);

        File pictureFileDir = Helper.getDir();

        String fullPath = pictureFileDir.getPath() + File.separator + _filename + "_right.jpg";

        File pictureFile = new File(fullPath);

        if(pictureFile.exists()) {
            firstBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

            ivLeft.setImageBitmap(firstBitmap);
        }

        fullPath = pictureFileDir.getPath() + File.separator + _filename + "_left.jpg";

        pictureFile = new File(fullPath);

        if(pictureFile.exists()) {
            secondBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

            ivRight.setImageBitmap(secondBitmap);
        }

        ivClose.setImageResource(R.drawable.icon_close);
        ivSwitch.setImageResource(R.drawable.icon_switch);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_fotos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClose(View view) {
        System.exit(0);
    }

    public void onSwitch(View view) {
        Bitmap help = firstBitmap;
        firstBitmap = secondBitmap;
        secondBitmap = help;
        ivLeft.setImageBitmap(firstBitmap);
        ivRight.setImageBitmap(secondBitmap);
    }
}
