package com.tpalayda.talkless.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.DatabaseUtils
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.OpenableColumns
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.tpalayda.talkless.database.database
import com.tpalayda.talkless.R
import com.tpalayda.talkless.recyclerview.StatisticsRecyclerViewFragment
import kotlinx.android.synthetic.main.fragment_presentation.*
import kotlinx.android.synthetic.main.fragment_presentation.view.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.doAsync
import java.io.File
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class PresentationFragment : Fragment() {

    private var startTime : Long = 0
    private var endTime : Long = 0
    private var hashMap : MutableMap<Int, Long> = mutableMapOf()
    private var presentationId : Long? = null
    private lateinit var preferences: SharedPreferences
    private var currentPage : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              sasvedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_presentation, container, false)

        rootView.uploadButton.visibility = View.VISIBLE
        rootView.uploadButton.setOnClickListener {
            val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("application/pdf")
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 1212)
        }

        rootView.closeButton.setOnClickListener {
            rootView.pdfView.visibility = View.GONE
            rootView.closeButton.visibility = View.GONE
            rootView.uploadButton.visibility = View.VISIBLE
            saveHashMap();
        }

        return rootView
    }

    override fun onDestroy() {
        if(startTime != 0L) {
            saveHashMap()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            1212 -> {
                if(resultCode == AppCompatActivity.RESULT_OK) {
                    val uri = data?.data
                    val uriString = uri.toString()
                    val file = File(uriString)
                    val path = file.absolutePath

                    if (uriString.startsWith("content://")) {
                        var cursor: Cursor?
                        cursor = null
                        try {
                            cursor = context?.contentResolver?.query(uri, null, null, null, null)
                            if (cursor != null && cursor.moveToFirst()) {
                                val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) as String
                                doAsync {
                                    val df = SimpleDateFormat("dd/MMM/yyyy HH:mm")
                                    val date : String = df.format(Calendar.getInstance().time)
                                    presentationId = context?.database?.writableDatabase?.insert("Presentation",
                                            "name" to displayName,
                                            "date" to date)
                                }
                            }
                        } finally {
                            cursor?.close()
                        }
                    } else if (uriString.startsWith("file://")) {
                        Log.wtf("123", "displayName: " + file.name)
                    }

                    uploadButton.visibility = View.GONE
                    closeButton.visibility = View.VISIBLE

                    val nightMode : Boolean = preferences.getBoolean("nightModePref", false)
                    //val swipeMode : Boolean = preferences.getBoolean("horizontalSwipePref", false)
                    val swipeMode = true
                    if(nightMode) {
                        closeButton.setImageResource(R.drawable.ic_close_white_24dp)
                    }

                    clickToStart.visibility = View.VISIBLE
                    clickToStart.setOnClickListener {
                        startTime = System.currentTimeMillis()
                        clickToStart.visibility = View.GONE
                        //updateTime(TimeUnit.MILLISECONDS.toSeconds(startTime).toString())
                    }

                    pdfView.visibility = View.VISIBLE
                    pdfView.fromUri(uri)
                            .defaultPage(0)
                            .enableSwipe(swipeMode)
                            .swipeHorizontal(swipeMode)
                            .enableDoubletap(true)
                            .enableAntialiasing(true)
                            .pageFling(swipeMode)
                            .pageSnap(swipeMode)
                            .autoSpacing(swipeMode)
                            .nightMode(nightMode)
                            .scrollHandle(null)
                            .pageFitPolicy(FitPolicy.BOTH)
                            .onPageChange { page, pageCount ->
                                if(hashMap.isEmpty()) {
                                    for(x in 0 until pageCount)
                                        hashMap[x] = 0
                                }
                                if(startTime != 0L ) {
                                    endTime = System.currentTimeMillis()
                                    val timeSpent = endTime - startTime
                                    val time = hashMap.get(page-1)
                                    if(time != null) {
                                        hashMap[page-1] = time + timeSpent
                                    } else {
                                        hashMap[page] = timeSpent
                                    }
                                    startTime = System.currentTimeMillis()
                                }
                                currentPage = page

                            }.load()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveHashMap() {

        endTime = System.currentTimeMillis()
        val timeSpent = endTime - startTime
        val time = hashMap.get(currentPage)
        if(time != null) {
            hashMap[currentPage] = timeSpent + time
        } else {
            hashMap[currentPage] = timeSpent
        }

        doAsync {
            if(presentationId != null && presentationId != -1L) {
                hashMap.forEach {
                    k, v ->

                    val id = presentationId?.toInt()
                    context?.database?.writableDatabase?.insert("PresentationInfo",
                            "slideNumber" to k,
                            "spentTime" to v,
                            "fg_presentation" to id)
                }
            }
        }
    }

/*    private fun updateTime(timeString : String) {
        val timerHandler = Handler()

        var updater = Runnable {  }
        updater = object  : Runnable {
            override fun run() {
                textViewTime.text = timeString
                timerHandler.postDelayed(updater, 1000)
            }
        }

        timerHandler.post(updater)
    }*/
}
