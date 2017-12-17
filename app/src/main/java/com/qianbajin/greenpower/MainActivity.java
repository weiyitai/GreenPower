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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {

    private ActivityManager mAm;
    private ClockReceive mReceive;

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

        AaHandler aaHandler = new AaHandler(Looper.getMainLooper());
        aaHandler.obtainMessage(2, "颠三倒四收到").sendToTarget();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
                .tag(BuildConfig.APPLICATION_ID)
                .build();
        CsvFormatStrategy strategy1 = CsvFormatStrategy.newBuilder()
                .build();

        Logger.addLogAdapter(new DiskLogAdapter(strategy1));
//        startService(new Intent(this, GreenPowerService.class));

        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.deskclock.ALARM_ALERT");
        intentFilter.addAction("android.intent.action.SHOW_ALARMS");
        intentFilter.addAction("com.android.deskclock.ALARM_DISMISS");
        intentFilter.addAction("com.android.deskclock.ALARM_DONE");
        intentFilter.addAction("com.android.deskclock.ALARM_SNOOZE");
        intentFilter.addAction(AlarmClock.ACTION_SHOW_ALARMS);
        intentFilter.addAction(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mReceive = new ClockReceive();
        registerReceiver(mReceive, intentFilter);

    }

    static class ClockReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("MainActivity", "闹钟响了>>>>>>>>>>>>>>>action:" + action);

        }
    }


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

    public void setClock(View view){
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                 mote
//        am.cancel(PendingIntent.getBroadcast());
        Intent intent = new Intent(this, ClockReceive.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1235, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
        am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 60000,pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 60000,AlarmManager.INTERVAL_DAY,pendingIntent);
        
    }

    public void stopClock(View view) {
        Intent intent = new Intent();
//        intent.setAction("com.android.deskclock.ALARM_DISMISS");
        intent.setAction(AlarmClock.ACTION_DISMISS_ALARM);
        sendBroadcast(intent);

    }

    static class AaHandler extends Handler {

        public AaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (msg.what ==1) {
            Object obj = msg.obj;
            Log.d("AaHandler", "obj:" + obj);
            Log.d("AaHandler", Thread.currentThread().getName());

            Instrumentation in = new Instrumentation();
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
//            }
        }
    }


    static class MoniClickTast extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Instrumentation in = new Instrumentation();
            long millis = SystemClock.uptimeMillis();
            Log.d("MoniClickTast", "millis:" + millis);
            in.sendPointerSync(MotionEvent.obtain(millis, millis, MotionEvent.ACTION_DOWN, 280.0f, 870.0f, 0));
            long millis1 = SystemClock.uptimeMillis();
            Log.d("MoniClickTast", "millis1:" + millis1);
            in.sendPointerSync(MotionEvent.obtain(millis1, millis1, MotionEvent.ACTION_UP, 280.0f, 870.0f, 0));

            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;

            return null;
        }
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

        handler.obtainMessage(1, "dsdsd").sendToTarget();

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "Exception");
        }

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
}
