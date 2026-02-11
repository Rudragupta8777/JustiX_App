package com.justix.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.justix.app.databinding.ActivityMeetingBinding
import com.google.firebase.auth.FirebaseAuth
import com.justix.app.data.StartMeetingRequest
import com.justix.app.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MeetingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeetingBinding
    private var meetingId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val caseId = intent.getStringExtra("CASE_ID") ?: return

        generateMeetingCode(caseId)

        binding.btnDone.setOnClickListener {
            // Optional: Call "End Meeting" API here if you want button control
            finish()
        }
    }

    private fun generateMeetingCode(caseId: String) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
            val token = "Bearer ${tokenResult.token}"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.startMeeting(token,
                        StartMeetingRequest(caseId)
                    )
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val code = response.body()!!.meetingCode
                            meetingId = response.body()!!.meetingId

                            // SHOW THE HUGE CODE
                            binding.txtCode.text = formatCode(code)
                        } else {
                            Toast.makeText(this@MeetingActivity, "Error starting meeting", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Makes "123456" look like "123 456"
    private fun formatCode(code: String): String {
        return if (code.length == 6) {
            "${code.substring(0, 3)} ${code.substring(3)}"
        } else code
    }
}