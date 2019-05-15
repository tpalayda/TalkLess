package com.tpalayda.talkless.recyclerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tpalayda.talkless.R

class PresentationRecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation_recycler_view)

        supportFragmentManager.beginTransaction()
                .replace(R.id.RecyclerViewActivity, PresentationRecyclerViewFragment())
                .commit()
    }
}
