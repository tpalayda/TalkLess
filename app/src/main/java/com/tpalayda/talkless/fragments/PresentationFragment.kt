package com.tpalayda.talkless.fragments

import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class PresentationFragment : Fragment() {

    private var startTime : Long = 0
    private var endTime : Long = 0
    private var hashMap : MutableMap<Int, Long> = mutableMapOf()
    private var presentationId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when(requestCode) {
            1212 -> {
                if(resultCode == AppCompatActivity.RESULT_OK) {
                    val uri = data.data
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
                                Log.wtf("123", "displayName: " + displayName)
                                doAsync {
                                    val df = SimpleDateFormat("dd/MMM/yyyy HH:mm")
                                    val date : String = df.format(Calendar.getInstance().time)
                                    presentationId = context?.database?.writableDatabase?.insert("Presentation",
                                            "name" to displayName,
                                            "date" to date)
                                }

                                val count = DatabaseUtils.queryNumEntries(context?.database?.readableDatabase, "Presentation")

                                //Log.wtf("123", "d: " + count)
                            }
                        } finally {
                            cursor?.close()
                        }
                    } else if (uriString.startsWith("file://")) {
                        Log.wtf("123", "displayName: " + file.name)
                    }
                    //Log.wtf("123", "path:" + path)

                    uploadButton.visibility = View.GONE
                    closeButton.visibility = View.VISIBLE

                    pdfView.visibility = View.VISIBLE
                    pdfView.fromUri(uri)
                            .defaultPage(0)
                            .enableSwipe(true)
                            .swipeHorizontal(true)
                            .enableDoubletap(true)
                            .onPageChange { page, pageCount ->
                                if(hashMap.isEmpty()) {
                                    for(x in 0 until pageCount)
                                        hashMap[x] = 0
                                }
                                if(startTime != 0L ) {
                                    endTime = System.currentTimeMillis()
                                    val timeSpent = endTime - startTime
                                    val time = hashMap.get(page-1)
                                    Log.wtf("123", "page:" + page)
                                    if(time != null) {
                                        hashMap[page-1] = time + timeSpent
                                    }
                                    else {
                                        hashMap[page] = timeSpent
                                    }
                                    startTime = System.currentTimeMillis()
                                    //Log.wtf("123", "time:" + timeSpent)
                                    //Log.wtf("123", "page:" + page) //page is a new page
                                } else {
                                    startTime = System.currentTimeMillis()
                                }
                            }.load()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun saveHashMap() {
        doAsync {
            if(presentationId != null && presentationId != -1L) {
                hashMap.forEach {
                    k, v ->

                    val key = k.toInt()
                    val id = presentationId?.toInt()
                    context?.database?.writableDatabase?.insert("PresentationInfo",
                            "slideNumber" to key,
                            "spentTime" to v,
                            "fg_presentation" to id)
                }
            }
            val count = DatabaseUtils.queryNumEntries(context?.database?.readableDatabase, "PresentationInfo")
            Log.wtf("123", "PresentantionInfo:" + count)
        }
    }
}
