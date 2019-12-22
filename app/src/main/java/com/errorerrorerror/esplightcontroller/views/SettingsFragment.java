package com.errorerrorerror.esplightcontroller.views;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.SettingsLayoutBinding;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;

public class SettingsFragment extends BaseFragment<SettingsLayoutBinding> {

    @Override
    public int getLayoutRes() {
        return R.layout.settings_layout;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void initToolbar() {
        binding.includeLayout.toolbar.setTitle(getFragmentTitle());
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_arrow_icon);
        disposable.add(RxToolbar.navigationClicks(binding.includeLayout.toolbar).subscribe(unit -> getActivity().onBackPressed()));
    }

    @Override
    public String getFragmentTitle() {
        return "Settings";
    }
}