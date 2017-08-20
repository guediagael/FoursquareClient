package mircod.com.foursquareclient.mvp.models.repositories;

import android.util.Log;

import java.util.List;

import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.mvp.models.daos.Photo;
import mircod.com.foursquareclient.mvp.models.daos.PhotoDao;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.daos.VenueDao;

/**
 * Created by guedi on 8/18/2017.
 */

public class DetailsRepository {
    private RepositoryListener mListener;
    private PhotoDao photoDao;
    private VenueDao venueDao;
    public DetailsRepository(DaoSession session) {
        photoDao = session.getPhotoDao();
        venueDao = session.getVenueDao();
    }

    public void loadDetails(String venueId){
        Venue venue = venueDao.queryBuilder().where(VenueDao.Properties.VenueId.eq(venueId)).unique();
        int size = venueDao.loadAll().size();
        Log.d("size ", String.valueOf(size));
        List<Photo> photos = photoDao.queryBuilder().where(PhotoDao.Properties.VenueId.eq(venueId)).list();
        mListener.detailsLoaded(venue,photos);
    }

    public void setListener(RepositoryListener listener){
        mListener = listener;
    }

    public interface RepositoryListener{
        void detailsLoaded(Venue venue, List<Photo> photos);
        void venueDeleted();
        void error();
    }
}
