package com.errorerrorerror.esplightcontroller.ui.adapters.viewholder;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.errorerrorerror.esplightcontroller.BR;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.ui.widgets.recyclerview.BaseViewHolder;

public class DeviceViewHolder<D extends ViewDataBinding> extends BaseViewHolder<Device, D> {

    public DeviceViewHolder(@NonNull D dataBinding) {
        super(dataBinding);
    }

    public void bind(final Device object) {
        binding.setVariable(BR.device, object);
        binding.executePendingBindings();
    }
}
