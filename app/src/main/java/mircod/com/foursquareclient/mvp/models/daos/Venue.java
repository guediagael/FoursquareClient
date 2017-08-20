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
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by guedi on 8/16/2017.
 */

@Entity(indexes = {@Index(value = "venueId", unique= true)})
public class Venue {
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String venueId;

    @NotNull
    private String name;

    private String address;
    private String categories;
    private int distance;
    private int likes;
    private double lat;
    private double lng;


    private String bestPhotoUri;
    
    @Generated(hash = 18947531)
    public Venue(Long id, String venueId, @NotNull String name, String address,
            String categories, int distance, int likes, double lat, double lng,
            String bestPhotoUri) {
        this.id = id;
        this.venueId = venueId;
        this.name = name;
        this.address = address;
        this.categories = categories;
        this.distance = distance;
        this.likes = likes;
        this.lat = lat;
        this.lng = lng;
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

    public Long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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

    public void setId(Long id) {
        this.id = id;
    }
}
