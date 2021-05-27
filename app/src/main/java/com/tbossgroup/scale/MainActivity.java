package com.tbossgroup.scale;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.tbossgroup.tbscale.EventBatteryPower;
import com.tbossgroup.tbscale.TBScaleAidlInterface;
import com.tbossgroup.utils.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String DEBUG_TAG = "MainActivity";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PACKAGE_NAME = "com.tbossgroup.tbscale.aidl";
    private static final String ACTION = "com.tbossgroup.tbscale.OTScaleService";
    ArrayList<String> per = new ArrayList<>();
    private static final int REQUEST_CODE = 0x004;
    private int id = 0;
    private int counts;
    private int printcount=0;
    private boolean continuityprint=false;
    private static Api.GetExample httpget;
    private static Api.PostExample httpPost;
    private static String weight;
    private static String timeStamp;
    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x007;
    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x008;
    /**
     * TSC查询打印机状态指令
     */
    private byte[] tsc = {0x1b, '!', '?'};
    private static final int CONN_MOST_DEVICES = 0x11;
    private static final int CONN_PRINTER = 0x12;
    private PendingIntent mPermissionIntent;
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };
    private String usbName;
    private ThreadPool threadPool;

    private static final int RESET_REFRESH_DELAY = 100;
    private static final int SERVICE_CONNECTION_DELAY = 1000;

    private static final int WAHT_REFRESH_UI = 1;
    private static final int WAHT_SERVICE_CONNECTION = 2;
    private TextView mTvNetWeight;
    private boolean isConfigAppInstall = false;

    private BatteryView mImgBattery;
    private TextView mTvBatteryPer;

    CameraPreview cameraView;


    private static TBScaleAidlInterface mTBScaleAidlInterface;
    private static ServiceConnection mServiceConnectionn = new ServiceConnection() {

        //绑定服务，回调onBind()方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTBScaleAidlInterface = TBScaleAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTBScaleAidlInterface = null;
        }
    };



    //静态内部类 弱引用
    private BleHandler mHandler = new BleHandler(getApplication());
    class BleHandler extends Handler{

        WeakReference weakReference;
        public BleHandler(Context context){
            weakReference = new WeakReference(context);
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WAHT_REFRESH_UI) {
                refreshWeightRecord();

                try {
                    if(mTBScaleAidlInterface.isWeightStable()){

                        weight = mTBScaleAidlInterface.getNetWeightString();
                        timeStamp = new SimpleDateFormat("yyyyMMddFHHmmss").format(new Date());
                        begionOperation(weight,timeStamp);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } else if (msg.what == WAHT_SERVICE_CONNECTION) {
                try {
                    if (mTBScaleAidlInterface == null
                            || TextUtils.isEmpty(mTBScaleAidlInterface.getNetWeightString())) {
                        unbindService(mServiceConnectionn);
                        bindService();
                        return;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        }


    /**
     * 开始后续操作
     * 拍照,打印,上传数据,上传图片
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void begionOperation(String w, String t){
//        takePic();
        btnLabelPrint(t);
        createData(w,t);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindService();
        initListener();

        //打印机连接
        SerialConnect();

        if (checkPackInfo(PACKAGE_NAME)) {
            isConfigAppInstall = true;
            if (!isAppBackground(PACKAGE_NAME)) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
                if (intent != null) {
                    //putExtra传递本包名，可以让sdk在开机零点获取完成后自动跳转会来
                    intent.putExtra("PackageName", this.getPackageName());
                    startActivity(intent);
                }
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isConfigAppInstall) {
            bindService();
            mHandler.sendEmptyMessage(WAHT_REFRESH_UI);
        } else {
            Toast.makeText(this, "智能秤配置APK未安装", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnectionn);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void initListener() {
        findViewById(R.id.btnTare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTBScaleAidlInterface.setTare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btnZero).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTBScaleAidlInterface.setZero();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPackInfo(PACKAGE_NAME)) {
                    openPackage(MainActivity.this, PACKAGE_NAME);
                } else {
                    Toast.makeText(MainActivity.this, "智能秤配置APK未安装", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        mTvNetWeight = findViewById(R.id.tvNetWeight);
        mImgBattery = findViewById(R.id.img_battery);
        mTvBatteryPer = findViewById(R.id.tv_battery_per);
        cameraView = (CameraPreview) findViewById(R.id.textureView);
        httpget = new Api.GetExample();
        httpPost = new Api.PostExample();

    }

    private void bindService() {
        //绑定服务端的service
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.setPackage(PACKAGE_NAME);
        //绑定的时候服务端自动创建
        bindService(intent, mServiceConnectionn, Context.BIND_AUTO_CREATE);

        mHandler.sendEmptyMessageDelayed(WAHT_SERVICE_CONNECTION, SERVICE_CONNECTION_DELAY);
    }

    private void refreshWeightRecord() {
        try {
            //获取净重
            mTvNetWeight.setText(mTBScaleAidlInterface.getNetWeight() + "");

            //获取电量
            EventBatteryPower batteryPower = mTBScaleAidlInterface.getBatteryPower();
            mTvBatteryPer.setText(batteryPower.getPower() + "%");
            mImgBattery.setPower(batteryPower.getPower());
            if (batteryPower.isLowPower()) {
                mTvBatteryPer.setTextColor(Color.RED);
                mImgBattery.setColor(Color.RED);
            } else {
                mTvBatteryPer.setTextColor(Color.WHITE);
                mImgBattery.setColor(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHandler.sendEmptyMessageDelayed(WAHT_REFRESH_UI, RESET_REFRESH_DELAY);
    }

    private boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }

    private Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }

    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    private Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String activityName = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                activityName = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(activityName)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, activityName));
        return intent;
    }

    private boolean isAppBackground(String packageName) {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals("packageName")) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     *拍照
     * initsp
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePic() {
        cameraView.capture();
    }



    private void checkPermission() {
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                per.add(permission);
            }
        }
    }

    private void requestPermission() {
        if (per.size() > 0) {
            String[] p = new String[per.size()];
            ActivityCompat.requestPermissions(this, per.toArray(p), REQUEST_CODE);
        }
    }

    /**
     *初始化方法
     * initsp
     */
    private void initsp() {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.str_tscmode));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

    }


    /**
     * 获取当前连接设备信息
     * @return
     */
    private String getConnDeviceInfo() {
        String str = "";
        DeviceConnFactoryManager deviceConnFactoryManager = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id];
        if (deviceConnFactoryManager != null
                && deviceConnFactoryManager.getConnState()) {
                if ("SERIAL_PORT".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "SERIAL_PORT\n";
                str += "Path: " + deviceConnFactoryManager.getSerialPortPath() + "\t";
                str += "Baudrate: " + deviceConnFactoryManager.getBaudrate();
            }
        }
        return str;
    }


    /**
     * 初始化连接打印机
     */
    private void SerialConnect(){
        closeport();
        int baudrate = 9600;
        String path = "/dev/ttyXRM0";
        if (baudrate != 0 && !TextUtils.isEmpty(path)) {
            //初始化DeviceConnFactoryManager
            new DeviceConnFactoryManager.Build()
                    //设置连接方式
                    .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.SERIAL_PORT)
                    .setId(id)
                    //设置波特率
                    .setBaudrate(baudrate)
                    //设置串口号
                    .setSerialPort(path)
                    .build();
            //打开端口
            threadPool = ThreadPool.getInstantiation();
            threadPool.addSerialTask(new Runnable() {
                @Override
                public void run() {
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                }
            });
        }
    }


    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closeport(){
        if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]!=null&&DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort!=null) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].reader.cancel();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort.closePort();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort=null;
        }
    }


    /**
     * 断开连接
     * @param view
     */
    public void btnDisConn(View view) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
            Utils.toast(this, getString(R.string.str_cann_printer));
            return;
        }
        mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
    }

    /**
     * 打印标签例子
     *  时间戳 t
     */
    public void btnLabelPrint(String t) {
        threadPool = ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(PrintContent.getLabel());
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
    }


    /**
     * 图片上传七牛
     *  时间戳 t
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
     public static void saveImage(String fileName,String timeStamp){
        //分拣包装目录
        String token = "";
        String fname = fileName;
        String path = Contact.QN_IMG;
        String url = Contact.GET_QINIU_TOKEN;
        String key = "source/sort/" + timeStamp.replace('_','/') + ".jpg";

        try {
            String result = httpget.run(url);
            JSONObject jsonObject = new JSONObject(result);
            JSONObject rspData = jsonObject.getJSONObject("data");
            token = rspData.getString("uptoken");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        UploadManager uploadManager = new UploadManager();

        uploadManager.put(fname, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                Log.e("TAG","七牛 " + s + ",\r\n " + responseInfo + ",\r\n " + jsonObject);
                if (responseInfo.isOK() && jsonObject != null && !TextUtils.isEmpty(jsonObject.optString("key"))) {
                    Log.i("TAG","上传成功");
                } else {
                    Log.e("TAG","上传失败");
                }
            }
        },new UploadOptions(null,null,false,null,null));
    }

    /**
     * 数据上传服务器
     *  时间戳 t
     */
    public void createData(String w,String t){

    }


}
