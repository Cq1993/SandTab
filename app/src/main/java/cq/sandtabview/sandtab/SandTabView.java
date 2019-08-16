package cq.sandtabview.sandtab;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import static cq.sandtabview.sandtab.ScaleMode.SCALE_MODE_ENLARGE;
import static cq.sandtabview.sandtab.ScaleMode.SCALE_MODE_NARROW;
import static cq.sandtabview.sandtab.TouchMode.TOUCH_MODE_DRAG;
import static cq.sandtabview.sandtab.TouchMode.TOUCH_MODE_SCALE;

/**
 * @author ：Chenqi
 * <p>
 * date ：2018/5/23 下午2:37
 * description ：自定义沙盘View
 */
@SuppressWarnings("unused")
public class SandTabView extends FrameLayout implements
        ScaleGestureDetector.OnScaleGestureListener {

    //点击事件的时间判定值
    private static final int CLICK_STANDARD_TIME = 200;
    //最大放大倍数
    private static final int SCALE_MAX_VALUE = Integer.MAX_VALUE;
    //声明拖拽帮助类
    private ViewDragHelper mDragHelper;
    //声明缩放手势手势监听
    private ScaleGestureDetector mScaleGestureDetector;
    //声明沙盘图、标注的容器
    private FrameLayout mContentView;

    //声明手指按下时间
    private long mPointDownTimeStamp;
    //声明触摸事件类型
    private int mTouchMode;
    //声明缩放事件类型
    private int mScaleMode;
    //记录开始触摸前两指距离
    private float mPointDistanceBeforeScale;
    //当前缩放比
    private float mCurScaleFactor = 1;
    //总缩放比
    private float mAllScaleFactor = 1;
    //初始缩放比
    private float mOriginalFactor;
    //单击按下坐标
    private PointF mPointDown = new PointF();
    //单击抬起坐标
    private PointF mPointUp = new PointF();
    //声明缩放中心点
    private PointF mScaleCenPoint = new PointF();
    //缩放前距左，距右边距
    private PointF mPointLeftAndTop = new PointF();
    //开始缩放前标注父容器位置记录
    private Rect mOriginRect = new Rect();
    //声明初始加载的View大小
    private PointF mViewOriginSize;

    //点击回调
    private ISandTabItemClick mISandTabItemClick;
    //加载回调
    private ISandTabLoadCallBack mLoadCallBack;

    //是否可以点击
    private boolean mIsTouchEnable = true;
    //是否已经生成图片展示
    private boolean mHaveViewCut = false;
    //第一次加载是否完成
    private boolean mFirstLoaded = false;

    public SandTabView(@NonNull Context context) {
        this(context, null);
    }

    public SandTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SandTabView(@NonNull Context context, @Nullable AttributeSet attrs,
                       int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setISandTabItemClick(ISandTabItemClick ISandTabItemClick) {
        mISandTabItemClick = ISandTabItemClick;
    }

    public void setTouchEnable(boolean touchEnable) {
        mIsTouchEnable = touchEnable;
    }

    private void init() {
        //初始化拖拽帮助类
        mDragHelper = ViewDragHelper.create(this, 2f, new ViewDragCallBack());
        //初始化缩放手势监听
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        //初始化容器
        mContentView = new FrameLayout(getContext());
        //添加容器
        addView(mContentView);
        LayoutParams layoutParams = (LayoutParams) mContentView.getLayoutParams();
        mContentView.setLayoutParams(layoutParams);
    }

    /**
     * 设置沙盘图片资源
     *
     * @param url：图片地址
     */
    public void setImagePath(String url, ISandTabLoadCallBack loadCallBack) {
        mLoadCallBack = loadCallBack;
        Glide.with(getContext()).load(url).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource,
                                        @Nullable Transition<? super Drawable> transition) {
                final Drawable sandTabDrawable = resource;
                mContentView.post(() -> {
                    mViewOriginSize = CalUtil.calScalePictureSize(SandTabView.this,
                            sandTabDrawable);
                    mOriginalFactor = mViewOriginSize.x / resource.getIntrinsicWidth();
                    mAllScaleFactor = mOriginalFactor;
                    LayoutParams contentViewLayoutParams =
                            (LayoutParams) mContentView.getLayoutParams();
                    contentViewLayoutParams.width = (int) mViewOriginSize.x;
                    contentViewLayoutParams.height = (int) mViewOriginSize.y;
                    mContentView.setLayoutParams(contentViewLayoutParams);
                    mContentView.setBackground(sandTabDrawable);
                    if (mLoadCallBack != null)
                        mLoadCallBack.onBgLoaded();
                });
            }
        });
    }

    /**
     * 添加标注View
     *
     * @param markers：标注View集合
     */
    public void addMarkers(List<? extends BaseSandTabMarker> markers) {
        if (markers == null)
            return;
        mContentView.removeAllViews();
        for (int i = 0; i < markers.size(); i++) {
            BaseSandTabMarker marker = markers.get(i);
            mContentView.addView(marker);
            LayoutParams layoutParams = (LayoutParams) marker.getLayoutParams();
            layoutParams.leftMargin = Math.round(marker.xAxis() * mAllScaleFactor) -
                    marker.getCenterLeft();
            layoutParams.topMargin = Math.round(marker.yAxis() * mAllScaleFactor) -
                    marker.getCenterTop();
            layoutParams.width = marker.measureWidth();
            layoutParams.height = marker.measureHeight();
            marker.setLayoutParams(layoutParams);
            if (i == markers.size() - 1) {
                marker.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    if (mLoadCallBack != null && !mFirstLoaded) {
                        mLoadCallBack.onMarkerLoaded();
                        mFirstLoaded = true;
                    }
                });
            }
        }
    }

    /**
     * 设置当前显示的marker
     *
     * @param position：目标marker下标
     */
    public void selectMarker(int position, boolean isSelect) {
        if (mContentView != null && mContentView.getChildCount() > 0)
            scrollToMarker(position, isSelect);
    }

    /**
     * 截图控件内容后生成图片展示并移除所有子控件
     */
    public void cutViewContent() {
        if (mHaveViewCut)
            return;
        destroyDrawingCache();
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        setBackground(new BitmapDrawable(getResources(), getDrawingCache()));
        removeAllViews();
        mHaveViewCut = true;
    }

    @Override
    public boolean performClick() {
        if (mContentView != null)
            for (int i = 0; i < mContentView.getChildCount(); i++) {
                BaseSandTabMarker childAt = (BaseSandTabMarker) mContentView.getChildAt(i);
                int correctX = Math.round((mPointDown.x + Math.abs(mContentView.getLeft())));
                int correctY = Math.round(mPointDown.y + Math.abs(mContentView.getTop()));
                if (correctX < childAt.getRight() && correctX > childAt.getLeft() &&
                        correctY > childAt.getTop() && correctY < childAt.getBottom() &&
                        mISandTabItemClick != null) {
                    mISandTabItemClick.onMarkerItemClick(i);
                    scrollToMarker(i, true);
                    break;
                }
            }
        return super.performClick();
    }

    /**
     * 将marker移动到控件中心点
     *
     * @param position：目标Marker对应下标
     */
    private void scrollToMarker(int position, boolean isSelect) {
        //重置marker状态
        if (isSelect)
            for (int i = 0; i < mContentView.getChildCount(); i++) {
                BaseSandTabMarker marker = (BaseSandTabMarker) mContentView.getChildAt(i);
                marker.select(i == position);
            }
        //计算child相对于父容器的父容器的位置
        BaseSandTabMarker child = (BaseSandTabMarker) mContentView.getChildAt(position);
        if (child == null)
            return;
        int childCenterX = child.getLeft() - Math.abs(mContentView.getLeft()) + child.getWidth() / 2;
        int childCenterY = child.getTop() - Math.abs(mContentView.getTop()) + child.getHeight() / 2;
        //child中心点横纵坐标相对于控件中心点的差值
        int deltaX = getWidth() / 2 - childCenterX;
        int deltaY = getHeight() / 2 - childCenterY;
        //最终标注View载体的左上位置
        int finalLeft = mContentView.getLeft() + deltaX;
        int finalTop = mContentView.getTop() + deltaY;
        //左边界判断
        finalLeft = finalLeft > 0 ? 0 : finalLeft;
        //右边界判断
        if (finalLeft + mContentView.getWidth() < getWidth())
            finalLeft = getWidth() - mContentView.getWidth();
        //上边界判断
        finalTop = finalTop > 0 ? 0 : finalTop;
        //下边界判断
        if (finalTop + mContentView.getHeight() < getHeight())
            finalTop = getHeight() - mContentView.getHeight();
        mDragHelper.smoothSlideViewTo(mContentView, finalLeft, finalTop);
        ViewCompat.postInvalidateOnAnimation(SandTabView.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SandTabView.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mHaveViewCut && mIsTouchEnable)
            switch (event.getAction() & event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    mTouchMode = TOUCH_MODE_DRAG;
                    //记录点击的时间
                    mPointDownTimeStamp = System.currentTimeMillis();
                    //记录单击坐标
                    mPointDown.set(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mTouchMode = TOUCH_MODE_SCALE;
                    mPointDistanceBeforeScale = CalUtil.calDistanceOf2Point(
                            event.getX(0), event.getY(0),
                            event.getX(1), event.getY(1));
                    //记录手指缩放中心点
                    mScaleCenPoint.set(
                            (event.getX(1) + event.getX(0)) / 2,
                            (event.getY(1) + event.getY(0)) / 2);
                    mOriginRect.set(mContentView.getLeft(), mContentView.getTop(),
                            mContentView.getRight(), mContentView.getBottom());
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() > 1 && mTouchMode == TOUCH_MODE_SCALE) {
                        mScaleMode = CalUtil.calDistanceOf2Point(
                                event.getX(0), event.getY(0),
                                event.getX(1), event.getY(1)) >
                                mPointDistanceBeforeScale ? SCALE_MODE_ENLARGE : SCALE_MODE_NARROW;
                    }
                    mPointLeftAndTop.set(mContentView.getLeft(), mContentView.getTop());
                    break;

                case MotionEvent.ACTION_UP:
                    //获取手指抬起的时间
                    mPointUp.set(event.getX(), event.getY());
                    long pointUpTimeStamp = System.currentTimeMillis();
                    long deltaTimeStamp = pointUpTimeStamp - mPointDownTimeStamp;
                    if (deltaTimeStamp <= CLICK_STANDARD_TIME)
                        performClick();
                    break;

                default:
                    break;
            }
        //分发事件
        switch (mTouchMode) {
            case TOUCH_MODE_DRAG:
                mDragHelper.processTouchEvent(event);
                break;

            case TOUCH_MODE_SCALE:
                mScaleGestureDetector.onTouchEvent(event);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mTouchMode == TOUCH_MODE_SCALE) {
            //缩放后重新设定位置
            int leftAfterScale;
            int topAfterScale;
            //缩放后的left = 缩放前left * 缩放率(重新定位) - 缩放中心点的偏移量
            if (mScaleMode == SCALE_MODE_ENLARGE) {
                leftAfterScale = (int) (mPointLeftAndTop.x * mCurScaleFactor -
                        mScaleCenPoint.x * (mCurScaleFactor - 1));
                topAfterScale = (int) (mPointLeftAndTop.y * mCurScaleFactor -
                        mScaleCenPoint.y * (mCurScaleFactor - 1));
            } else {
                leftAfterScale = (int) (mPointLeftAndTop.x * mCurScaleFactor +
                        mScaleCenPoint.x * (1 - mCurScaleFactor));
                topAfterScale = (int) (mPointLeftAndTop.y * mCurScaleFactor +
                        mScaleCenPoint.y * (1 - mCurScaleFactor));
            }
            int rightAfterScale = leftAfterScale + mContentView.getWidth();
            int bottomAfterScale = topAfterScale + mContentView.getHeight();
            //防止缩放时出现白边，进行边界判断，保证缩放在控件内进行。
            if (leftAfterScale > 0) {
                leftAfterScale = 0;
                rightAfterScale = mContentView.getWidth();
            }
            if (topAfterScale > 0) {
                topAfterScale = 0;
                bottomAfterScale = mContentView.getHeight();
            }
            if (rightAfterScale < getWidth()) {
                rightAfterScale = getWidth();
                leftAfterScale = getWidth() - mContentView.getWidth();
            }
            if (bottomAfterScale < getHeight()) {
                bottomAfterScale = getHeight();
                topAfterScale = getHeight() - mContentView.getHeight();
            }
            mContentView.layout(leftAfterScale, topAfterScale, rightAfterScale, bottomAfterScale);
            reLayoutMarkers();
        }
    }

    /**
     * 标注重新布局
     */
    private void reLayoutMarkers() {
        for (int i = 0; i < mContentView.getChildCount(); i++) {
            BaseSandTabMarker child = (BaseSandTabMarker) mContentView.getChildAt(i);
            int left = Math.round(child.xAxis() * mAllScaleFactor) -
                    child.getCenterLeft();
            int top = Math.round(child.yAxis() * mAllScaleFactor) -
                    child.getCenterTop();
            child.layout(left, top, left + child.getWidth(), top + child.getHeight());
        }
    }

    /**
     * 检查缩放范围
     *
     * @return ：是否可以缩放
     */
    private boolean checkScaleRange() {
        return mScaleMode == SCALE_MODE_ENLARGE &&
                mContentView.getWidth() / mViewOriginSize.x < SCALE_MAX_VALUE &&
                mContentView.getHeight() / mViewOriginSize.y < SCALE_MAX_VALUE ||
                mScaleMode == SCALE_MODE_NARROW &&
                        mContentView.getWidth() / mViewOriginSize.x > 1 &&
                        mContentView.getHeight() / mViewOriginSize.y > 1;
    }

    /**
     * 开始缩放
     *
     * @param detector ：缩放手势监听
     * @return ：是否可以缩放
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return checkScaleRange();
    }

    /**
     * 进行缩放
     *
     * @param detector ：缩放手势监听
     * @return ：返回true可进行缩放
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        mCurScaleFactor = detector.getScaleFactor();
        if (checkScaleRange()) {
            float newWidth = mContentView.getWidth() * mCurScaleFactor;
            LayoutParams layoutParams = (LayoutParams) mContentView.getLayoutParams();
            layoutParams.width = Math.round(mContentView.getWidth() * mCurScaleFactor);
            layoutParams.height = Math.round(mContentView.getHeight() * mCurScaleFactor);
            mAllScaleFactor *= mCurScaleFactor;
            if (newWidth < mViewOriginSize.x) {
                mCurScaleFactor = mViewOriginSize.x / newWidth;
                mAllScaleFactor = mOriginalFactor;
                layoutParams.width = Math.round(mViewOriginSize.x);
                layoutParams.height = Math.round(mViewOriginSize.y);
                layoutParams.setMargins(mOriginRect.left, mOriginRect.top,
                        mOriginRect.right, mOriginRect.bottom);
            }
            mContentView.setLayoutParams(layoutParams);
        }
        return true;
    }

    /**
     * 缩放结束回调
     *
     * @param detector ：缩放手势监听
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    /**
     * 拖拽回调
     */
    private class ViewDragCallBack extends ViewDragHelper.Callback {

        /**
         * 尝试捕捉目标（拖动的）视图
         *
         * @param child     ：子视图
         * @param pointerId ：正在被拖动捕获的子视图的指针Id
         * @return ：可以被拖动的子View
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return mContentView != null && child == mContentView && mTouchMode == TOUCH_MODE_DRAG;
        }

        /**
         * 子视图横向拖拽的距离监听
         *
         * @param child ：子视图
         * @param left  ：应该拖动到x轴坐标的位置
         * @param dx    ：横向拖动的距离
         * @return ：横向应该拖动的距离
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //横向拖动边界判断
            if (left > 0)
                left = 0;
            if (left < (getWidth() - child.getWidth()))
                left = getWidth() - child.getWidth();
            return left;
        }

        /**
         * 子视图纵向拖动的距离监听
         *
         * @param child ：子控件
         * @param top   ：应该拖动到y轴坐标的位置
         * @param dy    ：纵向拖动的距离
         * @return ：纵向应该拖动的距离
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            //纵向拖动边界判断
            if (top > 0)
                top = 0;
            if (top < (getHeight() - child.getHeight()))
                top = getHeight() - child.getHeight();
            return top;
        }
    }
}
