package com.tpalayda.talkless.graphs.recyclerviewstats

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tpalayda.talkless.R
import com.tpalayda.talkless.graphs.GraphActivity
import kotlinx.android.synthetic.main.stats_item.view.*

class InformationViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.stats_item, parent, false)) {

    fun bind(presentation: GraphActivity.PresentationInfo) {
        val s = "Slide: " + presentation.slideNumber
        val t = "Time: " + presentation.spentTime.toString() + " [s]"
        itemView.slide_time.text = t
        itemView.slide_number.text = s

    }
}