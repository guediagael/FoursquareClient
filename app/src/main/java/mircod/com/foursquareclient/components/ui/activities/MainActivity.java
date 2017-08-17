package mircod.com.foursquareclient.components.ui.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.ui.fragments.VenuesListFragment;

public class MainActivity extends BaseActivity implements
        VenuesListFragment.OnFragmentInteractionListener {

    private double mLat, mLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLat = 55.75316381092981;
        mLong = 48.74170753597821;

        Fragment fragment = VenuesListFragment.newInstance(mLat,mLong);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main,fragment).commit();
    }

    @Override
    public void showErrorMessage(String message) {

    }

    @Override
    public void openDetailsActivity(String venueId) {

    }
}
