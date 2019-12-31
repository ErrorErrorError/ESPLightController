package com.errorerrorerror.esplightcontroller.ui.adapters;

import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.data.ThemesSettingsModel;
import com.errorerrorerror.esplightcontroller.databinding.CheckboxRowItemBinding;
import com.errorerrorerror.esplightcontroller.ui.widgets.list.BaseItemListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThemesAdapter extends BaseItemListAdapter<ThemesSettingsModel, CheckboxRowItemBinding> {

    public ThemesAdapter(@NotNull ArrayList<ThemesSettingsModel> list) {
        super(list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.checkbox_row_item;
    }

    @Override
    protected ItemViewHolder createViewHolder(CheckboxRowItemBinding itemBinding) {
        return new ItemViewHolder(itemBinding) {
            @Override
            protected void setItemToView(ThemesSettingsModel item) {
                binding.iconItemTitle.setText(item.getTitle());
                binding.themeCheckbox.setChecked(item.isActive());
            }
        };
    }

}