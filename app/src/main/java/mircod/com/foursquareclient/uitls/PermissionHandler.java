package mircod.com.foursquareclient.uitls;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by guedi on 8/19/2017.
 */

public class PermissionHandler {


    public static final int STORAGE_AND_LOCATION_PERMISSION_REQUEST = 246;
//    public static final String LOACTION_PERMISSION = "354";
    private Activity mActivity;


    public PermissionHandler(Activity actovity) {
        this.mActivity = actovity;
    }

    public boolean shoudlHandleStorageAndLocationPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!checkPermission()){
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE )){

                    showExplanation("we need to cache some data for offline use ");
                }else if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION )){

                    showExplanation("we need to know your location to show you " +
                            "places that are close to you ");
                }else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            STORAGE_AND_LOCATION_PERMISSION_REQUEST);
                }
                return true;
            }
        }

        return false;
    }


    private boolean checkPermission(){
        return ((ContextCompat.checkSelfPermission(mActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED));
    }





    private void showExplanation(String message){
        Toast.makeText(mActivity,message,Toast.LENGTH_LONG);
    }
}
