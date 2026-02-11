package com.justix.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.justix.app.Adapter.TranscriptAdapter
import com.justix.app.data.MeetingModel
import com.justix.app.databinding.ActivityMeetingDetailBinding

class MeetingDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeetingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Light Status Bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.btnBack.setOnClickListener { finish() }

        val json = intent.getStringExtra("MEETING_DATA")
        if (json != null) {
            val meeting = Gson().fromJson(json, MeetingModel::class.java)

            // 1. Bind Score
            binding.txtScore.text = "${meeting.score ?: 0}/100"

            // 2. Bind Previews (Show only first 150 chars roughly)
            binding.txtSummaryPreview.text = meeting.summary ?: "No summary."
            binding.txtFeedbackPreview.text = meeting.feedback ?: "No feedback."

            // 3. Setup Click Listener for Full Dialog
            binding.cardReport.setOnClickListener {
                showFullReportDialog(
                    meeting.summary ?: "No summary available.",
                    meeting.feedback ?: "No feedback available."
                )
            }

            // 4. Setup Transcript
            if (meeting.transcript != null) {
                binding.recyclerTranscript.layoutManager = LinearLayoutManager(this)
                binding.recyclerTranscript.adapter = TranscriptAdapter(meeting.transcript)
            }
        }
    }

    private fun showFullReportDialog(summary: String, feedback: String) {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_full_report)

        val txtSummary = dialog.findViewById<TextView>(R.id.txtFullSummary)
        val txtFeedback = dialog.findViewById<TextView>(R.id.txtFullFeedback)

        txtSummary?.text = summary
        txtFeedback?.text = feedback

        dialog.show()
    }
}