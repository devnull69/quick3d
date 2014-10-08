package org.theiner.quick3d;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Quick3DMain extends Activity {

    public final static String DEBUG_TAG = "Quick3DMain";
    public final static String FILENAME_MESSAGE = "org.theiner.quick3d.filename";
    private String filename = "";
    private LeftEyePhoto lep;
    private RightEyePhoto rep;
    private Fragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick3dmain);

        lep = new LeftEyePhoto();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.flFragmentContainer, lep);
        ft.commit();

        currentFragment = lep;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
        String date = dateFormat.format(new Date());
        filename = "Picture_" + date;

        Bundle showFotoParameter = new Bundle();
        showFotoParameter.putString("filename", filename);

        rep = (RightEyePhoto) Fragment.instantiate(this, RightEyePhoto.class.getName(), showFotoParameter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_options, menu);
        return true;
    }



    public void exitApp(MenuItem item) {
        System.exit(0);
    }

    public void onClick(View view) {
        if(currentFragment instanceof LeftEyePhoto) {
            ((LeftEyePhoto) currentFragment).onClick(view, filename);
        } else if(currentFragment instanceof RightEyePhoto){
            ((RightEyePhoto) currentFragment).onClick(view, filename);
        }
    }

    public void callbackAfterPictureSaved() {
        if(currentFragment instanceof LeftEyePhoto) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.flFragmentContainer, rep);
            currentFragment = rep;
            ft.commit();
        } else if(currentFragment instanceof RightEyePhoto){
            Intent intent = new Intent(this, ShowFotos.class);
            intent.putExtra(FILENAME_MESSAGE, filename);
            startActivity(intent);
            finish();
        }
    }


}
