package com.errorerrorerror.esplightcontroller.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.utils.RxBus;
import com.errorerrorerror.esplightcontroller.viewmodel.DevicesCollectionViewModel;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    protected final String TAG = getClass().getSimpleName();

    protected CompositeDisposable disposable = new CompositeDisposable();
    protected T binding;

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;
    protected DevicesCollectionViewModel viewModel;

    @Inject
    protected RxBus eventBus;

    public String getFragmentTitle() {
        return "Dashboard"; // Default Base Title
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public abstract void injectFragment();

    public static <T extends BaseFragment> T newInstance(Class<T> clazz, @Nullable Bundle args)  {
        T instance = null;
        try {
            instance = clazz.newInstance();
            instance.setArguments(args);
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectFragment();
        if (getArguments() != null) {
            containsArguments();
        }
    }

    abstract protected void containsArguments();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DevicesCollectionViewModel.class);
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.requestApplyInsets(view);
        initToolbar();
        initializeFragment();
    }

    protected abstract void initializeFragment();

    protected void initToolbar() { }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        disposable = null;
    }
}
