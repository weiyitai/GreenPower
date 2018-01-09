package com.qianbajin.greenpower;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
/**
 * @author wWX407408
 * @Created at 2017/12/18  21:04
 * @des
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingFragment";
    public static String PASSWORD = "****************";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_config);
        SharedPreferences  sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        PreferenceScreen screen = getPreferenceScreen();
        if (screen != null) {
            int preferenceCount = screen.getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                Preference preference = screen.getPreference(i);
                if (preference instanceof EditTextPreference) {
                    onSharedPreferenceChanged(sp, preference.getKey());
                }
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Preference preference = findPreference(Constant.SP_KEY_SERVICE_STATUS);
        preference.setSummary(Util.isAccessibilitySettingsOn(getActivity()) ? "已开启" : "未开启");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (preference instanceof EditTextPreference) {
            EditText editText = ((EditTextPreference) preference).getEditText();
            editText.setSelection(editText.getText().length());

        } else if (Constant.SP_KEY_SERVICE_STATUS.equals(key)) {
            getActivity().startActivity(
                    new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Log.d(TAG, "onSharedPreferenceChanged:" + key);
        switch (key) {
            case Constant.SP_KEY_UNLOCK_PASSWORD:
                Preference password = findPreference(key);
                String pwd = sp.getString(key, "");
                if (!TextUtils.isEmpty(pwd)) {
                    int length = pwd.length();
                    if (length <= PASSWORD.length()) {
                        password.setSummary(PASSWORD.substring(0, length));
                    }
                }
                break;
            case Constant.SP_KEY_CLOCK_ALARM:
            case Constant.SP_KEY_POWER_GENERATE_TIME:
                Preference preference = findPreference(key);
                preference.setSummary(sp.getString(key, "7:20"));
                break;
            default:
                break;
        }
    }
}
