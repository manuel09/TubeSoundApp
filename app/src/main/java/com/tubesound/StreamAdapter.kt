package com.tubesound

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class StreamAdapter(
    private val onClick: (StreamInfoItem) -> Unit
) : RecyclerView.Adapter<StreamAdapter.ViewHolder>() {

    private var items = listOf<StreamInfoItem>()

    fun submitList(newItems: List<StreamInfoItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: StreamInfoItem) {
            titleView.text = item.name
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size
}