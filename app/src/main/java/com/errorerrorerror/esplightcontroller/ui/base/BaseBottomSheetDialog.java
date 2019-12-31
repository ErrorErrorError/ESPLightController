package com.errorerrorerror.esplightcontroller.ui.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.errorerrorerror.esplightcontroller.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseBottomSheetDialog<B extends ViewDataBinding> extends BottomSheetDialogFragment {

    protected final String TAG = this.getClass().getSimpleName();
    protected CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    protected SharedPreferences preferences;
    protected B binding;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @LayoutRes
    protected abstract int layoutRes();

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutRes(), container, false);

        View view = binding.getRoot();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                View parent = Objects.requireNonNull(getDialog()).findViewById(R.id.design_bottom_sheet);
                if (parent != null) {
                    bottomSheetBehavior = BottomSheetBehavior.from(parent);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
        disposable.dispose();
    }
}