package mircod.com.foursquareclient.components.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.App;
import mircod.com.foursquareclient.components.ui.adapters.VenuesListAdapter;
import mircod.com.foursquareclient.mvp.contracts.VenuesListContract;
import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.repositories.MainRepository;
import mircod.com.foursquareclient.mvp.presenters.VenuesPresenter;

public class VenuesListFragment extends Fragment implements BaseFragment,
        VenuesListContract.VenuesListView, SwipeRefreshLayout.OnRefreshListener, VenuesListAdapter.VenueInteractionListener {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LONG = "longitude";


    private double mLat;
    private double mLong;
    private List<Venue> mVenues;

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
        if (getArguments() != null) {
            mLat = getArguments().getDouble(ARG_LAT);
            mLong = getArguments().getDouble(ARG_LONG);
        }
        DaoSession daoSession = ((App)getActivity().getApplication()).getDaoSession();
        mPresenter = new VenuesPresenter(new MainRepository(daoSession),this);
        mVenues = new ArrayList<>();

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
        mPresenter.getVenues(mLat+","+mLong);
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

    }

    @Override
    public void venuesLoaded(final List<Venue> venues) {
        mVenues.clear();
        mVenues.addAll(venues);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                rvVenues.setAdapter(new VenuesListAdapter(mVenues,VenuesListFragment.this));
            }
        });

    }

    @Override
    public void onRefresh() {
//        TODO: check the connection, if there is load from the db else load from the net
    }

    private void loadVenues(){
        if (isNetworkAvailable()) mPresenter.getVenues(mLat+","+mLong);
        else mPresenter.getVenues(null);
    }

    private boolean isNetworkAvailable(){
//        TODO:
        return false;
    }

    @Override
    public void venueSelected(String venueId) {
        mListener.openDetailsActivity(venueId);
    }

    @Override
    public void deleteVenue(long id) {

    }


    public interface OnFragmentInteractionListener {
       void openDetailsActivity(String venueId);
    }
}
