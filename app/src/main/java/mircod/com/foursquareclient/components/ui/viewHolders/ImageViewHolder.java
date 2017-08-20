package mircod.com.foursquareclient.components.ui.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import mircod.com.foursquareclient.R;

/**
 * Created by guedi on 8/20/2017.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageImageHolder);
    }

    public ImageView getImageView() {
        return imageView;
    }
}


