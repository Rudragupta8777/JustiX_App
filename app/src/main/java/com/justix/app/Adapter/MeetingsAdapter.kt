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
            binding.txtMeetingCode.text = "Session Code: ${meeting.meeting_code}"

            // Handle null score or status
            binding.txtStatus.text = if (meeting.status == "completed") {
                "Score: ${meeting.score ?: 0}/100"
            } else {
                "In Progress"
            }

            binding.root.setOnClickListener { onClick(meeting) }
        }
    }

    class MeetingDiffCallback : DiffUtil.ItemCallback<MeetingModel>() {
        override fun areItemsTheSame(oldItem: MeetingModel, newItem: MeetingModel) = oldItem._id == newItem._id
        override fun areContentsTheSame(oldItem: MeetingModel, newItem: MeetingModel) = oldItem == newItem
    }
}