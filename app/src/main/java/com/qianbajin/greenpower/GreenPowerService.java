package com.qianbajin.greenpower;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author wWX407408
 * @Created at 2017/12/12  16:12
 * @des
 */

public class GreenPowerService extends AccessibilityService {

    private static final String TAG = "GreenPowerService";
    private static final String ALARM_ALERT = "com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT";
    private static final String FORMAT_PATTEN = "HH:mm";
    private static final int MAG_PRO_SWIFT_OFF_CLOCK = 0x001;
    private static final int MSG_PRO_PRESS_ENTER = 0x002;
    private static final int MSG_PRO_PRESS_BACK = 0x003;
    private static final int MSG_PRO_COLLECT_POWER = 0x004;
    private static final int MSG_PRO_PRESS_POWER = 0x005;
    private static final int MSG_PRO_TOGGLE_WIFI = 0x006;
    private static final int MSG_PRO_TOGGLE_MOBILE_NETWORK = 0x007;
    private static final int MSG_UI_UNLOCK_FINISH = 0x101;
    private static final int MSG_UI_JOB_FINISH = 0x102;
    private static final int MSG_UI_FIND_EDITTEXT = 0x103;
    private static final int MSG_UI_FIND_ENT_FOREST = 0x104;
    private static final String TEXT_BACKUP_PASSWORD = "备用密码";
    private static final String TEXT_EMERGENCY_CALL = "紧急电话";
    private static final String TEXT_ANT_FOREST = "蚂蚁森林";
    private static final String TEXT_CONFIRM = "确定";
    private static final String TEXT_INPUT_BACKUP_PASSWORD = "输入您的备份密码。";
    private static final String WIDGET_NAME_EDITTEXT = "android.widget.EditText";
    private static final String WIDGET_NAME_TEXTVIEW = "android.widget.TextView";
    private static final String WIDGET_NAME_BUTTON = "android.widget.Button";
    /**
     * wifi开关所在位置
     */
    private static final float[] POINT_WIFI = new float[]{140.f, 300.f};
    /**
     * 移动网络开关所在位置
     */
    private static final float[] POINT_MOBILE_NETWORK = new float[]{720.f, 300.f};
    /**
     * 使能寻找'输入您的备份密码。'控件
     */
    private static boolean enableInputPwd;
    /**
     * 使能遍历当前页面根节点找到 EditText 控件
     */
    private static boolean enableEach;
    /**
     * 使能寻找'备用密码'控件
     */
    private static boolean enableBackupPassword;
    /**
     * 使能寻找蚂蚁深林控件
     */
    private static boolean enableAntForest;
    /**
     * 是否进入了蚂蚁森林页面做好了准备
     */
    private static boolean enableReady;
    /**
     * 进入蚂蚁森林页面后判断网络次数,超过3次没有网络不收取能量,返回息屏
     */
    private static int netWorkCounter;
    /**
     * 使能打开网络开关遍历确定按键
     */
    private static boolean enableConfirmNetWork;
    /**
     * 使能接收网络变化通知
     */
    private static boolean enableReceiveNetwork;
    private final Handler mUiHandler = new UiHandler(Looper.getMainLooper());
    ;
    private final Handler mProHandler;
    private ClockReceiver mClockReceiver;
    private NetWorkReceiver mNetWorkReceiver;
    private SharedPreferences mSp;

    public GreenPowerService() {
        Log.d(TAG, "GreenPowerService:");

        // 处理子线程
        HandlerThread handlerThread = new HandlerThread("ProHandler");
        handlerThread.start();
        mProHandler = new ProHandler(handlerThread.getLooper());
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");

        registerClockReceiver();
    }

    private void registerClockReceiver() {
        mClockReceiver = new ClockReceiver();
        IntentFilter intentFilter = new IntentFilter(ALARM_ALERT);
        registerReceiver(mClockReceiver, intentFilter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        if (mClockReceiver != null) {
            unregisterReceiver(mClockReceiver);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.d(TAG, "event:" + event);
        AccessibilityNodeInfo source;
//        Log.d("GreenPowerService", "source:" + source);
//        Log.d(TAG, "enableInputPwd:" + enableInputPwd);

        // 寻找备用密码控件
        if (enableBackupPassword && (source = event.getSource()) != null) {
            List<AccessibilityNodeInfo> infoList = source.findAccessibilityNodeInfosByText(TEXT_BACKUP_PASSWORD);
            if (infoList.isEmpty()) {
                Log.d(TAG, "infoList.isEmpty()");
            } else {
                Log.d(TAG, "通过文字找到了'备用密码'控件**********************infoList.size():" + infoList.size());
                AccessibilityNodeInfo nodeInfo = infoList.get(0);
                boolean handleClick = handleClick(nodeInfo);
                if (!handleClick) {
                    for (AccessibilityNodeInfo info : infoList) {
                        boolean handleClick1 = handleClick(info);
                        Log.d(TAG, "handleClick1:" + handleClick1);
                    }
                }
                enableBackupPassword = false;
                enableInputPwd = true;
                mUiHandler.sendEmptyMessageDelayed(MSG_UI_FIND_EDITTEXT, 1000L);
                Log.d(TAG, "点击了'备用密码'控件>>handleClick:" + handleClick);
            }
        }

        // 寻找输入密码控件
//        if (enableInputPwd) {
//            if (source != null) {
//                List<AccessibilityNodeInfo> nodeInfoList = source.findAccessibilityNodeInfosByText(TEXT_INPUT_BACKUP_PASSWORD);
//                if (nodeInfoList.isEmpty()) {
//                    Log.d(TAG, "'输入您的备份密码。'nodeInfoList.isEmpty()");
//                } else {
//                    Log.d(TAG, "通过文字找到'输入您的备份密码。'控件****************:nodeInfoList.size():" + nodeInfoList.size());
//                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
//                    if (rootInActiveWindow != null) {
//                        enableEach = true;
//                        eachEdittext(rootInActiveWindow, WIDGET_NAME_EDITTEXT);
//                    } else {
//                        Log.e(TAG, "rootInActiveWindow == null------------------");
//                    }
//                }
//                if (TEXT_EMERGENCY_CALL.equals(source.getText())) {
//                    Log.d(TAG, "onAccessibilityEvent: 找到了'紧急电话'控件");
//                }
//            } else {
//                Log.d(TAG, "onAccessibilityEvent:-------source1 == null");
//            }
//        }

//        if (source != null && Constant.PKG_ALIPAY.equals(source.getPackageName())) {
//        if (enableAntForest && source != null) {
//            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
//            if (rootInActiveWindow != null) {
//                enableEach = true;
//                eachAntForest(rootInActiveWindow, TEXT_ANT_FOREST);
//            } else {
//                Log.d(TAG, "onAccessibilityEvent:rootInActiveWindow == null--------");
//            }
//        }

//        if (enableConfirmNetWork && (source = event.getSource()) != null) {
//            enableEach = true;
//            eachConfirmButton(source, TEXT_CONFIRM);
//        }
    }

    private void findConfirmButton() {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow != null) {
            enableConfirmNetWork = true;
            eachConfirmButton(rootInActiveWindow, TEXT_CONFIRM);
        } else {
            Log.d(TAG, "findConfirmButton: rootInActiveWindow == null");
        }
    }

    /**
     * 打开网络开关,寻找确定按钮
     *
     * @param nodeInfo 布局
     * @param text     确定文字
     */
    private void eachConfirmButton(AccessibilityNodeInfo nodeInfo, String text) {
        int childCount = nodeInfo.getChildCount();
        Log.d(TAG, "eachConfirmButton>>>>childCount:" + childCount);
        if (childCount == 0) {
            CharSequence className = nodeInfo.getClassName();
            Log.d(TAG, className + "--nodeInfo.getText():" + nodeInfo.getText());
            if (text.equals(nodeInfo.getText()) && WIDGET_NAME_BUTTON.equals(className)) {
                boolean click = handleClick(nodeInfo);
                if (click) {
                    enableConfirmNetWork = false;
                }
                Log.d(TAG, "click:" + click);
            }
        } else {
            for (int i = 0;i < childCount; i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null) {
                    if (!enableConfirmNetWork) {
                        break;
                    }
                    eachConfirmButton(child, text);
                }
            }
        }
    }

    private boolean handleClick(AccessibilityNodeInfo source) {
        return source.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    private void eachAntForest(AccessibilityNodeInfo nodeInfo, String text) {
        int childCount = nodeInfo.getChildCount();
        Log.d(TAG, "eachAntForest>>>>childCount:" + childCount);
        if (childCount == 0) {
            CharSequence className = nodeInfo.getClassName();
            Log.d(TAG, className + "--nodeInfo.getText():" + nodeInfo.getText());
            if (text.equals(nodeInfo.getText()) && WIDGET_NAME_TEXTVIEW.equals(className)) {
                Log.d(TAG, "通过遍历找到了'蚂蚁森林'控件********************");
                enableAntForest = false;
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                boolean action = handleClick(parent);

                Log.d(TAG, "action:" + (action ? "成功进入蚂蚁深林页面****************" : "进入蚂蚁深林失败-------"));
                if (action) {
                    // 10秒后收取能量
                    enableReady = true;
                    mProHandler.sendEmptyMessageDelayed(MSG_PRO_COLLECT_POWER, 15000L);
                } else {
                    mUiHandler.sendEmptyMessage(MSG_UI_FIND_ENT_FOREST);
                }

            }
        } else {
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null) {
                    if (!enableAntForest) {
                        break;
                    }
                    eachAntForest(child, text);
                }
            }
        }
    }

    private void eachEdittext(AccessibilityNodeInfo nodeInfo, String widgetName) {
        int childCount = nodeInfo.getChildCount();
        Log.d(TAG, "eachEdittext>>>childCount:" + childCount);
        if (childCount == 0) {
            CharSequence className = nodeInfo.getClassName();
            Log.d(TAG, className + "--nodeInfo.getText():" + nodeInfo.getText());
            if (widgetName.equals(className)) {
                enableInputPwd = false;
                Log.d(TAG, "通过遍历找到了输入控件********************");
                String password = mSp.getString(Constant.SP_KEY_UNLOCK_PASSWORD, "");
                if (!TextUtils.isEmpty(password)) {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
                    boolean performAction = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    Log.d(TAG, "输入密码操作:" + (performAction ? "成功*************" : "失败**************"));
                    if (!performAction) {
                        arguments.putCharSequence(
                                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
                        performAction = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                        Log.d(TAG, "再次输入密码操作:" + (performAction ? "成功*************" : "失败**************"));
                    }
                    mProHandler.sendMessageDelayed(Message.obtain(mProHandler, MSG_PRO_PRESS_ENTER, false), 100L);
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null) {
                    if (!enableInputPwd) {
                        break;
                    }
                    eachEdittext(child, widgetName);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt:----------------");
    }

    /**
     * 注册网络变化和时间变化监听
     */
    private void registerNetworkReceiver() {
        mNetWorkReceiver = new NetWorkReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mNetWorkReceiver, intentFilter);
    }

    public void unregisterNetworkReceiver() {
        if (mNetWorkReceiver != null) {
            unregisterReceiver(mNetWorkReceiver);
            mNetWorkReceiver = null;
        }
    }
    /*event:EventType: TYPE_WINDOW_STATE_CHANGED; EventTime: 14847324; PackageName: com.sec.android.app.clockpackage; MovementGranularity: 0; Action: 0 [ ClassName: com.sec.android.app.clockpackage.alarm.AlarmAlert; Text: [时钟]; ContentDescription: null; ItemCount: -1; CurrentItemIndex: -1; IsEnabled: true; IsPassword: false; IsChecked: false; IsFullScreen: true; Scrollable: false; BeforeText: null; FromIndex: -1; ToIndex: -1; ScrollX: -1; ScrollY: -1; MaxScrollX: -1; MaxScrollY: -1; AddedCount: -1; RemovedCount: -1; ParcelableData: null ]; recordCount: 0*/

    private boolean isTimeReach() {
        String collectTime = mSp.getString(Constant.SP_KEY_POWER_GENERATE_TIME, "");
        String time = new SimpleDateFormat(FORMAT_PATTEN, Locale.CHINA).format(new Date());
        boolean timeReach = time.compareTo(collectTime) >= 0;
        Log.d(TAG, "isTimeReach,time:" + time + "   collectTime:" + collectTime + (timeReach ? "   收集能量时间到了********" : "   收集能量时间没到"));
        return timeReach;
    }

    final class ProHandler extends Handler {

        public ProHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Instrumentation in = new Instrumentation();
            switch (msg.what) {
                case MAG_PRO_SWIFT_OFF_CLOCK:
                    long millis = SystemClock.uptimeMillis();
                    Log.d("MoniClickTast", "millis:" + millis);
                    in.sendPointerSync(MotionEvent.obtain(millis + 100, millis + 100, MotionEvent.ACTION_DOWN, 720.0f, 1784.0f, 0));

                    long millis2 = SystemClock.uptimeMillis();
                    in.sendPointerSync(MotionEvent.obtain(millis2 + 100, millis2 + 100, MotionEvent.ACTION_MOVE, 100.0f, 1784.0f, 0));

                    long millis1 = SystemClock.uptimeMillis();
                    Log.d("MoniClickTast", "millis1:" + millis1);
                    in.sendPointerSync(MotionEvent.obtain(millis1 + 200, millis1 + 200, MotionEvent.ACTION_UP, 100.0f, 1784.0f, 0));
                    Log.d(TAG, "handleMessage:解锁结束*****************");
                    break;
                case MSG_PRO_PRESS_ENTER:
                    // 停止闹钟和输入密码后须按下回车键
                    in.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                    // 使能寻找'备用密码'控件,停止闹钟后需要寻找'备用密码'控件,输入密码后不需要
                    boolean enable = (boolean) msg.obj;
                    enableBackupPassword = enable;
                    if (!enable) {
                        // 解锁完成后进入主界面
                        mUiHandler.sendEmptyMessageDelayed(MSG_UI_UNLOCK_FINISH, 2000L);
                    }
                    break;
                case MSG_PRO_PRESS_BACK:
                    in.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    break;
                case MSG_PRO_PRESS_POWER:
                    in.sendKeyDownUpSync(KeyEvent.KEYCODE_POWER);
                    break;
                case MSG_PRO_TOGGLE_WIFI:
                    // 模拟下拉通知栏切换wifi开关
                    toggleNetwork(in, POINT_WIFI);
                    break;
                case MSG_PRO_TOGGLE_MOBILE_NETWORK:
                    toggleNetwork(in, POINT_MOBILE_NETWORK);
                    break;
                case MSG_PRO_COLLECT_POWER:
                    boolean timeReach = isTimeReach();
                    if (!timeReach || !enableReady) {
                        Log.d(TAG, "handleMessage:" + (!timeReach ? "收取时间还没到" : !enableReady ? "还没进入蚂蚁深林页面做好准备" : ""));
                        return;
                    }
                    // 收集绿色能量
                    Log.d(TAG, "handleMessage:开始收取绿色能量*****************");
                    boolean debug = mSp.getBoolean(Constant.SP_KEY_DEBUG_MODE, true);
                    if (debug) {
                        Log.d(TAG, "handleMessage:模拟收集能量,睡眠两秒");
                        SystemClock.sleep(8000L);
                    } else {
//                    float minX = 200.f, maxX = 1250.f, minY = 500.f, maxY = 1200.f, intervalX = 180.f, intervalY = 180.f;
                        float minX = 160.f, maxX = 1280.f, minY = 500.f, maxY = 1300.f, intervalX = 160.f, intervalY = 160.f;
                        for (float j = minY; j <= maxY; j += intervalY) {
                            for (float i = minX; i <= maxX; i += intervalX) {
                                long milli = SystemClock.uptimeMillis();
                                in.sendPointerSync(MotionEvent.obtain(milli, milli, MotionEvent.ACTION_DOWN, i, j, 0));
                                long milli3 = SystemClock.uptimeMillis();
                                in.sendPointerSync(MotionEvent.obtain(milli3, milli3, MotionEvent.ACTION_UP, i, j, 0));
                                Log.d(TAG, "handleMessage:i:" + i + "  j:" + j + "  milli:" + milli);
                                SystemClock.sleep(300L);
                            }
                        }
                    }
                    Log.d(TAG, "handleMessage:收集绿色能量结束*******************");
                    mProHandler.sendEmptyMessageDelayed(MSG_PRO_PRESS_BACK, 500L);
                    mProHandler.sendEmptyMessageDelayed(MSG_PRO_PRESS_BACK, 1500L);
                    mUiHandler.sendEmptyMessageDelayed(MSG_UI_JOB_FINISH, 5000L);
                    break;
                default:
                    break;
            }
        }

        private void toggleNetwork(Instrumentation in, float[] point) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            float midX = widthPixels / 2;
            float midY = heightPixels / 2;
            // 下拉通知栏
            long millis = SystemClock.uptimeMillis();
            in.sendPointerSync(MotionEvent.obtain(millis, millis, MotionEvent.ACTION_DOWN, midX, 0.f, 0));
            long milli1 = SystemClock.uptimeMillis();
            in.sendPointerSync(MotionEvent.obtain(milli1, milli1, MotionEvent.ACTION_MOVE, midX, midY, 0));
            long milli2 = SystemClock.uptimeMillis();
            in.sendPointerSync(MotionEvent.obtain(milli2, milli2, MotionEvent.ACTION_UP, midX, midY, 0));

            // 点击事件
            SystemClock.sleep(500L);
            long milli3 = SystemClock.uptimeMillis();
            in.sendPointerSync(MotionEvent.obtain(milli3, milli3, MotionEvent.ACTION_DOWN, point[0], point[1], 0));
            in.sendPointerSync(MotionEvent.obtain(milli3 + 10L, milli3 + 10L, MotionEvent.ACTION_UP, point[0], point[1], 0));

            // 收起通知栏
            mProHandler.sendEmptyMessageDelayed(MSG_PRO_PRESS_BACK, 500L);
        }
    }


    /**
     * 处理UI线程任务
     */
    final class UiHandler extends Handler {

        public UiHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UI_UNLOCK_FINISH:
                    // 进入了主页面
                    boolean netWorkAvailable = Util.isNetWorkAvailable(getApplicationContext());
                    if (netWorkAvailable) {
                        Log.d(TAG, "handleMessage:启动支付宝****************");
                        Util.startAliPay(getApplicationContext());
                        mUiHandler.sendEmptyMessageDelayed(MSG_UI_FIND_ENT_FOREST, 2000L);
                    } else {
                        Log.d(TAG, "handleMessage:网络不可用,使能网络监听");
                        enableReceiveNetwork = true;
                        boolean wifi = mSp.getBoolean(Constant.SP_KEY_SWITCH_WIFI, false);
                        if (wifi) {
                            // 通过代码打开wifi,不需要弹窗确认是否允许打开
//                            enableConfirmNetWork = true;
//                            Util.toggleWifi(GreenPowerService.this, true);

                            // 通过下拉通知栏打开wifi
                            //  先判断wifi是否已经打开,没打开则去打开开关
                            boolean wifiSwitchOn = Util.isWifiSwitchOn(getApplicationContext());
                            if (!wifiSwitchOn) {
                                mProHandler.sendEmptyMessageDelayed(MSG_PRO_TOGGLE_WIFI, 1000L);
                            }
                        }
                        boolean mobile = mSp.getBoolean(Constant.SP_KEY_SWITCH_MOBILE_NETWORK, false);
                        if (mobile) {
                            // 通过代码打开网络开关,需要弹窗确认是否允许打开
//                            enableConfirmNetWork = true;
//                            Util.toggleMobileNetwork(getApplicationContext(), true);

                            // 通过下拉通知栏打开网络开关,不需要弹窗
                            boolean networkSwitchOn = Util.isMobileNetworkSwitchOn(getApplicationContext());
                            if (!networkSwitchOn) {
                                mProHandler.sendEmptyMessageDelayed(MSG_PRO_TOGGLE_MOBILE_NETWORK, 1000L);
                            }
                        }
                    }
                    break;
                case MSG_UI_FIND_EDITTEXT:
                    AccessibilityNodeInfo inActiveWindow = getRootInActiveWindow();
                    if (inActiveWindow != null) {
                        enableInputPwd = true;
                        eachEdittext(inActiveWindow, WIDGET_NAME_EDITTEXT);
                    } else {
                        Log.d(TAG, "MSG_UI_FIND_EDITTEXT>>:rootInActiveWindow == null--------");
                    }
                    break;
                case MSG_UI_FIND_ENT_FOREST:
                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                    if (rootInActiveWindow != null) {
                        enableAntForest = true;
                        eachAntForest(rootInActiveWindow, TEXT_ANT_FOREST);
                        enableAntForest = false;
                    } else {
                        Log.d(TAG, "MSG_UI_FIND_ENT_FOREST>>:rootInActiveWindow == null--------");
                    }
                    break;
                case MSG_UI_JOB_FINISH:
                    enableAntForest = false;
                    enableBackupPassword = false;
                    enableEach = false;
                    enableInputPwd = false;
                    enableReady = false;
                    enableConfirmNetWork = false;
                    enableReceiveNetwork = false;
                    boolean off = mSp.getBoolean(Constant.SP_KEY_TURN_OFF_NETWORK, false);
                    long gas = 0L;
                    //操作一项需 1200ms
                    if (off) {
                        if (Util.isWifiSwitchOn(getApplicationContext())) {
                            mProHandler.sendEmptyMessage(MSG_PRO_TOGGLE_WIFI);
                            gas += 1200L;
                        }

                        if (Util.isMobileNetworkSwitchOn(getApplicationContext())) {
                            mProHandler.sendEmptyMessage(MSG_PRO_TOGGLE_MOBILE_NETWORK);
                            gas += 1200L;
                        }
                    }
                    // 杀死支付宝进程
//                    killAliBackground();
                    unregisterNetworkReceiver();
                    mProHandler.sendEmptyMessageDelayed(MSG_PRO_PRESS_POWER, 1500L + gas);
                    break;
                default:
                    break;
            }
        }
    }

    private void killAliBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(Constant.PKG_ALIPAY);
    }

    private final class NetWorkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "NetWorkReceiver,action:" + action);
            switch (action) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    boolean available = Util.isNetWorkAvailable(getApplicationContext());
                    Log.d(TAG, "networkAvailable:" + available);
                    if (enableReceiveNetwork && available) {
                        mUiHandler.sendEmptyMessageDelayed(MSG_UI_UNLOCK_FINISH, 1000L);
                    }
                    break;
                case Intent.ACTION_TIME_TICK:
                    if (isTimeReach()) {
                        if (enableReady) {
                            Log.d(TAG, "onReceive:收能量时间到了,开始收集能量********************");
                            mProHandler.sendEmptyMessageDelayed(MSG_PRO_COLLECT_POWER, 3000L);
                        } else {
                            String alarm = mSp.getString(Constant.SP_KEY_CLOCK_ALARM, "");
                            String generateTime = mSp.getString(Constant.SP_KEY_POWER_GENERATE_TIME, "");
                            // 到了收能量时间,但是没有成功进入蚂蚁森林页面,停止操作并熄灭屏幕
                            if (generateTime.compareTo(alarm) > 0) {
                                Log.d(TAG, "onReceive:没能成功进入蚂蚁深林页面,停止操作并熄灭屏幕");
                                mUiHandler.sendEmptyMessage(MSG_UI_JOB_FINISH);
                            }
                        }
                    } else {
                        Log.d(TAG, "onReceive:收能量时间还没到-----------------");
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private final class ClockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "闹钟响了>>>action:" + action);
            if (ALARM_ALERT.equals(action)) {
                String time = mSp.getString(Constant.SP_KEY_CLOCK_ALARM, "");
                String format = new SimpleDateFormat(FORMAT_PATTEN, Locale.CHINA).format(new Date());
                Log.d(TAG, "ClockReceiver>>time:" + time + "  format:" + format);
                if (format.equals(time)) {
                    mProHandler.sendMessageDelayed(Message.obtain(mProHandler, MSG_PRO_PRESS_ENTER, true), 2000L);
                    registerNetworkReceiver();
                }
            }
        }
    }
}