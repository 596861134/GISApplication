package com.gis.common.view.banner.indicator;

import android.view.View;

import androidx.annotation.NonNull;

import com.gis.common.view.banner.config.IndicatorConfig;
import com.gis.common.view.banner.listener.OnPageChangeListener;


public interface Indicator extends OnPageChangeListener {
    @NonNull
    View getIndicatorView();

    IndicatorConfig getIndicatorConfig();

    void onPageChanged(int count, int currentPosition);

}
