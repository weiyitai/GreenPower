package com.qianbajin.greenpower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * @author wWX407408
 * @Created at 2017/12/18  21:04
 * @des
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getFragmentManager().beginTransaction().replace(R.id.fl_content, new SettingFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new TestFragment()).commit();

    }
}
