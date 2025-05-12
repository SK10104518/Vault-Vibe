package com.example.vv.ui.activities // Consider organizing auth-related activities in a package

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.example.vv.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterActivity" // Tag for logging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set OnClickListener for the Register button
        binding.btnRegisterAccount.setOnClickListener {
            registerUser()
        }

        // Set OnClickListener for the login prompt TextView
        binding.tvLoginPrompt.setOnClickListener {
            finish() // Go back to the previous activity (LoginActivity)
        }
    }

    /**
     * Handles user registration using Firebase Authentication.
     */
    private fun registerUser() {
        val email = binding.etEmailRegister.text.toString().trim()
        val password = binding.etPasswordRegister.text.toString().trim()
        val confirmPassword = binding.etConfirmPasswordRegister.text.toString().trim()

        // Basic input validation
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Password confirmation validation
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Optional: Add more password strength validation here

        // Create a new user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Log.d(TAG, "createUserWithEmail:success") // Log success
                    Toast.makeText(this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show()
                    // After successful registration, navigate back to the Login screen
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception) // Log failure
                    // Handle specific errors, like email already in use
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
}
