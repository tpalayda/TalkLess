package com.tpalayda.talkless.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.R.id.seekbar
import android.support.v7.preference.R.id.seekbar_value
import android.support.v7.widget.AppCompatSeekBar
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import com.tpalayda.talkless.R

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.viewTreeObserver.addOnDrawListener {
            val sizePref = findPreference(getString(R.string.vibrationTimeKey))

            val prefView: View? = listView.layoutManager.findViewByPosition(sizePref.order)
            prefView?.apply {
                findViewById<TextView>(seekbar_value).apply {
                    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                }

                findViewById<AppCompatSeekBar>(seekbar).apply {
                    (layoutParams as LinearLayout.LayoutParams).apply {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }
            }
        }
    }

}