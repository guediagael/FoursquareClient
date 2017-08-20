package mircod.com.foursquareclient.components.loaders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import mircod.com.foursquareclient.mvp.contracts.VenuesListContract;

/**
 * Created by guedi on 8/20/2017.
 */

public   class DetailsFetcher extends AsyncTaskLoader<Void> {
    private VenuesListContract.VenuesListPresenter presenter;
    private boolean networkState;
    private  String location;



    public DetailsFetcher(Context context, @Nullable String ltLong, VenuesListContract.VenuesListPresenter presenter) {
        super(context);
        networkState = ltLong != null;
        if (networkState) location = ltLong;
        this.presenter = presenter;


    }

    @Override
    public Void loadInBackground() {

        if (networkState) presenter.getVenues(location);
        else presenter.getVenues(null);

        return null;

    }
}
