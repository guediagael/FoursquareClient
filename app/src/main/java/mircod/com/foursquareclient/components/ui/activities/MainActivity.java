package mircod.com.foursquareclient.components.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.uitls.PermissionHandler;
import mircod.com.foursquareclient.components.ui.fragments.VenuesListFragment;
import mircod.com.foursquareclient.uitls.StorageHandler;

public class MainActivity extends BaseActivity implements
        VenuesListFragment.OnFragmentInteractionListener {

    private double mLat, mLong;
    public static final String VENUE_ID = "venueId";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    private boolean allPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private PermissionHandler mPermissionHandler;
    private LocationRequest mLocationRequest;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        mLat = Double.valueOf(sharedPreferences.getString(LATITUDE_KEY,"0"));
        mLong = Double.valueOf(sharedPreferences.getString(LONGITUDE_KEY,"0"));

        if (mLat==0 && mLong==0){
            mLat = 55.7983556;
            mLong = 49.1064488;

        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        mPermissionHandler = new PermissionHandler(this);
        if (!mPermissionHandler.shoudlHandleStorageAndLocationPermissions()) {
            allPermissionsGranted= true;
            getLocation();
            if (StorageHandler.APP_FOLDER ==null)
                throw  new RuntimeException("not storage ");
            loadFragment();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.itemClearCache){
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("main");
            if (fragment!=null && fragment.isVisible())
                ((VenuesListFragment)fragment).clearCache();
        }else if (item.getItemId()==R.id.itemRefresh) refreshFragment();
        return super.onOptionsItemSelected(item);
    }





    @Override
    public void openDetailsActivity(String venueId) {
        Intent intent = new Intent(this,VenueDetailsActivity.class);
        intent.putExtra(VENUE_ID,venueId);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PermissionHandler.STORAGE_AND_LOCATION_PERMISSION_REQUEST){
            if (grantResults.length >= 2  && grantResults[0]== PackageManager.PERMISSION_GRANTED
                    && grantResults[1] ==PackageManager.PERMISSION_GRANTED){

                allPermissionsGranted = true;
                getLocation();
                if (StorageHandler.APP_FOLDER ==null)
                    throw  new RuntimeException("no storage ");
                loadFragment();
            }else {
                showErrorMessage("Please go and grant permissions and come back here",
                        findViewById(R.id.coordinatorMain));
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location!=null){
                            mLat = location.getLatitude();
                            mLong = location.getLongitude();
                            fab.setVisibility(View.GONE);
                            saveToSharedPreference(mLat,mLong);
                        }else {
                            Log.d(MainActivity.class.getSimpleName()," last location exception : null");
                            requestLocation();
                            showErrorMessage(getString(R.string.msg_location_not_available),
                                    findViewById(R.id.coordinatorMain));
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }

    private void requestLocation(){
        mLocationRequest  = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10*1000)
                .setFastestInterval(1 * 1000);
    }

    private void saveToSharedPreference(double lat, double lng){
        sharedPreferences.edit().putString(LATITUDE_KEY,String.valueOf(lat)).apply();
        sharedPreferences.edit().putString(LONGITUDE_KEY,String.valueOf(lng)).apply();
    }
    private void loadFragment(){
        if (allPermissionsGranted){
            Fragment fragment = VenuesListFragment.newInstance(mLat,mLong);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_main,fragment,"main").commit();
        }else {
            mPermissionHandler.shoudlHandleStorageAndLocationPermissions();
        }

    }

    private void refreshFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("main");
        if (fragment!=null && fragment.isVisible()){
            ((VenuesListFragment)fragment).onRefresh();
        }
    }
}
