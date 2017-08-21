package mircod.com.foursquareclient.components.ui.activities;


import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.App;
import mircod.com.foursquareclient.components.ui.adapters.ImageViewAdapter;
import mircod.com.foursquareclient.mvp.contracts.VenueDetailsContract;
import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.mvp.models.daos.Photo;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.repositories.DetailsRepository;
import mircod.com.foursquareclient.mvp.presenters.VenueDetailsPresenter;

public class VenueDetailsActivity extends BaseActivity implements View.OnClickListener,
        VenueDetailsContract.VenueDetailsView, ImageViewAdapter.ImageListener {

    private ImageView ivPictures;
    private String mVenueId;
    private List<String> photoUrls;
    private VenueDetailsContract.VenuePresenter mPresenter;
    private String urlToLoad, address;
    private double lat, lng;
    private String venueName, venueId;
    private RecyclerView rvPhoto;
    private TextView tvAddress, tvDistance, tvRating, tvCategory, tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        mVenueId = getIntent().getStringExtra(MainActivity.VENUE_ID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ivPictures =(ImageView) findViewById(R.id.imagePhotosDetails);
        ivPictures.setOnClickListener(this);

        tvAddress = (TextView)findViewById(R.id.textAddressDetails);
        tvCategory  = (TextView)findViewById (R.id.textCategoriesDetails);
        tvDistance  = (TextView)findViewById (R.id.textDistance);
        tvRating  = (TextView)findViewById (R.id.textRating);
        tvName  = (TextView)findViewById(R.id.textVenueNameDetails);
        rvPhoto = (RecyclerView)findViewById(R.id.listPhoto);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPinDetails);
        fab.setOnClickListener(this);
        FloatingActionButton fabDelete =
                (FloatingActionButton)findViewById(R.id.fabDeleteFromCacheDetails);
        fabDelete.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DaoSession daoSession = ((App)getApplication()).getDaoSession();
        mPresenter = new VenueDetailsPresenter(this,new DetailsRepository(daoSession));
        mPresenter.getDetails(mVenueId);


    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabPinDetails:
                if (!address.equals("hidden")) {
                    Snackbar.make(view, "opening the map", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    intent.putExtra("venueName", venueName);
                    startActivity(intent);
                }else
                    Snackbar.make(view, "the venue you selected hide hir location", Snackbar.LENGTH_LONG).show();

                break;
            case R.id.fabDeleteFromCacheDetails :
                Snackbar.make(view, "deleting from cache", Snackbar.LENGTH_LONG).show();
                mPresenter.deleteVenue(venueId);
                break;
            case R.id.imagePhotosDetails:
                Intent intent2 = new Intent(this, PhotoFullscreenActivity.class);
                intent2.putExtra("photo",urlToLoad);
                startActivity(intent2);
                break;
        }
    }




    @Override
    public void onError(int errorCode) {

    }

    @Override
    public void venueDeleted() {
        onBackPressed();
    }

    @Override
    public void detailsLoaded(Venue venue, List<Photo> photos) {
        photoUrls = new ArrayList<>();
        if (photos.size()>0){
            for (Photo photo : photos){
                photoUrls.add(photo.getUri());
            }
            lat = venue.getLat();
            lng = venue.getLng();
            urlToLoad = photoUrls.get(0);
            Picasso.with(this).load(urlToLoad).into(ivPictures);

            tvName.setText(venue.getName());
            tvRating.setText(venue.getLikes() + " " + getString(R.string.msg_likes));
            tvDistance.setText(venue.getDistance() + " " +getString(R.string.msg_meters));
            tvCategory.setText(venue.getCategories());
            tvAddress.setText(venue.getAddress());
            address = venue.getAddress();
            venueName = venue.getName();
            venueId = venue.getVenueId();
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvPhoto.setLayoutManager(layoutManager);
            rvPhoto.setAdapter(new ImageViewAdapter(photoUrls));

        }



    }

    @Override
    public void imageSelected(String url) {
        urlToLoad = url;
        Picasso.with(this).load(url).into(ivPictures);
    }



}
