package com.example.bing.shopping.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.bing.shopping.R;
import com.example.bing.shopping.infrastructure.Utils;

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SortPreferenceFragment())
                .commit();
    }

    public static class SortPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_general);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_list_name)));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            setPreferenceSummary(preference, o);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Utils.LIST_ORDER_PREFERENCE, o.toString()).apply();
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            setPreferenceSummary(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                                    .getString(preference.getKey(), ""));
        }

        private void setPreferenceSummary(Preference preference, Object value) {
            String stringValue = value.toString();

            if(preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int preferenceIndex = listPreference.findIndexOfValue(stringValue);
                if(preferenceIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[preferenceIndex]);
                }
            }
        }
    }
}
