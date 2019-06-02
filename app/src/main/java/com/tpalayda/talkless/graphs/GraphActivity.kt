package com.tpalayda.talkless.graphs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.tpalayda.talkless.R
import com.tpalayda.talkless.database.database
import com.tpalayda.talkless.graphs.recyclerviewstats.InformationAdapter
import kotlinx.android.synthetic.main.activity_graph.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.TimeUnit

class GraphActivity : AppCompatActivity() {

    data class PresentationInfo(val slideNumber: String, val spentTime: String)

    val informations: ArrayList<PresentationInfo> = ArrayList()
    val series: BarGraphSeries<DataPoint> = BarGraphSeries(arrayOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

/*        val series : LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)
        ))*/

        //graph.addSeries(series)
        graph.title = "y = time, x = slide number"

        getData(intent.getIntExtra("id", 0))

        recyclerViewStats.layoutManager = LinearLayoutManager(this)
        recyclerViewStats.adapter = InformationAdapter(informations, this)

        graph.gridLabelRenderer.padding = 50
        //graph.gridLabelRenderer.setHumanRounding(true)
    }

    fun getData(id: Int) {
        doAsync {
            val query = "SELECT slideNumber, spentTime FROM PresentationInfo INNER JOIN Presentation ON fg_presentation = Presentation.id WHERE Presentation.id = " + id
            database.use {
                val cursor = database.readableDatabase.rawQuery(query, null)
                while(cursor.moveToNext()) {
                    val slideNumber = cursor.getString(0)
                    val spentTime = cursor.getString(1)
                    val presentationInfo = PresentationInfo(slideNumber, spentTime)
                    informations.add(presentationInfo)
                }
                cursor.close()
                recyclerViewStats.adapter.notifyDataSetChanged()
                //var arrayList = Array<DataPoint>(informations.size, {DataPoint(0.0, 0.0)})
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
