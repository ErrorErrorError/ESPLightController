package com.errorerrorerror.esplightcontroller.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.errorerrorerror.esplightcontroller.adapters.DeviceViewHolder;
import com.errorerrorerror.esplightcontroller.databinding.DeviceRecyclerviewBinding;
import com.errorerrorerror.esplightcontroller.databinding.HomeFragmentBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.utils.ObservableSocket;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.widget.RxPopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends BaseFragment<HomeFragmentBinding> implements MenuItem.OnMenuItemClickListener {

    private AllDevicesRecyclerAdapter adapter;

    private List<ObservableSocket> socketList = new ArrayList<>();

    private List<BaseDevice> oldList = new ArrayList<>();

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
        disposable.add(viewModel.getAllDevices().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(adapter::submitList));
        disposable.add(adapter.getPowerButtonClicked().observeOn(AndroidSchedulers.mainThread()).subscribe(this::onPowerButtonClicked));
        disposable.add(adapter.getErrorButtonPressed().observeOn(AndroidSchedulers.mainThread()).subscribe(this::showErrorButtonPopup));

        /// Network Handling
        disposable.add(viewModel.getAllDevices()
                .delay(100, TimeUnit.MILLISECONDS)
                .map(this::getNewOrChangedDevices)
                .subscribeOn(Schedulers.io())
                .subscribe(this::connectToSocketObservable)
        );


    }

    private void connectToSocketObservable(List<BaseDevice> baseDevices) {
        for (BaseDevice device : baseDevices) {
            boolean foundSocket = false;

            int count = 0;
            while (!foundSocket && count < socketList.size()) {
                final ObservableSocket socket = socketList.get(count);

                if (socket.getId() == device.getId()) {
                    socket.updateDevice(device);
                    foundSocket = true;

                } else {
                    count++;
                }
            }

            if (!foundSocket) {
                ObservableSocket socket = new ObservableSocket(device);
                socketList.add(socket);
                disposable.add(socket.getObservableConnection().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onConnectionStateChanged));
                socket.startObservingConnection();
            }
        }
    }

    private List<BaseDevice> getNewOrChangedDevices(List<BaseDevice> roomList) {
        List<BaseDevice> changedItems = new ArrayList<>(roomList);

        changedItems.removeAll(oldList);

        oldList = roomList;
        return changedItems;
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

    private DeviceViewHolder<BaseDevice, DeviceRecyclerviewBinding> findDevicePositionOnView(BaseDevice device) {
        int position;
        for (position = 0; position < adapter.getItemCount(); position++) {
           if (adapter.getItemId(position) == device.getId()) {
               break;
           }
        }

        return (DeviceViewHolder<BaseDevice, DeviceRecyclerviewBinding>) binding.recyclerViewUnassignedDevices.findViewHolderForAdapterPosition(position);
    }

    private void onDeviceLongClicked(BaseDevice device) {
        PopupMenu popup = new PopupMenu(getContext(), findDevicePositionOnView(device).itemView);

        popup.inflate(R.menu.popup_menu);

        disposable.add(RxPopupMenu.itemClicks(popup).subscribe(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit_menu:
                    //handle menu1 click
                    break;
                case R.id.delete_menu:
                    oldList.remove(device);
                    for (ObservableSocket socket : socketList) {
                        if (socket.getId() == device.getId()) {
                            socket.removeSelf();
                            socketList.remove(socket);
                            break;
                        }
                    }
                    viewModel.deleteDevice(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                    break;
            }
        }));

        popup.show();
    }

    private void showErrorButtonPopup(BaseDevice device) {
        @ObservableSocket.ConnectionState int errorCode = ObservableSocket.COULD_NOT_CONNECT;
        for (ObservableSocket socket : socketList) {
            if (socket.getId() == device.getId()) {
                errorCode = socket.getConnectionState();
                break;
            }
        }

        String connect = ObservableSocket.connectionErrorAsString(errorCode, false);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Connection Error")
                .setMessage("Error connecting to " + device.getDeviceName() + ".\n" +
                        "IP: " + device.getIp() + "\n" +
                        "Port: " + device.getPort() + " \n" +
                        "Error: " + connect)
                .setPositiveButton("Ok", null)
                .show();
    }

    private void onConnectionStateChanged(ObservableSocket.ObservableDevice observableDevice) {
        final BaseDevice device = observableDevice.getDevice();
        final @ObservableSocket.ConnectionState int connection = observableDevice.getConnection();
        Log.d(TAG, "onConnectionStateChanged: " + device.getDeviceName() + " on: " + device.isOn() + " connection: " + connection);
        final DeviceViewHolder<BaseDevice, DeviceRecyclerviewBinding> holder = findDevicePositionOnView(device);
        if (holder == null) { return; }

        String connect = ObservableSocket.connectionErrorAsString(connection, true);

        holder.binding.connectionStatus.setText(connect);

        if (connection != ObservableSocket.CONNECTING && connection != ObservableSocket.CONNECTED && connection != ObservableSocket.DISCONNECTED) {
            holder.binding.errorButton.setVisibility(View.VISIBLE);
        } else {
            holder.binding.errorButton.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void onPowerButtonClicked(BaseDevice device) {
        final DeviceViewHolder<BaseDevice, DeviceRecyclerviewBinding> viewHolder = findDevicePositionOnView(device);
        boolean switchDevice = viewHolder.binding.powerToggle.isChecked();
        device.setOn(switchDevice);
        disposable.add(viewModel.updateDevice(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
    }

    private void onDeviceClicked(BaseDevice device) {
        Bundle bundle = new Bundle();
        bundle.putLong(ID_BUNDLE, device.getId());
        bundle.putString(TITLE_BUNDLE, device.getDeviceName());
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