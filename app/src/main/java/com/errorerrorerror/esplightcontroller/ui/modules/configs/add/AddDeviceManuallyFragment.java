package com.errorerrorerror.esplightcontroller.ui.modules.configs.add;

import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.AddDeviceManuallyBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.modes.SteadyMode;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.main.MainActivity;
import com.errorerrorerror.esplightcontroller.utils.ValidationUtil;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAutoCompleteTextView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class AddDeviceManuallyFragment extends BaseFragment<AddDeviceManuallyBinding> {

    private static final String NO_GROUP = "None";

    private long mode = -1;

    private ArrayAdapter<String> adapter;

    @Override
    protected void initializeFragment() {
        adapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                R.layout.dropdown_menu_item);
        adapter.add(NO_GROUP);

        disposable.add(viewModel.getAllDevices().subscribe(this::add));

        disposable.add(viewModel.getAllDevices().switchMap(Observable::fromIterable).map((Function<? super Device, ? extends String>) Device::getGroup).filter((Predicate<String>) s -> !s.equals(Device.EMPTY_GROUP)).distinct().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                adapter.add(s);
                Log.d(TAG, "accept: " + s);
            }
        }));

        binding.groupNameInput.setAdapter(adapter);

        disposable.add(RxAutoCompleteTextView.itemClickEvents(binding.groupNameInput).subscribe(adapterViewItemClickEvent -> {
            hideKeyboard();
            binding.groupNameInput.dismissDropDown();
        }));

        disposable.add(RxTextView.editorActionEvents(binding.groupNameInput).subscribe(textViewEditorActionEvent -> {
            if (textViewEditorActionEvent.getActionId() == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                binding.groupNameInput.dismissDropDown();
            }
        }));
    }

    @Override
    protected void initToolbar() {
        binding.includeLayout.toolbar.setTitle(getFragmentTitle());
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.back_arrow_icon);

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

    @Override
    protected void containsArguments() {

    }

    private void add(@NonNull List<Device> devicesList) {
        disposable.add(
                observableValidation(devicesList, 1)
                        .flatMap(completable -> RxView.clicks(binding.addDeviceButton))
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .map(unit -> Device.builder()
                                .name(binding.deviceNameInput.getText().toString())
                                .ip(binding.ipAddressInput.getText().toString())
                                .port(Integer.parseInt(binding.portInput.getText().toString()))
                                .group(binding.groupNameInput.getText().toString().equals(NO_GROUP) ? Device.EMPTY_GROUP : binding.groupNameInput.getText().toString())
                                .on(true)
                                .brightness(100)
                                .mode(SteadyMode.getDefaultMode())
                                .build()
                        )
                        .flatMapCompletable(device -> viewModel.insertDevice(device)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> ((MainActivity) getActivity()).backToHomeScreen()))
                        .subscribe(() -> Log.d(TAG, "add: SUCCESS"), error -> Log.e(TAG, "add: ", error))
        );
    }

    private Observable<Boolean> observableValidation(@NonNull List<Device> devices, int skip) {
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

    private void hideKeyboard() {
        if (getActivity().getCurrentFocus() != null) {

            InputMethodManager imm = ContextCompat.getSystemService(getContext(), InputMethodManager.class);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

}
