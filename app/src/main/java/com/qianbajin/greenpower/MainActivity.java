package com.qianbajin.greenpower;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Log.d("MainActivity", "what:" + what);
            Log.d("MainActivity", Thread.currentThread().getName());

        }
    };
    private ActivityManager mAm;
    private ClockReceive mReceive;

    static int indexOf(String source,
                       String target,
                       int fromIndex) {
        final int sourceLength = source.length();
        final int targetLength = target.length();
        if (fromIndex >= sourceLength) {
            return (targetLength == 0 ? sourceLength : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetLength == 0) {
            return fromIndex;
        }

        char first = target.charAt(0);
        int max = (sourceLength - targetLength);

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source.charAt(i) != first) {
                while (++i <= max && source.charAt(i) != first) {
                }
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetLength - 1;
                for (int k = 1; j < end && source.charAt(j)
                        == target.charAt(k); j++, k++) {
                }

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public void moniClick(View view) {

//        Instrumentation in = new Instrumentation();
//        long millis = SystemClock.uptimeMillis();
//        Log.d("MoniClickTast", "millis:" + millis);
//        in.sendPointerSync(MotionEvent.obtain(millis, millis, MotionEvent.ACTION_DOWN, 280.0f, 870.0f, 0));
//        long millis1 = SystemClock.uptimeMillis();
//        Log.d("MoniClickTast", "millis1:" + millis1);
//        in.sendPointerSync(MotionEvent.obtain(millis1, millis1, MotionEvent.ACTION_UP, 280.0f, 870.0f, 0));

//        new MoniClickTast().execute();
        new MoniClickTast().execute();

//        AaHandler aaHandler = new AaHandler(Looper.getMainLooper());
//        aaHandler.obtainMessage(2, "颠三倒四收到").sendToTarget();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, DateFormat.getDateTimeInstance().format(new Date(1515340800235L)));

//        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
//                .tag(BuildConfig.APPLICATION_ID)
//                .build();
//        CsvFormatStrategy strategy1 = CsvFormatStrategy.newBuilder()
//                .build();
//
//        Logger.addLogAdapter(new DiskLogAdapter(strategy1));
//        startService(new Intent(this, GreenPowerService.class));

        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.deskclock.ALARM_ALERT");
        intentFilter.addAction("android.intent.action.SHOW_ALARMS");
        intentFilter.addAction("com.android.deskclock.ALARM_DISMISS");
        intentFilter.addAction("com.android.deskclock.ALARM_DONE");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT");
        intentFilter.addAction("android.intent.action.SHOW_ALARMS");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_NOTIFICATION_CLEAR");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_NOTIFICATION_DISMISS");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_VIEWALARM");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.DIRECT_ALARM_STOP");
        intentFilter.addAction("com.sec.android.clockpackage.SET_ALARM");
        intentFilter.addAction("com.sec.android.widgetapp.alarmclock.NOTIFY_ALARM_CHANGE_WIDGET_SNOOZE");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_NOTIFICATION_CLEAR");
        intentFilter.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_VIEWALARM");
        intentFilter.addAction(AlarmClock.ACTION_SHOW_ALARMS);
        intentFilter.addAction(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mReceive = new ClockReceive();
        registerReceiver(mReceive, intentFilter);

        SettingActivity.show(this, 1);
//
//        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_PHONE_STATE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MODIFY_PHONE_STATE}, 122);
//            }
//        }
    }

    public void wapp(View view) {
        boolean on;
        if (Util.isMobileNetworkSwitchOn(this)) {
            on = false;
        } else {
            on = true;
        }
//        boolean b1 = Util.toggleMobileNetwork(this, true);

        boolean wifiSwitchOn = Util.isWifiSwitchOn(this);
        boolean networkSwitchOn = Util.isMobileNetworkSwitchOn(this);

//        Log.d("MainActivity", "b1:" + b1);
//        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        boolean wifiEnabled = wifiManager.isWifiEnabled();
//        Log.d("MainActivity", "wifiEnabled:" + wifiEnabled);
//        wifiManager.setWifiEnabled(!wifiEnabled);
//
//        try {
//            Method getDataEnabled = tm.getClass().getDeclaredMethod("getDataEnabled", new Class[0]);
//            Method setDataEnabled = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
//            Object invoke = getDataEnabled.invoke(tm, new Object[0]);
//            Log.d("MainActivity", "invoke:" + invoke);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_PHONE_STATE);
////                if (permission == PackageManager.PERMISSION_GRANTED) {
//            Object invoke1 = setDataEnabled.invoke(tm, false);
//            Log.d("MainActivity", "invoke1:" + invoke1);
////                } else {
////                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
////                }
////            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        int a = 1, b = 2;
//        int i =

//        PathClassLoader pathClassLoader = new PathClassLoader("/system/app/ClockPackage_L/ClockPackage_L.apk", ClassLoader.getSystemClassLoader());
//
//        try {
//            Class<?> aClass = pathClassLoader.loadClass("com.sec.android.app.clockpackage.alarm.AlarmAlert");
//            if (aClass != null) {
//                Method[] declaredMethods = aClass.getDeclaredMethods();
//                for (Method declaredMethod : declaredMethods) {
//                    Log.d("MainActivity", "declaredMethod.getName():" + declaredMethod.getName());
//                }
//            }
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void setClock(View view) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        am.cancel(PendingIntent.getBroadcast());
        Intent intent = new Intent(this, ClockReceive.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1235, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    public void stopClock(View view) {
        try {
            PathClassLoader pathClassLoader = new PathClassLoader("/system/app/ClockPackage_L/ClockPackage_L.apk", ClassLoader.getSystemClassLoader());
            Class<?> aClass = pathClassLoader.loadClass("com.sec.android.app.clockpackage.alarm.AlarmAlert");
//            Class<?> aClass = Class.forName("com.sec.android.app.clockpackage.alarm.AlarmAlert", false, ClassLoader.getSystemClassLoader());
            Constructor<?>[] constructors = aClass.getConstructors();
            Object o = null;
            for (Constructor<?> constructor : constructors) {
                Log.d("MainActivity", "constructor.getName():" + constructor.getName());
                o = constructor.newInstance();
            }
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                boolean pressStopBtn = declaredMethod.getName().equals("pressStopBtn");
                if (pressStopBtn) {
                    Log.d("MainActivity", "找到了停止的方法");
                    declaredMethod.setAccessible(true);
                    Object invoke = declaredMethod.invoke(o, new Object[]{});
                    Log.d("MainActivity", "invoke:" + invoke);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction("com.samsung.sec.android.clockpackage.alarm.ALARM_NOTIFICATION_DISMISS");
//        intent.setAction(AlarmClock.ACTION_DISMISS_ALARM);
//        sendBroadcast(intent);

    }

    public void moniButton(View view) {
        Toast.makeText(this, "点击了能量按钮", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "点击了能量按钮");
    }

    public void click(View view) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        HandlerThread thread = new HandlerThread("hahahaha");
        thread.start();
        AaHandler handler = new AaHandler(thread.getLooper());

//        handler.obtainMessage(1, "dsdsd").sendToTarget();
        handler.sendEmptyMessageDelayed(4, 100L);

//        try {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity");
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("MainActivity", "Exception");
//        }

//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process su = runtime.exec("su");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean available = Shell.SU.available();
//                Log.d("MainActivity", "available:" + available);
//                if (available) {
//
//                }
////                upgradeRootPermission(getPackageCodePath());
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceive != null) {
            unregisterReceiver(mReceive);

        }
    }

    static class ClockReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("MainActivity", "闹钟响了>>>>>>>>>>>>>>>action:" + action);
            Bundle extras = intent.getExtras();
            Log.d("MainActivity", "extras:" + extras);
            if (extras != null) {
//                extras.getParcelable()
            }
        }
    }


    static class AaHandler extends Handler {

        public AaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Instrumentation in = new Instrumentation();
            if (msg.what == 1) {
                Object obj = msg.obj;
                Log.d("AaHandler", "obj:" + obj);
                Log.d("AaHandler", Thread.currentThread().getName());

                long millis = SystemClock.uptimeMillis();
                Log.d("MoniClickTast", "millis:" + millis);
                in.sendPointerSync(MotionEvent.obtain(millis, millis, MotionEvent.ACTION_DOWN, 280.0f, 870.0f, 0));
                long millis1 = SystemClock.uptimeMillis();
                Log.d("MoniClickTast", "millis1:" + millis1);
                in.sendPointerSync(MotionEvent.obtain(millis1, millis1, MotionEvent.ACTION_UP, 280.0f, 870.0f, 0));

                //                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            } else if (msg.what == 2) {
                in.sendKeyDownUpSync(KeyEvent.KEYCODE_POWER);
            } else if (msg.what == 3) {
                long millis = SystemClock.uptimeMillis();
                Log.d("MoniClickTast", "millis:" + millis);
                in.sendPointerSync(MotionEvent.obtain(millis, millis, MotionEvent.ACTION_DOWN, 720.0f, 0.f, 0));

                long millis2 = SystemClock.uptimeMillis();
                in.sendPointerSync(MotionEvent.obtain(millis + 100L, millis + 100L, MotionEvent.ACTION_MOVE, 720.0f, 1256.0f, 0));

                long millis1 = SystemClock.uptimeMillis();
                Log.d("MoniClickTast", "millis1:" + millis1);
                in.sendPointerSync(MotionEvent.obtain(millis + 200L, millis + 200L, MotionEvent.ACTION_UP, 720.0f, 1256.0f, 0));

                SystemClock.sleep(1000L);
                long millis3 = SystemClock.uptimeMillis();
                in.sendPointerSync(MotionEvent.obtain(millis3, millis3, MotionEvent.ACTION_DOWN, 720.f, 300.f, 0));
                in.sendPointerSync(MotionEvent.obtain(millis3, millis3, MotionEvent.ACTION_UP, 720.f, 300.f, 0));

            } else if (msg.what == 4) {
//                float minX = 160.f, maxX = 1280.f, minY = 500.f, maxY = 1300.f, intervalX = 160.f, intervalY = 160.f;
//                for (float j = minY; j <= maxY; j += intervalY) {
//                    for (float i = minX; i <= maxX; i += intervalX) {
//                        long milli = SystemClock.uptimeMillis();
//                        in.sendPointerSync(MotionEvent.obtain(milli, milli, MotionEvent.ACTION_DOWN, i, j, 0));
//                        long milli3 = SystemClock.uptimeMillis();
//                        in.sendPointerSync(MotionEvent.obtain(milli3, milli3, MotionEvent.ACTION_UP, i, j, 0));
//                        Log.d(TAG, "handleMessage:i:" + i + "  j:" + j + "  milli:" + milli);
//                        SystemClock.sleep(300L);
//                    }
//                }
            }

        }
    }


    static class MoniClickTast extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Instrumentation in = new Instrumentation();
            long millis = SystemClock.uptimeMillis();
            Log.d("MoniClickTast", "millis:" + millis);
            in.sendPointerSync(MotionEvent.obtain(millis + 100, millis + 100, MotionEvent.ACTION_DOWN, 280.0f, 870.0f, 0));

            long millis2 = SystemClock.uptimeMillis();
            in.sendPointerSync(MotionEvent.obtain(millis2 + 100, millis2 + 2500, MotionEvent.ACTION_MOVE, 280.0f, 1356.0f, 0));

            long millis1 = SystemClock.uptimeMillis();
            Log.d("MoniClickTast", "millis1:" + millis1);
            in.sendPointerSync(MotionEvent.obtain(millis1 + 3000, millis1 + 3000, MotionEvent.ACTION_UP, 280.0f, 870.0f, 0));

            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;

            return null;
        }
    }
}