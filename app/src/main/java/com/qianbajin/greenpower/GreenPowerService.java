package com.qianbajin.greenpower;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * @author wWX407408
 * @Created at 2017/12/12  16:12
 * @des
 */

public class GreenPowerService extends AccessibilityService {

    public static final String TAG = "GreenPowerService";

    public static final String TEXT_BACKUP_PASSWORD = "备用密码";
    public static final String TEXT_EMERGENCY_CALL = "紧急电话";
    public static final String TEXT_INPUT_BACKUP_PASSWORD = "输入您的备份密码。";
    private boolean ENABLE_UNLOCK, ENABLE_EACH;
    private String WIDGET_NAME_EDITTEXT = "android.widget.EditText";
    private Handler mHandler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("GreenPowerService", "event:" + event);
        int windowId = event.getWindowId();
        AccessibilityNodeInfo source = event.getSource();
//        Log.d("GreenPowerService", "source:" + source);
//        Log.d(TAG, "ENABLE_UNLOCK:" + ENABLE_UNLOCK);
        if (source != null) {
//            if ("com.sec.android.app.clockpackage".equals(event.getPackageName())) {
//                eachWidget(source);
//            }
                eachWidget(source);
//            if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
//            }


//            List<AccessibilityNodeInfo> infoList = source.findAccessibilityNodeInfosByText(TEXT_BACKUP_PASSWORD);
//            if (infoList.isEmpty()) {
////                Logger.d("infoList.isEmpty()");
//                Log.d(TAG, "infoList.isEmpty()");
//            } else {
////                Logger.d("通过文字找到了'备用密码'控件**********************");
//                Log.d(TAG, "通过文字找到了'备用密码'控件**********************");
//                boolean handleClick = handleClick(source);
//                ENABLE_UNLOCK = true;
//                Log.d(TAG, "handleClick:" + handleClick);
//            }


//            for (AccessibilityNodeInfo info : infoList) {
//            }
//            CharSequence text = source.getText();
//            if (TEXT_BACKUP_PASSWORD.equals(text)) {
//                Log.d(TAG, "onAccessibilityEvent: 找到了'备用密码'控件");
//                boolean handleClick = handleClick(source);
//                if (!handleClick) {
//                    handleClick(source);
//                }
//                Log.d(TAG, "handleClick:" + handleClick);
////                boolean handle = handleClick(source);
////                Log.d(TAG, "handle:" + handle);
//            } else {
//                Log.d(TAG, "-------source == null");
//            }
        }

        if (ENABLE_UNLOCK) {
            if (source != null) {
                List<AccessibilityNodeInfo> nodeInfoList = source.findAccessibilityNodeInfosByText(TEXT_INPUT_BACKUP_PASSWORD);
                if (nodeInfoList.isEmpty()) {
                    Log.d(TAG, "'输入您的备份密码。'nodeInfoList.isEmpty()");
                } else {
                    Log.d(TAG, "通过文字找到'输入您的备份密码。'控件****************:nodeInfoList.size():" + nodeInfoList.size());
                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                    if (rootInActiveWindow != null) {
                        ENABLE_EACH = true;
                        eachWidget(rootInActiveWindow);
                    } else {
                        Log.e(TAG, "rootInActiveWindow == null------------------");
                    }
                }
                if (TEXT_EMERGENCY_CALL.equals(source.getText())) {
                    Log.d(TAG, "onAccessibilityEvent: 找到了'紧急电话'控件");
                }
            } else {
                Log.d(TAG, "onAccessibilityEvent:-------source1 == null");
            }
        }
    }

    private boolean handleClick(AccessibilityNodeInfo source) {
        return source.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    private void eachWidget(AccessibilityNodeInfo nodeInfo) {
        int childCount = nodeInfo.getChildCount();
        Log.d(TAG, "childCount:" + childCount);
        if (childCount == 0) {
            CharSequence className = nodeInfo.getClassName();
//            nodeInfo.findAccessibilityNodeInfosByViewId()
//            Log.d(TAG, className + "--nodeInfo.getText():" + nodeInfo.getText());
            Log.d(TAG, "eachWidget:" + nodeInfo);
//            if (WIDGET_NAME_EDITTEXT.equals(className)) {
//                ENABLE_EACH = false;
//                Log.d(TAG, "找到了输入控件********************");
//                ENABLE_UNLOCK = false;
//                Bundle arguments = new Bundle();
//                arguments.putCharSequence(
//                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "woyi178");
//                boolean performAction = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                Log.d(TAG, "输入了密码,performAction:****************" + performAction);
//                handleUnLock();
////                InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                ime.
//            }
        } else {
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null) {
//                    if (!ENABLE_EACH) {
//                        break;
//                    }
                    eachWidget(child);
                }
            }
        }
    }

    private void handleUnLock() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new UnlockTask().execute("");
            }
        }, 500);

    }
    /*event:EventType: TYPE_WINDOW_STATE_CHANGED; EventTime: 14847324; PackageName: com.sec.android.app.clockpackage; MovementGranularity: 0; Action: 0 [ ClassName: com.sec.android.app.clockpackage.alarm.AlarmAlert; Text: [时钟]; ContentDescription: null; ItemCount: -1; CurrentItemIndex: -1; IsEnabled: true; IsPassword: false; IsChecked: false; IsFullScreen: true; Scrollable: false; BeforeText: null; FromIndex: -1; ToIndex: -1; ScrollX: -1; ScrollY: -1; MaxScrollX: -1; MaxScrollY: -1; AddedCount: -1; RemovedCount: -1; ParcelableData: null ]; recordCount: 0*/

    /*event:EventType: TYPE_WINDOW_STATE_CHANGED; EventTime: 14847324; PackageName: com.sec.android.app.clockpackage; MovementGranularity: 0; Action: 0 [ ClassName: com.sec.android.app.clockpackage.alarm.AlarmAlert; Text: [时钟]; ContentDescription: null; ItemCount: -1; CurrentItemIndex: -1; IsEnabled: true; IsPassword: false; IsChecked: false; IsFullScreen: true; Scrollable: false; BeforeText: null; FromIndex: -1; ToIndex: -1; ScrollX: -1; ScrollY: -1; MaxScrollX: -1; MaxScrollY: -1; AddedCount: -1; RemovedCount: -1; ParcelableData: null ]; recordCount: 0*/
    @Override
    public void onInterrupt() {

    }



    static class UnlockTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute:按下回车键之前**********************");
        }

        @Override
        protected Void doInBackground(String... strings) {
            Instrumentation inst = new Instrumentation();
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
//            inst.sendPointerSync();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute:输入密码后按下了回车键**********************");
        }
    }

}
