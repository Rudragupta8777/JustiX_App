package com.justix.app

import android.content.Intent
import android.os.Bundle
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
        intent.putExtra("CASE_SUMMARY", case.text_content)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerCases.layoutManager = LinearLayoutManager(this)
        binding.recyclerCases.adapter = casesAdapter

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
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = "Bearer ${result.token}"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.getMyCases(token)
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Dispatchers.Main) {
                            casesAdapter.submitList(response.body()!!)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}