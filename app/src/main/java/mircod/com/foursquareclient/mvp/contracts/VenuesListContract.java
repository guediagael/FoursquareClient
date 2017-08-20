package mircod.com.foursquareclient.mvp.contracts;

import android.support.annotation.Nullable;

import java.util.List;

import mircod.com.foursquareclient.mvp.models.daos.Venue;

/**
 * Created by guedi on 8/17/2017.
 */

public interface VenuesListContract {

    interface VenuesListView extends BaseView{
        void venuesLoaded(List<Venue> venues);
    }

    interface VenuesListPresenter{
        void getVenues(@Nullable String latlong);
        void deleteVenues();
        void deleteVenue(String id);

    }
}
