package com.errorerrorerror.esplightcontroller.adapters;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public abstract class DataBindingAdapter<T, V extends ViewDataBinding> extends ListAdapter<T, DeviceViewHolder<T, V>> {
    private PublishSubject<T> objectClicked = PublishSubject.create();
    private PublishSubject<T> objectLongClicked = PublishSubject.create();

    DataBindingAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }


    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder<T, V> holder, int position) {
        holder.bind(getItem(position));
        RxView.clicks(holder.itemView).takeUntil(RxView.detaches(holder.itemView)).subscribe(unit -> objectClicked.onNext(getItem(holder.getAdapterPosition())));
        RxView.longClicks(holder.itemView).takeUntil(RxView.detaches(holder.itemView)).subscribe(unit -> objectLongClicked.onNext(getItem(holder.getAdapterPosition())));
    }

    public Observable<T> getClickedObjectsObservable() {
        return objectClicked;
    }

    public Observable<T> getLongClickedObjectsObservable() {
        return objectLongClicked;
    }

    @Override
    public void onDetachedFromRecyclerView(@NotNull RecyclerView recyclerView) {
        objectLongClicked.onComplete();
        objectClicked.onComplete();
    }
}
