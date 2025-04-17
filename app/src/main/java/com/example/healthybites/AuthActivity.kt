package com.example.healthybites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthybites.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditTextLogin.text.toString()
            val password = binding.passwordEditTextLogin.text.toString()
            onLoginClicked(email, password)
        }

        binding.registerButton.setOnClickListener {
            binding.viewFlipper.showNext()
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditTextRegister.text.toString()
            val password = binding.passwordEditTextRegister.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            onSignupClicked(email, password, confirmPassword)
        }

        binding.loginButtonRegister.setOnClickListener {
            binding.viewFlipper.showPrevious()
        }
    }

    private fun onLoginClicked(email: String, password: String) {
        binding.loginButton.visibility = View.GONE
        binding.errorTextViewLogin.visibility = View.GONE
        binding.loginLoading.visibility = View.VISIBLE

        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (isEmailValid && password.length >= 6) {
            val activity = this
            lifecycleScope.launch {
                val result = login(email, password)
                binding.loginButton.visibility = View.VISIBLE
                binding.loginLoading.visibility = View.GONE
                if (result.isSuccess) {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("Email", email)
                    startActivity(intent)
                    finish()
                } else if (result.isFailure) {
                    binding.errorTextViewLogin.visibility = View.VISIBLE
                    binding.errorTextViewLogin.text = result.exceptionOrNull()?.message
                }
            }
        }
    }

    private fun onSignupClicked(email: String, password: String, confirmPassword: String) {
        binding.signUpButton.visibility = View.GONE
        binding.errorTextViewRegister.visibility = View.GONE
        binding.registerLoading.visibility = View.VISIBLE
        val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordMatch = (password.isNotEmpty()) && password == confirmPassword
        if (isEmailValid && isPasswordMatch && password.length >= 6) {
            val activity = this
            lifecycleScope.launch {
                val result = signUp(email, password)
                Log.e("TAG", "EmailRegisterForm: " + result.isFailure + result.toString())
                binding.signUpButton.visibility = View.VISIBLE
                binding.registerLoading.visibility = View.GONE
                if (result.isSuccess) {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("Email", email)
                    startActivity(intent)
                    finish()
                } else if (result.isFailure) {
                    binding.errorTextViewRegister.visibility = View.VISIBLE
                    binding.errorTextViewRegister.text = result.exceptionOrNull()?.message
                }
            }
        } else {
            binding.errorTextViewRegister.visibility = View.VISIBLE
            if (!isEmailValid) {
                binding.errorTextViewRegister.text = "Please enter a valid email address"
            } else if (!isPasswordMatch) {
                binding.errorTextViewRegister.text = "Passwords do not match"
            } else if (password.length < 6) {
                binding.errorTextViewRegister.text = "Password must be at least 6 characters long"
            }
        }
    }

    private suspend fun login(email: String, password: String): Result<FirebaseAuth> = try {
        val auth = FirebaseAuth.getInstance()
        withTimeout(2000) {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun signUp(email: String, password: String): Result<FirebaseAuth> = try {
        val auth = FirebaseAuth.getInstance()
        withTimeout(2000) {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(auth)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}