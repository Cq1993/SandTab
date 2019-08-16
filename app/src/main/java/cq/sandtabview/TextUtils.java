package cq.sandtabview;

import android.graphics.Rect;
import android.text.TextPaint;

/**
 * @author Chenqi
 * <p>
 * date 2019-08-12 14:15
 * description 文字测量
 */
public final class TextUtils {

    public static Rect getTextBounds(String content,float textSize) {
        //计算文本bounds
        Rect rect = new Rect();
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.getTextBounds(content, 0, content.length(), rect);
        return rect;
    }
}
