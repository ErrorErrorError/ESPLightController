package com.errorerrorerror.esplightcontroller.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.errorerrorerror.esplightcontroller.databinding.DeviceRecyclerviewBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.ui.adapters.viewholder.DeviceViewHolder;
import com.errorerrorerror.esplightcontroller.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.subjects.PublishSubject;

public class DevicesRecyclerViewAdapter extends BaseRecyclerAdapter<Device, DeviceViewHolder<DeviceRecyclerviewBinding>> {

    private PublishSubject<Device> powerButtonDevice = PublishSubject.create();
    private PublishSubject<Device> errorButtonPressed = PublishSubject.create();

    private static final DiffUtil.ItemCallback<Device> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Device>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Device oldItem, @NonNull Device newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Device oldItem,
                                                  @NonNull Device newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private LayoutInflater layoutInflater;

    private int maxCount = -1;

    public DevicesRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public DeviceViewHolder<DeviceRecyclerviewBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        final DeviceRecyclerviewBinding binding = DeviceRecyclerviewBinding.inflate(layoutInflater, parent, false);
        return new DeviceViewHolder<>(binding);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder<DeviceRecyclerviewBinding> holder, int position) {
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

    public PublishSubject<Device> getPowerButtonClicked() {
        return powerButtonDevice;
    }

    public PublishSubject<Device> getErrorButtonPressed() {
        return errorButtonPressed;
    }
}
