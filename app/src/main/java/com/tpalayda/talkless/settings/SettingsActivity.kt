package com.tpalayda.talkless.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tpalayda.talkless.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction()
                .replace(R.id.settings_layout_container, PreferencesFragment())
                .commit()
    }
}
