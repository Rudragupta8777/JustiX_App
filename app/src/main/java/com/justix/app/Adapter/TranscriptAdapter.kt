package com.justix.app.Adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.justix.app.R // <--- IMPORT THIS TO FIX 'R' ERROR
import com.justix.app.data.TranscriptItem

class TranscriptAdapter(private val list: List<TranscriptItem>) : RecyclerView.Adapter<TranscriptAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val layout: LinearLayout = v.findViewById(R.id.layoutBubble)
        val text: TextView = v.findViewById(R.id.txtMessage)
        val speaker: TextView = v.findViewById(R.id.txtSpeaker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_transcript, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.text.text = item.text
        holder.speaker.text = item.speaker

        if (item.speaker == "User") {
            holder.layout.gravity = Gravity.END
            holder.text.setBackgroundResource(R.drawable.bg_bubble_user)
        } else {
            holder.layout.gravity = Gravity.START
            holder.text.setBackgroundResource(R.drawable.bg_bubble_ai)
        }
    }

    override fun getItemCount() = list.size
}