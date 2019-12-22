package com.errorerrorerror.esplightcontroller.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.BoolRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class is basically MaterialButtonToggleGroup but it allows to keep the shape of the button
 */

public class ToggleGroup extends LinearLayout {

    public interface OnButtonCheckedListener {

        void onButtonChecked(ToggleGroup group, @IdRes int checkedId, boolean isChecked);
    }

    private static final String LOG_TAG = MaterialButton.class.getSimpleName();

    private final CheckedStateTracker checkedStateTracker = new CheckedStateTracker();
    private final LinkedHashSet<OnButtonCheckedListener> onButtonCheckedListeners =
            new LinkedHashSet<>();
    private final Comparator<MaterialButton> childOrderComparator =
            (v1, v2) -> {
                int checked = Boolean.valueOf(v1.isChecked()).compareTo(v2.isChecked());
                if (checked != 0) {
                    return checked;
                }

                int stateful = Boolean.valueOf(v1.isPressed()).compareTo(v2.isPressed());
                if (stateful != 0) {
                    return stateful;
                }

                // don't return 0s
                return Integer.valueOf(indexOfChild(v1)).compareTo(indexOfChild(v2));
            };

    private Integer[] childOrder;
    private boolean skipCheckedStateTracker = false;
    private boolean singleSelection;
    @IdRes
    private int checkedId;

    public ToggleGroup(@NonNull Context context) {
        this(context, null);
    }

    public ToggleGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleGroup(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setSingleSelection(true);
        checkedId = View.NO_ID;
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Checks the appropriate button as requested via XML
        if (checkedId != View.NO_ID) {
            checkForced(checkedId);
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        updateChildOrder();
        super.dispatchDraw(canvas);
    }

    /**
     * This override prohibits Views other than {@link com.google.android.material.button.MaterialButton} to be added. It also makes
     * updates to the add button shape and margins.
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!(child instanceof MaterialButton)) {
            Log.e(LOG_TAG, "Child views must be of type MaterialButton.");
            return;
        }

        super.addView(child, index, params);
        final MaterialButton buttonChild = (MaterialButton) child;
        setGeneratedIdIfNeeded(buttonChild);

        setupButtonChild(buttonChild);

        if (buttonChild.isChecked()) {
            updateCheckedStates(buttonChild.getId(), true);
            setCheckedId(buttonChild.getId());
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

        if (child instanceof MaterialButton) {
            ((MaterialButton) child).removeOnCheckedChangeListener(checkedStateTracker);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @NonNull
    @Override
    public CharSequence getAccessibilityClassName() {
        return ToggleGroup.class.getName();
    }

    public void check(@IdRes int id) {
        if (id == checkedId) {
            return;
        }

        checkForced(id);
    }

    public void uncheck(@IdRes int id) {
        setCheckedStateForView(id, false);
        updateCheckedStates(id, false);
        checkedId = View.NO_ID;
        dispatchOnButtonChecked(id, false);
    }

    public void clearChecked() {
        skipCheckedStateTracker = true;
        for (int i = 0; i < getChildCount(); i++) {
            MaterialButton child = getChildButton(i);
            child.setChecked(false);

            dispatchOnButtonChecked(child.getId(), false);
        }
        skipCheckedStateTracker = false;

        setCheckedId(View.NO_ID);
    }

    @IdRes
    public int getCheckedButtonId() {
        return singleSelection ? checkedId : View.NO_ID;
    }

    @NonNull
    public List<Integer> getCheckedButtonIds() {
        ArrayList<Integer> checkedIds = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            MaterialButton child = getChildButton(i);
            if (child.isChecked()) {
                checkedIds.add(child.getId());
            }
        }

        return checkedIds;
    }

    public void addOnButtonCheckedListener(@NonNull OnButtonCheckedListener listener) {
        onButtonCheckedListeners.add(listener);
    }

    public void removeOnButtonCheckedListener(@NonNull OnButtonCheckedListener listener) {
        onButtonCheckedListeners.remove(listener);
    }

    public void clearOnButtonCheckedListeners() {
        onButtonCheckedListeners.clear();
    }

    /**
     * Returns whether this group only allows a single button to be checked.
     *
     * @return whether this group only allows a single button to be checked
     * @attr ref R.styleable#ToggleGroup_singleSelection
     */
    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        if (this.singleSelection != singleSelection) {
            this.singleSelection = singleSelection;

            clearChecked();
        }
    }

    public void setSingleSelection(@BoolRes int id) {
        setSingleSelection(getResources().getBoolean(id));
    }

    private void setCheckedStateForView(@IdRes int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView instanceof MaterialButton) {
            skipCheckedStateTracker = true;
            ((MaterialButton) checkedView).setChecked(checked);
            skipCheckedStateTracker = false;
        }
    }

    private void setCheckedId(int checkedId) {
        this.checkedId = checkedId;

        dispatchOnButtonChecked(checkedId, true);
    }


    private MaterialButton getChildButton(int index) {
        return (MaterialButton) getChildAt(index);
    }

    private int getFirstVisibleChildIndex() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (isChildVisible(i)) {
                return i;
            }
        }

        return -1;
    }

    private int getLastVisibleChildIndex() {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            if (isChildVisible(i)) {
                return i;
            }
        }

        return -1;
    }

    private boolean isChildVisible(int i) {
        View child = getChildAt(i);
        return child.getVisibility() != View.GONE;
    }

    private void updateCheckedStates(int childId, boolean childIsChecked) {
        for (int i = 0; i < getChildCount(); i++) {
            MaterialButton button = getChildButton(i);
            if (button.isChecked()) {
                if (singleSelection && childIsChecked && button.getId() != childId) {
                    setCheckedStateForView(button.getId(), false);
                    dispatchOnButtonChecked(button.getId(), false);
                }
            }
        }
    }

    private void dispatchOnButtonChecked(@IdRes int buttonId, boolean checked) {
        for (OnButtonCheckedListener listener : onButtonCheckedListeners) {
            listener.onButtonChecked(this, buttonId, checked);
        }
    }

    private void checkForced(int checkedId) {
        setCheckedStateForView(checkedId, true);
        updateCheckedStates(checkedId, true);
        setCheckedId(checkedId);
    }

    private void setGeneratedIdIfNeeded(@NonNull MaterialButton toggleButton) {
        // Generates an ID if none is set, for relative positioning purposes
        if (toggleButton.getId() == View.NO_ID) {
            toggleButton.setId(ViewCompat.generateViewId());
        }
    }


    private void setupButtonChild(@NonNull MaterialButton buttonChild) {
        buttonChild.setMaxLines(1);
        buttonChild.setEllipsize(TextUtils.TruncateAt.END);

        buttonChild.addOnCheckedChangeListener(checkedStateTracker);
    }

    @NonNull
    private LinearLayout.LayoutParams buildLayoutParams(@NonNull View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            return (LayoutParams) layoutParams;
        }

        LinearLayout.LayoutParams newParams =
                new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);

        return newParams;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (childOrder == null || i >= childOrder.length) {
            Log.w(LOG_TAG, "Child order wasn't updated");
            return i;
        }

        return childOrder[i];
    }

    private void updateChildOrder() {
        final SortedMap<MaterialButton, Integer> viewToIndexMap = new TreeMap<>(childOrderComparator);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            viewToIndexMap.put(getChildButton(i), i);
        }

        childOrder = viewToIndexMap.values().toArray(new Integer[0]);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = false;
        if (singleSelection) {
            if (!(getCheckedButtonId() == View.NO_ID)) {
                Rect rect = new Rect();
                findViewById(getCheckedButtonId()).getHitRect(rect);

                shouldIntercept = rect.contains((int) ev.getX(), (int) ev.getY());
            }
        }

        return shouldIntercept;
    }

    private class CheckedStateTracker implements MaterialButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(MaterialButton button, boolean isChecked) {
            // Prevents infinite recursion
            if (skipCheckedStateTracker) {
                return;
            }

            if (singleSelection) {
                checkedId = isChecked ? button.getId() : View.NO_ID;
            }

            dispatchOnButtonChecked(button.getId(), isChecked);
            updateCheckedStates(button.getId(), isChecked);
            invalidate();
        }
    }
}
