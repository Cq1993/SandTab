package cq.sandtabview.sandtab;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * @author ：Chenqi
 * <p>
 * date ：2018/5/29 下午1:32
 * description ：沙盘Marker
 */
public abstract class BaseSandTabMarker<T> extends FrameLayout {
    private T mData;

    public BaseSandTabMarker(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(markerLayoutRes(), this, true);
    }

    public BaseSandTabMarker(Context context, T data) {
        this(context);
        mData = data;
    }

    protected T getData(){
        return mData;
    }

    /**
     * 布局资源文件
     */
    protected abstract int markerLayoutRes();

    /**
     * 横坐标
     *
     * @return ：左上角横坐标
     */
    protected abstract int xAxis();

    /**
     * 纵坐标
     *
     * @return ：左上角纵坐标
     */
    protected abstract int yAxis();

    /**
     * 标签距左偏移量
     *
     * @return ：左偏移量
     */
    protected abstract int getCenterLeft();

    /**
     * 标签距上偏移量
     *
     * @return ：上偏移量
     */
    protected abstract int getCenterTop();

    /**
     * 宽度重新计算
     *
     * @return ：宽度
     */
    protected abstract int measureWidth();

    /**
     * 高度重新计算
     *
     * @return ：高度
     */
    protected abstract int measureHeight();

    /**
     * 设置是否选中
     */
    protected abstract void select(boolean select);
}
