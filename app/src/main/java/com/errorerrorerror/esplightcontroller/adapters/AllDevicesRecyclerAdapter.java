package com.errorerrorerror.esplightcontroller.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;

import com.errorerrorerror.esplightcontroller.databinding.DeviceRecyclerviewBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;

public class AllDevicesRecyclerAdapter extends DataBindingAdapter<BaseDevice> {

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
                    return oldItem.equals(newItem) && oldItem.isOn().equals(newItem.isOn());
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
    public DeviceViewHolder<BaseDevice> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        final ViewDataBinding binding = DeviceRecyclerviewBinding.inflate(layoutInflater, parent, false);
        return new DeviceViewHolder<>(binding);
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
}
