package mircod.com.foursquareclient.uitls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by guedi on 8/19/2017.
 */

public class StorageHandler {
    private static final String TAG = StorageHandler.class.getSimpleName();
    private static final String APP_NAME = "foursquareclient";
    public static final Uri APP_FOLDER = createPictureFolder();

    private static Uri createPictureFolder(){
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),APP_NAME);
        if (!file.mkdirs() ){
            if (file.exists()) {
                Log.d(TAG, "directory already exist");
                return Uri.fromFile(file);
            }else return null;

        }else {
            return  Uri.fromFile(file);
        }
    }

    public static Uri convertsrcUrlToLocalUri(String srcUrl, String venueId){

        String filePath = APP_FOLDER.getPath() + "/"+ venueId;
        File dir = new File(filePath);
            if (!dir.exists())
                dir.mkdirs();
        File file = new File(dir,srcUrl.substring(srcUrl.length()-10,srcUrl.length()-5)+".jpeg");
        try {
            FileOutputStream fout = new FileOutputStream(file);
            URL url = new URL(srcUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap.compress(Bitmap.CompressFormat.JPEG,85,fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG,e.getMessage());
        } catch (MalformedURLException e) {
            Log.d(TAG,e.getMessage());
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }

        return Uri.fromFile(file);

    }






}
