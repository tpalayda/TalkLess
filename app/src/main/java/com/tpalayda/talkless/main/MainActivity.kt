package com.tpalayda.talkless.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.tpalayda.talkless.R
import com.tpalayda.talkless.fragments.PresentationFragment
import com.tpalayda.talkless.fragments.TimerFragment
import com.tpalayda.talkless.fragments.TimerFragment.Companion.MY_PERMISSIONS_VIBRATE
import com.tpalayda.talkless.recyclerview.StatisticsRecyclerViewFragment
import com.tpalayda.talkless.settings.PreferencesFragment
import com.tpalayda.talkless.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val timerFragment = TimerFragment()
    private val presentationFragment = PresentationFragment()
    private var activeFragment = timerFragment as Fragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_timer -> {
                if(activeFragment != timerFragment) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, timerFragment)
                            .show(timerFragment)
                            .commit()

                    activeFragment = timerFragment
                    checkPermissions()
                    Log.wtf("123", "timerFragment")
                }
                //.replace(R.id.container, timerFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_presentation -> {
                if(activeFragment != presentationFragment) {
                    if(activeFragment == timerFragment) {
                        supportFragmentManager.beginTransaction()
                                .add(R.id.container, presentationFragment, "2")
                                .hide(activeFragment)
                                .show(presentationFragment)
                                .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.container, presentationFragment)
                                .show(presentationFragment)
                                .commit()
                    }
                    Log.wtf("123", "presentationFragment")
                    activeFragment = presentationFragment
                }

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_statistics -> {
                val statisticsFragment = StatisticsRecyclerViewFragment()
                if(activeFragment != statisticsFragment) {
                    if(activeFragment == presentationFragment) {
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.container, statisticsFragment, "3")
                                .show(statisticsFragment)
                                .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                                .add(R.id.container, statisticsFragment, "3")
                                .hide(activeFragment)
                                .show(statisticsFragment)
                                .commit()
                        Log.wtf("123", "statisticsFragment")
                    }
                    activeFragment = statisticsFragment
                }

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val nightMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("nightModePref", false)

        if(nightMode) {
            setTheme(R.style.NightTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(R.id.container, timerFragment, "1")
                .commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            Log.wtf("123", "permission granted")
        }
    }

    private fun requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.VIBRATE)) {
            Log.wtf("123", "text here");
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.VIBRATE), MY_PERMISSIONS_VIBRATE)
            Log.wtf("123", "request permission")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }
}
