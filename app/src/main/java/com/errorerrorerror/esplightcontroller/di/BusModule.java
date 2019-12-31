package com.errorerrorerror.esplightcontroller.di;

import com.errorerrorerror.esplightcontroller.utils.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BusModule {

    private final RxBus rxBus;

    public BusModule() {
        rxBus = new RxBus();
    }

    @Provides
    @Singleton
    RxBus providesRxBus() {
        return rxBus;
    }
}
