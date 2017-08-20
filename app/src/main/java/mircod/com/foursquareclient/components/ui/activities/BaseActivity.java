package mircod.com.foursquareclient.components.ui.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by guedi on 8/17/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public  void showErrorMessage(String message, View container){
        Snackbar.make(container,message,Snackbar.LENGTH_LONG).show();
    };
}
