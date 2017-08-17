package mircod.com.foursquareclient.mvp.models.daos;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by guedi on 8/16/2017.
 */

@Entity(indexes = {@Index(value = "venueId", unique= true)})
public class Venue {
    @Id(autoincrement = true)
    private long id;

    @NotNull
    private String venueId;

    @NotNull
    private String name;

    private String address;
    private String categories;
    private int distance;
    private int likes;
    private String bestPhotoUri;
    
    @Generated(hash = 1594187984)
    public Venue(long id, @NotNull String venueId, @NotNull String name, String address,
            String categories, int distance, int likes, String bestPhotoUri) {
        this.id = id;
        this.venueId = venueId;
        this.name = name;
        this.address = address;
        this.categories = categories;
        this.distance = distance;
        this.likes = likes;
        this.bestPhotoUri = bestPhotoUri;
    }

    @Generated(hash = 478316511)
    public Venue() {
    }


    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBestPhotoUri() {
        return bestPhotoUri;
    }

    public void setBestPhotoUri(String bestPhotoUri) {
        this.bestPhotoUri = bestPhotoUri;
    }

    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
