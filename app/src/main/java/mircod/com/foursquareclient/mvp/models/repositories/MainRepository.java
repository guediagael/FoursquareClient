package mircod.com.foursquareclient.mvp.models.repositories;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
import mircod.com.foursquareclient.uitls.StorageHandler;
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
    private final String CLIENT_SECRET = "WT5XJEFUSVHTQYGXQ2CJDCIOCNLH3L1ORQP41BST0SS1EQHK";
    private final String VERSION = "20170816";
    private final String M = "foursquare";
    private final String INTENT = "checkin";
    private AtomicInteger numberOfVenueToLoad;

    private CopyOnWriteArrayList<Venue> venues;
    private VenueDao venueDao;
    private PhotoDao photoDao;

    private RepositoryListener mListener;
    private DataExtractor extractor;

    public MainRepository(DaoSession daoSession) {
        mApi = RetrofitClient.getInstance().create(FoursquareApi.class);
        DaoSession mDaoSession = daoSession;
        venueDao = mDaoSession.getVenueDao();
        photoDao = mDaoSession.getPhotoDao();
        venues = new CopyOnWriteArrayList<>();
        numberOfVenueToLoad = new AtomicInteger(0);

        extractor = new DataExtractor();

    }





    public void getNearbyVenues(@Nullable String latLong) {
        if (latLong != null) {
            Call<ResponseBody> call = mApi.getVenuesList(latLong, INTENT, CLIENT_ID, CLIENT_SECRET, VERSION, M);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        venueDao.deleteAll();
                        photoDao.deleteAll();
                        try {
                            Log.d("response full", response.raw().toString());
                            String body = response.body().string();

                            JSONObject object = new JSONObject(body);
                            Log.d(MainRepository.class.getSimpleName(), "response ob:" + object + "," + object.length());
                            JSONObject meta = object.getJSONObject("meta");
                            if ((int) meta.get("code") == 200) {
                                JSONObject data = object.getJSONObject("response");
                                JSONArray venues = data.getJSONArray("venues");
                                Log.d(MainRepository.class.getSimpleName(), "venues count: " +
                                        venues.length());
                                numberOfVenueToLoad = new AtomicInteger(venues.length());
                                for (int i = 0; i < venues.length(); i++) {
                                    JSONObject venue = venues.getJSONObject(i);
                                    String name = venue.getString("name");
                                    String address = " ";
                                    int distance = 0;
                                    if (venue.has("location")) {
                                        JSONObject location = venue.getJSONObject("location");
                                        if (location.has("address") && location.has("address")) {
                                            address = location.getString("address");
                                            distance = location.getInt("distance");
                                        }
                                    }
                                    JSONArray categories = venue.getJSONArray("categories");
                                    StringBuilder builder = new StringBuilder();
                                    for (int j = 0; j < categories.length(); j++) {
                                        String catName = categories.getJSONObject(j).getString("name") + " ";
                                        builder.append(catName);
                                    }
                                    String cats = builder.toString();


                                    final String venueId = venue.getString("id");

                                    Venue v = new Venue();
                                    v.setVenueId(venueId);
                                    v.setName(name);
                                    v.setAddress(address);
                                    v.setDistance(distance);
                                    v.setCategories(cats);

                                    addVenueToList(v);


                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getVenueDetails(venueId);
                                        }
                                    }).start();


                                }


                            } else {
//                        TODO: show error message
                                Log.d(MainRepository.class.getSimpleName(), "response error: " +
                                        response.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(MainRepository.class.getSimpleName(), "response:" + e.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(MainRepository.class.getSimpleName(), "response eror:" + e.getMessage());
                        }
                    } else getFromDb();

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(MainRepository.class.getSimpleName(), "response failure:" + t.getMessage());
                    getFromDb();
                }
            });

        } else {
            getFromDb();
        }


    }

    private void getVenueDetails(final String id) {
        Call<ResponseBody> call = mApi.getVenueDetails(id, INTENT, CLIENT_ID, CLIENT_SECRET, VERSION, M);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body = response.body().string();
                    JSONObject respBody = new JSONObject(body);
                    JSONObject meta = respBody.getJSONObject("meta");
                    if ((int) meta.get("code") == 200) {
                        JSONObject venueResponse = respBody.getJSONObject("response");
                        JSONObject venue = venueResponse.getJSONObject("venue");
                        String bestPhotoUrl = null;
                        if (venue.has("bestPhoto")) {
                            JSONObject bestPhoto = venue.getJSONObject("bestPhoto");
                            if (bestPhoto.has("prefix") && bestPhoto.has("suffix")) {
                                String prefix = bestPhoto.getString("prefix");
                                String suffix = bestPhoto.getString("suffix");
                                bestPhotoUrl = prefix + "600x600" + suffix;
                            }
                        }


                        int likesCount = 0;
                        if (venue.has("likes")) {
                            JSONObject likes = venue.getJSONObject("likes");
                            likesCount = likes.getInt("count");
                        }


                        JSONObject photos = venue.getJSONObject("photos");

                        JSONArray groups = photos.getJSONArray("groups");
                        Log.d("groupssssss", groups.toString());
                        List<String> urls = new ArrayList<>();
                        for (int i = 0; i < groups.length(); i++) {
                            JSONArray items = groups.getJSONObject(i).getJSONArray("items");
                            Log.d("itemmmm", items.toString());
                            for (int j = 0; j < items.length(); j++) {
                                String url;
                                JSONObject item = items.getJSONObject(j);
                                String prefix = item.getString("prefix");
                                String suffix = item.getString("suffix");
                                url = prefix + "600x600" + suffix;
                                urls.add(url);
                                Log.d("tof", url);
                                if (j == 10) break;

                            }
                        }

//                        updateVenueFromList(id, likesCount, urls, bestPhotoUrl);
                        new DataExtractor().execute(id,likesCount,urls,bestPhotoUrl);

                    }
                } catch (JSONException e) {
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());
                } catch (NullPointerException e) {
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());

                } catch (IOException e) {
                    Log.d(MainRepository.class.getSimpleName(), e.getMessage());
                }


                int rest = numberOfVenueToLoad.decrementAndGet();
                Log.d("venue queues", String.valueOf(rest));
                if (rest < 1) {
                    mListener.dataLoaded(venues);
                    Log.d("insert size", String.valueOf(venueDao.count()));

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(MainRepository.class.getSimpleName(), t.getMessage());
            }
        });


    }


    private void addVenueToList(Venue venue) {
        venues.add(venue);
    }

    private void updateVenueFromList(String id, int likes, List<String> photoUrls, @Nullable String bestPhoto) {
        for (Venue venue : venues) {
            if (venue.getVenueId().equals(id)) {
                venue.setLikes(likes);
                if (bestPhoto != null) {
                    String newUrl = StorageHandler.convertsrcUrlToLocalUri(bestPhoto, id).toString();
                    venue.setBestPhotoUri(newUrl);
                } else {
                    if (photoUrls.size() > 0) {
                        Log.d(getClass().getSimpleName(), "best tof :" + photoUrls.get(0));
                        venue.setBestPhotoUri(photoUrls.get(0));
                    }
                }

                venueDao.insert(venue);

            }
        }
        for (String url : photoUrls) {
            Log.d(getClass().getSimpleName(), "photos :" + url);
            Photo photo = new Photo();
            photo.setVenueId(id);
            String newUrl = StorageHandler.convertsrcUrlToLocalUri(url, id).toString();
            photo.setUri(newUrl);
            photoDao.insertOrReplace(photo);
//            TODO: save to the sd card
        }


    }

    void getFromDb() {
        mListener.dataLoaded(venueDao.loadAll());
    }


    public void setListener(RepositoryListener listener) {
        mListener = listener;
    }


    public interface RepositoryListener {
        void dataLoaded(List<Venue> venues);

        void error(int code);
    }


    public class DataExtractor extends AsyncTask<Object, Void, Void> {


        @Override
        protected Void doInBackground(Object... objects) {
            String id =(String) objects[0];
            int likes = (int ) objects[1];
            List list = (List)objects[2];
            String last = null ;
            try {
                last = (String)objects[3];
            }catch (NullPointerException e){

            }
            updateVenueFromList(id,likes,list,last);
            return null;
        }
    }

}
