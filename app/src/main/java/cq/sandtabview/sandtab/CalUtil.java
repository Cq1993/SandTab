package cq.sandtabview.sandtab;

import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * @author ：Chenqi
 * <p>
 * date ：2018/5/24 上午10:32
 * description ：自定义沙盘距离计算帮助类
 */
final class CalUtil {

    private CalUtil() {
    }

    /**
     * 计算两个手指间的距离（勾股定理）
     *
     * @param pX1：横坐标1
     * @param pY1：纵坐标1
     * @param pX2：横坐标2
     * @param pY2：纵坐标2
     * @return ：距离
     */
    public static float calDistanceOf2Point(double pX1, double pY1, double pX2, double pY2) {
        return (float) Math.sqrt(Math.pow((pX2 - pX1), 2) + Math.pow((pY2 - pY1), 2));
    }

    /**
     * 计算图片初始化缩放大小
     *
     * @param view：图片控件父容器
     * @param drawable：图片
     * @return ：pointF对象保存图片重新计算后的大小(x：宽；y：高)
     */
    public static PointF calScalePictureSize(View view, Drawable drawable) {
        float viewW = view.getWidth();//控件宽度
        float viewH = view.getHeight();//控件高度
        float imgW = drawable.getIntrinsicWidth();//图片宽度
        float imgH = drawable.getIntrinsicHeight();//图片高度
        if (imgW < viewW || imgH < viewH) {
            //图片宽高比，放置计算结果不准确，必须强转其中一个值为float
            float ratio = imgW / imgH;
            if (imgW / viewW > imgH / viewH) {
                //图片宽大于高
                //高设置为View高度
                imgH = viewH;
                //宽度按照原宽高比缩放
                imgW = imgH * ratio;
            } else {
                //图片宽小于高
                //宽设置为View宽度
                imgW = viewW;
                //高度按照原宽高比缩放
                imgH = imgW / ratio;
            }
        }
        return new PointF(imgW, imgH);
    }
}
