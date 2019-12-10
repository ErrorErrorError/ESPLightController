package com.errorerrorerror.esplightcontroller.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.errorerrorerror.esplightcontroller.interfaces.OnItemClickedListener;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import kotlin.Unit;

public abstract class DataBindingAdapter<T> extends ListAdapter<T, DeviceViewHolder<T>> implements BindableAdapter<T> {
    private PublishSubject<T> objectClicked = PublishSubject.create();
    private PublishSubject<T> objectLongClicked = PublishSubject.create();

    DataBindingAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder<T> holder, int position) {
        holder.bind(getItem(position));

        RxView.clicks(holder.itemView).takeUntil(RxView.detaches(holder.itemView)).subscribe(unit -> objectClicked.onNext(getItem(holder.getAdapterPosition())));
        RxView.longClicks(holder.itemView).takeUntil(RxView.detaches(holder.itemView)).subscribe(unit -> objectLongClicked.onNext(getItem(holder.getAdapterPosition())));

    }

    @Override
    public void setData(List<T> data) {
        submitList(data);
    }

    public Observable<T> getClickedObjectsObservable() {
        return objectClicked;
    }

    public Observable<T> getLongClickedObjectsObservable() {
        return objectLongClicked;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        objectLongClicked.onComplete();
        objectClicked.onComplete();
    }
}
