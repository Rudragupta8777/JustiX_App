package com.justix.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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

        binding.btnSelectPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF)
        }

        binding.btnUpload.setOnClickListener {
            if (selectedUri != null && binding.etTitle.text.toString().isNotEmpty()) {
                uploadCase()
            } else {
                Toast.makeText(this, "Please fill details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF && resultCode == Activity.RESULT_OK) {
            selectedUri = data?.data
            binding.txtFileName.text = "File Selected"
        }
    }

    private fun uploadCase() {
        binding.progressBar.visibility = View.VISIBLE
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
                        binding.progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            Toast.makeText(this@CreateCaseActivity, "Case Created!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CreateCaseActivity, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, "temp.pdf")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        return tempFile
    }
}