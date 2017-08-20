package mircod.com.foursquareclient.components;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import mircod.com.foursquareclient.mvp.models.daos.DaoMaster;
import mircod.com.foursquareclient.mvp.models.daos.DaoSession;
import mircod.com.foursquareclient.uitls.StorageHandler;

/**
 * Created by guedi on 8/17/2017.
 */

public class App extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-Db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession(){
        return daoSession;
    }
}
