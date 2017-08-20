package mircod.com.foursquareclient.components.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import mircod.com.foursquareclient.R;
import mircod.com.foursquareclient.components.ui.viewHolders.ImageViewHolder;

/**
 * Created by guedi on 8/20/2017.
 */

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewHolder> {
    private List<String> urls;
    private ImageListener listener;
    private Context context;
    public ImageViewAdapter(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent.getContext() instanceof ImageListener)
            listener = (ImageListener)parent.getContext();
        else throw new ClassCastException(parent.getContext().toString()+
                "must implement imageListener");

        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.image_holder,parent,false);

        return  new ImageViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        final String url = urls.get(position);
        Picasso.with(context).load(url).into(holder.getImageView());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.imageSelected(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public interface ImageListener {
        void imageSelected(String url);
    }
}
