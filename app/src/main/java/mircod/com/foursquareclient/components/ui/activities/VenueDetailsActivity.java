package mircod.com.foursquareclient.components.ui.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.App;
import mircod.com.foursquareclient.components.loaders.BitmapLoader;
import mircod.com.foursquareclient.mvp.contracts.VenueDetailsContract;
import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.mvp.models.daos.Photo;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.repositories.DetailsRepository;
import mircod.com.foursquareclient.mvp.presenters.VenueDetailsPresenter;

public class VenueDetailsActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener, VenueDetailsContract.VenueDetailsView,
        android.support.v4.app.LoaderManager.LoaderCallbacks<Drawable> {

    private ImageSwitcher isPictures;
    private String mVenueId;
    private float x1,x2;
    private static int MIN_DISTANCE = 150;
    private int position=0;
    private List<String> photoUrls;
    private VenueDetailsContract.VenuePresenter mPresenter;
    private String urlToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        mVenueId = getIntent().getStringExtra(MainActivity.VENUE_ID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isPictures =(ImageSwitcher) findViewById(R.id.switcherPhotosDetails);
        isPictures.setOnClickListener(this);
        isPictures.setOnTouchListener(this);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPinDetails);
        fab.setOnClickListener(this);
        FloatingActionButton fabDelete =
                (FloatingActionButton)findViewById(R.id.fabDeleteFromCacheDetails);
        fabDelete.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(0,null,this);

        DaoSession daoSession = ((App)getApplication()).getDaoSession();
        mPresenter = new VenueDetailsPresenter(this,new DetailsRepository(daoSession));
        mPresenter.getDetails(mVenueId);


    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabPinDetails:
                Snackbar.make(view, "opening the map", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.fabDeleteFromCacheDetails :
                Snackbar.make(view, "deleting from cache", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.switcherPhotosDetails:
                Intent intent = new Intent(this, PhotoFullscreenActivity.class);

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this,isPictures,"photos");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, optionsCompat.toBundle());
                }else {
                    startActivity(intent);

                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId()==R.id.switcherPhotosDetails)
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    x1 = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = motionEvent.getX();
                    float deltaX = x2 -x1;
                    if (Math.abs(deltaX)> MIN_DISTANCE){
                       if (x2 > x1)  switchToPreviousPhoto();
                       else switchToNextPhoto();
                    }
            }
        return false;
    }


    private void switchToNextPhoto(){
        position++;
        if (photoUrls.size()>0 && position<photoUrls.size()-1) {
            urlToLoad = photoUrls.get(position);
//            getSupportLoaderManager().initLoader(0,null,this).forceLoad();
            isPictures.setImageURI(Uri.parse(urlToLoad));

        }
    }

    private void switchToPreviousPhoto(){
        position--;
        if (photoUrls.size()>0 && position>=1){
            urlToLoad = photoUrls.get(position);
//            getSupportLoaderManager().initLoader(0,null,this).forceLoad();
            isPictures.setImageURI(Uri.parse(urlToLoad));
        }
    }


    @Override
    public void onError(int errorCode) {

    }

    @Override
    public void venueDeleted(boolean isDeleted) {

    }

    @Override
    public void detailsLoaded(Venue venue, List<Photo> photos) {
        photoUrls = new ArrayList<>();
        if (photos.size()>0){
            for (Photo photo : photos){
                photoUrls.add(photo.getUri());
            }
            isPictures.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(
                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT));
                    return imageView;
                }
            });
            urlToLoad = photoUrls.get(0);
//            getSupportLoaderManager().initLoader(0,null,this).forceLoad();
            isPictures.setImageURI(Uri.parse(urlToLoad));
        }



    }

    @Override
    public Loader<Drawable> onCreateLoader(int id, Bundle args) {
        return new BitmapLoader(this,urlToLoad);
    }

    @Override
    public void onLoadFinished(Loader<Drawable> loader, Drawable data) {
        isPictures.setImageDrawable(data);
    }

    @Override
    public void onLoaderReset(Loader<Drawable> loader) {

    }


}
