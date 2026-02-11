package com.justix.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.justix.app.Adapter.MeetingsAdapter
import com.justix.app.databinding.ActivityCaseDetailBinding
import com.justix.app.network.RetrofitClient
import kotlinx.coroutines.*

class CaseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaseDetailBinding
    private var fullSummary: String = "" // Store full text here

    private val meetingsAdapter = MeetingsAdapter { meeting ->
        val intent = Intent(this, MeetingDetailActivity::class.java)
        intent.putExtra("MEETING_DATA", Gson().toJson(meeting))
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val caseId = intent.getStringExtra("CASE_ID") ?: return
        fullSummary = intent.getStringExtra("CASE_SUMMARY") ?: "No analysis available."

        // 1. Setup UI
        binding.txtCaseTitle.text = intent.getStringExtra("CASE_TITLE")
        binding.txtCaseSummary.text = fullSummary // It will ellipsize automatically

        // 2. Recycler & Refresh Setup
        binding.recyclerMeetings.layoutManager = LinearLayoutManager(this)
        binding.recyclerMeetings.adapter = meetingsAdapter

        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.bg_card_white)
        binding.swipeRefresh.setColorSchemeResources(R.color.accent_sage_dark, R.color.text_ink_primary)

        binding.swipeRefresh.setOnRefreshListener {
            fetchHistory(caseId)
        }

        // 3. Actions
        binding.btnBack.setOnClickListener { finish() }

        binding.btnStartMeeting.setOnClickListener {
            val intent = Intent(this, MeetingActivity::class.java)
            intent.putExtra("CASE_ID", caseId)
            startActivity(intent)
        }

        // 4. "Smart" Summary Reader (Bottom Sheet)
        binding.btnReadMore.setOnClickListener {
            showFullSummarySheet()
        }

        // Also clicking the card works
        binding.cardSummary.setOnClickListener { showFullSummarySheet() }

        // Initial Load
        fetchHistory(caseId)
    }

    private fun showFullSummarySheet() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_summary_sheet) // We need to create this layout

        // Set text
        val txtContent = dialog.findViewById<TextView>(R.id.txtFullSummary)
        txtContent?.text = fullSummary

        dialog.show()
    }

    private fun fetchHistory(caseId: String) {
        binding.swipeRefresh.isRefreshing = true

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = "Bearer ${result.token}"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.getCaseHistory(token, caseId)
                    withContext(Dispatchers.Main) {
                        binding.swipeRefresh.isRefreshing = false
                        if (response.isSuccessful && response.body() != null) {
                            meetingsAdapter.submitList(response.body()!!)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.swipeRefresh.isRefreshing = false
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val caseId = intent.getStringExtra("CASE_ID")
        if (caseId != null) fetchHistory(caseId)
    }
}