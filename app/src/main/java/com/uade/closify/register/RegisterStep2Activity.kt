package com.uade.closify.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.uade.closify.R
import com.uade.closify.databinding.ActivityRegisterStep2Binding
import kotlinx.coroutines.launch

class RegisterStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep2Binding
    private val viewModel: RegisterStep2ViewModel by viewModels()
    private lateinit var registerData: RegisterData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        registerData = intent.getParcelableExtra("register_data") ?: RegisterData()

        setupUI()
        observeState()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnContinue.setOnClickListener {
            registerData.nombre = binding.etName.text.toString()
            registerData.biografia = binding.etBio.text.toString()
            
            val day = binding.etDay.text.toString()
            val month = binding.etMonth.text.toString()
            val year = binding.etYear.text.toString()

            viewModel.register(registerData, day, month, year)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
                launch {
                    viewModel.nameError.collect { error ->
                        binding.tilName.error = error
                    }
                }
                launch {
                    viewModel.birthdateError.collect { error ->
                        if (error != null) {
                            binding.tvBirthdateError.text = error
                            binding.tvBirthdateError.isVisible = true
                        } else {
                            binding.tvBirthdateError.isVisible = false
                        }
                    }
                }
                launch {
                    viewModel.bioError.collect { error ->
                        binding.tilBio.error = error
                    }
                }
            }
        }
    }

    private fun handleUiState(state: RegisterUiState) {
        when (state) {
            is RegisterUiState.Loading -> {
                binding.btnContinue.isEnabled = false
                // Show some progress if needed
            }
            is RegisterUiState.Success -> {
                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                // Navegar a MainActivity (simulada por ahora o si existe)
                // val intent = Intent(this, MainActivity::class.java)
                // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // startActivity(intent)
                finish()
            }
            is RegisterUiState.Error -> {
                binding.btnContinue.isEnabled = true
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
            is RegisterUiState.Idle -> {
                binding.btnContinue.isEnabled = true
            }
        }
    }
}
