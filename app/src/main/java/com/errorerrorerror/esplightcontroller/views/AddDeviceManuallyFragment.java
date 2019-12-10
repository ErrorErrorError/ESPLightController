package com.errorerrorerror.esplightcontroller.views;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.MainActivity;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.AddDeviceManuallyBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolid;
import com.errorerrorerror.esplightcontroller.utils.ValidationUtil;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddDeviceManuallyFragment extends BaseFragment<AddDeviceManuallyBinding> {

    private static final String TAG = "AddDeviceManuallyDebug";

    private long mode = -1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposable.add(viewModel.getAllDevices().subscribe(this::add));
    }

    @Override
    protected void initToolbar() {
        binding.includeLayout.toolbar.setTitle(getFragmentTitle());
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_arrow_icon);

        disposable.add(RxToolbar.navigationClicks(binding.includeLayout.toolbar).subscribe(unit -> getActivity().onBackPressed()));
    }


    @Override
    public String getFragmentTitle() {
        return "Manually";
    }

    @Override
    public int getLayoutRes() {
        return R.layout.add_device_manually;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }


    private void add(@NonNull List<BaseDevice> devicesList) {
        disposable.add(
                observableValidation(devicesList, 1)
                        .flatMap(completable -> RxView.clicks(binding.addDeviceButton))
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .map(unit -> new BaseDevice(binding.deviceNameInput.getText().toString(),
                                binding.ipAddressInput.getText().toString(),
                                binding.portInput.getText().toString(),
                                true,
                                80,
                                BaseDevice.EMPTY_GROUP)
                        )
                        .map(device -> new DeviceSolid(
                                device,
                                Color.RED))
                        .flatMapCompletable(deviceMusic -> viewModel.insertDevice(deviceMusic)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> ((MainActivity) getActivity()).backToHomeScreen()))
                        .subscribe(() -> Log.d(TAG, "add: SUCCESS"), error -> Log.e(TAG, "add: ", error))
        );
    }

    private Observable<Boolean> observableValidation(@NonNull List<BaseDevice> devices, int skip) {
        Observable<String> deviceNameObservable = RxTextView.afterTextChangeEvents(binding.deviceNameInput)
                .skip(skip)
                .map(i -> i.getView().getText().toString())
                .map(CharSequence::toString)
                .map(String::trim);

        Observable<String> ipAddressObservable = RxTextView.afterTextChangeEvents(binding.ipAddressInput)
                .skip(skip)
                .map(i -> i.getView().getText().toString())
                .map(CharSequence::toString);

        Observable<Boolean> isPortValid = RxTextView.afterTextChangeEvents(binding.portInput)
                .skip(skip)
                .map(i -> i.getView().getText().toString())
                .map(CharSequence::toString)
                .map(String::trim)
                .map(ValidationUtil::portValid)
                .map(i -> ValidationUtil.validation(i, false, binding.portTextLayout, null, ValidationUtil.INVALID_PORT));

        Observable<Boolean> canAddName = deviceNameObservable
                .map(ValidationUtil::nameValid)
                .zipWith(deviceNameObservable
                        .map(i -> ValidationUtil.nameRepeated(devices, i, mode)), (valid, repeated) -> ValidationUtil.validation(valid, repeated, binding.deviceNameTextLayout, ValidationUtil.USED_NAME, ValidationUtil.INVALID_NAME));
        Observable<Boolean> canAddIp = ipAddressObservable
                .map(ValidationUtil::ipValid)
                .zipWith(ipAddressObservable
                        .map(i -> ValidationUtil.ipRepeated(devices, i, mode)), (valid, repeated) -> ValidationUtil.validation(valid, repeated, binding.ipAddressTextLayout, ValidationUtil.USED_IP, ValidationUtil.INVALID_IP));


        return ValidationUtil.isAllValid(canAddName, canAddIp, isPortValid).map(aBoolean -> {
            binding.addDeviceButton.setEnabled(aBoolean);
            return aBoolean;
        });
    }

}
