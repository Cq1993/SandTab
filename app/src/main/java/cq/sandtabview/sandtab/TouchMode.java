package cq.sandtabview.sandtab;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cq.sandtabview.sandtab.TouchMode.TOUCH_MODE_DRAG;
import static cq.sandtabview.sandtab.TouchMode.TOUCH_MODE_SCALE;

/**
 * @author ：Chenqi
 * <p>
 * date ：2018/5/29 下午12:18
 * description ：沙盘View触摸类型定义
 */
@IntDef({TOUCH_MODE_DRAG, TOUCH_MODE_SCALE})
@Retention(RetentionPolicy.SOURCE)
@interface TouchMode {
    //触摸类型声明-拖拽
    int TOUCH_MODE_DRAG = 0xa1;
    //触摸类型声明-缩放
    int TOUCH_MODE_SCALE = 0xa2;
}
