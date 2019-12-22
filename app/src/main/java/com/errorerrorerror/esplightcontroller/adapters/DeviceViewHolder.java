package com.errorerrorerror.esplightcontroller.adapters;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.errorerrorerror.esplightcontroller.BR;

public class DeviceViewHolder<T, V extends ViewDataBinding> extends RecyclerView.ViewHolder {

    @NonNull
    public final V binding;

    DeviceViewHolder(@NonNull V view) {
        super(view.getRoot());
        binding = view;
    }

    public void bind(final T object) {
        binding.setVariable(BR.baseDevice, object);
        binding.executePendingBindings();
    }
}
