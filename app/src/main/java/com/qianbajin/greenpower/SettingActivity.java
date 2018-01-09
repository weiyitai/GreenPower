package com.qianbajin.greenpower;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author wWX407408
 * @Created at 2017/12/18  21:04
 * @des
 */
public class SettingActivity extends AppCompatActivity {

    public static final String PAGE = "page";
    private int mIntExtra;
    private PreferenceFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mIntExtra = getIntent().getIntExtra(SettingActivity.PAGE, 1);

        if (mIntExtra == 1) {
            mFragment = new SettingFragment();
        } else {
            mFragment = new UnlockConfigFragment();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        getFragmentManager().beginTransaction().replace(R.id.fl_content, mFragment).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new TestFragment()).commit();

    }


    public static void show(Context context, int value) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra(SettingActivity.PAGE, value);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIntExtra == 1) {
            getMenuInflater().inflate(R.menu.unlock, menu);
        } else {
            getMenuInflater().inflate(R.menu.add_action, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.unlock_config:
                SettingActivity.show(this, 2);
                break;
            case R.id.action_add:
                if (mFragment != null) {
                    ((UnlockConfigFragment) mFragment).onAddActionClick();
                }
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
//        return super.onOptionsItemSelected(item);
    }

    public interface OnAddButtonClickListener {

        /**
         * 添加动作点击事件
         */
        void onAddClick();

    }

}
