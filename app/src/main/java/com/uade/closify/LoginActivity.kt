package com.uade.closify

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.uade.closify.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeState()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        setupRegisterText()
    }

    private fun setupRegisterText() {
        val fullText = "¿No tenés cuenta? Registrate"
        val spannableString = SpannableString(fullText)
        
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Navegar a Registro
                startActivity(Intent(this@LoginActivity, com.uade.closify.register.RegisterStep1Activity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = getColor(R.color.primary_violet)
                ds.isFakeBoldText = true
            }
        }

        val start = fullText.indexOf("Registrate")
        val end = start + "Registrate".length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvRegister.text = spannableString
        binding.tvRegister.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun handleUiState(state: LoginUiState) {
        when (state) {
            is LoginUiState.Idle -> {
                binding.progressBar.isVisible = false
                binding.btnLogin.isEnabled = true
            }
            is LoginUiState.Loading -> {
                binding.progressBar.isVisible = true
                binding.btnLogin.isEnabled = false
            }
            is LoginUiState.Success -> {
                binding.progressBar.isVisible = false
                Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                // Navegar a MainActivity
                // startActivity(Intent(this, MainActivity::class.java))
                // finish()
            }
            is LoginUiState.Error -> {
                binding.progressBar.isVisible = false
                binding.btnLogin.isEnabled = true
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
