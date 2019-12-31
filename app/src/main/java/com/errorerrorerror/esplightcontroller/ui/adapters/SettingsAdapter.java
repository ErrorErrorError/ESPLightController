package com.errorerrorerror.esplightcontroller.ui.adapters;

import android.view.View;

import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.data.SettingsModel;
import com.errorerrorerror.esplightcontroller.databinding.IconRowItemBinding;
import com.errorerrorerror.esplightcontroller.ui.widgets.list.BaseItemListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SettingsAdapter extends BaseItemListAdapter<SettingsModel, IconRowItemBinding> {

    public SettingsAdapter(@NotNull ArrayList<SettingsModel> list) {
        super(list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.icon_row_item;
    }

    @Override
    protected ItemViewHolder createViewHolder(IconRowItemBinding itemBinding) {
        return new ItemViewHolder(itemBinding) {
            @Override
            protected void setItemToView(SettingsModel item) {
                binding.iconItemSummary.setVisibility(View.GONE);
                binding.iconItemImage.setImageDrawable(item.getImage());
                binding.iconItemTitle.setText(item.getTitle());
            }
        };
    }
}
