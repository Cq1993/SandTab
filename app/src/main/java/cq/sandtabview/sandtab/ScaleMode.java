package cq.sandtabview.sandtab;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cq.sandtabview.sandtab.ScaleMode.SCALE_MODE_ENLARGE;
import static cq.sandtabview.sandtab.ScaleMode.SCALE_MODE_NARROW;

/**
 * @author ：Chenqi
 * <p>
 * date ：2018/5/29 下午12:22
 * description ：沙盘缩放类型定义
 */
@IntDef({SCALE_MODE_ENLARGE, SCALE_MODE_NARROW})
@Retention(RetentionPolicy.SOURCE)
@interface ScaleMode {
    //缩放类型声明-放大
    int SCALE_MODE_ENLARGE = 0xb1;
    //缩放类型声明-缩小
    int SCALE_MODE_NARROW = 0xb2;
}
