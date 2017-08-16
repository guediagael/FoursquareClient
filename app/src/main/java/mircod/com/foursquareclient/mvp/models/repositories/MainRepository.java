package mircod.com.foursquareclient.mvp.models.repositories;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import mircod.com.foursquareclient.mvp.models.apis.FoursquareApi;
import mircod.com.foursquareclient.mvp.models.apis.RetrofitClient;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by guedi on 8/16/2017.
 */

public class MainRepository {
    private final FoursquareApi mApi;
    private final String CLIENT_ID = "TWM0D0XO3ZS0BTRXTFK3K1TRVZDS0LZMRHOJBGPUJTF2Z42P";
    private final String CLIENT_SECRET= "WT5XJEFUSVHTQYGXQ2CJDCIOCNLH3L1ORQP41BST0SS1EQHK";
    private final String VERSION = "20170816";
    private final String M = "foursquare";
    private final String INTENT = "checkin";
    private int numberOfVenueToLoad;

    private CopyOnWriteArrayList<Venue> venues;


    public MainRepository() {
        mApi = RetrofitClient.getInstance().create(FoursquareApi.class);
    }


    public List<Venue> getNearbyVenues(String latLong){
        Call<ResponseBody> call = mApi.getVenuesList(latLong, INTENT, CLIENT_ID, CLIENT_SECRET,VERSION,M);

//        TODO: get the data  and get id for each data and fetch details;



        while (numberOfVenueToLoad >0){
//             TODO: fetch data
        }
        return venues;
    }

    private Venue getVenueDetails(String id){
//        TODO:

        numberOfVenueToLoad--;
        return null;
    }
}
