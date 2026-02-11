package com.justix.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.justix.app.Adapter.TranscriptAdapter // Import the adapter since it's in a sub-package now
import com.justix.app.data.MeetingModel
import com.justix.app.databinding.ActivityMeetingDetailBinding

class MeetingDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeetingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = intent.getStringExtra("MEETING_DATA")
        val meeting = Gson().fromJson(json, MeetingModel::class.java)

        // Handle nulls safely just in case
        binding.txtScore.text = "Score: ${meeting.score ?: 0}/100"
        binding.txtFeedback.text = meeting.feedback ?: "No feedback available."

        if (meeting.transcript != null) {
            binding.recyclerTranscript.layoutManager = LinearLayoutManager(this)
            binding.recyclerTranscript.adapter = TranscriptAdapter(meeting.transcript)
        }
    }
}