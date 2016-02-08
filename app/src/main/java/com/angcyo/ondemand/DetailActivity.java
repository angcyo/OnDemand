package com.angcyo.ondemand;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.angcyo.ondemand.base.BaseActivity;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventOddnumOk;
import com.angcyo.ondemand.model.DeliveryserviceBean;
import com.angcyo.ondemand.util.Util;
import com.angcyo.ondemand.view.OddnumAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by angcyo on 15-09-27-027.
 */
public class DetailActivity extends BaseActivity implements LocationSource, AMapLocationListener, SensorEventListener {

    public static final String KEY_ODDNUM = "key_oddnum";
    private final int TIME_SENSOR = 100;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.oddnum_list)
    RecyclerView oddnumList;
    @Bind(R.id.map)
    MapView mapView;
    MaterialDialog mMaterialDialog;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastTime = 0;
    private float mAngle;
    private Marker mGPSMarker;
    private Polyline polyline;
    private PolylineOptions polyOption;
    private ArrayList<DeliveryserviceBean> allTakeOddnum;
    private OddnumAdapter oddnumAdapter;
    private boolean waitBack = false;

    /**
     * 带参数启动activity
     */
    public static void launch(BaseActivity activity, ArrayList<DeliveryserviceBean> beans) {
        Intent intent = new Intent(activity, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ODDNUM, beans);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 获取当前屏幕旋转角度
     *
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int getScreenRotationOnPhone(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;

            case Surface.ROTATION_90:
                return 90;

            case Surface.ROTATION_180:
                return 180;

            case Surface.ROTATION_270:
                return -90;
        }
        return 0;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initBefore() {
        super.initBefore();
        allTakeOddnum = getIntent().getExtras().getParcelableArrayList(KEY_ODDNUM);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
//        setSupportActionBar(toolbar);
//        initWindow(R.color.colorAccent);
//        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setTitle("订单跟踪中:(" + allTakeOddnum.size() + ")");

        // 数据列表
        oddnumList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        oddnumAdapter = new OddnumAdapter(this, allTakeOddnum);
        oddnumList.setAdapter(oddnumAdapter);

        // 初始化传感器
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventOddnumOk event) {
        oddnumAdapter.netCommandCount.decrementAndGet();
        if (!event.isSuccess) {
            oddnumAdapter.datas.get(event.position).setStatus(2);//失败了,回退状态
            oddnumAdapter.notifyItemChanged(event.position);
        }

        boolean jump = true;
        for (DeliveryserviceBean bean : oddnumAdapter.datas) {
            if (bean.getStatus() != 9) {
                jump = false;
            }
        }

        if (jump) {
            finish();
        }

        if (waitBack) {
            onBackPressed();
        }
    }

    @Override
    protected void initAfter() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mGPSMarker = aMap.addMarker(new MarkerOptions().icon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_marker)))
                    .anchor((float) 0.5, (float) 0.5));
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        }
    }

    @Override
    public void onBackPressed() {
        if (oddnumAdapter.netCommandCount.get() != 0) {
            showDialogTip("请等待...");
            waitBack = true;
            return;
        }
        waitBack = false;
        hideDialogTip();

        boolean isOk = true;
        for (DeliveryserviceBean bean : allTakeOddnum) {
            if (bean.getStatus() != 9) {
                isOk = false;
            }
        }
        if (isOk) {
            super.onBackPressed();
        } else {
            mMaterialDialog = new MaterialDialog(this)
                    .setTitle("提醒")
                    .setMessage("您有订单未送达,确认放弃并退出吗?")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
            mMaterialDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            //第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
            mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10, this);
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
        unRegisterSensorListener();
    }

    public void registerSensorListener() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterSensorListener() {
        mSensorManager.unregisterListener(this, mSensor);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(final AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            // mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            mGPSMarker.setPosition(new LatLng(aLocation.getLatitude(), aLocation.getLongitude()));
            aMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(aLocation.getLatitude(), aLocation.getLongitude()), 18));
            if (polyOption != null) {
                polyOption.add(new LatLng(aLocation.getLatitude(), aLocation.getLongitude()));
            } else {
                polyOption = new PolylineOptions().add(new LatLng(aLocation.getLatitude(), aLocation.getLongitude()))
                        .color(getResources().getColor(R.color.colorAccent)).width(20f);
            }
            aMap.addPolyline(polyOption);

            RWorkService.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    if (Util.isNetOk(DetailActivity.this)) {
                        try {
                            RTableControl.callUpdate(OdApplication.userInfo.member.getSid(), aLocation.getLatitude(), aLocation.getLongitude());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    // 回调函数处理逻辑
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
            return;
        }
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION: {
                float x = event.values[0];
                x += getScreenRotationOnPhone(this);
                x %= 360.0F;
                if (x > 180.0F)
                    x -= 360.0F;
                else if (x < -180.0F)
                    x += 360.0F;
                if (Math.abs(mAngle - 90 + x) < 3.0f) {
                    break;
                }
                mAngle = x;
                if (mGPSMarker != null) {
                    mGPSMarker.setRotateAngle(-mAngle);
                    aMap.invalidate();
                }
                lastTime = System.currentTimeMillis();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
