package com.justix.app.Adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.justix.app.R
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

        when (item.speaker) {
            "User" -> {
                // USER (Right Side, Green)
                holder.layout.gravity = Gravity.END
                holder.text.setBackgroundResource(R.drawable.bg_bubble_user)
                holder.speaker.gravity = Gravity.END
                holder.speaker.setTextColor(Color.parseColor("#00E676")) // Neon Green
            }
            "Judge" -> {
                // JUDGE (Left Side, Gold)
                holder.layout.gravity = Gravity.START
                holder.text.setBackgroundResource(R.drawable.bg_bubble_judge)
                holder.speaker.gravity = Gravity.START
                holder.speaker.setTextColor(Color.parseColor("#D4AF37")) // Gold
            }
            else -> {
                // LAWYER / OTHER (Left Side, Purple)
                holder.layout.gravity = Gravity.START
                holder.text.setBackgroundResource(R.drawable.bg_bubble_lawyer)
                holder.speaker.gravity = Gravity.START
                holder.speaker.setTextColor(Color.parseColor("#A855F7")) // Purple
            }
        }
    }

    override fun getItemCount() = list.size
}