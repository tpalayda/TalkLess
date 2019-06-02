package com.tpalayda.talkless.recyclerview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tpalayda.talkless.R
import com.tpalayda.talkless.database.MyDatabaseOpenHelper
import com.tpalayda.talkless.database.database
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticsRecyclerViewFragment : Fragment() {

    data class Presentation(val id: Int, val name: String, val date: String)

    private var presentations : List<Presentation> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.wtf("123", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rowParser = classParser<Presentation>()
        presentations = context?.database?.readableDatabase?.select("Presentation", "id", "name", "date")?.parseList(rowParser)!!
        Log.wtf("123", "size:" + presentations.size)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = StatisticsAdapter(presentations, view.context)
        }
    }
}
