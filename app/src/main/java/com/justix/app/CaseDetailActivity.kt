package com.justix.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.justix.app.Adapter.MeetingsAdapter
import com.justix.app.databinding.ActivityCaseDetailBinding
import com.justix.app.network.RetrofitClient
import kotlinx.coroutines.*

class CaseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaseDetailBinding

    private val meetingsAdapter = MeetingsAdapter { meeting ->
        // --- FIX 1: Correct Intent to the main package Activity ---
        val intent = Intent(this, MeetingDetailActivity::class.java) // Ensure no "Adapter" import above
        intent.putExtra("MEETING_DATA", Gson().toJson(meeting))
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val caseId = intent.getStringExtra("CASE_ID") ?: return
        binding.txtCaseTitle.text = intent.getStringExtra("CASE_TITLE")
        binding.txtCaseSummary.text = intent.getStringExtra("CASE_SUMMARY")?.take(300) + "..."

        binding.recyclerMeetings.layoutManager = LinearLayoutManager(this)
        binding.recyclerMeetings.adapter = meetingsAdapter

        fetchHistory(caseId)

        // --- FIX 2: Connect the "Start Meeting" button to your new MeetingActivity ---
        binding.btnStartMeeting.setOnClickListener {
            val intent = Intent(this, MeetingActivity::class.java)
            intent.putExtra("CASE_ID", caseId)
            startActivity(intent)
        }
    }

    private fun fetchHistory(caseId: String) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = "Bearer ${result.token}"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.getCaseHistory(token, caseId)
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Dispatchers.Main) {
                            meetingsAdapter.submitList(response.body()!!)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Refresh history when coming back from a meeting
    override fun onResume() {
        super.onResume()
        val caseId = intent.getStringExtra("CASE_ID")
        if (caseId != null) fetchHistory(caseId)
    }
}