package com.errorerrorerror.esplightcontroller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ObservableList<T> {

    protected final List<T> list;
    private final PublishSubject<List<T>> publishSubject;

    public ObservableList() {
        this.list = new ArrayList<>();
        this.publishSubject = PublishSubject.create();
        publishSubject.onNext(this.list);
    }

    public ObservableList(List<T> list){
        this.list = list;
        this.publishSubject = PublishSubject.create();
        publishSubject.onNext(this.list);
    }

    public ObservableList(int max){
        this.list = new ArrayList<>(max);
        this.publishSubject = PublishSubject.create();
        publishSubject.onNext(this.list);
    }

    public void reInsertList(){
        publishSubject.onNext(list);
    }

    public boolean add(T value) {
        boolean t = list.add(value);
        publishSubject.onNext(list);
        return t;
    }


    public void add(int index, T value) {
        list.add(index,value);
        publishSubject.onNext(list);
    }



    public boolean remove(Object o){
        boolean t = list.remove(o);
        publishSubject.onNext(list);
        return t;
    }

    public T remove(int index){
        T t = list.remove(index);
        publishSubject.onNext(list);
        return t;
    }


    public T set(int o, T element){
        T t = list.set(o, element);
        publishSubject.onNext(list);
        return t;
    }

    public void addAll(Collection<? extends T> c){
        list.addAll(c);
        publishSubject.onNext(list);
    }

    public List<T> getList(){
        return list;
    }

    public boolean contains(T obj){
        return list.contains(obj);
    }

    public Observable<List<T>> getObservableList() {
        return publishSubject;
    }
}

