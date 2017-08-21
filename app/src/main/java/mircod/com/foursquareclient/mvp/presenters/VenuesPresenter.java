package mircod.com.foursquareclient.mvp.presenters;

import android.support.annotation.Nullable;

import java.util.List;

import mircod.com.foursquareclient.mvp.contracts.VenuesListContract;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.repositories.MainRepository;

/**
 * Created by guedi on 8/17/2017.
 */

public class VenuesPresenter implements VenuesListContract.VenuesListPresenter,
        MainRepository.RepositoryListener {
    private MainRepository mMainrepository;
    private VenuesListContract.VenuesListView mView;

    public VenuesPresenter(MainRepository mMainrepository, VenuesListContract.VenuesListView mView) {
        this.mMainrepository = mMainrepository;
        mMainrepository.setListener(this);
        this.mView = mView;
    }

    @Override
    public void getVenues(@Nullable  String latlong) {
        mMainrepository.getNearbyVenues(latlong);

    }

    @Override
    public void deleteVenues() {
        mMainrepository.deleteVenues();
    }

    @Override
    public void deleteVenue(String id) {
        mMainrepository.deleteVenue(id);
    }


    @Override
    public void dataLoaded(List<Venue> venues) {
        mView.venuesLoaded(venues);
    }

    @Override
    public void venueDeleted() {
        mView.venueDeleted();
    }

    @Override
    public void venuesDeleted() {
        mView.venuesDeleted();
    }

    @Override
    public void error(int code) {

    }
}
