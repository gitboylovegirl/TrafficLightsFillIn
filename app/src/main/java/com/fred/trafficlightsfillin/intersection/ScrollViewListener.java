package com.fred.trafficlightsfillin.intersection;

import com.fred.trafficlightsfillin.base.ObservableScrollView;

public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView observableScrollView, int x, int y , int oldx, int oldy);
}
