package com.tpalayda.talkless.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.tpalayda.talkless.R
import com.tpalayda.talkless.database.MyDatabaseOpenHelper
import com.tpalayda.talkless.database.database
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticsRecyclerViewFragment : Fragment() {

    data class Presentation(val id: Int, val name: String, val date: String)

    private var presentations : ArrayList<Presentation> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rowParser = classParser<Presentation>()
        presentations = ArrayList(context?.database?.readableDatabase?.select("Presentation", "id", "name", "date")?.parseList(rowParser)!!)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = StatisticsAdapter(presentations, view.context)
        }

        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val adapter = recyclerView.adapter as StatisticsAdapter

                val deletedIndex = viewHolder!!.adapterPosition
                val deletedItem = adapter.removeAt(viewHolder.adapterPosition)
                showSnackbar(view, deletedItem, deletedIndex, adapter)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showSnackbar(view: View, deletedItem : Presentation, deletedIndex : Int, adapter: StatisticsAdapter) {
        val snackbar = Snackbar.make(view, deletedItem.name + " removed from list!", Snackbar.LENGTH_LONG)
        var isRestored= false
        snackbar.setActionTextColor(Color.GREEN).setAction("UNDO", View.OnClickListener {
            adapter.restoreAt(deletedItem, deletedIndex)
            isRestored = true
        })
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if(!isRestored) {
                    val id = deletedItem.id
                    val numRowsDeleted = context!!.database.writableDatabase.delete("Presentation",
                            "id = {presId}",
                            "presId" to id)
                    if(numRowsDeleted > 0) {
                        Toast.makeText(context, "Successfully deleted item from the list!", Snackbar.LENGTH_LONG).show()
                    }
                    super.onDismissed(transientBottomBar, event)
                }
            }
        })
        snackbar.show()
    }
}
