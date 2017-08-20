package mircod.com.foursquareclient.mvp.models.daos;

import android.net.Uri;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

/**
 * Created by guedi on 8/17/2017.
 */

@Entity
public class Photo {

    @Id(autoincrement = true)
    private Long id;

    private String venueId;

   
    @NotNull
    private String uri;

    @Generated(hash = 1483489889)
    public Photo(Long id, String venueId, @NotNull String uri) {
        this.id = id;
        this.venueId = venueId;
        this.uri = uri;
    }

    @Generated(hash = 1043664727)
    public Photo() {
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getId() {
        return id;
    }



    public void setId(Long id) {
        this.id = id;
    }
}
