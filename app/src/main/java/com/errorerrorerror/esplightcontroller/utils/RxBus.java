package com.errorerrorerror.esplightcontroller.utils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

public final class RxBus {

    @Inject
    public RxBus() {
        /// Used for dagger 2
    }

    private static final PublishSubject<Object> pSubject = PublishSubject.create();

    public void publish(@NonNull Object event) {
        if (pSubject.hasObservers()) {
            pSubject.onNext(event);
        }
    }

    public <T> Observable<T> observable(@NonNull Class<T> eventClass) {
        return pSubject
                .filter(eventClass::isInstance)
                .cast(eventClass)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
