package mircod.com.foursquareclient.mvp.presenters;

import java.util.List;

import mircod.com.foursquareclient.mvp.contracts.VenueDetailsContract;
import mircod.com.foursquareclient.mvp.models.daos.Photo;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.repositories.DetailsRepository;

/**
 * Created by guedi on 8/18/2017.
 */

public class VenueDetailsPresenter implements VenueDetailsContract.VenuePresenter,
        DetailsRepository.RepositoryListener {

    private VenueDetailsContract.VenueDetailsView mView;
    private DetailsRepository mRepository;

    public VenueDetailsPresenter(VenueDetailsContract.VenueDetailsView mView,
                                 DetailsRepository repository) {
        this.mView = mView;
        mRepository = repository;
        mRepository.setListener(this);
    }

    @Override
    public void getDetails(String venueId) {
        mRepository.loadDetails(venueId);
    }

    @Override
    public void deleteVenue(String venueId) {

    }

    @Override
    public void detailsLoaded(Venue venue, List<Photo> photos) {
        mView.detailsLoaded(venue,photos);
    }

    @Override
    public void venueDeleted() {

    }

    @Override
    public void error() {

    }
}
