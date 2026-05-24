package com.uade.closify.register

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.uade.closify.LoginActivity
import com.uade.closify.R
import com.uade.closify.databinding.ActivityRegisterStep1Binding
import kotlinx.coroutines.launch

class RegisterStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep1Binding
    private val viewModel: RegisterStep1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeState()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.validateStep1(username, email, password, confirmPassword) { data ->
                val intent = Intent(this, RegisterStep2Activity::class.java).apply {
                    putExtra("register_data", data)
                }
                startActivity(intent)
            }
        }

        setupLoginText()
    }

    private fun setupLoginText() {
        val fullText = "¿Ya tenés una cuenta? Iniciar Sesión"
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(this@RegisterStep1Activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = getColor(R.color.primary_violet)
                ds.isFakeBoldText = true
            }
        }

        val start = fullText.indexOf("Iniciar Sesión")
        val end = start + "Iniciar Sesión".length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvLogin.text = spannableString
        binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.usernameError.collect { error ->
                        binding.tilUsername.error = error
                    }
                }
                launch {
                    viewModel.emailError.collect { error ->
                        binding.tilEmail.error = error
                    }
                }
                launch {
                    viewModel.passwordError.collect { error ->
                        binding.tilPassword.error = error
                    }
                }
                launch {
                    viewModel.confirmPasswordError.collect { error ->
                        binding.tilConfirmPassword.error = error
                    }
                }
            }
        }
    }
}
