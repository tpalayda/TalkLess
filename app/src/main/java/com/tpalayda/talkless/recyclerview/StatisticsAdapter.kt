package com.tpalayda.talkless.recyclerview

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tpalayda.talkless.database.database
import com.tpalayda.talkless.graphs.GraphActivity
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync

class StatisticsAdapter(private val items: ArrayList<StatisticsRecyclerViewFragment.Presentation>, private val context: Context)
    : RecyclerView.Adapter<StatisticsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StatisticsViewHolder(inflater, parent).listen { position, type ->
            val item = items.get(position)
            val intent = Intent(context, GraphActivity::class.java)
            intent.putExtra("id", item.id)
            context.startActivity(intent)
        }
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val presentation = items[position]
        holder.bind(presentation)
    }

    fun removeAt(position: Int) : StatisticsRecyclerViewFragment.Presentation {
        val result = items[position]
        items.removeAt(position)
        notifyItemRemoved(position)
        return result
    }

    fun restoreAt(presentation : StatisticsRecyclerViewFragment.Presentation, position: Int) {
        items.add(position, presentation)
        notifyItemInserted(position)
    }
}