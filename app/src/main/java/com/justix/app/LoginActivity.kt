package com.justix.app

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.justix.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Light Status Bar Icons for Cream Background
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()

        playEntranceAnimation()

        if (auth.currentUser != null) {
            navigateToDashboard()
        }

        binding.btnGoogleSign.setOnClickListener {
            signIn()
        }
    }

    private fun playEntranceAnimation() {
        binding.headerContainer.alpha = 0f
        binding.headerContainer.animate().alpha(1f).setDuration(1200).start()

        binding.loginCard.translationY = 100f
        binding.loginCard.alpha = 0f
        binding.loginCard.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(200)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun signIn() {
        // Slight press animation
        binding.btnGoogleSign.animate().scaleX(0.98f).scaleY(0.98f).setDuration(100).withEndAction {
            binding.btnGoogleSign.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        }.start()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(this, gso)
        startActivityForResult(signInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoadingAnimation()

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToDashboard()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    hideLoadingAnimation()
                }
            }
    }

    private fun showLoadingAnimation() {
        binding.layoutLoader.visibility = View.VISIBLE
        binding.btnGoogleSign.isEnabled = false
        binding.btnGoogleSign.alpha = 0.5f

        val dots = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)
        dots.forEachIndexed { index, dot ->
            val animator = ObjectAnimator.ofFloat(dot, "translationY", 0f, -15f)
            animator.duration = 450
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.startDelay = (index * 120).toLong()
            animator.start()
        }
    }

    private fun hideLoadingAnimation() {
        binding.layoutLoader.visibility = View.GONE
        binding.btnGoogleSign.isEnabled = true
        binding.btnGoogleSign.alpha = 1f
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}