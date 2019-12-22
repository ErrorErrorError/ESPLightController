package com.errorerrorerror.esplightcontroller.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.errorerrorerror.esplightcontroller.databinding.DeviceRecyclerviewBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.subjects.PublishSubject;

public class AllDevicesRecyclerAdapter extends DataBindingAdapter<BaseDevice, DeviceRecyclerviewBinding> {

    private PublishSubject<BaseDevice> powerButtonDevice = PublishSubject.create();
    private PublishSubject<BaseDevice> errorButtonPressed = PublishSubject.create();

    private static final DiffUtil.ItemCallback<BaseDevice> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BaseDevice>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull BaseDevice oldItem, @NonNull BaseDevice newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull BaseDevice oldItem,
                                                  @NonNull BaseDevice newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private LayoutInflater layoutInflater;

    private int maxCount = -1;

    public AllDevicesRecyclerAdapter() {
        super(DIFF_CALLBACK);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public DeviceViewHolder<BaseDevice, DeviceRecyclerviewBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        final DeviceRecyclerviewBinding binding = DeviceRecyclerviewBinding.inflate(layoutInflater, parent, false);
        return new DeviceViewHolder<>(binding);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder<BaseDevice, DeviceRecyclerviewBinding> holder, int position) {
        super.onBindViewHolder(holder, position);
        RxView.clicks(holder.binding.powerToggle).takeUntil(RxView.detaches(holder.itemView)).subscribe(unit -> powerButtonDevice.onNext(getItem(holder.getAdapterPosition())));
        RxView.clicks(holder.binding.errorButton).takeUntil(RxView.detaches(holder.itemView)).subscribe(unit -> errorButtonPressed.onNext(getItem(holder.getAdapterPosition())));
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        if (maxCount == -1) {
            return super.getItemCount();
        } else {
            return Math.min(maxCount, super.getItemCount());
        }
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public PublishSubject<BaseDevice> getPowerButtonClicked() {
        return powerButtonDevice;
    }

    public PublishSubject<BaseDevice> getErrorButtonPressed() {
        return errorButtonPressed;
    }
}
