package mircod.com.foursquareclient.components.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.ui.viewHolders.VenueListHolder;
import mircod.com.foursquareclient.mvp.models.daos.Venue;

/**
 * Created by guedi on 8/17/2017.
 */

public class VenuesListAdapter extends RecyclerView.Adapter<VenueListHolder> implements VenueListHolder.ClearButtonListener {
    private List<Venue> venues;
    private Context mContext;
    private VenueInteractionListener mListener;
    private int positionToDelete;

    public VenuesListAdapter(List<Venue> venues,VenueInteractionListener listener) {
        this.venues = venues;
        mListener = listener;
    }

    @Override
    public VenueListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_list_viewholder,parent,false);

        return new VenueListHolder(itemView, this);



    }


    @Override
    public void onBindViewHolder(final VenueListHolder holder, int position) {
        Venue venue = venues.get(position);
        String name = venue.getName();
        Log.d(getClass().getSimpleName(),venue.getName());
        String distance = String.valueOf(venue.getDistance());
        final String venueId = venue.getVenueId();

//        long id = venue.getId();
        holder.bindData(name,distance,venueId);
        String imageUrl = venue.getBestPhotoUri();
        Picasso.with(mContext).load(imageUrl).into(holder.getIvThumbnail(), new Callback() {
            @Override
            public void onSuccess() {
                holder.stopLoading();
            }

            @Override
            public void onError() {
                holder.stopLoading();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.venueSelected(venueId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venues.size();
    }

    @Override
    public void clearButtonPressed(String id) {
        for (Venue venue : venues){
            if (venue.getVenueId().equals(id)){
                positionToDelete = venues.indexOf(venue);
                venues.remove(positionToDelete);
                break;
            }
        }
        mListener.deleteVenue(id,positionToDelete);
    }


    public interface VenueInteractionListener {
        void venueSelected(String venueId);
        void deleteVenue(String id, int position);

    }
}
