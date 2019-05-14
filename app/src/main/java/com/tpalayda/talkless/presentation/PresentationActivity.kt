package com.tpalayda.talkless.presentation

import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.tpalayda.talkless.R
import com.tpalayda.talkless.database.MyDatabaseOpenHelper
import com.tpalayda.talkless.database.database
import kotlinx.android.synthetic.main.activity_presentation.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.doAsync
import java.io.File


//TODO save state
class PresentationActivity : AppCompatActivity() {

    private var startTime : Long = 0
    private var endTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation)

        uploadButton.setOnClickListener {
            val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("application/pdf")
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 1212)
        }

        showStatisticsButton.setOnClickListener {
            val intent = Intent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when(requestCode) {
            1212 -> {
                if(resultCode == RESULT_OK) {
                    val uri = data.data
                    val uriString = uri.toString()
                    val file = File(uriString)
                    val path = file.absolutePath

                    if (uriString.startsWith("content://")) {
                        var cursor: Cursor?
                        cursor = null
                        try {
                            cursor = contentResolver.query(uri, null, null, null, null)
                            if (cursor != null && cursor.moveToFirst()) {
                                val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) as String
                                Log.wtf("123", "displayName: " + displayName)
                                doAsync {
                                    database.writableDatabase.insert("Presentation", "name" to displayName)
                                }
                                val count = DatabaseUtils.queryNumEntries(database.readableDatabase, "Presentation")
                                Log.wtf("123", "d: " + count)
                            }
                        } finally {
                            cursor?.close()
                        }
                    } else if (uriString.startsWith("file://")) {
                        Log.wtf("123", "displayName: " + file.name)
                    }
                    Log.wtf("123", "path:" + path)

                    uploadButton.visibility = View.GONE
                    showStatisticsButton.visibility = View.GONE

                    pdfView.visibility = View.VISIBLE
                    pdfView.fromUri(uri)
                            .defaultPage(0)
                            .enableSwipe(true)
                            .swipeHorizontal(true)
                            .enableDoubletap(true)
                            .onPageChange { page, pageCount ->
                                if(startTime != 0L ) {
                                    endTime = System.currentTimeMillis()
                                    val timeSpent = endTime - startTime
                                    startTime = System.currentTimeMillis()
                                    Log.wtf("123", "time:" + timeSpent)
                                    Log.wtf("123", "page:" + page) //page is a new page
                                } else {
                                    startTime = System.currentTimeMillis()
                                }
                            }.load()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.presentation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.close -> {
                pdfView.visibility = View.GONE
                uploadButton.visibility = View.VISIBLE
                showStatisticsButton.visibility = View.VISIBLE
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
