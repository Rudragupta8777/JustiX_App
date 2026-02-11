package com.justix.app

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.justix.app.databinding.ActivityCreateCaseBinding
import com.justix.app.network.RetrofitClient
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class CreateCaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCaseBinding
    private val PICK_PDF = 101
    private var selectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Light Status Bar for Cream Background
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 1. Back Button Logic
        binding.btnBack.setOnClickListener {
            finish() // Go back to Dashboard
        }

        binding.btnSelectPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF)
        }

        binding.btnUpload.setOnClickListener {
            if (selectedUri != null && binding.etTitle.text.toString().isNotEmpty()) {
                uploadCase()
            } else {
                Toast.makeText(this, "Please enter a title and select a PDF.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF && resultCode == Activity.RESULT_OK) {
            selectedUri = data?.data
            val fileName = File(selectedUri?.path ?: "").name
            binding.txtFileName.text = "Attached: $fileName" // Show file name
            // Use Sage Dark for success state
            binding.txtFileName.setTextColor(getColor(R.color.accent_sage_dark))
        }
    }

    private fun uploadCase() {
        // Start Custom Animation
        showLoadingAnimation()

        val file = getFileFromUri(selectedUri!!)
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val pdfPart = MultipartBody.Part.createFormData("pdf", file.name, requestFile)
        val titlePart = binding.etTitle.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = "Bearer ${result.token}"
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.uploadCase(token, pdfPart, titlePart)
                    withContext(Dispatchers.Main) {

                        // Stop Animation
                        hideLoadingAnimation()

                        if (response.isSuccessful) {
                            Toast.makeText(this@CreateCaseActivity, "Case Filed Successfully.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CreateCaseActivity, "Filing Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        hideLoadingAnimation()
                        e.printStackTrace()
                        Toast.makeText(this@CreateCaseActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // --- CUSTOM ANIMATION LOGIC ---
    private fun showLoadingAnimation() {
        binding.layoutLoader.visibility = View.VISIBLE
        binding.btnUpload.visibility = View.INVISIBLE // Hide button while loading

        val dots = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)

        dots.forEachIndexed { index, dot ->
            val animator = ObjectAnimator.ofFloat(dot, "translationY", 0f, -15f)
            animator.duration = 400
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.startDelay = (index * 120).toLong()
            animator.start()
        }
    }

    private fun hideLoadingAnimation() {
        binding.layoutLoader.visibility = View.GONE
        binding.btnUpload.visibility = View.VISIBLE
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, "temp_upload.pdf")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        return tempFile
    }
}