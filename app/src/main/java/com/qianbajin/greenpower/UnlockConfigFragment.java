package com.qianbajin.greenpower;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
/**
 * @author Administrator
 * @Created at 2018/1/9 0009  22:56
 * @des
 */

public class UnlockConfigFragment extends PreferenceFragment {

    private PreferenceScreen mPreferenceScreen;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_unlock_config);
        mPreferenceScreen = getPreferenceScreen();
    }

    /**
     * 添加动作
     */
    public void onAddActionClick() {
        Log.d("UnlockConfigFragment", "添加动作");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择动作");
        builder.setItems(new String[]{"dd", "dsd"}, (dialog, which) -> {
            Log.d("UnlockConfigFragment", "which:" + which);
            Preference preference = new Preference(getActivity());
            preference.setTitle("dsds");

            mPreferenceScreen.addPreference(preference);
        });
        builder.show();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
//                     case :
//
//            break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
