package mircod.com.foursquareclient.components.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.AsyncTaskLoader;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by guedi on 8/18/2017.
 */

public class BitmapLoader extends AsyncTaskLoader<Drawable> {

    private String url;
    private Drawable drawable = null;

    public BitmapLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public Drawable loadInBackground() {

        try {
            Bitmap bitmap = Picasso.with(this.getContext()).load(url).get();
            drawable = new BitmapDrawable(getContext().getResources(),bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

//    @Override
//    public void deliverResult(Drawable data) {
//        super.deliverResult(data);
//        Dr
//    }
}
