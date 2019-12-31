package com.errorerrorerror.esplightcontroller;

import android.app.Application;

import com.errorerrorerror.esplightcontroller.di.AppModule;
import com.errorerrorerror.esplightcontroller.di.BusModule;
import com.errorerrorerror.esplightcontroller.di.RoomModule;
import com.errorerrorerror.esplightcontroller.di.component.AppComponent;
import com.errorerrorerror.esplightcontroller.di.component.DaggerAppComponent;


public class App extends Application  {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .roomModule(new RoomModule(this))
                .busModule(new BusModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
