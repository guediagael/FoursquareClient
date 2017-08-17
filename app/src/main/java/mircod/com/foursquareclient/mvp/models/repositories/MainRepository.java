package mircod.com.foursquareclient.mvp.models.repositories;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import mircod.com.foursquareclient.mvp.models.apis.FoursquareApi;
import mircod.com.foursquareclient.mvp.models.apis.RetrofitClient;
import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.mvp.models.daos.Photo;
import mircod.com.foursquareclient.mvp.models.daos.PhotoDao;
import mircod.com.foursquareclient.mvp.models.daos.Venue;
import mircod.com.foursquareclient.mvp.models.daos.VenueDao;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private AtomicInteger numberOfVenueToLoad;

    private CopyOnWriteArrayList<Venue> venues;
    private DaoSession mDaoSession;
    private VenueDao venueDao;
    private PhotoDao photoDao;


    public MainRepository(DaoSession daoSession) {
        mApi = RetrofitClient.getInstance().create(FoursquareApi.class);
        mDaoSession = daoSession;
        venueDao = mDaoSession.getVenueDao();
        photoDao = mDaoSession.getPhotoDao();
        venues = new CopyOnWriteArrayList<>();

    }


    public List<Venue> getNearbyVenues(String latLong){
        Call<JSONObject> call = mApi.getVenuesList(latLong, INTENT, CLIENT_ID, CLIENT_SECRET,VERSION,M);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                JSONObject respBody = response.body();
                try {
                    JSONObject meta = respBody.getJSONObject("meta");
                    if (meta.get("code")==200){
                        JSONObject data = respBody.getJSONObject("response");
                        JSONArray venues = data.getJSONArray("venues");
                        numberOfVenueToLoad = new AtomicInteger(venues.length());
                        for (int i=0;i<venues.length();i++){
                            JSONObject venue = venues.getJSONObject(i);
                            String name = venue.getString("name");
                            JSONObject location = venue.getJSONObject("location");
                            String address = " ";
                            int distance = 0;
                            if (location!=null) {
                                address = location.getString("address");
                                distance = location.getInt("distance");
                            }
                            JSONArray categories = venue.getJSONArray("categories");
                            String cats = "";
                            for (int j = 0 ; j <categories.length();j++){
                                String catName = categories.getJSONObject(j).getString("name");
                                cats = cats.concat("," + " "+ catName);
                            }

                            final String venueId = venue.getString("id");

                            Venue v = new Venue();
                            v.setVenueId(venueId);
                            v.setName(name);
                            v.setAddress(address);
                            v.setDistance(distance);

                            addVenueToList(v);


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getVenueDetails(venueId);
                                }
                            }).start();
                        }


                    }else {
//                        TODO: ahow error message
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());
                } catch (NullPointerException e) {
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());
                }


            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
//        TODO: get the data  and get id for each data and fetch details;


        while (isRequestQueued());
        return venues;

    }

    private void getVenueDetails(final String id){
        Call<JSONObject> call = mApi.getVenueDetails(id,INTENT,CLIENT_ID,CLIENT_SECRET,VERSION,M);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                JSONObject respBody = response.body();
                try {
                    JSONObject meta = respBody.getJSONObject("meta");
                    if (meta.get("code")==200){
                        JSONObject venue = respBody.getJSONObject("response");
                        String bestPhotoUrl = null;
                        if (venue.has("bestPhoto")){
                            JSONObject bestPhoto = venue.getJSONObject("bestPhoto");
                            if (bestPhoto.has("prefix") && bestPhoto.has("suffix")){
                                String prefix = bestPhoto.getString("prefix");
                                String suffix = bestPhoto.getString("suffix");
                                bestPhotoUrl = prefix +"600x600"+ suffix;
                            }
                        }

                        JSONObject likes = venue.getJSONObject("likes ");
                        int likesCount = likes.getInt("count");

                        JSONObject photos = venue.getJSONObject("photos");
                        JSONArray groups = photos.getJSONArray("groups");
                        List<String> urls = new ArrayList<>();
                        for (int i = 0; i<groups.length(); i++){
                            JSONArray items = groups.getJSONArray(i);
                            for (int j = 0; j<items.length(); j++){
                                String url;
                                JSONObject item  = items.getJSONObject(j);
                                String prefix = item.getString("prefix");
                                String suffix = item.getString("suffix");
                                url = prefix + "600x600" + suffix;
                                urls.add(url);
                                if (j==10) break;

                            }
                        }

                        updateVenueFromList(id,likesCount,urls,bestPhotoUrl);

                    }
                } catch (JSONException e) {
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());
                }catch (NullPointerException e){
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());

                }

            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
        numberOfVenueToLoad.decrementAndGet();
    }

    private boolean isRequestQueued(){
        return (numberOfVenueToLoad.get()>0);
    }

    private void addVenueToList(Venue venue){
        venues.add(venue);
    }

    private void updateVenueFromList(String id, int likes, List<String> photoUrls, @Nullable String bestPhoto){
        for (Venue venue : venues) {
            if (venue.equals(id)) {
                venue.setLikes(likes);
                if (bestPhoto != null) venue.setBestPhotoUri(bestPhoto);
                else venue.setBestPhotoUri(photoUrls.get(0));
            }
        }
        for (String url : photoUrls){
            Photo photo = new Photo();
            photo.setVenueId(id);
            photo.setUri(url);
            photoDao.insert(photo);
//            TODO: save to the sd card
        }


    }

}
