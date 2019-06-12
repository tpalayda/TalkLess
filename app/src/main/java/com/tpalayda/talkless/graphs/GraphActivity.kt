package com.tpalayda.talkless.graphs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.tpalayda.talkless.R
import com.tpalayda.talkless.database.database
import com.tpalayda.talkless.graphs.recyclerviewstats.InformationAdapter
import com.tpalayda.talkless.recyclerview.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_graph.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.TimeUnit

class GraphActivity : AppCompatActivity() {

    data class PresentationInfo(val slideNumber: String, val spentTime: Long)

    private val informations: ArrayList<PresentationInfo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        val nightMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("nightModePref", false)

        if(nightMode) {
            setTheme(R.style.NightTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)


        graph.title = "y = time, x = slide number"

        getData(intent.getIntExtra("id", 0))

        recyclerViewStats.layoutManager = LinearLayoutManager(this)
        recyclerViewStats.adapter = InformationAdapter(informations, this)

        graph.gridLabelRenderer.padding = 50
    }

    fun getData(id: Int) {
        doAsync {
            val query = "SELECT slideNumber, spentTime FROM PresentationInfo INNER JOIN Presentation ON fg_presentation = Presentation.id WHERE Presentation.id = " + id
            database.use {
                val cursor = database.readableDatabase.rawQuery(query, null)
                while(cursor.moveToNext()) {
                    val slideNumber = cursor.getString(0)
                    val spentTime = cursor.getString(1).toLong()
                    val spentTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(spentTime)
                    val presentationInfo = PresentationInfo(slideNumber, spentTimeInSeconds)
                    informations.add(presentationInfo)
                }
                cursor.close()
                recyclerViewStats.adapter.notifyDataSetChanged()
                var arrayList = arrayOf<DataPoint>()
                informations.forEachIndexed { index, presentationInfo ->
                    val slideNumber = presentationInfo.slideNumber.toDouble()
                    val spentTime = presentationInfo.spentTime.toDouble()
                    Log.wtf("123", "spentTime:" + spentTime)
                    val data = DataPoint(slideNumber, spentTime)
                    arrayList += data
                }
                val series = BarGraphSeries(arrayList)
                series.spacing = 50
                graph.addSeries(series)

            }
        }
    }
}
