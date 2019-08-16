package cq.sandtabview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import cq.sandtabview.sandtab.ISandTabLoadCallBack;
import cq.sandtabview.sandtab.SandTabView;

/**
 * @author Chenqi
 * <p>
 * date 2019-08-12 13:39
 * description 沙盘测试Activity
 */
public class MainActivity extends AppCompatActivity {
    private static final String mTestUrl = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2588522764,1370361111&fm=26&gp=0.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("沙盘测试");
        SandTabView stb = findViewById(R.id.stb);
        stb.setISandTabItemClick(position -> stb.selectMarker(position,true));
        stb.setImagePath(mTestUrl, new ISandTabLoadCallBack() {
            @Override
            public void onBgLoaded() {
                //背景图片加载完毕，设置填充标注
                List<TestMarker> markers = new ArrayList<>();
                markers.add(new TestMarker(MainActivity.this,new TestMarkerData(50,35,"1号楼")));
                markers.add(new TestMarker(MainActivity.this,new TestMarkerData(120,95,"2号楼")));
                markers.add(new TestMarker(MainActivity.this,new TestMarkerData(430,50,"3号楼")));
                markers.add(new TestMarker(MainActivity.this,new TestMarkerData(330,80,"4号楼")));
                markers.add(new TestMarker(MainActivity.this,new TestMarkerData(230,110,"5号楼")));
                stb.addMarkers(markers);
            }

            @Override
            public void onMarkerLoaded() {
                stb.selectMarker(2,true);
            }
        });
    }
}
