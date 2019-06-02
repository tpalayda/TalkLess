package com.tpalayda.talkless.graphs.recyclerviewstats

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tpalayda.talkless.graphs.GraphActivity

class InformationAdapter(val items: ArrayList<GraphActivity.PresentationInfo>, val context: Context)
    : RecyclerView.Adapter<InformationViewHolder>() {

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        val presentation = items[position]
        holder.bind(presentation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InformationViewHolder(inflater, parent)
    }
}