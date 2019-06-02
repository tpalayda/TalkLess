package com.tpalayda.talkless.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tpalayda.talkless.R
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_view_item, parent, false)) {

    fun bind(presentation: StatisticsRecyclerViewFragment.Presentation) {
        itemView.presentation_title.text = presentation.name
        itemView.presentation_date.text = presentation.date
    }
}
