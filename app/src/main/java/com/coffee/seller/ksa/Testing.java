package com.coffee.seller.ksa;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tami.seller.ksa.R;
import com.utils.RotationGestureDetector;


public class Testing extends AppCompatActivity{

    RelativeLayout drawer_layout;
    private RelativeLayout.LayoutParams layoutParams;
    private RelativeLayout.LayoutParams layoutParams2;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    EditText currentEd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        drawer_layout   = (RelativeLayout)findViewById(R.id.drawer_layout);

        layoutParams             = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//(int)function.dpToPx(50),(int)function.dpToPx(30))
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams2            = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//(int)function.dpToPx(50),(int)function.dpToPx(30))
        layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);


        final LinearLayout ff    = new LinearLayout(this);
        ff.setLayoutParams(layoutParams);
        ff.setBackgroundColor(Color.GREEN);
        ff.setGravity(Gravity.CENTER);
        final EditText zoom     = new EditText(this);
        zoom.setTextColor(Color.BLACK);
        zoom.requestFocus();
        zoom.setText("ddd d d d d");
        zoom.setLongClickable(false);
        zoom.setGravity(Gravity.CENTER);
        zoom.setLayoutParams(layoutParams2);
        zoom.setBackgroundColor(Color.TRANSPARENT);
        zoom.setTextSize(25);
        zoom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        zoom.setHighlightColor(Color.TRANSPARENT);
        zoom.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        final TextView tview = new TextView(this);
        tview.setTextColor(Color.BLACK);



        //ff.addView(zoom);
        drawer_layout.addView(zoom);
        zoom.setOnTouchListener(new TouchListnar(this,zoom));



    }

    interface OnDragActionListener {
        /**
         * Called when drag event is started
         *
         * @param view The view dragged
         */
        void onDragStart(View view);

        /**
         * Called when drag event is completed
         *
         * @param view The view dragged
         */
        void onDragEnd(View view);
    }

    class TouchListnar implements View.OnTouchListener, RotationGestureDetector.OnRotationGestureListener {


        View mView;
        boolean isDragging , isScaling;
        View drawing_del;

        private boolean isInitialized = false;

        private int width;
        private float xWhenAttached;
        private float maxLeft;
        private float maxRight;
        private float dX;

        private int height;
        private float yWhenAttached;
        private float maxTop;
        private float maxBottom;
        private float dY;
        int indexToDel;
        private View mParent;
        private OnDragActionListener mOnDragActionListener;
        InputMethodManager imm;
        int index;
        RotationGestureDetector mRotationDetector;
        int MAX_FINGERS = 5;
        int cappedPointerCount = 1;

        public TouchListnar(Context context,View v)
        {
            drawing_del             = new View(context);
            isDragging              = true;
            mView                   = v;
            int ind                 = 0;
            mParent                 = (View)mView.getParent();
            imm 			        = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            initListener(mView, mParent,drawing_del,ind);

            mRotationDetector       = new RotationGestureDetector(this,mView);
            mOnDragActionListener   = null;
            mScaleGestureDetector   = new ScaleGestureDetector(context,new ScaleListener());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onDragTouch(view,motionEvent);
            mScaleGestureDetector.onTouchEvent(motionEvent);
            mRotationDetector.onTouchEvent(motionEvent);
            return true;
        }

        @Override
        public void onRotation(RotationGestureDetector rotationDetector) {
            float angle     = rotationDetector.getAngle();
            BaseActivity.globalLog("onRotation :: "+cappedPointerCount+" -- "+angle);
            if(cappedPointerCount >= 2)
                mView.setRotation(angle);
        }


        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                BaseActivity.globalLog("ontouch :: onScale "+isDragging);
                isDragging = false;
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 4.0f));
                mView.setScaleX(mScaleFactor);
                mView.setScaleY(mScaleFactor);

//                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mView.getLayoutParams();
//                params.width    = (int)(mView.getWidth()*mScaleFactor);
//                params.height    = (int)(mView.getHeight()*mScaleFactor);
//                mView.setLayoutParams(params);

               // globalLog("bounds :: "+mScaleFactor+" -- "+mView.getWidth()+" -- "+mView.getHeight());
                updateBounds();

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                BaseActivity.globalLog("ontouch :: onScaleBegin");
                isDragging  = false;
                isScaling   = true;
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                BaseActivity.globalLog("ontouch :: onScaleEnd");
                isScaling   = false;
                super.onScaleEnd(detector);
            }

        }



        public void initListener(View view, View parent,View img,int ind) {
            mView           = view;
            drawing_del     = img;
            indexToDel      = ind;
            mParent         = parent;
            isDragging      = false;
            isInitialized   = false;
        }


        private boolean isViewOverlapping(View firstView, View secondView) {
            int[] firstPosition     = new int[2];
            int[] secondPosition    = new int[2];

            firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            firstView.getLocationOnScreen(firstPosition);
            secondView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            secondView.getLocationOnScreen(secondPosition);

            return firstPosition[0] < secondPosition[0] + secondView.getMeasuredWidth()
                    && firstPosition[0] + firstView.getMeasuredWidth() > secondPosition[0]
                    && firstPosition[1] < secondPosition[1] + secondView.getMeasuredHeight()
                    && firstPosition[1] + firstView.getMeasuredHeight() > secondPosition[1];
        }


        public void updateBounds() {
            updateViewBounds();
            updateParentBounds();
            isInitialized = true;
        }

        public void updateViewBounds() {
            width           = mView.getWidth();
            xWhenAttached   = mView.getX();
            dX              = 0;

            height          = mView.getHeight();
            yWhenAttached   = mView.getY();
            dY              = 0;

            BaseActivity.globalLog("updatedBounds :: "+width+" -- "+height);
        }

        public void updateParentBounds() {
            maxLeft         = 0;
            maxRight        = maxLeft + mParent.getWidth();

            maxTop          = 0;
            maxBottom       = maxTop + mParent.getHeight();
        }

        boolean onDragTouch(View v, MotionEvent event) {
                if (isDragging)
                {
                    boolean overlapped = isViewOverlapping(mView,drawing_del);
                    if(overlapped)
                    {
                        mView.setVisibility(View.GONE);
                        if(mView.isFocused())
                        {
                            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                        }
                    }
                    float[] bounds = new float[4];
                    // LEFT
                    bounds[0] = event.getRawX() + dX;
                    if (bounds[0] < maxLeft) {
                        bounds[0] = maxLeft;
                    }
                    // RIGHT
                    bounds[2] = bounds[0] + width;
                    if (bounds[2] > maxRight) {
                        bounds[2] = maxRight;
                        bounds[0] = bounds[2] - width;
                    }
                    // TOP
                    bounds[1] = event.getRawY() + dY;
                    if (bounds[1] < maxTop) {
                        bounds[1] = maxTop;
                    }
                    // BOTTOM
                    bounds[3] = bounds[1] + height;
                    if (bounds[3] > maxBottom) {
                        bounds[3] = maxBottom;
                        bounds[1] = bounds[3] - height;
                    }

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            onDragFinish();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mView.animate().x(bounds[0]).y(bounds[1]).setDuration(0).start();
                            break;
                    }
                    return true;
                }
                else
                {
                    int pointerCount            = event.getPointerCount();
                    cappedPointerCount          = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;

                    BaseActivity.globalLog("cappedCounted :: "+cappedPointerCount);

                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            if(cappedPointerCount == 1 )
                            {
                                BaseActivity.globalLog("ontouch :: dragstarted");
                                isDragging = true;
                                if (!isInitialized) {
                                    updateBounds();
                                }
                                dX = v.getX() - event.getRawX();
                                dY = v.getY() - event.getRawY();
                                if (mOnDragActionListener != null) {
                                    mOnDragActionListener.onDragStart(mView);
                                }
                            }
                            return true;
                    }

                }
                return false;
            }

            private void onDragFinish() {
                BaseActivity.globalLog("ontouch :: dragfinish "+isScaling+" -- "+isDragging);
                if(!isScaling && isDragging)
                {
                    if (mOnDragActionListener != null) {
                        mOnDragActionListener.onDragEnd(mView);
                    }

                    dX = 0;
                    dY = 0;
                    isDragging = false;
                }

            }

    }







}
