package com.tpalayda.talkless.Settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.tpalayda.talkless.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_settings)
    }
}