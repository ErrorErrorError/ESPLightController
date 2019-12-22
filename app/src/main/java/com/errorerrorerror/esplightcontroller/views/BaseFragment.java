package com.errorerrorerror.esplightcontroller.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.errorerrorerror.esplightcontroller.viewmodel.DevicesCollectionViewModel;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    protected final String TAG = getClass().getSimpleName();
    protected final String ID_BUNDLE = "id_bundle";
    protected final String TITLE_BUNDLE = "title_bundle";

    CompositeDisposable disposable = new CompositeDisposable();
    protected T binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    DevicesCollectionViewModel viewModel;

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

    void containsArguments() {
        /// Override for classes who receive arguments///
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DevicesCollectionViewModel.class);
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolbar();
    }

    protected void initToolbar() { }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        disposable = null;
    }
}
