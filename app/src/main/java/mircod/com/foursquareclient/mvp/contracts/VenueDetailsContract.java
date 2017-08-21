package mircod.com.foursquareclient.mvp.contracts;

import java.util.List;

import mircod.com.foursquareclient.mvp.models.daos.Photo;
import mircod.com.foursquareclient.mvp.models.daos.Venue;

/**
 * Created by guedi on 8/18/2017.
 */

public interface VenueDetailsContract {

    interface VenueDetailsView extends BaseView {

        void venueDeleted();
        void detailsLoaded(Venue venue, List<Photo> photos);
    }

    interface VenuePresenter {
        void getDetails(String venueId);
        void deleteVenue(String venueId);
    }
}
