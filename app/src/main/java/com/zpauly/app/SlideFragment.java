package com.zpauly.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpauly.swipeLayout.SlideDirection;
import com.zpauly.swipeLayout.SlideLayout;

/**
 * Created by zpauly on 2016/11/20.
 */

public class SlideFragment extends Fragment {
    private static final String TAG = SlideFragment.class.getName();

    public static final String DIRECTION = "DIRECTION";
    public static final int LEFT = 0;
    public static final int UP = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;

    private SlideLayout mSlideLayout;

    private int direction;

    public static SlideFragment create(int direction) {
        SlideFragment slideFragment = new SlideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DIRECTION, direction);
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParams();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        mSlideLayout = (SlideLayout) view.findViewById(R.id.sl_layout);
        switch (direction) {
            case LEFT:
                mSlideLayout.setSlideDirection(SlideDirection.DIRECTION_LEFT);
                break;
            case UP:
                mSlideLayout.setSlideDirection(SlideDirection.DIRECTION_UP);
                break;
            case RIGHT:
                mSlideLayout.setSlideDirection(SlideDirection.DIRECTION_RIGHT);
                break;
            case DOWN:
                mSlideLayout.setSlideDirection(SlideDirection.DIRECTION_DOWN);
                break;
            default:
                break;
        }
        mSlideLayout.setRatioOfResistance(0f);
        return view;
    }

    private void getParams() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            direction = bundle.getInt(DIRECTION);
        }
    }
}
