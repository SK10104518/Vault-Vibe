package com.example.vv.ui.activities

import com.example.vv.MainActivity
import com.example.vv.databinding.ActivityLoginBinding
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

// LoginActivity.kt
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"// Tag for logging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        // If a user is logged in, navigate directly to the main activity
        if (auth.currentUser != null) {
            navigateToMainActivity()
        }
        // Set OnClickListener for the Login button
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        // Set OnClickListener for the Register button
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }
    /**
    * Handles user login using Firebase Authentication.
    */
    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Basic input validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInWithEmail:success")
                    navigateToMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show()
                    // Optionally, you can automatically log in the user after registration
                    // navigateToMainActivity()
                } else {
                    // If registration fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email address is already registered.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            baseContext, "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    /**
     * Navigates to the main activity and finishes the current activity.
     */

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity so the user can't go back to it
    }
}
