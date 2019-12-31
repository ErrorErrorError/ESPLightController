package com.errorerrorerror.esplightcontroller.ui.widgets.recyclerview;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T, D extends ViewDataBinding> extends RecyclerView.ViewHolder {
    @NonNull
    public final D binding;

    @SuppressLint("CheckResult")
    public BaseViewHolder(@NonNull D view) {
        super(view.getRoot());
        binding = view;
    }

    public abstract void bind(final T object);
}
