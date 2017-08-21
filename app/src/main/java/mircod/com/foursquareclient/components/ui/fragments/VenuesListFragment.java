package mircod.com.foursquareclient.components.ui.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.App;
import mircod.com.foursquareclient.components.loaders.DetailsFetcher;
import mircod.com.foursquareclient.components.ui.adapters.VenuesListAdapter;
import mircod.com.foursquareclient.mvp.contracts.VenuesListContract;
import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.repositories.MainRepository;
import mircod.com.foursquareclient.mvp.presenters.VenuesPresenter;

public class VenuesListFragment extends Fragment implements BaseFragment,
        VenuesListContract.VenuesListView, SwipeRefreshLayout.OnRefreshListener,
        VenuesListAdapter.VenueInteractionListener, LoaderManager.LoaderCallbacks<Void> {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LONG = "longitude";


    private double mLat;
    private double mLong;
    private List<Venue> mVenues;

    private Loader mLoader;
    private VenuesListContract.VenuesListPresenter mPresenter;
    private OnFragmentInteractionListener mListener;
    private RecyclerView rvVenues;
    private SwipeRefreshLayout refreshLayout;
    public VenuesListFragment() {
        // Required empty public constructor
    }


    public static VenuesListFragment newInstance(double lat, double lng) {
        VenuesListFragment fragment = new VenuesListFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LONG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mLat = getArguments().getDouble(ARG_LAT);
            mLong = getArguments().getDouble(ARG_LONG);
            Log.d( "latlong:",mLat + ":" + mLong);
        }
        DaoSession daoSession = ((App)getActivity().getApplication()).getDaoSession();
        mPresenter = new VenuesPresenter(new MainRepository(daoSession),this);
        mVenues = new ArrayList<>();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable layoutManagerConfig = rvVenues.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("layoutManager",layoutManagerConfig);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoader = getLoaderManager().initLoader(0,savedInstanceState,this);
        mLoader.forceLoad();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_venues_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvVenues = view.findViewById(R.id.recyclerMainList);
        rvVenues.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout)view;
        refreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onError(int errorCode) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void venuesLoaded(final List<Venue> venues) {

        if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
        mVenues.clear();
        mVenues.addAll(venues);
        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    rvVenues.setAdapter(new VenuesListAdapter(mVenues,VenuesListFragment.this));
                }
            });
        }catch (Exception e){
//            When the api is locked no need for a handler
            rvVenues.setAdapter(new VenuesListAdapter(mVenues,VenuesListFragment.this));
            Toast.makeText(getActivity(),"API blocked", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void venueDeleted() {

    }

    @Override
    public void venuesDeleted() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mVenues.clear();
                rvVenues.getAdapter().notifyDataSetChanged();
                Toast.makeText(getActivity(),"cache cleared", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onRefresh() {
//
        mLoader.forceLoad();
    }

//    private void loadVenues(){
//        refreshLayout.setRefreshing(true);
//        if (isNetworkAvailable()) mPresenter.getVenues(mLat+","+mLong);
//        else mPresenter.getVenues(null);
//    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager  =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo !=null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void venueSelected(String venueId) {
        mListener.openDetailsActivity(venueId);
    }

    @Override
    public void deleteVenue(String id, int position) {
        rvVenues.getAdapter().notifyItemRemoved(position);
        mPresenter.deleteVenue(id);
    }



    public void clearCache(){
        mPresenter.deleteVenues();
    }


    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        String location;
        if ((mLat==0 && mLong== 0)|| !isNetworkAvailable()) location = null;
        else location = mLat+","+mLong;
        refreshLayout.setRefreshing(true);
        return new DetailsFetcher(getActivity(),location,mPresenter);
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }



    public interface OnFragmentInteractionListener {
       void openDetailsActivity(String venueId);
    }




}
