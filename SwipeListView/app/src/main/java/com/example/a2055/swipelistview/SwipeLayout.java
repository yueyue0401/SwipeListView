package com.example.a2055.swipelistview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

public class SwipeLayout extends ViewGroup {
    private View content;
    private View[] rightButton, leftButton;
    private int right, left, t, b;
    private int maxRightWidth, maxLeftWidth;
    private int rightButtonCount, leftButtonCount;
    private VelocityTracker mTracker;
    private float startX;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Swipe, 0, 0);
        rightButtonCount = mTypedArray.getInteger(R.styleable.Swipe_rightButton, 0);
        leftButtonCount = mTypedArray.getInteger(R.styleable.Swipe_leftButton, 0);
        mTypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        right = r;
        left = l;
        this.t = t;
        this.b = b;

        int count = 0;

        content = getChildAt(count++);

        maxRightWidth = 0;
        rightButton = new View[rightButtonCount];
        for (int i = 0; i < rightButtonCount; i++) {
            rightButton[i] = getChildAt(count++);
            maxRightWidth += rightButton[i].getMeasuredWidth();
        }

        maxLeftWidth = 0;
        leftButton = new View[leftButtonCount];
        Log.d("@@", leftButtonCount + "");
        for (int i = 0; i < leftButtonCount; i++) {
            leftButton[i] = getChildAt(count++);
            maxLeftWidth += leftButton[i].getMeasuredWidth();
        }

        setViewMid();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                float currentX = ev.getX();
                float scrollX = currentX - startX;
                startX = currentX;
                return Math.abs(scrollX) > 3;
        }
        return false;
    }


    public void addVelocityTracker(final MotionEvent ev) {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        }
        mTracker.addMovement(ev);
    }

    public void computeVelocityTracker() {
        mTracker.computeCurrentVelocity(500);
    }

    public void releaseTracker() {
        if (mTracker != null) {
            mTracker.clear();
            mTracker.recycle();
            mTracker = null;
        }
    }

    // Locate the item.
    public void doReset() {
        int location = -content.getLeft();
        float XVelocity = mTracker.getXVelocity();

        if (XVelocity > 500) {
            if (leftButtonCount != 0 && leftButton[0].getRight() > left) {
                setViewLeftAnimation(-location);
            } else {
                setViewMidAnimation(-location);
            }

        } else if (XVelocity < -500) {
            if (rightButtonCount == 0 || rightButton[0].getLeft() > right) {
                setViewMidAnimation(-location);
            } else {
                setViewRightAnimation(-location);
            }

        } else if (rightButtonCount == 0 || location < maxRightWidth / 2) {
            int reverseMaxLeft = -maxLeftWidth / 2;
            if (location < reverseMaxLeft) {
                setViewLeftAnimation(-location);
            } else {
                setViewMidAnimation(-location);
            }

        } else {
            setViewRightAnimation(-location);
        }
    }

    public void doMove(MotionEvent ev) {
        float deltaX = startX - ev.getX();
        int lenRight = rightButtonCount;
        int lenLeft = leftButtonCount;

        if (leftButtonCount != 0 && leftButton[lenLeft - 1].getLeft() - deltaX > left) {
            setViewLeft();
            return;
        } else if (leftButtonCount == 0 && content.getLeft() - deltaX > left) {
            setViewLeft();
            return;

        } else if (rightButtonCount != 0 && rightButton[lenRight - 1].getRight() - deltaX < right) {
            setViewRight();
            return;
        } else if (rightButtonCount == 0 && content.getRight() - deltaX < right) {
            setViewMid();
            return;
        }

        startX = ev.getX();

        content.layout(content.getLeft() - (int) deltaX, t, content.getRight() - (int) deltaX, b);

        for (int i = 0; i < rightButtonCount; i++) {
            float moveRate = ((float) rightButtonCount - i) / rightButtonCount;
            int buttonDelta = (int) (deltaX * moveRate);
            rightButton[i].layout(rightButton[i].getLeft() - buttonDelta, t, rightButton[i].getRight() - buttonDelta, b);
        }

        for (int i = 0; i < leftButtonCount; i++) {
            float moveRate = ((float) leftButtonCount - i) / leftButtonCount;
            int buttonDelta = (int) (deltaX * moveRate);
            leftButton[i].layout(leftButton[i].getLeft() - buttonDelta, t, leftButton[i].getRight() - buttonDelta, b);
        }

    }

    private void setViewMid() {
        content.layout(left, t, right, b);

        for (int i = 0; i < rightButtonCount; i++) {
            rightButton[i].layout(right, t, right + rightButton[i].getMeasuredWidth(), b);
        }

        for (int i = 0; i < leftButtonCount; i++) {
            leftButton[i].layout(left - leftButton[i].getMeasuredWidth(), t, left, b);
        }
    }

    private void setViewRight() {
        int width = 0;
        for (int i = rightButtonCount - 1; i > -1; i--) {
            rightButton[i].layout(right - rightButton[i].getMeasuredWidth() - width, t, right - width, b);
            width += rightButton[i].getMeasuredWidth();
        }
        content.layout(left - width, t, right - width, b);
    }

    private void setViewLeft() {
        int width = 0;
        for (int i = leftButtonCount - 1; i > -1; i--) {
            leftButton[i].layout(left + width, t, left + leftButton[i].getMeasuredWidth() + width, b);
            width += leftButton[i].getMeasuredWidth();
        }
        content.layout(left + width, t, right + width, b);
    }

    private void setViewMidAnimation(final float nowPosition) {
        ValueAnimator animator = ObjectAnimator.ofInt(100, 0);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Integer) animation.getAnimatedValue();
                float percent = value / 100;
                float position = nowPosition * percent;
                content.layout(left + (int) position, t, right + (int) position, b);

                for (int i = 0; i < rightButtonCount; i++) {
                    float moveRate = ((float) rightButtonCount - i) / rightButtonCount;
                    int ratePosition = (int) (position * moveRate);
                    rightButton[i].layout(right + ratePosition, t, right + ratePosition + rightButton[i].getMeasuredWidth(), b);
                }

                for (int i = 0; i < leftButtonCount; i++) {
                    float moveRate = ((float) leftButtonCount - i) / leftButtonCount;
                    int ratePosition = (int) (position * moveRate);
                    leftButton[i].layout(left - leftButton[i].getMeasuredWidth(), t, left + ratePosition, b);
                }
            }
        });
        animator.start();
    }

    private void setViewRightAnimation(final float nowPosition) {
        ValueAnimator animator = ObjectAnimator.ofInt(100, 0);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Integer) animation.getAnimatedValue();
                float percent = value / 100;
                float position = (-maxRightWidth - nowPosition) * percent;

                int width = 0;
                for (int i = rightButtonCount - 1; i > -1; i--) {
                    float moveRate = ((float) rightButtonCount - i) / rightButtonCount;
                    int ratePosition = (int) (position * moveRate);
                    rightButton[i].layout(right - rightButton[i].getMeasuredWidth() - width - ratePosition, t, right - width - ratePosition, b);
                    width += rightButton[i].getMeasuredWidth();
                }

                content.layout(left - maxRightWidth - (int) position, t, right - maxRightWidth - (int) position, b);
            }
        });
        animator.start();

        for (int i = 0; i < leftButtonCount; i++) {
            float moveRate = ((float) leftButtonCount - i) / leftButtonCount;
            int width = (int) (maxRightWidth * moveRate);
            leftButton[i].layout(left - leftButton[i].getMeasuredWidth() - width, t, left - width, b);
        }
    }

    private void setViewLeftAnimation(final float nowPosition) {
        ValueAnimator animator = ObjectAnimator.ofInt(100, 0);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Integer) animation.getAnimatedValue();
                float percent = value / 100;
                float position = (maxLeftWidth - nowPosition) * percent;

                int width = 0;
                for (int i = leftButtonCount - 1; i > -1; i--) {
                    float moveRate = ((float) leftButtonCount - i) / leftButtonCount;
                    int ratePosition = (int) (position * moveRate);
                    leftButton[i].layout(left + width - ratePosition, t, left + leftButton[i].getMeasuredWidth() + width - ratePosition, b);
                    width += leftButton[i].getMeasuredWidth();
                }

                content.layout(left + maxLeftWidth - (int) position, t, left + maxLeftWidth + content.getMeasuredWidth() - (int) position, b);
            }
        });
        animator.start();

        for (int i = 0; i < rightButtonCount; i++) {
            float moveRate = ((float) rightButtonCount - i) / rightButtonCount;
            int width = (int) (maxRightWidth * moveRate);
            rightButton[i].layout(right + width, t, right + rightButton[i].getMeasuredWidth() + width, b);
        }
    }
}
