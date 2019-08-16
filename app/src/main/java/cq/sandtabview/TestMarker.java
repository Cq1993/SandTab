package cq.sandtabview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;

import cq.sandtabview.sandtab.BaseSandTabMarker;

/**
 * @author Chenqi
 * <p>
 * date 2019-08-12 13:47
 * description 测试标注
 */
@SuppressLint("ViewConstructor")
public class TestMarker extends BaseSandTabMarker<TestMarkerData> {
    private AppCompatTextView mAtvMarker;

    public TestMarker(Context context, TestMarkerData data) {
        super(context, data);
        mAtvMarker = findViewById(R.id.atvMarker);
        mAtvMarker.setText(data.getContent());
    }

    @Override
    protected int markerLayoutRes() {
        return R.layout.layout_test_marker;
    }

    @Override
    protected int xAxis() {
        return getData().getAxisX();
    }

    @Override
    protected int yAxis() {
        return getData().getAxisY();
    }

    @Override
    protected int getCenterLeft() {
        return 0;
    }

    @Override
    protected int getCenterTop() {
        return 0;
    }

    @Override
    protected int measureWidth() {
        return DensityUtil.dp2px(100);
    }

    @Override
    protected int measureHeight() {
        return DensityUtil.dp2px(30);
    }

    @Override
    protected void select(boolean select) {
        mAtvMarker.setTextColor(getResources().getColor(select ? R.color.colorAccent : R.color.colorPrimary));
    }
}
