package org.theiner.quick3d.asynch;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Display;
import android.view.View;

import org.theiner.quick3d.Helper;
import org.theiner.quick3d.Q3DApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by TTheiner on 16.02.2016.
 */
public class ImageSaver extends AsyncTask<ImageData, Void, String> {

    public static interface SaveCompleteListener {
        void onSaveComplete(String result);
    }

    private Q3DApplication myApp;
    private SaveCompleteListener sc = null;

    public ImageSaver(Application context, SaveCompleteListener sc) {
        this.sc = sc;
        myApp = (Q3DApplication) context;
    }

    @Override
    protected String doInBackground(ImageData... imageData) {

        ImageData myImageData = imageData[0];
        if(myImageData.getImageType() == ImageType.CARDBOARD) {
            File pictureFileDir = Helper.getDir();
            pictureFileDir.mkdirs();
            String appendix = "_cardboard.jpg";
            String photoFile = pictureFileDir.getPath() + File.separator + myImageData.getFileName() + appendix;

            File pictureFile = new File(photoFile);

            OutputStream fOutputStream = null;
            try {
                fOutputStream = new FileOutputStream(pictureFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            myImageData.getFirstBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            try {
                fOutputStream.flush();
                fOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            myApp.setCardboardFilename(photoFile);
            myApp.setCardboardSaved(true);
        } else if(myImageData.getImageType() == ImageType.ANAGLYPH || myImageData.getImageType() == ImageType.HALFTONE) {
            File pictureFileDir = Helper.getDir();
            pictureFileDir.mkdirs();
            String appendix = "_anaglyph.jpg";
            if(myImageData.getImageType() == ImageType.HALFTONE)
                appendix = "_halftone.jpg";
            String photoFile = pictureFileDir.getPath() + File.separator + myImageData.getFileName() + appendix;

            File pictureFile = new File(photoFile);

            OutputStream fOutputStream = null;
            try {
                fOutputStream = new FileOutputStream(pictureFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            myImageData.getFirstBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            try {
                fOutputStream.flush();
                fOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (myImageData.getImageType() == ImageType.ANAGLYPH) {
                myApp.setAnaglyphFilename(photoFile);
                myApp.setAnaglyphSaved(true);
            } else {
                myApp.setHalftoneFilename(photoFile);
                myApp.setHalftoneSaved(true);
            }
        } else {
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
            canvas.drawBitmap(imageData[0].getFirstBitmap(), leftMargin, 0, paint);

            // Rechtes Images genauso weit von der Mitte nach rechts verschieben
            leftMargin = leftMargin + (int)Math.floor((double)size.x/2);
            canvas.drawBitmap(imageData[0].getSecondBitmap(), leftMargin, 0, paint);

            String appendix = "_crosseyed.jpg";

            if (imageData[0].getImageType() == ImageType.PARALLELEYED) {
                appendix = "_paralleleyed.jpg";
            }

            File pictureFileDir = Helper.getDir();
            pictureFileDir.mkdirs();
            String photoFile = pictureFileDir.getPath() + File.separator + imageData[0].getFileName() + appendix;

            File pictureFile = new File(photoFile);

            OutputStream fOutputStream = null;
            try {
                fOutputStream = new FileOutputStream(pictureFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            zielBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            try {
                fOutputStream.flush();
                fOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageData[0].getImageType() == ImageType.CROSSEYED) {
                myApp.setCrossEyedFilename(photoFile);
                myApp.setCrossEyedSaved(true);
            } else {
                myApp.setParallelEyedFilename(photoFile);
                myApp.setParallelEyedSaved(true);
            }

        }
        return "successful";
    }

    @Override
    protected void onPostExecute(String result) {
        sc.onSaveComplete(result);
    }
}
