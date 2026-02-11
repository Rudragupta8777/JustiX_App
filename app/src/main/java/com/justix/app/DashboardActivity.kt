package com.justix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.justix.app.Adapter.CasesAdapter
import com.justix.app.databinding.ActivityDashboardBinding
import com.justix.app.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val casesAdapter = CasesAdapter { case ->
        val intent = Intent(this, CaseDetailActivity::class.java)
        intent.putExtra("CASE_ID", case._id)
        intent.putExtra("CASE_TITLE", case.title)
        intent.putExtra("CASE_SUMMARY", case.summary)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Light Status Bar for Cream Background
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Setup Recycler
        binding.recyclerCases.layoutManager = LinearLayoutManager(this)
        binding.recyclerCases.adapter = casesAdapter

        // --- UPDATED SWIPE REFRESH COLORS ---
        // Background: Clean White
        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.bg_card_white)
        // Spinner: Sage Green & Ink Grey
        binding.swipeRefresh.setColorSchemeResources(R.color.accent_sage_dark, R.color.text_ink_primary)

        // Handle Swipe Action
        binding.swipeRefresh.setOnRefreshListener {
            fetchCases()
        }

        binding.fabAddCase.setOnClickListener {
            startActivity(Intent(this, CreateCaseActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCases()
    }

    private fun fetchCases() {
        binding.swipeRefresh.isRefreshing = true // Show loader

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = "Bearer ${result.token}"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.getMyCases(token)
                    withContext(Dispatchers.Main) {
                        binding.swipeRefresh.isRefreshing = false // Hide loader

                        if (response.isSuccessful && response.body() != null) {
                            val list = response.body()!!
                            casesAdapter.submitList(list)

                            // Optional: Show "No Cases" text if empty
                            if(list.isEmpty()){
                                Toast.makeText(this@DashboardActivity, "No active briefs found. Create one!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@DashboardActivity, "Unable to retrieve case files.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.swipeRefresh.isRefreshing = false
                        e.printStackTrace()
                        Toast.makeText(this@DashboardActivity, "Connection Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }?.addOnFailureListener {
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
        }
    }
}