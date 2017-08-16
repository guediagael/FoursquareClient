package mircod.com.foursquareclient.components.ui.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import mircod.com.foursquareclient.R;

/**
 * Created by guedi on 8/16/2017.
 */

public class VenueListHolder extends RecyclerView.ViewHolder {

    private ClearButtonListener mListener;

    private ImageButton ibDeleteFromCache;
    private ImageView ivThumbnail;
    private ProgressBar pbThumbnail;
    private TextView tvName, tvDistance;

    public VenueListHolder(View itemView,ClearButtonListener listener) {
        super(itemView);

        mListener = listener;

        ibDeleteFromCache = itemView.findViewById(R.id.buttonClearVenueListHolder);


        ivThumbnail = itemView.findViewById(R.id.imageVenueListHolder);
        ivThumbnail.setVisibility(View.GONE);


        pbThumbnail = itemView.findViewById(R.id.progressBarVenueListHolder);

        tvName = itemView.findViewById(R.id.textNameVenueListHolder);
        tvDistance = itemView.findViewById(R.id.textDistanceVenueListHolder);
    }


    public void bindData(String name,String distance, final String id){
        tvName.setText(name);
        tvDistance.setText(distance);

        ibDeleteFromCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clearButtonPressed(id);
            }
        });
    }

    public ImageView getIvThumbnail(){
        pbThumbnail.setVisibility(View.GONE);
        return ivThumbnail;
    }

    public interface ClearButtonListener{

        void clearButtonPressed(String id);
    }


}
