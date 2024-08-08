package com.gis.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by 00382071 on 2019/1/25.
 */

public class KeyboardStatusDetector implements ViewTreeObserver.OnGlobalLayoutListener {

    private KeyBoardVisibilityListener mListener;
    private State preState;
    private View mView;

    @SuppressLint("NewApi")
    public KeyboardStatusDetector registerFragment(Fragment fragment){
        return registerView(fragment.getView());
    }

    public KeyboardStatusDetector registerActivity(Activity activity){
        return registerView(activity.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public KeyboardStatusDetector setKeyBoardVisibilityListener(KeyBoardVisibilityListener listener){
        this.mListener = listener;
        return this;
    }

    public KeyboardStatusDetector registerView(final View view){
        mView = view;
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return this;
    }


    @SuppressLint("NewApi")
    public final static boolean isKeyBoardShow(View view) {
        final int softKeyBoardHeight = 100;
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);

        DisplayMetrics dm = view.getResources().getDisplayMetrics();

        return heightDiff > softKeyBoardHeight * dm.density;
    }

    @SuppressLint("NewApi")
    @Override
    protected void finalize() throws Throwable {
        if (mView != null){
            mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        super.finalize();
    }

    /**
     *
     * 设置默认状态
     *
     * @param state 当设置默认状态后，首次处于state状态时，回调函数{@link KeyBoardVisibilityListener#onChange(boolean)}将不会调用
     *
     * */
    public KeyboardStatusDetector skipDefaultState(State state){
        preState = state;
        return this;
    }

    @Override
    public void onGlobalLayout() {
        if (mListener != null && mView != null){
            if (isKeyBoardShow(mView) && preState != State.SHOW){
                preState = State.SHOW;
                mListener.onChange(true);
            }
            if (!isKeyBoardShow(mView) && preState != State.HIDDEN){
                preState = State.HIDDEN;
                mListener.onChange(false);
            }
        }
    }

    public interface KeyBoardVisibilityListener {
        void onChange(boolean visible);
    }

    public enum State{SHOW, HIDDEN}
}

