package io.bloc.android.bloctalk;

import android.app.Application;
import android.support.v7.app.ActionBarActivity;

import io.bloc.android.bloctalk.api.DataSource;


/**
 * Created by Mark on 3/8/2015.
 */
public class BlocTalkApplication extends Application {
    public static BlocTalkApplication getSharedInstance() {
        return sharedInstance;
    }

    public static DataSource getSharedDataSource() {
        return BlocTalkApplication.getSharedInstance().getDataSource();
    }

    private static BlocTalkApplication sharedInstance;
    private DataSource dataSource;
    private ActionBarActivity currentActivity = null;

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        sharedInstance = this;
        dataSource = new DataSource();
    }

    public ActionBarActivity getCurrentActivity(){
        return currentActivity;
    }
    public void setCurrentActivity(ActionBarActivity mCurrentActivity){
        this.currentActivity = mCurrentActivity;
    }

}
