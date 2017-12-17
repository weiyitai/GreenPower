package com.qianbajin.greenpower;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;

import java.io.File;
/**
 * @author Administrator
 * @Created at 2017/12/16 0016  23:01
 * @des
 */

public class MyStrategy extends DiskLogStrategy implements FormatStrategy {

    public MyStrategy(Handler handler) {
        super(handler);
    }

    @Override
    public void log(int priority, String tag, String message) {



    }

    public static final class Builder {

        public void build() {

            String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String folder = diskPath + File.separatorChar + "logger";

            HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
            ht.start();

        }
    }

}
