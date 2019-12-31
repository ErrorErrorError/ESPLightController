package com.errorerrorerror.esplightcontroller.ui.widgets.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;

public abstract class BaseItemListAdapter<M, B extends ViewDataBinding> extends BaseAdapter {

    @NonNull
    private ArrayList<M> list;

    public BaseItemListAdapter(@NotNull ArrayList<M> list) {
        this.list = list;
    }

    public void setList(@NonNull ArrayList<M> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public M getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder;
        if (convertView == null) {
            B itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutId(), parent, false);
            viewHolder = createViewHolder(itemBinding);
            viewHolder.getView().setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }

        viewHolder.setItemToView(getItem(position));
        return viewHolder.getView();
    }

    protected abstract ItemViewHolder createViewHolder(B itemBinding);

    @Getter
    protected abstract class ItemViewHolder {
        protected final B binding;
        protected final View view;

        protected ItemViewHolder(B binding) {
            this.binding = binding;
            this.view = binding.getRoot();
        }

        protected abstract void setItemToView(M item);
    }
}
