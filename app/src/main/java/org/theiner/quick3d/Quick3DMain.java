package org.theiner.quick3d;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Quick3DMain extends Activity {

    public final static String DEBUG_TAG = "Quick3DMain";
    public final static String FILENAME_MESSAGE = "org.theiner.quick3d.filename";
    public boolean isLandscape = false;
    private String filename = "";
    private LeftEyePhoto lep;
    private RightEyePhoto rep;
    private Fragment currentFragment;
    Q3DApplication myApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            myApp = ((Q3DApplication) this.getApplicationContext());
            myApp.setTrace("");

            setContentView(R.layout.activity_quick3dmain);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = getWindow().getDecorView();

                int uiOptions = decorView.getSystemUiVisibility();
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }

            myApp.appendTrace("Quick3DMain: Content View gesetzt\n");

            lep = new LeftEyePhoto();
            rep = new RightEyePhoto();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.flFragmentContainer, lep);
            ft.commit();

            currentFragment = lep;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
            String date = dateFormat.format(new Date());
            filename = "Picture_" + date;

            myApp.appendTrace("Quick3DMain: Fragments instantiiert\n");
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onClick(View view) {
        try {
            myApp.appendTrace("Quick3DMain: Photo geklickt\n");
            if (currentFragment instanceof LeftEyePhoto) {
                ((LeftEyePhoto) currentFragment).onClick(view, filename);
            } else if (currentFragment instanceof RightEyePhoto) {
                ((RightEyePhoto) currentFragment).onClick(view, filename);
            }
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

    public void callbackAfterPictureSaved() {
        try {
            myApp.appendTrace("Quick3DMain: Nach Photo speichern\n");
            if (currentFragment instanceof LeftEyePhoto) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.flFragmentContainer, rep);
                currentFragment = rep;
                ft.commit();
                myApp.appendTrace("Quick3DMain: Wechseln auf RightEyePhoto\n");
            } else if (currentFragment instanceof RightEyePhoto) {
                Intent intent = new Intent(this, ShowFotos.class);
                intent.putExtra(FILENAME_MESSAGE, filename);
                startActivity(intent);
                myApp.appendTrace("Quick3DMain: Wechseln auf ShowFotos. Finish\n");
                finish();
            }
        } catch(Throwable e) {
            StackTraceElement se = e.getStackTrace()[0];
            myApp.prependTrace(e.toString() + "\n" + se.getClassName() + ":" + se.getLineNumber() + "\n\n");
            Helper.showTraceDialog(myApp, this);
        }
    }

}
