package com.errorerrorerror.esplightcontroller.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.MainActivity;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.adapters.AllDevicesRecyclerAdapter;
import com.errorerrorerror.esplightcontroller.databinding.HomeFragmentBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.widget.RxPopupMenu;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends BaseFragment<HomeFragmentBinding> implements MenuItem.OnMenuItemClickListener {

    private AllDevicesRecyclerAdapter adapter;

    @Override
    public int getLayoutRes() {
        return R.layout.home_fragment;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerViews();

    }

    private void initRecyclerViews() {
        adapter = new AllDevicesRecyclerAdapter();
        adapter.setMaxCount(6);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        binding.recyclerViewUnassignedDevices.setLayoutManager(layoutManager);
        binding.recyclerViewUnassignedDevices.setAdapter(adapter);
        binding.recyclerViewUnassignedDevices.setHasFixedSize(false);

        disposable.add(adapter.getClickedObjectsObservable().debounce(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onDeviceClicked));
        disposable.add(adapter.getLongClickedObjectsObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(this::onDeviceLongClicked));
        disposable.add(viewModel.getAllDevices().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(baseDevices -> adapter.setData(baseDevices)));
    }

    @Override
    protected void initToolbar() {
        binding.includeLayout.toolbar.setTitle(getFragmentTitle());
        binding.includeLayout.toolbar.inflateMenu(R.menu.dashboard_toolbar_menu);

        ((MainActivity)getActivity()).setToolbarToDrawer(binding.includeLayout.toolbar);

        disposable.add(RxToolbar.itemClicks(binding.includeLayout.toolbar).debounce(300, TimeUnit.MILLISECONDS).subscribe(this::onMenuItemClick));
    }

    private void addNewRecyclerView(String groupName) {
    }

    private View findViewFromDevice(BaseDevice device) {
        int position;
        for (position = 0; position < adapter.getItemCount(); position++) {
           if (adapter.getItemId(position) == device.getId()) {
               break;
           }
        }

        return binding.recyclerViewUnassignedDevices.getChildAt(position);
    }

    private void onDeviceLongClicked(BaseDevice device) {
        PopupMenu popup = new PopupMenu(getContext(), findViewFromDevice(device));

        popup.inflate(R.menu.popup_menu);

        disposable.add(RxPopupMenu.itemClicks(popup).subscribe(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit_menu:
                    //handle menu1 click
                    break;
                case R.id.delete_menu:
                    viewModel.deleteDevice(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                    break;
            }
        }));

        popup.show();
    }

    private void onDeviceClicked(BaseDevice device) {
        Bundle bundle = new Bundle();
        bundle.putLong(ID_BUNDLE, device.getId());
        bundle.putString(DeviceConfigurationFragment.TITLE_BUNDLE, device.getDeviceName());
        DeviceConfigurationFragment fragment = BaseFragment.newInstance(DeviceConfigurationFragment.class, bundle);
        ((MainActivity) getActivity()).addFragmentToTop(fragment);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        boolean clickedOnMenu = false;

        if (id == R.id.action_add_device_menu) {
            AddDeviceScanFragment fragment = BaseFragment.newInstance(AddDeviceScanFragment.class, null);
            ((MainActivity) Objects.requireNonNull(getActivity())).addFragmentToTop(fragment);
            clickedOnMenu = true;
        }

        return clickedOnMenu;
    }
}