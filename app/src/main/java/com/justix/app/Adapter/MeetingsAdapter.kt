package com.justix.app.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.justix.app.data.MeetingModel
import com.justix.app.databinding.ItemMeetingBinding

class MeetingsAdapter(private val onClick: (MeetingModel) -> Unit) :
    ListAdapter<MeetingModel, MeetingsAdapter.MeetingViewHolder>(MeetingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val binding = ItemMeetingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MeetingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class MeetingViewHolder(private val binding: ItemMeetingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meeting: MeetingModel) {
            // Set Reference Code
            binding.txtMeetingCode.text = "REF: ${meeting.meeting_code}"

            // SAFE BINDING: Handle nulls if old data exists
            // If meeting_number is null, default to #1 or "?"
            val sessionNum = meeting.meeting_number ?: 1
            binding.txtMeetingNumber.text = "Session $sessionNum"

            // Handle Status/Score
            if (meeting.status == "completed") {
                binding.txtStatus.text = "${meeting.score ?: 0}"
                binding.txtStatus.textSize = 24f
            } else {
                binding.txtStatus.text = "Active"
                binding.txtStatus.textSize = 14f
            }

            binding.root.setOnClickListener {
                onClick(meeting)
            }
        }
    }

    class MeetingDiffCallback : DiffUtil.ItemCallback<MeetingModel>() {
        override fun areItemsTheSame(oldItem: MeetingModel, newItem: MeetingModel) = oldItem._id == newItem._id
        override fun areContentsTheSame(oldItem: MeetingModel, newItem: MeetingModel) = oldItem == newItem
    }
}