package com.errorerrorerror.esplightcontroller.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.MainActivity;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ScanNewDeviceFragmentBinding;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class AddDeviceScanFragment extends BaseFragment<ScanNewDeviceFragmentBinding> {

    private WifiManager wifiManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposable.add(RxView.clicks(binding.addDeviceManuallyButton).debounce(300, TimeUnit.MILLISECONDS).subscribe(unit -> ((MainActivity) getActivity()).addFragmentToTop(BaseFragment.newInstance(AddDeviceManuallyFragment.class, null))));

        wifiManager = (WifiManager)
                getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    Log.d(TAG, "onReceive: scan success!!");
                    scanSuccess();
                } else {
                    // scan failure handling
                    Log.d(TAG, "onReceive: scan failed!!");
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            Log.d(TAG, "onReceive: scan failed!!");
            scanFailure();
        }
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        Log.d(TAG, "scanSuccess: success: " + results);
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        Log.d(TAG, "scanSuccess: failed: " + results);
    }




    @Override
    protected void initToolbar() {
        binding.includeLayout.toolbar.setTitle(getFragmentTitle());
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_arrow_icon);

        disposable.add(RxToolbar.navigationClicks(binding.includeLayout.toolbar).subscribe(unit -> getActivity().onBackPressed()));
    }

    @Override
    public String getFragmentTitle() {
        return "Scan";
    }

    @Override
    public int getLayoutRes() {
        return R.layout.scan_new_device_fragment;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }
}
