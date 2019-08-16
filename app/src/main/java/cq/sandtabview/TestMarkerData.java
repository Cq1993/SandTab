package cq.sandtabview;

/**
 * @author Chenqi
 * <p>
 * date 2019-08-12 13:55
 * description 测试标注数据（数据实体）
 */
public class TestMarkerData {

    private int axisX;//横坐标
    private int axisY;//纵坐标
    private String content;//显示内容

    public TestMarkerData(int axisX, int axisY, String content) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.content = content;
    }

    int getAxisX() {
        return axisX;
    }

    int getAxisY() {
        return axisY;
    }

    String getContent() {
        return content;
    }
}
