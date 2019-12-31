package com.errorerrorerror.esplightcontroller.ui.modules.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.ui.adapters.DevicesRecyclerViewAdapter;
import com.errorerrorerror.esplightcontroller.ui.adapters.viewholder.DeviceViewHolder;
import com.errorerrorerror.esplightcontroller.databinding.DeviceRecyclerviewBinding;
import com.errorerrorerror.esplightcontroller.databinding.HomeFragmentBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.add.AddDeviceScanFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.DeviceConfigActivity;
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

    private DevicesRecyclerViewAdapter adapter;

    private List<ObservableSocket> socketList = new ArrayList<>();

    private List<Device> oldList = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.home_fragment;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void containsArguments() {
        /// Empty
    }

    @Override
    protected void initializeFragment() {
        initRecyclerViews();
    }

    private void initRecyclerViews() {
        adapter = new DevicesRecyclerViewAdapter();
        adapter.setMaxCount(6);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        binding.recyclerViewUnassignedDevices.setLayoutManager(layoutManager);
        binding.recyclerViewUnassignedDevices.setAdapter(adapter);
        binding.recyclerViewUnassignedDevices.setHasFixedSize(false);
        binding.recyclerViewUnassignedDevices.addItemDecoration(new RecyclerView.ItemDecoration() {
            int spaceHeight = (int) getResources().getDimension(R.dimen.item_padding_recycler);

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) % 2 == 0) {
                    outRect.right = spaceHeight/2;
                } else {
                    outRect.left = spaceHeight/2;
                }
                outRect.top = spaceHeight/2;
                outRect.bottom = spaceHeight/2;
            }
        });

        disposable.add(adapter.getClickedObjectsObservable().debounce(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onDeviceClicked));
        disposable.add(adapter.getLongClickedObjectsObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(this::onItemLongClicked));
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

    private void connectToSocketObservable(List<Device> devices) {
        for (Device device : devices) {
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

    private List<Device> getNewOrChangedDevices(List<Device> roomList) {
        List<Device> changedItems = new ArrayList<>(roomList);

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

    private DeviceViewHolder<DeviceRecyclerviewBinding> findDevicePositionOnView(Device device) {
        int position;
        for (position = 0; position < adapter.getItemCount(); position++) {
           if (adapter.getItemId(position) == device.getId()) {
               break;
           }
        }

        return (DeviceViewHolder<DeviceRecyclerviewBinding>) binding.recyclerViewUnassignedDevices.findViewHolderForAdapterPosition(position);
    }

    private void onItemLongClicked(Device device) {
        PopupMenu popup = new PopupMenu(getContext(), findDevicePositionOnView(device).itemView);

        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

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

    private void showErrorButtonPopup(Device device) {
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
                .setMessage("Error connecting to " + device.getName() + ".\n" +
                        "IP: " + device.getIp() + "\n" +
                        "Port: " + device.getPort() + " \n" +
                        "Error: " + connect)
                .setPositiveButton("Ok", null)
                .show();
    }

    private void onConnectionStateChanged(ObservableSocket.ObservableDevice observableDevice) {
        final Device device = observableDevice.getDevice();
        final @ObservableSocket.ConnectionState int connection = observableDevice.getConnection();
        Log.d(TAG, "onConnectionStateChanged: " + device.getName() + " on: " + device.isOn() + " connection: " + connection);
        final DeviceViewHolder<DeviceRecyclerviewBinding> holder = findDevicePositionOnView(device);
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
    private void onPowerButtonClicked(Device device) {
        final DeviceViewHolder<DeviceRecyclerviewBinding> viewHolder = findDevicePositionOnView(device);
        boolean switchDevice = viewHolder.binding.powerToggle.isChecked();
        device.setOn(switchDevice);
        disposable.add(viewModel.updateDevice(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
    }

    private void onDeviceClicked(Device device) {
        Intent intent = new Intent();
        intent.setClass(getActivity().getBaseContext(), DeviceConfigActivity.class);
        intent.putExtra("com.errorerrorerror.device", device);
        startActivity(intent);
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