package com.errorerrorerror.esplightcontroller.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.errorerrorerror.esplightcontroller.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    App provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public SharedPreferences providePreferences() {
        return application.getSharedPreferences(application.getPackageName() + "_preferences",
                Context.MODE_PRIVATE);
    }
}
