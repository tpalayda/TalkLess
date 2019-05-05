package com.tpalayda.talkless.presentation

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import com.tpalayda.talkless.R
import kotlinx.android.synthetic.main.activity_presentation.*
import java.io.File

class PresentationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation)

        uploadButton.setOnClickListener {
            val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("application/pdf")
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 1212)
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

                    if(uriString.startsWith("content://")) {
                        var cursor : Cursor?
                        cursor = null
                        try {
                            cursor = contentResolver.query(uri, null, null, null, null)
                            if(cursor != null && cursor.moveToFirst()) {
                                Log.wtf("123", "displayName: " + cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)))
                            }
                        } finally {
                            cursor?.close()
                        }
                    } else if(uriString.startsWith("file://")) {
                        Log.wtf("123", "displayName: " + file.name)
                    }
                    Log.wtf("123", "path:" + path)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}