package com.errorerrorerror.esplightcontroller.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.errorerrorerror.esplightcontroller.BR;
import com.errorerrorerror.esplightcontroller.interfaces.OnItemClickedListener;

public class DeviceViewHolder<T> extends RecyclerView.ViewHolder {

    @NonNull
    public final ViewDataBinding binding;

    DeviceViewHolder(@NonNull ViewDataBinding view) {
        super(view.getRoot());
        binding = view;
    }

    public void bind(final T object) {
        binding.setVariable(BR.baseDevice, object);
        binding.executePendingBindings();
    }
}
