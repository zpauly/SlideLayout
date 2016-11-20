package com.zpauly.swipeLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import static com.zpauly.swipeLayout.SlideDirection.DIRECTION_DOWN;
import static com.zpauly.swipeLayout.SlideDirection.DIRECTION_LEFT;
import static com.zpauly.swipeLayout.SlideDirection.DIRECTION_RIGHT;
import static com.zpauly.swipeLayout.SlideDirection.DIRECTION_UP;

/**
 * Created by zpauly on 2016/11/19.
 */

public class SlideLayout extends FrameLayout {
    public static final String TAG = SlideLayout.class.getName();

    private static final float DEFAULT_RATIO_OF_RESISTANCE = 0.3f;
    private static final float DEFAULT_RATIO_OF_BACK_TO_SHOW = 0.3f;
    private static final float DEFAULT_RATIO_OF_CHANGE_TEASE_DIRECTION = 0.7f;

    private static final int STATE_SLIDING = 0;
    private static final int STATE_IDLE = 1;

    private ViewDragHelper mDragHelper;
    private DragCallback mDragCallback;

    private boolean hasTwoLayout = false;

    private View mFrontLayout;
    private View mBehindLayout;

    private float startXLocation;
    private float startYLocation;

    private SlideDirection mSlideDirection;
    private float ratioOfResistance;
    private float ratioOfBackToShow;
    private float ratioOfAutoSlideDirectionChange;
    private boolean enableSlideAuto;
    private boolean enableSlideOutside;
    private boolean enableSlide;

    private int mWidth;
    private int mHeight;
    private int mChildWidth;
    private int mChildHeight;
    private int mBackContentWidth;
    private int mBackContentHeight;

    private float mSlideWidth;
    private float mSlideHeight;
    private float movedDistance;
    private float slidingEdgePosition;
    private float edge;

    private int startChildLeft;
    private int startChildTop;
    private int startChildRight;
    private int startChildBottom;

    private int childPaddingLeft;
    private int childPaddingTop;
    private int childPaddingRight;
    private int childPaddingBottom;

    private Callback mCallback;

    public SlideLayout(Context context) {
        super(context);
        init(null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mSlideDirection = DIRECTION_DOWN;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        ratioOfResistance = typedArray.getFloat(R.styleable.SlideLayout_ratioOfResistance, DEFAULT_RATIO_OF_RESISTANCE);
        ratioOfBackToShow = typedArray.getFloat(R.styleable.SlideLayout_ratioOfBackToShow, DEFAULT_RATIO_OF_BACK_TO_SHOW);
        ratioOfAutoSlideDirectionChange = typedArray.getFloat(R.styleable.SlideLayout_ratioOfAutoSlideDirectionChange, DEFAULT_RATIO_OF_CHANGE_TEASE_DIRECTION);
        enableSlide = typedArray.getBoolean(R.styleable.SlideLayout_enableSlide, true);
        enableSlideAuto = typedArray.getBoolean(R.styleable.SlideLayout_enableSlideAuto, true);
        enableSlideOutside = typedArray.getBoolean(R.styleable.SlideLayout_enableSlideOutside, true);
        typedArray.recycle();

        mDragCallback = new DragCallback();
        mDragHelper = ViewDragHelper.create(this, 1.0f, mDragCallback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() == 2) {
            this.hasTwoLayout = true;
        } else if (getChildCount() > 2){
            throw new IllegalStateException("SlideLayout has more than two child.");
        }

        mFrontLayout = getChildAt(1);
        mBehindLayout = getChildAt(0);

        mWidth = getWidth();
        mHeight = getHeight();

        mChildWidth = mFrontLayout.getWidth();
        mChildHeight = mFrontLayout.getHeight();

        computeBackContent();

        mSlideWidth = mChildWidth * ratioOfBackToShow * ratioOfAutoSlideDirectionChange;
        mSlideHeight = mChildHeight * ratioOfBackToShow * ratioOfAutoSlideDirectionChange;

        startChildLeft = mFrontLayout.getLeft();
        startChildTop = mFrontLayout.getTop();
        startChildRight = mFrontLayout.getRight();
        startChildBottom = mFrontLayout.getBottom();



        MarginLayoutParams lp = (MarginLayoutParams) mFrontLayout.getLayoutParams();
        childPaddingLeft = getPaddingLeft() + lp.leftMargin;
        childPaddingTop = getPaddingTop() + lp.topMargin;
        childPaddingRight = getPaddingRight() + lp.rightMargin;
        childPaddingBottom = getPaddingBottom() + lp.bottomMargin;
    }

    private void computeBackContent() {
        switch (mSlideDirection) {
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                this.mBackContentWidth = (int) (mChildWidth * ratioOfBackToShow);
                this.mBackContentHeight = mChildHeight;
                break;
            case DIRECTION_UP:
            case DIRECTION_DOWN:
                this.mBackContentWidth = mChildWidth;
                this.mBackContentHeight = (int) (mChildHeight * ratioOfBackToShow);
                break;
            default:
                this.mBackContentWidth = mChildWidth;
                this.mBackContentHeight = mChildHeight;
                break;
        }
    }

    private boolean horizontalSmoothSlideTo(View child, int slideOffset) {
        if (mDragHelper.smoothSlideViewTo(child, slideOffset, childPaddingTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private boolean verticalSmoothSlideTo(View child, int slideOffset) {
        if (mDragHelper.smoothSlideViewTo(child, childPaddingLeft, slideOffset)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    public void enableSlide(boolean enableScroll) {
        this.enableSlide = enableScroll;
    }

    public void enableSlideAuto(boolean enable) {
        this.enableSlideAuto = enable;
    }

    public void enableSlideOutside(boolean enable) {
        this.enableSlideOutside = enable;
        computeBackContent();
    }

    public void setSlideDirection(SlideDirection direction) {
        this.mSlideDirection = direction;
        computeBackContent();
    }

    public void setRatioOfResistance(float ratio) {
        if (ratio < 0f || ratio > 1f) {
            throw new IllegalArgumentException("ratio should be a positive number above 0 and below 1.");
        }
        this.ratioOfResistance = (1f - ratio);
    }

    public void setRatioOfBackToShow(float ratio) {
        if (ratio < 0f || ratio > 1f) {
            throw new IllegalArgumentException("ratio should be a positive number above 0 and below 1.");
        }
        this.ratioOfBackToShow = ratio;
    }

    public void setRatioOfAutoSlideDirectionChange(float ratio) {
        if (ratio < 0f || ratio > 1f) {
            throw new IllegalArgumentException("ratio should be a positive number above 0 and below 1.");
        }
        this.ratioOfAutoSlideDirectionChange = ratio;
    }

    public boolean isEnableSlide() {
        return enableSlide;
    }

    public boolean isEnableSlideAuto() {
        return enableSlideAuto;
    }

    public boolean isEnableSlideOutside() {
        return enableSlideOutside;
    }

    public SlideDirection getSlideDirection() {
        return mSlideDirection;
    }

    public float getRatioOfResistance() {
        return (1 - ratioOfResistance);
    }

    public float getRatioOfBackToShow() {
        return ratioOfBackToShow;
    }

    public float getRatioOfAutoSlideDirectionChange() {
        return ratioOfAutoSlideDirectionChange;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enableSlide) {
            int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startXLocation = event.getX();
                    startYLocation = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    switch (mSlideDirection) {
                        case DIRECTION_UP:
                            movedDistance = (startYLocation - event.getY()) * ratioOfResistance;
                            event.offsetLocation(0, startYLocation - movedDistance - event.getY());
                            break;
                        case DIRECTION_DOWN:
                            movedDistance = (event.getY() - startYLocation) * ratioOfResistance;
                            event.offsetLocation(0, startYLocation + movedDistance - event.getY());
                            break;
                        case DIRECTION_LEFT:
                            movedDistance = (startXLocation - event.getX()) * ratioOfResistance;
                            event.offsetLocation(startXLocation - movedDistance - event.getX(), 0);
                            break;
                        case DIRECTION_RIGHT:
                            movedDistance = (event.getX() - startXLocation) * ratioOfResistance;
                            event.offsetLocation(startXLocation + movedDistance - event.getX(), 0);
                            break;
                        default:
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private class DragCallback extends ViewDragHelper.Callback {

        private int horizontalDistanceToShow;
        private int verticalDistanceToShow;

        private void computeDistanceToShow() {
            if (enableSlideOutside) {
                horizontalDistanceToShow = mChildWidth;
                verticalDistanceToShow = mChildHeight;
            } else {
                horizontalDistanceToShow = mBackContentWidth;
                verticalDistanceToShow = mBackContentHeight;
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (!enableSlideAuto) {
                return;
            }
            computeDistanceToShow();
            startXLocation = 0;
            switch (mSlideDirection) {
                case DIRECTION_UP:
                    if (mFrontLayout.getBottom() > startChildBottom - mSlideHeight) {
                        verticalSmoothSlideTo(mFrontLayout, startChildTop);
                    } else if (mFrontLayout.getBottom() <= startChildBottom - mSlideHeight) {
                        verticalSmoothSlideTo(mFrontLayout, startChildTop - verticalDistanceToShow);
                    }
                    break;
                case DIRECTION_DOWN:
                    if (mFrontLayout.getTop() < startChildTop + mSlideHeight) {
                        verticalSmoothSlideTo(mFrontLayout, startChildTop);
                    } else if (mFrontLayout.getTop() >= startChildTop + mSlideHeight) {
                        verticalSmoothSlideTo(mFrontLayout, startChildTop + verticalDistanceToShow);
                    }
                    break;
                case DIRECTION_LEFT:
                    if (mFrontLayout.getRight() > startChildRight - mSlideWidth) {
                        horizontalSmoothSlideTo(mFrontLayout, startChildLeft);
                    } else if (mFrontLayout.getRight() <= startChildRight - mSlideWidth) {
                        horizontalSmoothSlideTo(mFrontLayout, startChildLeft - horizontalDistanceToShow);
                    }
                    break;
                case DIRECTION_RIGHT:
                    if (mFrontLayout.getLeft() < startChildLeft + mSlideWidth) {
                        horizontalSmoothSlideTo(mFrontLayout, startChildLeft);
                    } else if (mFrontLayout.getLeft() >= startChildLeft + mSlideWidth) {
                        horizontalSmoothSlideTo(mFrontLayout, startChildLeft + horizontalDistanceToShow);
                    }
                    break;
                default:
                    break;
            }
            movedDistance = 0;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mCallback == null) {
                return;
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
                mCallback.onSlideStateChanged(STATE_SLIDING);
            } else {
                mCallback.onSlideStateChanged(STATE_IDLE);
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (mCallback == null) {
                return;
            }
            mCallback.onSliding(mFrontLayout, mBehindLayout, dx, dy);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean result = ((child == mFrontLayout)
                    && enableSlide);
            return result;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            computeDistanceToShow();
            if (mSlideDirection == DIRECTION_RIGHT) {
                int limitLeft = startChildLeft;
                int limitRight = startChildLeft + horizontalDistanceToShow;
                return Math.max(limitLeft, Math.min(left, limitRight));
            }
            if (mSlideDirection == DIRECTION_LEFT) {
                int limitLeft = startChildLeft - horizontalDistanceToShow;
                int limitRight = startChildLeft;
                return Math.max(limitLeft, Math.min(left, limitRight));
            }
            return startChildLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            computeDistanceToShow();
            if (mSlideDirection == DIRECTION_UP) {
                int limitTop = startChildTop - verticalDistanceToShow;
                int limitBottom = startChildTop;
                return Math.max(limitTop, Math.min(top, limitBottom));
            }
            if (mSlideDirection == DIRECTION_DOWN) {
                int limitTop = startChildTop;
                int limitBottom = startChildTop + verticalDistanceToShow;
                return Math.max(limitTop, Math.min(top, limitBottom));
            }
            return startChildTop;
        }
    }

    public interface Callback {
        void onSliding(View frontView, View behindView, int dx, int dy);

        void onSlideStateChanged(int newState);
    }
}
